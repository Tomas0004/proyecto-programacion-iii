package co.edu.uptc.ui;

import co.edu.uptc.model.Graph;
import co.edu.uptc.model.Node;
import co.edu.uptc.model.Shape;
import co.edu.uptc.service.CartesianPlaneService;
import co.edu.uptc.service.GraphService;
import co.edu.uptc.service.GraphService.DijkstraResult;
import co.edu.uptc.service.PlaneSerializer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

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
    
    @FXML
    private TextField segmentStart1TextField;
    
    @FXML
    private TextField segmentStart2TextField;
    
    @FXML
    private TextField segmentEnd1TextField;
    
    @FXML
    private TextField segmentEnd2TextField;
    
    @FXML
    private TextField segmentWidthTextField;
    
    @FXML
    private TextField filenameTextField;

    private CartesianPlane2D cartesianCanvas;
    private CartesianPlaneService planeService;

    @FXML
    public void initialize() {
        // Inicializar ListView con todas las formas
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
        
        // Robot siempre tiene tamaño 1
        double size = selectedShape == Shape.ROBOT || selectedShape == Shape.DESTINATION ? 1.0 : parseDouble(sizeTextField, 10);

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

            System.out.println(cartesianCanvas.getGraph()); ////////////////

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
    
    /**
     * Encuentra el robot en las figuras.
     */
    public CartesianPlane2D.Figure getRobot() {
        if (cartesianCanvas == null) return null;
        return cartesianCanvas.getFigureByShape(Shape.ROBOT);
    }
    
    /**
     * Encuentra el destino en las figuras.
     */
    public CartesianPlane2D.Figure getDestination() {
        if (cartesianCanvas == null) return null;
        return cartesianCanvas.getFigureByShape(Shape.DESTINATION);
    }
    
    /**
     * Encuentra el camino más corto entre el robot y el destino.
     */
    @FXML
    private void onFindShortestPath() {
        if (cartesianCanvas == null) {
            System.err.println("CartesianCanvas no está inicializado");
            return;
        }
        
        CartesianPlane2D.Figure robot = getRobot();
        CartesianPlane2D.Figure destination = getDestination();
        
        if (robot == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Robot No Encontrado");
            alert.setHeaderText("El robot no está colocado");
            alert.setContentText("Por favor, coloca el robot en el plano primero.");
            alert.showAndWait();
            return;
        }
        
        if (destination == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Destino No Encontrado");
            alert.setHeaderText("El destino no está colocado");
            alert.setContentText("Por favor, coloca el destino en el plano primero.");
            alert.showAndWait();
            return;
        }
        
        // Encontrar el nodo más cercano al robot y al destino
        Node robotNode = cartesianCanvas.getClosestNode(robot.x, robot.y);
        Node destNode = cartesianCanvas.getClosestNode(destination.x, destination.y);
        
        if (robotNode == null || destNode == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nodo No Encontrado");
            alert.setHeaderText("No se encontró nodo válido");
            alert.setContentText("No hay nodos disponibles en las ubicaciones especificadas.");
            alert.showAndWait();
            return;
        }
        
        // Ejecutar Dijkstra
        GraphService graphService = new GraphService(cartesianCanvas.getGraph());
        DijkstraResult result = graphService.dijkstra(robotNode.getId(), destNode.getId());
        
        if (!result.hasPath()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin Camino");
            alert.setHeaderText("No existe camino");
            alert.setContentText("No hay camino disponible entre el robot y el destino.");
            alert.showAndWait();
            return;
        }
        
        // Mostrar resultado
        StringBuilder pathInfo = new StringBuilder();
        pathInfo.append("Camino encontrado! Distancia total: ").append(String.format("%.2f", result.totalDistance)).append("\n\nNodos en el camino:\n");
        for (int i = 0; i < result.path.size(); i++) {
            Node node = result.path.get(i);
            pathInfo.append(String.format("%d. %s (%.1f, %.1f)", i + 1, node.getLabel(), node.getX(), node.getY()));
            if (i < result.path.size() - 1) {
                pathInfo.append("\n");
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Camino Más Corto");
        alert.setHeaderText("Resultado de Dijkstra");
        alert.setContentText(pathInfo.toString());
        alert.showAndWait();
        
        // Mostrar el camino en el canvas
        cartesianCanvas.setShortestPath(result.path);
        cartesianCanvas.draw();
    }
    
    /**
     * Crea un segmento lateral entre dos coordenadas específicas.
     */
    @FXML
    private void onAddSegment() {
        if (cartesianCanvas == null) {
            System.err.println("CartesianCanvas no está inicializado");
            return;
        }
        
        Graph graph = cartesianCanvas.getGraph();
        if (graph == null) {
            System.err.println("El grafo no está inicializado");
            return;
        }
        
        double startX = parseDouble(segmentStart1TextField, 0);
        double startY = parseDouble(segmentStart2TextField, 0);
        double endX = parseDouble(segmentEnd1TextField, 5);
        double endY = parseDouble(segmentEnd2TextField, 0);
        double width = parseDouble(segmentWidthTextField, 1.0);
        
        // Validar que sea horizontal o vertical
        if (Math.abs(startX - endX) > 0.01 && Math.abs(startY - endY) > 0.01) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Segmento Inválido");
            alert.setHeaderText("El segmento debe ser lateral");
            alert.setContentText("El segmento debe ser horizontal o vertical, no diagonal.");
            alert.showAndWait();
            return;
        }
        
        // Crear nodos temporales para el segmento
        Node startNode = new Node(999, startX, startY, "Seg_Start");
        Node endNode = new Node(998, endX, endY, "Seg_End");
        
        try {
            cartesianCanvas.addLateralSegment(startNode, endNode, width);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Segmento Creado");
            alert.setHeaderText("Segmento Lateral Creado");
            alert.setContentText(String.format("Segmento creado desde (%.1f, %.1f) a (%.1f, %.1f) con ancho %.2f",
                    startX, startY, endX, endY, width));
            alert.showAndWait();
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al crear segmento");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    
    /**
     * Guarda el plano actual (figuras y segmentos) en un archivo JSON.
     */
    @FXML
    private void onSavePlane() {
        if (cartesianCanvas == null || planeService == null) {
            System.err.println("CartesianCanvas o PlaneService no están inicializados");
            return;
        }
        
        String filename = filenameTextField.getText().trim();
        if (filename.isEmpty()) {
            filename = "mi_plano";
        }
        
        String filepath = filename + ".json";
        
        try {
            PlaneSerializer.savePlane(
                    filepath,
                    planeService,
                    cartesianCanvas.getFigures(),
                    cartesianCanvas.getLateralSegments()
            );
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Plano Guardado");
            alert.setHeaderText("Guardado Exitoso");
            alert.setContentText("El plano ha sido guardado en: " + filepath);
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al Guardar");
            alert.setHeaderText("Error");
            alert.setContentText("Error al guardar el plano: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
    
    /**
     * Carga un plano previamente guardado desde un archivo JSON.
     */
    @FXML
    private void onLoadPlane() {
        if (cartesianCanvas == null || planeService == null) {
            System.err.println("CartesianCanvas o PlaneService no están inicializados");
            return;
        }
        
        String filename = filenameTextField.getText().trim();
        if (filename.isEmpty()) {
            filename = "mi_plano";
        }
        
        String filepath = filename + ".json";
        
        try {
            PlaneSerializer.PlaneState state = PlaneSerializer.loadPlane(filepath);
            
            // Aplicar rango del plano
            planeService.setPlaneRange(state.range[0], state.range[1], state.range[2], state.range[3]);
            
            // Limpiar figuras y segmentos anteriores
            cartesianCanvas.clearFigures();
            cartesianCanvas.clearLateralSegments();
            
            // Recargar figuras
            for (PlaneSerializer.FigureData figData : state.figures) {
                Shape shape = PlaneSerializer.shapeFromId(figData.shape);
                cartesianCanvas.addFigure(shape, figData.x, figData.y, figData.size);
            }
            
            // Recargar segmentos laterales
            for (PlaneSerializer.LateralSegmentData segData : state.segments) {
                Node startNode = new Node(999, segData.startX, segData.startY, "Seg_Start");
                Node endNode = new Node(998, segData.endX, segData.endY, "Seg_End");
                cartesianCanvas.addLateralSegment(startNode, endNode, segData.width);
            }
            
            // Redibujar el canvas
            cartesianCanvas.draw();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Plano Cargado");
            alert.setHeaderText("Carga Exitosa");
            alert.setContentText("El plano ha sido cargado desde: " + filepath);
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al Cargar");
            alert.setHeaderText("Error");
            alert.setContentText("Error al cargar el plano: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}