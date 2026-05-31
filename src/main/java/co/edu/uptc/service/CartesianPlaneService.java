package co.edu.uptc.service;

import co.edu.uptc.model.Node;
import java.util.Collection;

/**
 * Servicio para manejar operaciones del plano cartesiano.
 */
public class CartesianPlaneService {
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    public CartesianPlaneService(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    /**
     * Calcula la distancia euclidiana entre dos nodos.
     */
    public double calculateDistance(Node node1, Node node2) {
        double dx = node2.getX() - node1.getX();
        double dy = node2.getY() - node1.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Verifica si un punto está dentro del plano cartesiano.
     */
    public boolean isPointInPlane(double x, double y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    /**
     * Verifica si un nodo está dentro del plano cartesiano.
     */
    public boolean isNodeInPlane(Node node) {
        return isPointInPlane(node.getX(), node.getY());
    }

    /**
     * Obtiene los nodos que están dentro del plano.
     */
    public void filterNodesInPlane(Collection<Node> nodes) {
        nodes.removeIf(node -> !isNodeInPlane(node));
    }

    /**
     * Calcula el centroide de un conjunto de nodos.
     */
    public double[] calculateCentroid(Collection<Node> nodes) {
        if (nodes.isEmpty()) {
            return new double[]{0, 0};
        }
        double sumX = 0, sumY = 0;
        for (Node node : nodes) {
            sumX += node.getX();
            sumY += node.getY();
        }
        return new double[]{sumX / nodes.size(), sumY / nodes.size()};
    }

    /**
     * Obtiene el rango del plano cartesiano.
     */
    public double[] getPlaneRange() {
        return new double[]{minX, maxX, minY, maxY};
    }

    /**
     * Establece el rango del plano cartesiano.
     */
    public void setPlaneRange(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    @Override
    public String toString() {
        return "Plano Cartesiano [" + minX + ", " + maxX + "] x [" + minY + ", " + maxY + "]";
    }
}
