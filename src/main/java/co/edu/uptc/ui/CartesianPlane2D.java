package co.edu.uptc.ui;

import co.edu.uptc.model.Node;
import co.edu.uptc.model.Edge;
import co.edu.uptc.model.Graph;
import co.edu.uptc.model.Shape;
import co.edu.uptc.service.CartesianPlaneService;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Canvas que visualiza un plano cartesiano 2D con el grafo usando JavaFX.
 * Soporta múltiples figuras y una cuadrícula donde cada intersección es un nodo.
 */
public class CartesianPlane2D extends Canvas {
    private Graph graph;
    private CartesianPlaneService planeService;
    private List<Figure> figures;
    private Map<String, Node> gridIntersections;  // Mapa de posiciones de cuadrícula a nodos
    private static final int PADDING = 60;
    private static final int NODE_RADIUS = 15;
    private boolean showGrid = true;
    private int gridSpacing = 1;

    // Variables para manejo del ratón
    private Figure selectedFigure;
    private double mouseOffsetX;
    private double mouseOffsetY;

    /**
     * Clase interna para representar una figura genérica en el plano cartesiano.
     */
    public static class Figure {
        public double x;
        public double y;
        public double size;
        public Shape shape;

        public Figure(Shape shape, double x, double y, double size) {
            this.shape = shape;
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }

    public CartesianPlane2D(Graph graph, CartesianPlaneService planeService) {
        super(1400, 1000);
        this.graph = graph;
        this.planeService = planeService;
        this.figures = new ArrayList<>();
        this.gridIntersections = new HashMap<>();
        this.selectedFigure = null;
        
        // Inicializar nodos en las intersecciones de la cuadrícula
        initializeGridIntersections();
        
        // Agregar event handlers para el ratón
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);
        
        draw();
    }

    /**
     * Inicializa los nodos en las intersecciones de la cuadrícula.
     */
    private void initializeGridIntersections() {

        double[] range = planeService.getPlaneRange();
        double minX = range[0];
        double maxX = range[1];
        double minY = range[2];
        double maxY = range[3];

        int nodeId = graph.getNodeCount() + 1;
        for (int gridX = (int) minX; gridX <= (int) maxX; gridX += gridSpacing) {
            for (int gridY = (int) minY; gridY <= (int) maxY; gridY += gridSpacing) {
                String key = gridX + "," + gridY;

                if (!gridIntersections.containsKey(key)) {
                    Node node = new Node(nodeId, gridX, gridY);
                    gridIntersections.put(key, node);
                    graph.addNode(node);
                    nodeId++;
                }
            }
        }
    }

    /**
     * Dibuja el plano cartesiano y el grafo.
     */
    public void draw() {
        initializeGridIntersections();
        GraphicsContext gc = getGraphicsContext2D();
        
        // Limpiar canvas con color de fondo oscuro
        gc.setFill(Color.web("#1E1E1E"));
        gc.fillRect(0, 0, getWidth(), getHeight());
        
        if (showGrid) {
            drawGrid(gc);
        }
        drawPlane(gc);
        drawFigures(gc);
        drawEdges(gc);
        drawNodes(gc);
    }

    /**
     * Dibuja la cuadrícula del plano.
     */
    private void drawGrid(GraphicsContext gc) {
        double[] range = planeService.getPlaneRange();
        double minX = range[0];
        double maxX = range[1];
        double minY = range[2];
        double maxY = range[3];

        int width = (int) getWidth();
        int height = (int) getHeight();

        gc.setStroke(Color.web("#333333"));
        gc.setLineWidth(0.5);

        // Líneas verticales de la cuadrícula

        for (int x = (int) minX; x <= (int) maxX; x += gridSpacing) {
            int px = screenX(x, minX, maxX, width);

            gc.strokeLine(px, PADDING, px, height - PADDING);
        }

        // Líneas horizontales de la cuadrícula
        for (int y = (int) minY; y <= (int) maxY; y += gridSpacing) {
            int py = screenY(y, minY, maxY, height);
            gc.strokeLine(PADDING, py, width - PADDING, py);
        }

        // Dibujar puntos de intersección
        gc.setFill(Color.web("#555555"));
        for (int x = (int) minX; x <= (int) maxX; x += gridSpacing) {
            for (int y = (int) minY; y <= (int) maxY; y += gridSpacing) {
                int px = screenX(x, minX, maxX, width);
                int py = screenY(y, minY, maxY, height);
                gc.fillOval(px - 2, py - 2, 4, 4);
            }
        }
    }

