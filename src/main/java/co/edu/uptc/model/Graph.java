package co.edu.uptc.model;

import java.util.*;

/**
 * Representa un grafo que contiene nodos y aristas.
 */
public class Graph {
    private Map<Integer, Node> nodes;
    private List<Edge> edges;
    private boolean directed;

    public Graph() {
        this(false);
    }

    public Graph(boolean directed) {
        this.nodes = new HashMap<>();
        this.edges = new ArrayList<>();
        this.directed = directed;
    }

    /**
     * Añade un nodo al grafo.
     */
    public void addNode(Node node) {
        nodes.put(node.getId(), node);
    }

    /**
     * Elimina un nodo del grafo.
     */
    public void removeNode(int nodeId) {
        Node node = nodes.remove(nodeId);
        if (node != null) {
            edges.removeIf(edge -> edge.getSource().equals(node) || edge.getDestination().equals(node));
        }
    }

    /**
     * Obtiene un nodo por su ID.
     */
    public Node getNode(int nodeId) {
        return nodes.get(nodeId);
    }

    /**
     * Añade una arista al grafo.
     */
    public void addEdge(Edge edge) {
        if (nodes.containsKey(edge.getSource().getId()) && nodes.containsKey(edge.getDestination().getId())) {
            edges.add(edge);
        }
    }

    /**
     * Elimina una arista del grafo.
     */
    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

    /**
     * Obtiene todas las aristas de un nodo.
     */
    public List<Edge> getEdgesFromNode(int nodeId) {
        List<Edge> result = new ArrayList<>();
        Node node = nodes.get(nodeId);
        if (node != null) {
            for (Edge edge : edges) {
                if (edge.getSource().equals(node) || (!directed && edge.getDestination().equals(node))) {
                    result.add(edge);
                }
            }
        }
        return result;
    }

    /**
     * Obtiene todos los nodos.
     */
    public Collection<Node> getNodes() {
        return nodes.values();
    }

    /**
     * Obtiene todas las aristas.
     */
    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    /**
     * Obtiene el número de nodos.
     */
    public int getNodeCount() {
        return nodes.size();
    }

    /**
     * Obtiene el número de aristas.
     */
    public int getEdgeCount() {
        return edges.size();
    }

    /**
     * Verifica si el grafo está dirigido.
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     * Limpia el grafo eliminando todos los nodos y aristas.
     */
    public void clear() {
        nodes.clear();
        edges.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grafo ").append(directed ? "Dirigido" : "No Dirigido").append("\n");
        sb.append("Nodos: ").append(nodes.values()).append("\n");
        sb.append("Aristas: ").append(edges).append("\n");
        return sb.toString();
    }
}
