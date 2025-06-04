package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.shapes.MyPolygon;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * An adapter for converting a JavaFX {@link Polygon} object
 * back to a model-level {@link MyPolygon} object. This class follows the Singleton pattern
 * to ensure only one instance is created.
 */
public class ReversePolygonAdapter implements ReverseShapeAdapter {

    // Private static final constants
    private static final ReversePolygonAdapter INSTANCE = new ReversePolygonAdapter();

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private ReversePolygonAdapter() {
        // Private constructor
    }

    /**
     * Returns the singleton instance of the {@code ReversePolygonAdapter}.
     *
     * @return The single instance of {@code ReversePolygonAdapter}.
     */
    public static ReversePolygonAdapter getInstance() {
        return INSTANCE;
    }

    /**
     * Converts a JavaFX {@link Polygon} into a {@link MyPolygon} model shape.
     * The coordinates are scaled based on the provided width and height of the drawing area.
     *
     * @param fxShape The JavaFX {@link Shape} object (expected to be {@link Polygon}) to convert.
     * @param width The current width of the drawing area, used for scaling coordinates.
     * @param height The current height of the drawing area, used for scaling coordinates.
     * @return The {@link MyPolygon} model representation of the JavaFX shape.
     * @throws IllegalArgumentException If the provided {@code fxShape} is not an instance of {@link Polygon},
     * or if the number of points is odd (indicating an invalid polygon).
     */
    @Override
    public MyShape getModelShape(Shape fxShape, double width, double height) {
        if (!(fxShape instanceof Polygon fxPolygon)) { // Use pattern matching for instanceof
            throw new IllegalArgumentException("Expected Polygon");
        }

        List<Double> points = fxPolygon.getPoints();

        if (points.size() % 2 != 0) {
            throw new IllegalArgumentException("Invalid polygon points: odd number of coordinates");
        }

        List<Double> xPoints = new ArrayList<>();
        List<Double> yPoints = new ArrayList<>();

        // Scale points back to normalized coordinates
        for (int i = 0; i < points.size(); i += 2) {
            xPoints.add(points.get(i) / width);
            yPoints.add(points.get(i + 1) / height);
        }

        MyPolygon modelPolygon = new MyPolygon(
                xPoints,
                yPoints,
                fxPolygon.getRotate(), // Get rotation from JavaFX shape
                convertToModelColor((Color) fxPolygon.getStroke()), // Convert stroke color
                convertToModelColor((Color) fxPolygon.getFill()) // Convert fill color
        );

        return modelPolygon;
    }
}