    /**
     * Dibuja el plano cartesiano con los ejes.
     */
    private void drawPlane(GraphicsContext gc) {
        double[] range = planeService.getPlaneRange();
        double minX = range[0];
        double maxX = range[1];
        double minY = range[2];
        double maxY = range[3];

        int width = (int) getWidth();
        int height = (int) getHeight();

        // Dibujar ejes
        gc.setStroke(Color.web("#FFFFFF"));
        gc.setLineWidth(2);

        int originX = screenX(0, minX, maxX, width);
        int originY = screenY(0, minY, maxY, height);

        gc.strokeLine(PADDING, originY, width - PADDING, originY); // Eje X
        gc.strokeLine(originX, PADDING, originX, height - PADDING); // Eje Y

        // Dibujar etiquetas de ejes
        gc.setFont(new Font("Arial", 14));
        gc.setFill(Color.web("#FFFFFF"));
        gc.fillText("X", width - PADDING - 20, originY + 20);
        gc.fillText("Y", originX + 10, PADDING - 10);

        // Dibujar marcas de escala
        gc.setFont(new Font("Arial", 10));
        gc.setFill(Color.web("#CCCCCC"));
        for (double x = minX; x <= maxX; x += (maxX - minX) / 10) {
            int px = screenX(x, minX, maxX, width);
            gc.strokeLine(px, originY - 5, px, originY + 5);
            if (x != 0) {
                gc.fillText(String.format("%.1f", x), px - 10, originY + 20);
            }
        }

        for (double y = minY; y <= maxY; y += (maxY - minY) / 10) {
            int py = screenY(y, minY, maxY, height);
            gc.strokeLine(originX - 5, py, originX + 5, py);
            if (y != 0) {
                gc.fillText(String.format("%.1f", y), originX - 35, py + 5);
            }
        }
    }

    /**
     * Dibuja las figuras en el plano cartesiano.
     */
    private void drawFigures(GraphicsContext gc) {
        if (figures.isEmpty()) return;

        double[] range = planeService.getPlaneRange();
        int width = (int) getWidth();
        int height = (int) getHeight();

        for (Figure figure : figures) {
            int x = screenX(figure.x, range[0], range[1], width);
            int y = screenY(figure.y, range[2], range[3], height);

            // Convertir el tamaño a píxeles
            double sizePixels = (figure.size / (range[1] - range[0])) * (width - 2 * PADDING);

            drawShape(gc, figure.shape, x, y, sizePixels);
        }
    }

    /**
     * Dibuja una forma específica en las coordenadas dadas.
     */
    private void drawShape(GraphicsContext gc, Shape shape, int x, int y, double size) {
        gc.setFill(Color.web("#FF6B6B"));
        gc.setStroke(Color.web("#FF0000"));
        gc.setLineWidth(2);

        switch (shape) {
            case SQUARE:
                gc.fillRect(x - size / 2, y - size / 2, size, size);
                gc.strokeRect(x - size / 2, y - size / 2, size, size);
                break;
            case CIRCLE:
                gc.fillOval(x - size / 2, y - size / 2, size, size);
                gc.strokeOval(x - size / 2, y - size / 2, size, size);
                break;
            case RECTANGLE:
                double width = size * 1.5;
                double height = size * 0.75;
                gc.fillRect(x - width / 2, y - height / 2, width, height);
                gc.strokeRect(x - width / 2, y - height / 2, width, height);
                break;
            case TRIANGLE:
                drawTriangle(gc, x, y, size);
                break;
            case PENTAGON:
                drawPolygon(gc, x, y, size, 5);
                break;
            case HEXAGON:
                drawPolygon(gc, x, y, size, 6);
                break;
            case DIAMOND:
                drawDiamond(gc, x, y, size);
                break;
            case STAR:
                drawStar(gc, x, y, size);
                break;
        }
    }

    /**
     * Dibuja un triángulo.
     */
    private void drawTriangle(GraphicsContext gc, int x, int y, double size) {
        double[] xs = {x, x - size / 2, x + size / 2};
        double[] ys = {y - size / 2, y + size / 2, y + size / 2};
        gc.fillPolygon(xs, ys, 3);
        gc.strokePolygon(xs, ys, 3);
    }

