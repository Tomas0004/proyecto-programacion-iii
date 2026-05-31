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

    @Override
    public String toString() {
        return graph.toString();
    }
}
