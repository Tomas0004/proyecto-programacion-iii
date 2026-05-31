package co.edu.uptc;

import co.edu.uptc.model.Graph;
import co.edu.uptc.model.Node;
import co.edu.uptc.service.CartesianPlaneService;
import co.edu.uptc.service.GraphService;
import co.edu.uptc.model.Edge;
import co.edu.uptc.ui.GraphVisualizer;

/**
 * Ejemplos adicionales de uso del proyecto.
 * Esta clase demuestra diferentes formas de usar la arquitectura de N capas.
 */
public class MainExamples {

    /**
     * Ejemplo 1: Crear un grafo dirigido (como un diagrama de flujo)
     */
    public static void example1_DirectedGraph() {
        System.out.println("\n========== EJEMPLO 1: GRAFO DIRIGIDO ==========");
        
        Graph directedGraph = new Graph(true); // true = dirigido

        Node n1 = new Node(1, 1.0, 3.0, "Inicio");
        Node n2 = new Node(2, 3.0, 3.0, "Proceso");
        Node n3 = new Node(3, 5.0, 3.0, "Decisión");
        Node n4 = new Node(4, 7.0, 3.0, "Fin");

        directedGraph.addNode(n1);
        directedGraph.addNode(n2);
        directedGraph.addNode(n3);
        directedGraph.addNode(n4);

        directedGraph.addEdge(new Edge(n1, n2, 1.0, true));
        directedGraph.addEdge(new Edge(n2, n3, 1.0, true));
        directedGraph.addEdge(new Edge(n3, n4, 1.0, true));

        System.out.println(directedGraph);
        
        GraphService service = new GraphService(directedGraph);
        System.out.println("Recorrido desde Inicio: " + service.breadthFirstSearch(1));
    }

    /**
     * Ejemplo 2: Grafo de una red de ciudades
     */
    public static void example2_CityNetwork() {
        System.out.println("\n========== EJEMPLO 2: RED DE CIUDADES ==========");
        
        Graph cityGraph = new Graph(false); // No dirigido

        Node bogota = new Node(1, 2.0, 4.0, "Bogotá");
        Node medellin = new Node(2, 2.0, 2.5, "Medellín");
        Node cali = new Node(3, 1.0, 2.0, "Cali");
        Node barranquilla = new Node(4, 2.5, 0.5, "Barranquilla");
        Node cartagena = new Node(5, 3.5, 0.5, "Cartagena");

        cityGraph.addNode(bogota);
        cityGraph.addNode(medellin);
        cityGraph.addNode(cali);
        cityGraph.addNode(barranquilla);
        cityGraph.addNode(cartagena);

        // Aristas con distancia como peso
        cityGraph.addEdge(new Edge(bogota, medellin, 430)); // km
        cityGraph.addEdge(new Edge(bogota, cali, 520));
        cityGraph.addEdge(new Edge(medellin, barranquilla, 590));
        cityGraph.addEdge(new Edge(cali, cartagena, 600));
        cityGraph.addEdge(new Edge(barranquilla, cartagena, 130));
        cityGraph.addEdge(new Edge(medellin, cali, 520));

        System.out.println(cityGraph);

        GraphService graphService = new GraphService(cityGraph);

        System.out.println("Estadísticas: " + graphService.getGraphStatistics());
        System.out.println("¿Existe ruta de Bogotá a Cartagena? " + 
                           graphService.hasPath(1, 5));
        System.out.println("Vecinos de Bogotá: " + graphService.getNeighbors(1));
    }

    /**
     * Ejemplo 3: Grafo de una red social pequeña
     */
    public static void example3_SocialNetwork() {
        System.out.println("\n========== EJEMPLO 3: RED SOCIAL ==========");
        
        Graph socialGraph = new Graph(false);

        Node alice = new Node(1, 1.0, 3.0, "Alice");
        Node bob = new Node(2, 3.0, 3.5, "Bob");
        Node charlie = new Node(3, 5.0, 3.0, "Charlie");
        Node diana = new Node(4, 2.0, 1.0, "Diana");
        Node eve = new Node(5, 4.0, 1.0, "Eve");

        socialGraph.addNode(alice);
        socialGraph.addNode(bob);
        socialGraph.addNode(charlie);
        socialGraph.addNode(diana);
        socialGraph.addNode(eve);

        socialGraph.addEdge(new Edge(alice, bob));
        socialGraph.addEdge(new Edge(bob, charlie));
        socialGraph.addEdge(new Edge(alice, diana));
        socialGraph.addEdge(new Edge(diana, eve));
        socialGraph.addEdge(new Edge(bob, eve));

        GraphService service = new GraphService(socialGraph);
        
        System.out.println("Red Social:");
        System.out.println(socialGraph);
        
        for (Node node : socialGraph.getNodes()) {
            int degree = service.getNodeDegree(node.getId());
            System.out.println(node.getLabel() + " tiene " + degree + " conexiones");
        }

        System.out.println("\nGrados de separación de Alice:");
        var dfs = service.depthFirstSearch(1);
        System.out.println("Contactos de Alice: " + dfs);
    }