    /**
     * Dibuja un polígono regular.
     */
    private void drawPolygon(GraphicsContext gc, int x, int y, double size, int sides) {
        double[] xs = new double[sides];
        double[] ys = new double[sides];
        
        for (int i = 0; i < sides; i++) {
            double angle = 2 * Math.PI * i / sides - Math.PI / 2;
            xs[i] = x + size / 2 * Math.cos(angle);
            ys[i] = y + size / 2 * Math.sin(angle);
        }
        
        gc.fillPolygon(xs, ys, sides);
        gc.strokePolygon(xs, ys, sides);
    }

    /**
     * Dibuja un diamante.
     */
    private void drawDiamond(GraphicsContext gc, int x, int y, double size) {
        double[] xs = {x, x + size / 2, x, x - size / 2};
        double[] ys = {y - size / 2, y, y + size / 2, y};
        gc.fillPolygon(xs, ys, 4);
        gc.strokePolygon(xs, ys, 4);
    }

    /**
     * Dibuja una estrella.
     */
    private void drawStar(GraphicsContext gc, int x, int y, double size) {
        double[] xs = new double[10];
        double[] ys = new double[10];
        
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 2 + 2 * Math.PI * i / 10;
            double radius = (i % 2 == 0) ? size / 2 : size / 4;
            xs[i] = x + radius * Math.cos(angle);
            ys[i] = y - radius * Math.sin(angle);
        }
        
