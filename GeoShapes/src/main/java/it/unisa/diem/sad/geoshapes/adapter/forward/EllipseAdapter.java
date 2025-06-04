package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;

/**
 * An adapter for converting a model-level {@link MyEllipse} object
 * to a JavaFX {@link Ellipse} object. This class follows the Singleton pattern
 * to ensure only one instance is created.
 */
public class EllipseAdapter implements ShapeAdapter {

    // Private static final constants
    private static final EllipseAdapter INSTANCE = new EllipseAdapter();

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    EllipseAdapter() {
        // Private constructor
    }

    /**
     * Returns the singleton instance of the {@code EllipseAdapter}.
     *
     * @return The single instance of {@code EllipseAdapter}.
     */
    public static EllipseAdapter getInstance() {
        return INSTANCE;
    }

    /**
     * Converts a {@link MyEllipse} model shape into a JavaFX {@link Ellipse}.
     * The coordinates are scaled based on the provided width and height of the drawing area.
     *
     * @param modelShape The {@link MyShape} object (expected to be {@link MyEllipse}) to convert.
     * @param width The current width of the drawing area, used for scaling coordinates.
     * @param height The current height of the drawing area, used for scaling coordinates.
     * @return The JavaFX {@link Ellipse} representation of the model shape.
     * @throws IllegalArgumentException If the provided {@code modelShape} is not an instance of {@link MyEllipse}.
     */
    @Override
    public Shape getFxShape(MyShape modelShape, double width, double height) {
        if (!(modelShape instanceof MyEllipse myEllipse)) { // Use pattern matching for instanceof
            throw new IllegalArgumentException("Expected MyEllipse");
        }

        // Calculate center and radii based on scaled start/end coordinates
        double centerX = (myEllipse.getStartX() + myEllipse.getEndX()) / 2.0 * width;
        double centerY = (myEllipse.getStartY() + myEllipse.getEndY()) / 2.0 * height;
        double radiusX = Math.abs(myEllipse.getEndX() - myEllipse.getStartX()) / 2.0 * width;
        double radiusY = Math.abs(myEllipse.getEndY() - myEllipse.getStartY()) / 2.0 * height;

        Ellipse fxEllipse = new Ellipse(centerX, centerY, radiusX, radiusY);
        fxEllipse.setStroke(convertToJavaFxColor(myEllipse.getBorderColor())); // Apply border color
        fxEllipse.setFill(convertToJavaFxColor(myEllipse.getFillColor())); // Apply fill color
        fxEllipse.setStrokeWidth(2.0); // Set stroke width
        fxEllipse.setRotate(myEllipse.getRotation()); // Apply rotation from model
        return fxEllipse;
    }
}