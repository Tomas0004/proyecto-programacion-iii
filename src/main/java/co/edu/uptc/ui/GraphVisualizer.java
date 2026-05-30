package co.edu.uptc.ui;

import co.edu.uptc.model.Graph;
import co.edu.uptc.logic.CartesianPlaneService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Ventana principal para visualizar el grafo en el plano cartesiano usando JavaFX.
 * Utiliza arquitectura FXML con controladores.
 */
public class GraphVisualizer {
    private Stage stage;
    private Graph graph;
    private CartesianPlaneService planeService;
    private MainGraphController controller;

    public GraphVisualizer(Stage stage, String title, Graph graph, CartesianPlaneService planeService) {
        this.stage = stage;
        this.graph = graph;
        this.planeService = planeService;

        initializeUI(title);
    }

    /**
     * Inicializa la interfaz de usuario desde el archivo FXML.
     */
    private void initializeUI(String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-graph-view.fxml"));
            Pane root = loader.load();
            
            controller = loader.getController();
            controller.initializeGraph(graph, planeService);
            
            // Crear la escena
            Scene scene = new Scene(root, 1100, 700);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            // Configurar la ventana
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la interfaz gráfica: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public CartesianPlane2D getCartesianPlane() {
        return controller.getCartesianPlane();
    }

    public Graph getGraph() {
        return graph;
    }

    public Stage getStage() {
        return stage;
    }
}

