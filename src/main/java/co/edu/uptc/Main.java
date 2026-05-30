package co.edu.uptc;

import co.edu.uptc.model.Graph;
import co.edu.uptc.model.Node;
import co.edu.uptc.model.Edge;
import co.edu.uptc.logic.CartesianPlaneService;
import co.edu.uptc.logic.GraphService;
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
        // Crear un grafo no dirigido
        Graph graph = new Graph(false);

        // Crear nodos con posiciones en el plano cartesiano
        Node node1 = new Node(1, 2.0, 3.0, "A");
        Node node2 = new Node(2, 5.0, 4.0, "B");
        Node node3 = new Node(3, 8.0, 2.0, "C");
        Node node4 = new Node(4, 3.0, 1.0, "D");
        Node node5 = new Node(5, 6.0, 0.5, "E");

        // Agregar nodos al grafo
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        graph.addNode(node5);

        // Crear aristas entre los nodos
        graph.addEdge(new Edge(node1, node2, 2.5));
        graph.addEdge(new Edge(node1, node4, 1.8));
        graph.addEdge(new Edge(node2, node3, 3.2));
        graph.addEdge(new Edge(node2, node5, 2.0));
        graph.addEdge(new Edge(node3, node5, 1.5));
        graph.addEdge(new Edge(node4, node5, 2.8));

        // Crear el servicio del plano cartesiano
        CartesianPlaneService planeService = new CartesianPlaneService(0, 10, 0, 5);

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
        double distance = planeService.calculateDistance(node1, node2);
        System.out.println("Distancia entre A y B: " + String.format("%.2f", distance));
        System.out.println();

        // Visualizar el grafo
        System.out.println("Abriendo visualización del grafo...");
        new GraphVisualizer(primaryStage, "Visualizador de Grafo - Plano Cartesiano 2D", graph, planeService);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
