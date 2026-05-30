package co.edu.uptc.model;

/**
 * Representa una arista que conecta dos nodos en el grafo.
 */
public class Edge {
    private Node source;
    private Node destination;
    private double weight;
    private boolean directed;

    public Edge(Node source, Node destination) {
        this(source, destination, 1.0, false);
    }

    public Edge(Node source, Node destination, double weight) {
        this(source, destination, weight, false);
    }

    public Edge(Node source, Node destination, double weight, boolean directed) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.directed = directed;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getDestination() {
        return destination;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    @Override
    public String toString() {
        String arrow = directed ? " -> " : " -- ";
        return source.getLabel() + arrow + destination.getLabel() + " (w:" + weight + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Edge edge = (Edge) obj;
        return source.equals(edge.source) && destination.equals(edge.destination);
    }

    @Override
    public int hashCode() {
        return source.hashCode() * 31 + destination.hashCode();
    }
}