        gc.fillPolygon(xs, ys, 10);
        gc.strokePolygon(xs, ys, 10);
    }

    /**
     * Agrega una figura al lienzo.
     */
    public void addFigure(Shape shape, double x, double y, double size) {
        figures.add(new Figure(shape, x, y, size));
    }

    /**
     * Limpia todas las figuras del lienzo.
     */
    public void clearFigures() {
        figures.clear();
    }

    /**
     * Alterna la visibilidad de la cuadrícula.
     */
    public void toggleGridVisibility(int spacing) {
        showGrid = !showGrid;
        this.gridSpacing = spacing;
    }

    /**
     * Dibuja las aristas del grafo.
     */
    private void drawEdges(GraphicsContext gc) {
        if (graph == null) return;

        double[] range = planeService.getPlaneRange();
        int width = (int) getWidth();
        int height = (int) getHeight();

        gc.setStroke(Color.web("#A0A0A0"));
        gc.setLineWidth(1.5);

        for (Edge edge : graph.getEdges()) {
            Node source = edge.getSource();
            Node dest = edge.getDestination();

            int x1 = screenX(source.getX(), range[0], range[1], width);
            int y1 = screenY(source.getY(), range[2], range[3], height);
            int x2 = screenX(dest.getX(), range[0], range[1], width);
            int y2 = screenY(dest.getY(), range[2], range[3], height);

            gc.strokeLine(x1, y1, x2, y2);

            // Dibujar peso de la arista
            if (edge.getWeight() != 1.0) {
                int midX = (x1 + x2) / 2;
                int midY = (y1 + y2) / 2;
                gc.setFill(Color.web("#64B5F6"));
                gc.setFont(new Font("Arial", 10));
                gc.fillText(String.format("%.1f", edge.getWeight()), midX, midY - 5);
            }

            // Dibujar punta de flecha si es dirigido
            if (edge.isDirected()) {
                drawArrow(gc, x1, y1, x2, y2);
            }
        }
    }

    /**
     * Dibuja una punta de flecha para aristas dirigidas.
     */
    private void drawArrow(GraphicsContext gc, int x1, int y1, int x2, int y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 10;

        int x3 = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
        int y3 = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));
        int x4 = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
        int y4 = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));

        gc.strokeLine(x2, y2, x3, y3);
        gc.strokeLine(x2, y2, x4, y4);
    }

    /**
     * Dibuja los nodos del grafo.
     */
    private void drawNodes(GraphicsContext gc) {
        if (graph == null) return;

        double[] range = planeService.getPlaneRange();
        int width = (int) getWidth();
        int height = (int) getHeight();

        Collection<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            int x = screenX(node.getX(), range[0], range[1], width);
            int y = screenY(node.getY(), range[2], range[3], height);

            // Dibujar círculo del nodo
            gc.setFill(Color.web("#4682B4"));
            gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

            // Dibujar borde del nodo
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

            // Dibujar etiqueta del nodo
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 12));
            String label = node.getLabel();
            int labelWidth = (int) (label.length() * 7);
            gc.fillText(label, x - labelWidth / 2, y + 5);
        }
    }

    /**
     * Convierte coordenadas del plano a píxeles de la pantalla (eje X).
     */
    private int screenX(double x, double minX, double maxX, int width) {
        double range = maxX - minX;
        if (range == 0) return PADDING;
        return PADDING + (int) ((x - minX) / range * (width - 2 * PADDING));
    }

    /**
     * Convierte coordenadas del plano a píxeles de la pantalla (eje Y).
     */
    private int screenY(double y, double minY, double maxY, int height) {
        double range = maxY - minY;
        if (range == 0) return height - PADDING;
        return height - PADDING - (int) ((y - minY) / range * (height - 2 * PADDING));
    }

    /**
     * Convierte coordenadas de pantalla a coordenadas del plano cartesiano (eje X).
     */
    private double screenToPlaneX(int screenX, double minX, double maxX, int width) {
        double range = maxX - minX;
        if (range == 0) return minX;
        return minX + ((screenX - PADDING) / (double) (width - 2 * PADDING)) * range;
    }

    /**
     * Convierte coordenadas de pantalla a coordenadas del plano cartesiano (eje Y).
     */
    private double screenToPlaneY(int screenY, double minY, double maxY, int height) {
        double range = maxY - minY;
        if (range == 0) return minY;
        return maxY - ((screenY - PADDING) / (double) (height - 2 * PADDING)) * range;
    }

    /**
     * Detecta si hay una figura en la posición especificada y la retorna.
     */
    private Figure getFigureAtPosition(double screenX, double screenY) {
        double[] range = planeService.getPlaneRange();
        int width = (int) getWidth();
        int height = (int) getHeight();

        // Recorrer las figuras en orden inverso para detectar la que está arriba
        for (int i = figures.size() - 1; i >= 0; i--) {
            Figure figure = figures.get(i);
            int x = screenX(figure.x, range[0], range[1], width);
            int y = screenY(figure.y, range[2], range[3], height);

            // Convertir el tamaño a píxeles
            double sizePixels = (figure.size / (range[1] - range[0])) * (width - 2 * PADDING);

            // Verificar si el punto está dentro de la figura
            if (screenX >= x - sizePixels / 2 && screenX <= x + sizePixels / 2 &&
                screenY >= y - sizePixels / 2 && screenY <= y + sizePixels / 2) {
                return figure;
            }
        }
        return null;
    }

    /**
     * Maneja el evento de presión del botón del ratón.
     */
    private void handleMousePressed(MouseEvent event) {
        selectedFigure = getFigureAtPosition(event.getX(), event.getY());
        if (selectedFigure != null) {
            // Calcular el offset entre la posición de la figura y el cursor
            double[] range = planeService.getPlaneRange();
            int width = (int) getWidth();
            int height = (int) getHeight();
            
            int figureScreenX = screenX(selectedFigure.x, range[0], range[1], width);
            int figureScreenY = screenY(selectedFigure.y, range[2], range[3], height);
            
            mouseOffsetX = event.getX() - figureScreenX;
            mouseOffsetY = event.getY() - figureScreenY;
        }
    }

    /**
     * Maneja el evento de arrastre del ratón.
     */
    private void handleMouseDragged(MouseEvent event) {
        if (selectedFigure != null) {
            double[] range = planeService.getPlaneRange();
            int width = (int) getWidth();
            int height = (int) getHeight();

            // Calcular la nueva posición del cursor sin el offset
            double newScreenX = event.getX() - mouseOffsetX;
            double newScreenY = event.getY() - mouseOffsetY;

            // Convertir a coordenadas del plano cartesiano
            selectedFigure.x = screenToPlaneX((int) newScreenX, range[0], range[1], width);
            selectedFigure.y = screenToPlaneY((int) newScreenY, range[2], range[3], height);

            // Redibujar el canvas
            draw();
        }
    }

    /**
     * Maneja el evento de liberación del botón del ratón.
     */
    private void handleMouseReleased(MouseEvent event) {
        selectedFigure = null;
    }

    public void updateGraph(Graph newGraph) {
        this.graph = newGraph;
        draw();
    }
}

