package co.edu.uptc.service;

import co.edu.uptc.model.Edge;
import co.edu.uptc.model.Graph;
import co.edu.uptc.model.Node;
import java.util.*;

/**
 * Servicio para manejar operaciones sobre el grafo.
 */
public class GraphService {
    private Graph graph;

    public GraphService(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;
    }

    /**
     * Realiza un recorrido en amplitud (BFS) del grafo.
     */
    public List<Node> breadthFirstSearch(int startNodeId) {
        List<Node> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        Node startNode = graph.getNode(startNodeId);
        if (startNode == null) return result;

        queue.add(startNode);
        visited.add(startNodeId);

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            result.add(node);

            for (Edge edge : graph.getEdges()) {
                Node neighbor = null;
                if (edge.getSource().equals(node)) {
                    neighbor = edge.getDestination();
                } else if (!graph.isDirected() && edge.getDestination().equals(node)) {
                    neighbor = edge.getSource();
                }

                if (neighbor != null && !visited.contains(neighbor.getId())) {
                    visited.add(neighbor.getId());
                    queue.add(neighbor);
                }
            }
        }
        return result;
    }

    /**
     * Realiza un recorrido en profundidad (DFS) del grafo.
     */
    public List<Node> depthFirstSearch(int startNodeId) {
        List<Node> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Node startNode = graph.getNode(startNodeId);
        if (startNode != null) {
            dfsHelper(startNode, visited, result);
        }
        return result;
    }

    private void dfsHelper(Node node, Set<Integer> visited, List<Node> result) {
        visited.add(node.getId());
        result.add(node);

        for (Edge edge : graph.getEdges()) {
            Node neighbor = null;
            if (edge.getSource().equals(node)) {
                neighbor = edge.getDestination();
            } else if (!graph.isDirected() && edge.getDestination().equals(node)) {
                neighbor = edge.getSource();
            }

            if (neighbor != null && !visited.contains(neighbor.getId())) {
                dfsHelper(neighbor, visited, result);
            }
        }
    }

    /**
     * Obtiene el grado de un nodo.
     */
    public int getNodeDegree(int nodeId) {
        int degree = 0;
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().getId() == nodeId) {
                degree++;
            }
            if (!graph.isDirected() && edge.getDestination().getId() == nodeId) {
                degree++;
            }
        }
        return degree;
    }

    /**
     * Verifica si existe un camino entre dos nodos.
     */
    public boolean hasPath(int startNodeId, int endNodeId) {
        List<Node> visited = breadthFirstSearch(startNodeId);
        return visited.stream().anyMatch(node -> node.getId() == endNodeId);
    }

    /**
     * Obtiene los vecinos de un nodo.
     */
    public List<Node> getNeighbors(int nodeId) {
        List<Node> neighbors = new ArrayList<>();
        Node node = graph.getNode(nodeId);
        if (node != null) {
            for (Edge edge : graph.getEdges()) {
                if (edge.getSource().equals(node)) {
                    neighbors.add(edge.getDestination());
                } else if (!graph.isDirected() && edge.getDestination().equals(node)) {
                    neighbors.add(edge.getSource());
                }
            }
        }
        return neighbors;
    }

    /**
     * Obtiene información estadística del grafo.
     */
    public Map<String, Object> getGraphStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("nodos", graph.getNodeCount());
        stats.put("aristas", graph.getEdgeCount());
        stats.put("dirigido", graph.isDirected());
        
        double[] grados = graph.getNodes().stream()
                .mapToDouble(node -> getNodeDegree(node.getId()))
                .toArray();
        
        if (grados.length > 0) {
            Arrays.sort(grados);
            stats.put("grado_min", (int) grados[0]);
            stats.put("grado_max", (int) grados[grados.length - 1]);
            stats.put("grado_promedio", Arrays.stream(grados).average().orElse(0));
        }
        
        return stats;
    }

    /**
     * Encuentra el camino más corto entre dos nodos usando el algoritmo de Dijkstra.
     */
    public DijkstraResult dijkstra(int startNodeId, int endNodeId) {
        Node startNode = graph.getNode(startNodeId);
        Node endNode = graph.getNode(endNodeId);
        
        if (startNode == null || endNode == null) {
            return new DijkstraResult(new ArrayList<>(), Double.POSITIVE_INFINITY);
        }
        
        // Inicializar distancias
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();
        PriorityQueue<Integer> queue = new PriorityQueue<>(
            (a, b) -> Double.compare(distances.getOrDefault(a, Double.POSITIVE_INFINITY),
                                     distances.getOrDefault(b, Double.POSITIVE_INFINITY))
        );
        
        // Inicializar todas las distancias a infinito y el nodo inicial a 0
        for (Node node : graph.getNodes()) {
            distances.put(node.getId(), Double.POSITIVE_INFINITY);
            previous.put(node.getId(), -1);
        }
        distances.put(startNodeId, 0.0);
        queue.add(startNodeId);
        
        // Algoritmo de Dijkstra
        Set<Integer> visited = new HashSet<>();
        
        while (!queue.isEmpty()) {
            int currentId = queue.poll();
            
            if (visited.contains(currentId)) continue;
            visited.add(currentId);
            
            Node currentNode = graph.getNode(currentId);
            double currentDistance = distances.get(currentId);
            
            // Si alcanzamos el nodo destino, podemos terminar
            if (currentId == endNodeId) break;
            
            // Explorar vecinos
            for (Edge edge : graph.getEdges()) {
                Node neighbor = null;
                
                if (edge.getSource().equals(currentNode)) {
                    neighbor = edge.getDestination();
                } else if (!graph.isDirected() && edge.getDestination().equals(currentNode)) {
                    neighbor = edge.getSource();
                }
                
                if (neighbor != null && !visited.contains(neighbor.getId())) {
                    double newDistance = currentDistance + edge.getWeight();
                    
                    if (newDistance < distances.get(neighbor.getId())) {
                        distances.put(neighbor.getId(), newDistance);
                        previous.put(neighbor.getId(), currentId);
                        queue.add(neighbor.getId());
                    }
                }
            }
        }
        
        // Reconstruir el camino
        List<Node> path = new ArrayList<>();
        int current = endNodeId;
        
        if (distances.get(endNodeId) != Double.POSITIVE_INFINITY) {
            while (current != -1) {
                path.add(0, graph.getNode(current));
                current = previous.get(current);
            }
        }
        
        return new DijkstraResult(path, distances.get(endNodeId));
    }
    
    /**
     * Clase interna para almacenar el resultado de Dijkstra.
     */
    public static class DijkstraResult {
        public List<Node> path;
        public double totalDistance;
        
        public DijkstraResult(List<Node> path, double totalDistance) {
            this.path = path;
            this.totalDistance = totalDistance;
        }
        
        public boolean hasPath() {
            return totalDistance != Double.POSITIVE_INFINITY;
        }
    }

    @Override
    public String toString() {
        return graph.toString();
    }
}
