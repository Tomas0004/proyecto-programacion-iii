package co.edu.uptc.ui;

import co.edu.uptc.model.Graph;
import co.edu.uptc.service.CartesianPlaneService;
import co.edu.uptc.service.GraphService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Controlador principal para la ventana de visualización del grafo.
 * Integra el canvas, la información del grafo y los controles.
 */
public class MainGraphController {
    @FXML
    private Pane canvasContainer;
    
    @FXML
    private Label infoLabel;
    
    @FXML
    private VBox rightPanel;

    private Graph graph;
    private CartesianPlaneService planeService;
    private GraphService graphService;
    private CartesianPlane2D cartesianCanvas;
    private ControlsPanelController controlsPanelController;

    @FXML
    public void initialize() {
        // Este método se llama después de que FXML carga el archivo
    }

    /**
     * Inicializa el grafo y los componentes de la interfaz.
     */
    public void initializeGraph(Graph graph, CartesianPlaneService planeService) {
        this.graph = graph;
        this.planeService = planeService;
        this.graphService = new GraphService(graph);

        // Crear canvas del plano cartesiano
        cartesianCanvas = new CartesianPlane2D(graph, planeService);
        cartesianCanvas.setWidth(1400);
        cartesianCanvas.setHeight(1000);
        canvasContainer.getChildren().add(cartesianCanvas);

        // Cargar panel de controles desde FXML
        loadControlsPanel();

        // Actualizar información
        updateInfo();
    }

    /**
     * Carga el panel de controles desde el archivo FXML.
     */
    private void loadControlsPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controls-panel.fxml"));
            VBox controlsPanel = loader.load();
            controlsPanelController = loader.getController();
            
            // Establecer referencias en el controlador de controles
            controlsPanelController.setCartesianCanvas(cartesianCanvas);
            controlsPanelController.setPlaneService(planeService);
            
            // Agregar el panel a la derecha
            rightPanel.getChildren().add(controlsPanel);
        } catch (IOException e) {
            System.err.println("Error al cargar el panel de controles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la información del grafo mostrada en el panel inferior.
     */
    private void updateInfo() {
        var stats = graphService.getGraphStatistics();
        String infoText = String.format(
                "Nodos: %d  |  Aristas: %d  |  Dirigido: %s  |  Grado Promedio: %.2f",
                stats.get("nodos"),
                stats.get("aristas"),
                stats.get("dirigido"),
                stats.get("grado_promedio")
        );
        infoLabel.setText(infoText);
    }

    @FXML
    private void onRefresh() {
        if (cartesianCanvas != null) {
            updateInfo();
            cartesianCanvas.draw();
        }
    }

    @FXML
    private void onShowDetails() {
        if (graphService != null) {
            StringBuilder info = new StringBuilder();
            info.append("=== DETALLES DEL GRAFO ===\n");
            info.append("Nodos: ").append(graph.getNodeCount()).append("\n");
            info.append("Aristas: ").append(graph.getEdgeCount()).append("\n\n");
            
            info.append("NODOS:\n");
            for (var node : graph.getNodes()) {
                info.append(String.format("  %s: (%.2f, %.2f)\n", node.getLabel(), node.getX(), node.getY()));
            }
            
            info.append("\nARISTAS:\n");
            for (var edge : graph.getEdges()) {
                String arrow = edge.isDirected() ? " → " : " -- ";
                info.append(String.format("  %s%s%s (peso: %.1f)\n",
                        edge.getSource().getLabel(),
                        arrow,
                        edge.getDestination().getLabel(),
                        edge.getWeight()
                ));
            }
            
            System.out.println(info.toString());
        }
    }

    @FXML
    private void onBFS() {
        if (graphService != null && graph.getNodeCount() > 0) {
            var result = graphService.breadthFirstSearch(1);
            System.out.println("BFS: " + result);
        }
    }

    @FXML
    private void onDFS() {
        if (graphService != null && graph.getNodeCount() > 0) {
            var result = graphService.depthFirstSearch(1);
            System.out.println("DFS: " + result);
        }
    }

    public CartesianPlane2D getCartesianPlane() {
        return cartesianCanvas;
    }

    public Graph getGraph() {
        return graph;
    }

    public GraphService getGraphService() {
        return graphService;
    }
}
