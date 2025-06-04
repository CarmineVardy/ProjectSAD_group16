package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyPolygon;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import java.util.List;

/**
 * An adapter for converting a model-level {@link MyPolygon} object
 * to a JavaFX {@link Polygon} object. This class follows the Singleton pattern
 * to ensure only one instance is created.
 */
public class PolygonAdapter implements ShapeAdapter {

    // Private static final constants
    private static final PolygonAdapter INSTANCE = new PolygonAdapter();

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private PolygonAdapter() {
        // Private constructor
    }

    /**
     * Returns the singleton instance of the {@code PolygonAdapter}.
     *
     * @return The single instance of {@code PolygonAdapter}.
     */
    public static PolygonAdapter getInstance() {
        return INSTANCE;
    }

    /**
     * Converts a {@link MyPolygon} model shape into a JavaFX {@link Polygon}.
     * The coordinates are scaled based on the provided width and height of the drawing area.
     *
     * @param modelShape The {@link MyShape} object (expected to be {@link MyPolygon}) to convert.
     * @param width The current width of the drawing area, used for scaling coordinates.
     * @param height The current height of the drawing area, used for scaling coordinates.
     * @return The JavaFX {@link Polygon} representation of the model shape.
     * @throws IllegalArgumentException If the provided {@code modelShape} is not an instance of {@link MyPolygon}.
     */
    @Override
    public Shape getFxShape(MyShape modelShape, double width, double height) {
        if (!(modelShape instanceof MyPolygon myPolygon)) { // Use pattern matching for instanceof
            throw new IllegalArgumentException("Expected MyPolygon");
        }

        Polygon fxPolygon = new Polygon();

        List<Double> xPoints = myPolygon.getXPoints();
        List<Double> yPoints = myPolygon.getYPoints();

        // Scale and add all points to the JavaFX polygon
        for (int i = 0; i < xPoints.size(); i++) {
            fxPolygon.getPoints().add(xPoints.get(i) * width);
            fxPolygon.getPoints().add(yPoints.get(i) * height);
        }

        fxPolygon.setStroke(convertToJavaFxColor(myPolygon.getBorderColor())); // Apply border color
        fxPolygon.setFill(convertToJavaFxColor(myPolygon.getFillColor())); // Apply fill color
        fxPolygon.setStrokeWidth(2.0); // Set stroke width
        // Polygon rotation is typically handled by the JavaFX node itself after creation,
        // but MyShape has a rotation property. If the rotation logic needs to be based on this adapter,
        // it should be applied to fxPolygon here as well. The AdapterFactory will apply the rotation
        // from MyShape to the fxShape after this method returns.

        return fxPolygon;
    }
}