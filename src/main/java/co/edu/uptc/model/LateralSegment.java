package co.edu.uptc.model;

/**
 * Representa un segmento lateral (horizontal o vertical) que conecta dos nodos.
 * Los nodos bajo este segmento NO forman parte del grafo.
 */
public class LateralSegment {
    private Node startNode;
    private Node endNode;
    private SegmentType type; // HORIZONTAL o VERTICAL
    private double width; // Ancho del segmento visual
    
    public enum SegmentType {
        HORIZONTAL,
        VERTICAL
    }
    
    public LateralSegment(Node startNode, Node endNode, double width) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.width = width;
        this.type = determineSegmentType();
    }
    
    /**
     * Determina si el segmento es horizontal o vertical basándose en las coordenadas.
     */
    private SegmentType determineSegmentType() {
        if (Math.abs(startNode.getX() - endNode.getX()) < 0.01) {
            return SegmentType.VERTICAL;
        } else if (Math.abs(startNode.getY() - endNode.getY()) < 0.01) {
            return SegmentType.HORIZONTAL;
        } else {
            throw new IllegalArgumentException("El segmento debe ser horizontal o vertical");
        }
    }
    
    public Node getStartNode() {
        return startNode;
    }
    
    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }
    
    public Node getEndNode() {
        return endNode;
    }
    
    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }
    
    public SegmentType getType() {
        return type;
    }
    
    public double getWidth() {
        return width;
    }
    
    public void setWidth(double width) {
        this.width = width;
    }
    
    /**
     * Verifica si un punto está dentro del segmento (con su ancho).
     */
    public boolean containsPoint(double x, double y) {

        if (type == SegmentType.HORIZONTAL) {
            // Verificar si el punto está en la banda horizontal
            if (Math.abs(y - startNode.getY()) != 0) {
                return false;
            }
            
            // Verificar si el punto está entre los nodos horizontalmente
            double minX = Math.min(startNode.getX(), endNode.getX());
            double maxX = Math.max(startNode.getX(), endNode.getX());
            return x >= minX && x <= maxX;
        } else {
            // VERTICAL: Verificar si el punto está en la banda vertical
            if (Math.abs(x - startNode.getX()) != 0) {
                return false;
            }
            
            // Verificar si el punto está entre los nodos verticalmente
            double minY = Math.min(startNode.getY(), endNode.getY());
            double maxY = Math.max(startNode.getY(), endNode.getY());
            return y >= minY && y <= maxY;
        }
    }
    
    @Override
    public String toString() {
        return String.format("LateralSegment[%s] from %s to %s (width: %.2f)",
                type, startNode.getLabel(), endNode.getLabel(), width);
    }
}