    /**
     * Ejemplo 4: Comparar algoritmos BFS vs DFS
     */
    public static void example4_AlgorithmComparison() {
        System.out.println("\n========== EJEMPLO 4: COMPARACIÓN BFS vs DFS ==========");
        
        Graph graph = new Graph(false);

        for (int i = 1; i <= 7; i++) {
            graph.addNode(new Node(i, i * 1.2, (i % 3) * 1.5));
        }

        // Crear un árbol
        graph.addEdge(new Edge(graph.getNode(1), graph.getNode(2)));
        graph.addEdge(new Edge(graph.getNode(1), graph.getNode(3)));
        graph.addEdge(new Edge(graph.getNode(2), graph.getNode(4)));
        graph.addEdge(new Edge(graph.getNode(2), graph.getNode(5)));
        graph.addEdge(new Edge(graph.getNode(3), graph.getNode(6)));
        graph.addEdge(new Edge(graph.getNode(3), graph.getNode(7)));

        GraphService service = new GraphService(graph);

        System.out.println("Estructura del árbol:");
        System.out.println("       1");
        System.out.println("      / \\");
        System.out.println("     2   3");
        System.out.println("    / \\ / \\");
        System.out.println("   4  5 6  7");
        System.out.println();

        var bfs = service.breadthFirstSearch(1);
        var dfs = service.depthFirstSearch(1);

        System.out.println("BFS (por niveles): " + bfs);
        System.out.println("DFS (profundidad): " + dfs);
    }

    /**
     * Ejemplo 5: Crear un grafo con coordenadas específicas
     */
    public static void example5_PreciseCoordinates() {
        System.out.println("\n========== EJEMPLO 5: COORDENADAS PRECISAS ==========");
        
        Graph graph = new Graph(false);

        // Crear nodos en posiciones específicas
        Node v1 = new Node(1, 0.0, 0.0, "V1");
        Node v2 = new Node(2, 3.0, 4.0, "V2");
        Node v3 = new Node(3, 6.0, 0.0, "V3");

        graph.addNode(v1);
        graph.addNode(v2);
        graph.addNode(v3);

        graph.addEdge(new Edge(v1, v2));
        graph.addEdge(new Edge(v2, v3));
        graph.addEdge(new Edge(v1, v3));

        CartesianPlaneService planeService = new CartesianPlaneService(-1, 7, -1, 5);

        System.out.println("Nodos con coordenadas:");
        for (Node node : graph.getNodes()) {
            System.out.println("  " + node.getLabel() + ": (" + node.getX() + ", " + node.getY() + ")");
            System.out.println("    ¿Dentro del plano? " + planeService.isNodeInPlane(node));
        }

        System.out.println("\nDistancias entre nodos:");
        System.out.println("  V1 a V2: " + String.format("%.2f", planeService.calculateDistance(v1, v2)));
        System.out.println("  V2 a V3: " + String.format("%.2f", planeService.calculateDistance(v2, v3)));
        System.out.println("  V1 a V3: " + String.format("%.2f", planeService.calculateDistance(v1, v3)));

        System.out.println("\nCentroide del triángulo: " + 
                           java.util.Arrays.toString(planeService.calculateCentroid(graph.getNodes())));
    }

    /**
     * Método para visualizar cualquiera de los ejemplos
     */
    public static void visualizeExample(String title, Graph graph, CartesianPlaneService planeService) {
        javafx.stage.Stage stage = new javafx.stage.Stage();
        new GraphVisualizer(stage, title, graph, planeService);
    }

    public static void main(String[] args) {
        // Ejecutar todos los ejemplos
        example1_DirectedGraph();
        example2_CityNetwork();
        example3_SocialNetwork();
        example4_AlgorithmComparison();
        example5_PreciseCoordinates();

        System.out.println("\n========== FIN DE EJEMPLOS ==========");
        System.out.println("Para visualizar gráficamente, ejecuta la clase Main.java");
    }
}
