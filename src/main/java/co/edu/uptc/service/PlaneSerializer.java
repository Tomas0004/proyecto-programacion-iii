package co.edu.uptc.service;

import co.edu.uptc.model.*;
import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Servicio para serializar y deserializar el plano cartesiano completo.
 * Guarda figuras, segmentos laterales y la configuración del plano.
 */
public class PlaneSerializer {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Estructura para guardar el estado del plano.
     */
    public static class PlaneState {
        public double[] range; // [minX, maxX, minY, maxY]
        public List<FigureData> figures;
        public List<LateralSegmentData> segments;
        
        public PlaneState() {
            this.figures = new ArrayList<>();
            this.segments = new ArrayList<>();
        }
    }
    
    /**
     * Estructura para guardar datos de una figura.
     */
    public static class FigureData {
        public String shape;
        public double x;
        public double y;
        public double size;
    }
    
    /**
     * Estructura para guardar datos de un segmento lateral.
     */
    public static class LateralSegmentData {
        public double startX;
        public double startY;
        public double endX;
        public double endY;
        public double width;
        public String type; // HORIZONTAL o VERTICAL
    }
    
    /**
     * Guarda el estado del plano en un archivo JSON.
     */
    public static void savePlane(String filePath,
                                  CartesianPlaneService planeService,
                                  List<?> figures,
                                  List<LateralSegment> segments) throws IOException {
        PlaneState state = new PlaneState();
        
        // Guardar rango del plano
        double[] range = planeService.getPlaneRange();
        state.range = range;
        
        // Guardar figuras
        for (Object figObj : figures) {
            // Usar reflexión para acceder a los campos de Figure
            try {
                FigureData figData = new FigureData();
                
                // Obtener el campo 'shape' del objeto Figure
                Object shapeObj = figObj.getClass().getDeclaredField("shape").get(figObj);
                Shape shape = (Shape) shapeObj;
                figData.shape = shape.getId();
                
                // Obtener los campos x, y, size
                figData.x = (double) figObj.getClass().getDeclaredField("x").get(figObj);
                figData.y = (double) figObj.getClass().getDeclaredField("y").get(figObj);
                figData.size = (double) figObj.getClass().getDeclaredField("size").get(figObj);
                
                state.figures.add(figData);
            } catch (Exception e) {
                System.err.println("Error al serializar figura: " + e.getMessage());
            }
        }
        
        // Guardar segmentos laterales
        for (LateralSegment segment : segments) {
            LateralSegmentData segData = new LateralSegmentData();
            segData.startX = segment.getStartNode().getX();
            segData.startY = segment.getStartNode().getY();
            segData.endX = segment.getEndNode().getX();
            segData.endY = segment.getEndNode().getY();
            segData.width = segment.getWidth();
            segData.type = segment.getType().toString();
            state.segments.add(segData);
        }
        
        // Guardar a archivo
        String json = gson.toJson(state);
        Files.write(Paths.get(filePath), json.getBytes());
        System.out.println("Plano guardado en: " + filePath);
    }
    
    /**
     * Carga el estado del plano desde un archivo JSON.
     */
    public static PlaneState loadPlane(String filePath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        PlaneState state = gson.fromJson(json, PlaneState.class);
        System.out.println("Plano cargado desde: " + filePath);
        return state;
    }
    
    /**
     * Convierte datos de figura JSON a Shape enum.
     */
    public static Shape shapeFromId(String id) {
        for (Shape shape : Shape.values()) {
            if (shape.getId().equals(id)) {
                return shape;
            }
        }
        throw new IllegalArgumentException("Shape no encontrado: " + id);
    }
}
