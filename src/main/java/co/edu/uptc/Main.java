package co.edu.uptc;

import co.edu.uptc.model.Graph;
import co.edu.uptc.service.CartesianPlaneService;
import co.edu.uptc.service.GraphService;
import co.edu.uptc.ui.GraphVisualizer;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Clase principal que demuestra la arquitectura de N capas con un grafo en un plano cartesiano 2D.
 * Utiliza JavaFX para la interfaz gráfica.
 * 
 * Arquitectura de capas:
 * - Capa de Modelo (Model): Node, Edge, Graph
 * - Capa de Lógica de Negocios (Logic): GraphService, CartesianPlaneService
 * - Capa de Presentación (UI): CartesianPlane2D, GraphVisualizer
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crear un grafo no dirigido vacío
        Graph graph = new Graph(false);

        // Crear el servicio del plano cartesiano
        CartesianPlaneService planeService = new CartesianPlaneService(0, 20, 0, 20);

        // Crear el servicio de lógica del grafo
        GraphService graphService = new GraphService(graph);

        // Mostrar información en consola
        System.out.println("========== INFORMACIÓN DEL GRAFO ==========");
        System.out.println(graph);
        System.out.println();
        System.out.println("========== ESTADÍSTICAS ==========");
        var stats = graphService.getGraphStatistics();
        System.out.println("Nodos: " + stats.get("nodos"));
        System.out.println("Aristas: " + stats.get("aristas"));
        System.out.println("Grado promedio: " + stats.get("grado_promedio"));
        System.out.println();

        // Demostración de algoritmos
        System.out.println("========== RECORRIDO BFS ==========");
        var bfsResult = graphService.breadthFirstSearch(1);
        System.out.println("BFS desde nodo 1: " + bfsResult);
        System.out.println();

        System.out.println("========== RECORRIDO DFS ==========");
        var dfsResult = graphService.depthFirstSearch(1);
        System.out.println("DFS desde nodo 1: " + dfsResult);
        System.out.println();

        // Demostración de servicios del plano cartesiano
        System.out.println("========== PLANO CARTESIANO ==========");
        System.out.println(planeService);
        // Nota: Para demostrar distancia, es necesario agregar nodos primero
        System.out.println();

        // Visualizar el grafo
        System.out.println("Abriendo visualización del grafo...");
        new GraphVisualizer(primaryStage, "Visualizador de Grafo - Plano Cartesiano 2D", graph, planeService);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
