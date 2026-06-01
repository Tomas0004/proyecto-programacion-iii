package co.edu.uptc.model;

/**
 * Enumeración de figuras disponibles para colocar en el plano cartesiano.
 */
public enum Shape {
    SQUARE("Cuadrado", "square"),
    CIRCLE("Círculo", "circle"),
    ROBOT("Robot", "robot"),
    DESTINATION("Destino", "destination");

    private final String displayName;
    private final String id;

    Shape(String displayName, String id) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
