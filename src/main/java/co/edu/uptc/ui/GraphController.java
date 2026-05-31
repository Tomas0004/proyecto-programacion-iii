package co.edu.uptc.ui;

import co.edu.uptc.model.Graph;
import co.edu.uptc.service.CartesianPlaneService;
import co.edu.uptc.service.GraphService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Controlador FXML para la vista del grafo.
 */
public class GraphController {
    @FXML
    private Pane canvasContainer;
    
    @FXML
    private Label infoLabel;

    private Graph graph;
    private GraphService graphService;
    private CartesianPlane2D cartesianCanvas;

    @FXML
    public void initialize() {
        // Este método se llama después de que FXML carga el archivo
    }

    public void initializeGraph(Graph graph, CartesianPlaneService planeService) {
        this.graph = graph;
        this.graphService = new GraphService(graph);

        // Crear canvas
        cartesianCanvas = new CartesianPlane2D(graph, planeService);
        canvasContainer.getChildren().add(cartesianCanvas);

        // Actualizar información
        updateInfo();
    }

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
            cartesianCanvas.draw();
        }
    }

    @FXML
    private void onShowDetails() {
        if (graphService != null) {
            StringBuilder info = new StringBuilder();
            info.append("=== DETALLES DEL GRAFO ===\n");
            info.append("Nodos: ").append(graph.getNodeCount()).append("\n");
            info.append("Aristas: ").append(graph.getEdgeCount()).append("\n");
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
}
