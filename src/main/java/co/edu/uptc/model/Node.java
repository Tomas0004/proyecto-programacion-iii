package co.edu.uptc.model;

/**
 * Representa un nodo en el grafo con coordenadas en el plano cartesiano.
 */
public class Node {
    private int id;
    private double x;
    private double y;
    private String label;

    public Node(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.label = "N" + id;
    }

    public Node(int id, double x, double y, String label) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label + "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
