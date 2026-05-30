package co.edu.uptc.model;

/**
 * Clase que representa la intersección de una línea de cuadrícula en el plano cartesiano.
 * Cada intersección se comporta como un nodo del grafo.
 */
public class PlaneIntersection {
    private double x;
    private double y;
    private int gridX;  // Coordenada de cuadrícula
    private int gridY;  // Coordenada de cuadrícula
    private Node associatedNode;

    public PlaneIntersection(double x, double y, int gridX, int gridY, Node associatedNode) {
        this.x = x;
        this.y = y;
        this.gridX = gridX;
        this.gridY = gridY;
        this.associatedNode = associatedNode;
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

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public Node getAssociatedNode() {
        return associatedNode;
    }

    public void setAssociatedNode(Node node) {
        this.associatedNode = node;
    }

    @Override
    public String toString() {
        return String.format("Intersección(%d, %d) -> (%.2f, %.2f)", gridX, gridY, x, y);
    }
}
