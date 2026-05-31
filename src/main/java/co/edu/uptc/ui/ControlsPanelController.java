package co.edu.uptc.ui;

import co.edu.uptc.model.Shape;
import co.edu.uptc.model.Graph;
import co.edu.uptc.model.Node;
import co.edu.uptc.service.CartesianPlaneService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

/**
 * Controlador para el panel de controles lateral.
 */
public class ControlsPanelController {
    @FXML
    private ListView<Shape> shapesListView;
    
    @FXML
    private TextField xTextField;
    
    @FXML
    private TextField yTextField;
    
    @FXML
    private TextField sizeTextField;
    
    @FXML
    private TextField minXTextField;
    
    @FXML
    private TextField maxXTextField;
    
    @FXML
    private TextField minYTextField;
    
    @FXML
    private TextField maxYTextField;
    
    @FXML
    private TextField gridSpacingTextField;

    @FXML
    private TextField deleteNodeXTextField;

    @FXML
    private TextField deleteNodeYTextField;

    private CartesianPlane2D cartesianCanvas;
    private CartesianPlaneService planeService;

    @FXML
    public void initialize() {
        // Inicializar ListView con todas las formas disponibles
        shapesListView.setItems(FXCollections.observableArrayList(Shape.values()));
        
        // Seleccionar la primera forma por defecto
        if (Shape.values().length > 0) {
            shapesListView.getSelectionModel().select(0);
        }
        
        // Establecer valores por defecto en los TextFields
        xTextField.setText("0");
        yTextField.setText("0");
        sizeTextField.setText("10");
        minXTextField.setText("-50");
        maxXTextField.setText("50");
        minYTextField.setText("-50");
        maxYTextField.setText("50");
        gridSpacingTextField.setText("10");
    }

    /**
     * Establece las referencias necesarias para el controlador.
     */
    public void setCartesianCanvas(CartesianPlane2D cartesianCanvas) {
        this.cartesianCanvas = cartesianCanvas;
    }

    /**
     * Establece el servicio del plano cartesiano.
     */
    public void setPlaneService(CartesianPlaneService planeService) {
        this.planeService = planeService;
    }

    /**
     * Obtiene la forma seleccionada actualmente.
     */
    public Shape getSelectedShape() {
        return shapesListView.getSelectionModel().getSelectedItem();
    }

    /**
     * Parsea un valor numérico desde un TextField.
     */
    private double parseDouble(TextField field, double defaultValue) {
        try {
            String text = field.getText().trim();
            if (text.isEmpty()) {
                return defaultValue;
            }
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            System.err.println("Valor inválido en campo: " + field.getPromptText() + ". Usando valor por defecto: " + defaultValue);
            field.setText(String.valueOf(defaultValue));
            return defaultValue;
        }
    }

    /**
     * Parsea un valor entero desde un TextField.
     */
    private int parseInt(TextField field, int defaultValue) {
        try {
            String text = field.getText().trim();
            if (text.isEmpty()) {
                return defaultValue;
            }
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            System.err.println("Valor inválido en campo. Usando valor por defecto: " + defaultValue);
            field.setText(String.valueOf(defaultValue));
            return defaultValue;
        }
    }

    @FXML
    private void onAddShape() {
        if (cartesianCanvas == null) {
            System.err.println("CartesianCanvas no está inicializado");
            return;
        }

        Shape selectedShape = getSelectedShape();
        if (selectedShape == null) {
            System.err.println("No hay una forma seleccionada");
            return;
        }

        double x = parseDouble(xTextField, 0);
        double y = parseDouble(yTextField, 0);
        double size = parseDouble(sizeTextField, 10);

        cartesianCanvas.addFigure(selectedShape, x, y, size);
        cartesianCanvas.draw();
    }

    @FXML
    private void onUpdateRange() {
        if (planeService == null) {
            System.err.println("PlaneService no está inicializado");
            return;
        }

        double minX = parseDouble(minXTextField, -50);
        double maxX = parseDouble(maxXTextField, 50);
        double minY = parseDouble(minYTextField, -50);
        double maxY = parseDouble(maxYTextField, 50);

        planeService.setPlaneRange(minX, maxX, minY, maxY);
        if (cartesianCanvas != null) {
            cartesianCanvas.draw();
        }
    }

    @FXML
    private void onToggleGrid() {
        if (cartesianCanvas == null) {
            System.err.println("CartesianCanvas no está inicializado");
            return;
        }
        
        int spacing = parseInt(gridSpacingTextField, 10);
        cartesianCanvas.toggleGridVisibility(spacing);
        cartesianCanvas.draw();
    }

    /**
     * Obtiene el espaciado actual de la cuadrícula.
     */
    public int getGridSpacing() {
        return parseInt(gridSpacingTextField, 10);
    }

    @FXML
    private void onDeleteNode() {
        if (cartesianCanvas == null) {
            System.err.println("CartesianCanvas no está inicializado");
            return;
        }

        double x = parseDouble(deleteNodeXTextField, 0);
        double y = parseDouble(deleteNodeYTextField, 0);

        Graph graph = cartesianCanvas.getGraph();
        if (graph == null) {
            System.err.println("El grafo no está inicializado");
            return;
        }

        // Buscar y eliminar el nodo con las coordenadas exactas
        Node nodeToDelete = null;
        for (Node node : graph.getNodes()) {
            if (node.getX() == x && node.getY() == y) {
                nodeToDelete = node;
                break;
            }
        }

        if (nodeToDelete != null) {
            graph.removeNode(nodeToDelete.getId());
            cartesianCanvas.draw();
            
            // Mostrar confirmación
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nodo Eliminado");
            alert.setHeaderText("Eliminación Exitosa");
            alert.setContentText("El nodo en coordenadas (" + x + ", " + y + ") ha sido eliminado.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nodo No Encontrado");
            alert.setHeaderText("No existe nodo");
            alert.setContentText("No se encontró un nodo en las coordenadas (" + x + ", " + y + ")");
            alert.showAndWait();
        }
    }
}