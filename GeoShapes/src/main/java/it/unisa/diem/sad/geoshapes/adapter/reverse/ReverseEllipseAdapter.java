package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;

/**
 * An adapter for converting a JavaFX {@link Ellipse} object
 * back to a model-level {@link MyEllipse} object. This class follows the Singleton pattern
 * to ensure only one instance is created.
 */
public class ReverseEllipseAdapter implements ReverseShapeAdapter {

    // Private static final constants
    private static final ReverseEllipseAdapter INSTANCE = new ReverseEllipseAdapter();

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private ReverseEllipseAdapter() {
        // Private constructor
    }

    /**
     * Returns the singleton instance of the {@code ReverseEllipseAdapter}.
     *
     * @return The single instance of {@code ReverseEllipseAdapter}.
     */
    public static ReverseEllipseAdapter getInstance() {
        return INSTANCE;
    }

    /**
     * Converts a JavaFX {@link Ellipse} into a {@link MyEllipse} model shape.
     * The coordinates are scaled based on the provided width and height of the drawing area.
     *
     * @param fxShape The JavaFX {@link Shape} object (expected to be {@link Ellipse}) to convert.
     * @param width The current width of the drawing area, used for scaling coordinates.
     * @param height The current height of the drawing area, used for scaling coordinates.
     * @return The {@link MyEllipse} model representation of the JavaFX shape.
     * @throws IllegalArgumentException If the provided {@code fxShape} is not an instance of {@link Ellipse}.
     */
    @Override
    public MyShape getModelShape(Shape fxShape, double width, double height) {
        if (!(fxShape instanceof Ellipse fxEllipse)) { // Use pattern matching for instanceof
            throw new IllegalArgumentException("Expected Ellipse");
        }

        // Calculate normalized center and radii
        double centerX = fxEllipse.getCenterX() / width;
        double centerY = fxEllipse.getCenterY() / height;
        double radiusX = fxEllipse.getRadiusX() / width;
        double radiusY = fxEllipse.getRadiusY() / height;

        // Convert center and radii back to start/end coordinates for the model
        double startX = centerX - radiusX;
        double startY = centerY - radiusY;
        double endX = centerX + radiusX;
        double endY = centerY + radiusY;

        MyEllipse modelEllipse = new MyEllipse(
                startX, startY, endX, endY,
                fxEllipse.getRotate(), // Get rotation from JavaFX shape
                convertToModelColor((Color) fxEllipse.getStroke()), // Convert stroke color
                convertToModelColor((Color) fxEllipse.getFill()) // Convert fill color
        );

        return modelEllipse;
    }
}