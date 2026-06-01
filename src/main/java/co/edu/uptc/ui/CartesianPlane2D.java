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
    private boolean showGrid = true;
    private int gridSpacing = 1;
    private List<Node> shortestPath;  // Camino más corto a mostrar

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
        super(1600, 1000);
        this.graph = graph;
        this.planeService = planeService;
        this.figures = new ArrayList<>();
        this.gridIntersections = new HashMap<>();
        this.selectedFigure = null;
        this.shortestPath = new ArrayList<>();
        
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
     * Solo agrega nodos que NO estén debajo de figuras.
     */
    private void initializeGridIntersections() {
        graph.clear();
        gridIntersections.clear();

        double[] range = planeService.getPlaneRange();
        double minX = range[0];
        double maxX = range[1];
        double minY = range[2];
        double maxY = range[3];

        int nodeId = 1;
        
        // Primera pasada: crear nodos que no estén bajo figuras
        for (int gridX = (int) minX; gridX <= (int) maxX; gridX += gridSpacing) {
            for (int gridY = (int) minY; gridY <= (int) maxY; gridY += gridSpacing) {
                String key = gridX + "," + gridY;

                if (!gridIntersections.containsKey(key) && !isNodeUnderFigure(gridX, gridY)) {
                    Node node = new Node(nodeId, gridX, gridY);
                    gridIntersections.put(key, node);
                    graph.addNode(node);
                    nodeId++;
                }
            }
        }
        
        // Segunda pasada: crear aristas entre nodos adyacentes
        createAllEdges();
    }
    
    /**
     * Verifica si un nodo está debajo de alguna figura (solo SQUARE y CIRCLE).
     * Los nodos bajo ROBOT y DESTINATION permanecen en el grafo.
     */
    private boolean isNodeUnderFigure(double nodeX, double nodeY) {
        double[] range = planeService.getPlaneRange();
        int width = (int) getWidth();
        int height = (int) getHeight();
        
        int nodeScreenX = screenX(nodeX, range[0], range[1], width);
        int nodeScreenY = screenY(nodeY, range[2], range[3], height);
        
        for (Figure figure : figures) {
            // Solo excluir nodos bajo SQUARE y CIRCLE, no bajo ROBOT ni DESTINATION
            if (figure.shape != Shape.SQUARE && figure.shape != Shape.CIRCLE) {
                continue;
            }
            
            int figScreenX = screenX(figure.x, range[0], range[1], width);
            int figScreenY = screenY(figure.y, range[2], range[3], height);
            
            double sizePixels = (figure.size / (range[1] - range[0])) * (width - 2 * PADDING);
            
            // Verificar si el nodo está dentro de la figura
            if (nodeScreenX >= figScreenX - sizePixels / 2 && nodeScreenX <= figScreenX + sizePixels / 2 &&
                nodeScreenY >= figScreenY - sizePixels / 2 && nodeScreenY <= figScreenY + sizePixels / 2) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Crea todas las aristas posibles entre nodos adyacentes.
     */
    private void createAllEdges() {
        graph.getEdges().clear();
        
        double[] range = planeService.getPlaneRange();
        double minX = range[0];
        double maxX = range[1];
        double minY = range[2];
        double maxY = range[3];
        
        // Crear aristas para cada nodo con sus vecinos adyacentes
        for (int gridX = (int) minX; gridX <= (int) maxX; gridX += gridSpacing) {
            for (int gridY = (int) minY; gridY <= (int) maxY; gridY += gridSpacing) {
                String key = gridX + "," + gridY;
                Node sourceNode = gridIntersections.get(key);
                
                if (sourceNode == null) continue;
                
                // Crear aristas hacia los 8 vecinos adyacentes
                int[][] directions = {
                    {1, 0}, {-1, 0}, {0, 1}, {0, -1},  // Arriba, abajo, derecha, izquierda
                };
                
                for (int[] dir : directions) {
                    int neighborX = gridX + dir[0] * gridSpacing;
                    int neighborY = gridY + dir[1] * gridSpacing;
                    String neighborKey = neighborX + "," + neighborY;
                    Node destNode = gridIntersections.get(neighborKey);
                    
                    if (destNode != null) {
                        // Calcular peso como la distancia euclidiana
                        double dx = destNode.getX() - sourceNode.getX();
                        double dy = destNode.getY() - sourceNode.getY();
                        double weight = Math.sqrt(dx * dx + dy * dy);
                        
                        Edge edge = new Edge(sourceNode, destNode, weight);
                        graph.addEdge(edge);
                    }
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
     * Dibuja una forma específica en las coordenadas dadas (solo contorno, sin relleno).
     */
    private void drawShape(GraphicsContext gc, Shape shape, int x, int y, double size) {
        switch (shape) {
            case SQUARE:
                gc.setStroke(Color.web("#FF0000"));
                gc.setLineWidth(2);
                gc.strokeRect(x - size / 2, y - size / 2, size, size);
                break;
            case CIRCLE:
                gc.setStroke(Color.web("#FF0000"));
                gc.setLineWidth(2);
                gc.strokeOval(x - size / 2, y - size / 2, size, size);
                break;
            case ROBOT:
                drawRobot(gc, shape, x, y, size);
                break;
            case DESTINATION:
                // Dibujar destino como una estrella roja
                drawDestinationStar(gc, x, y, size);
                break;
        }
    }
    
    /**
     * Dibuja una estrella para representar el destino.
     */
    private void drawDestinationStar(GraphicsContext gc, int x, int y, double size) {
        double[] xs = new double[10];
        double[] ys = new double[10];
        
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 2 + 2 * Math.PI * i / 10;
            double radius = (i % 2 == 0) ? size / 2 : size / 4;
            xs[i] = x + radius * Math.cos(angle);
            ys[i] = y - radius * Math.sin(angle);
        }
        
        gc.setFill(Color.web("#FF6B6B"));
        gc.fillPolygon(xs, ys, 10);
        gc.setStroke(Color.web("#FF0000"));
        gc.setLineWidth(2);
        gc.strokePolygon(xs, ys, 10);
    }

    /**
     * Agrega una figura al lienzo y reinicializa el grafo.
     */
    public void addFigure(Shape shape, double x, double y, double size) {
        figures.add(new Figure(shape, x, y, size));
        // Reinicializar el grafo para actualizar qué nodos están bajo figuras
        initializeGridIntersections();
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
        
        // Dibujar el camino más corto
        drawShortestPath(gc);
    }
    
    /**
     * Dibuja el camino más corto encontrado por Dijkstra.
     */
    private void drawShortestPath(GraphicsContext gc) {
        if (shortestPath == null || shortestPath.size() < 2) return;
        
        double[] range = planeService.getPlaneRange();
        int width = (int) getWidth();
        int height = (int) getHeight();
        
        // Dibujar líneas del camino en color amarillo
        gc.setStroke(Color.web("#FFFF00"));
        gc.setLineWidth(4);
        
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            Node node1 = shortestPath.get(i);
            Node node2 = shortestPath.get(i + 1);
            
            int x1 = screenX(node1.getX(), range[0], range[1], width);
            int y1 = screenY(node1.getY(), range[2], range[3], height);
            int x2 = screenX(node2.getX(), range[0], range[1], width);
            int y2 = screenY(node2.getY(), range[2], range[3], height);
            
            gc.strokeLine(x1, y1, x2, y2);
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
     * Dibuja los nodos del grafo como puntos grises pequeños.
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

            // Dibujar punto gris pequeño (2 píxeles de radio)
            gc.setFill(Color.web("#A0A0A0"));
            gc.fillOval(x - 2, y - 2, 4, 4);
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

    public Graph getGraph() {
        return graph;
    }
    
    /**
     * Obtiene una figura por su tipo de forma.
     */
    public Figure getFigureByShape(Shape shape) {
        for (Figure figure : figures) {
            if (figure.shape == shape) {
                return figure;
            }
        }
        return null;
    }
    
    /**
     * Obtiene el nodo más cercano a las coordenadas especificadas.
     */
    public Node getClosestNode(double x, double y) {
        Collection<Node> nodes = graph.getNodes();
        Node closest = null;
        double minDistance = Double.POSITIVE_INFINITY;
        
        for (Node node : nodes) {
            double dx = node.getX() - x;
            double dy = node.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance < minDistance) {
                minDistance = distance;
                closest = node;
            }
        }
        
        return closest;
    }
    
    /**
     * Establece el camino más corto a mostrar.
     */
    public void setShortestPath(List<Node> path) {
        this.shortestPath = path != null ? path : new ArrayList<>();
    }

    public void drawRobot(GraphicsContext gc, Shape shape, int x, int y, double size) {
        // Cuerpo principal (verde metálico, más pequeño)
        double bodySize = size * 0.85;
        gc.setFill(Color.web("#3CB371"));
        gc.fillOval(x - bodySize / 2, y - bodySize / 2, bodySize, bodySize);
        gc.setStroke(Color.web("#2E8B57"));
        gc.setLineWidth(2);
        gc.strokeOval(x - bodySize / 2, y - bodySize / 2, bodySize, bodySize);

        // Ojos robóticos tipo LED (rectangulares con brillo)
        double eyeWidth = bodySize / 4.5;
        double eyeHeight = bodySize / 7;
        double eyeYOffset = bodySize / 6;

        // Ojo izquierdo
        gc.setFill(Color.web("#a91400"));  // Cyan brillante
        gc.fillRoundRect(x - bodySize / 3 - eyeWidth / 2, y - eyeYOffset - eyeHeight / 2, eyeWidth, eyeHeight, 4, 4);
        gc.setFill(Color.web("#f3f3f3"));
        gc.fillRoundRect(x - bodySize / 3 - eyeWidth / 3, y - eyeYOffset - eyeHeight / 3, eyeWidth / 3, eyeHeight / 3, 2, 2);

        // Ojo derecho
        gc.setFill(Color.web("#a91400"));
        gc.fillRoundRect(x + bodySize / 3 - eyeWidth / 2, y - eyeYOffset - eyeHeight / 2, eyeWidth, eyeHeight, 4, 4);
        gc.setFill(Color.web("#f3f3f3"));
        gc.fillRoundRect(x + bodySize / 3 - eyeWidth / 6, y - eyeYOffset - eyeHeight / 3, eyeWidth / 3, eyeHeight / 3, 2, 2);

        // Antena (línea + círculo)
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(3);
        gc.strokeLine(x, y - bodySize / 2, x, y - bodySize / 2 - bodySize / 5);
        gc.setFill(Color.RED);
        gc.fillOval(x - bodySize / 10, y - bodySize / 2 - bodySize / 5 - bodySize / 10, bodySize / 5, bodySize / 5);

        // Tornillos decorativos
        double screwSize = bodySize / 12;
        gc.setFill(Color.DARKGRAY);
        gc.fillOval(x - bodySize / 2 + screwSize, y - bodySize / 2 + screwSize, screwSize, screwSize);
        gc.fillOval(x + bodySize / 2 - screwSize * 2, y - bodySize / 2 + screwSize, screwSize, screwSize);
        gc.fillOval(x - bodySize / 2 + screwSize, y + bodySize / 2 - screwSize * 2, screwSize, screwSize);
        gc.fillOval(x + bodySize / 2 - screwSize * 2, y + bodySize / 2 - screwSize * 2, screwSize, screwSize);

        // Brillo especular (efecto metálico)
        gc.setFill(Color.rgb(255, 255, 255, 0.25));
        gc.fillOval(x - bodySize / 3, y - bodySize / 3, bodySize / 5, bodySize / 5);
    }
}

