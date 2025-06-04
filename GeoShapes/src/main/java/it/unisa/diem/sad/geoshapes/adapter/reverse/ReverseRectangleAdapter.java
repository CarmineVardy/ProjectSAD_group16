package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;

/**
 * An adapter for converting a JavaFX {@link Rectangle} object
 * back to a model-level {@link MyRectangle} object. This class follows the Singleton pattern
 * to ensure only one instance is created.
 */
public class ReverseRectangleAdapter implements ReverseShapeAdapter {

    // Private static final constants
    private static final ReverseRectangleAdapter INSTANCE = new ReverseRectangleAdapter();

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private ReverseRectangleAdapter() {
        // Private constructor
    }

    /**
     * Returns the singleton instance of the {@code ReverseRectangleAdapter}.
     *
     * @return The single instance of {@code ReverseRectangleAdapter}.
     */
    public static ReverseRectangleAdapter getInstance() {
        return INSTANCE;
    }

    /**
     * Converts a JavaFX {@link Rectangle} into a {@link MyRectangle} model shape.
     * The coordinates are scaled based on the provided width and height of the drawing area.
     *
     * @param fxShape The JavaFX {@link Shape} object (expected to be {@link Rectangle}) to convert.
     * @param width The current width of the drawing area, used for scaling coordinates.
     * @param height The current height of the drawing area, used for scaling coordinates.
     * @return The {@link MyRectangle} model representation of the JavaFX shape.
     * @throws IllegalArgumentException If the provided {@code fxShape} is not an instance of {@link Rectangle}.
     */
    @Override
    public MyShape getModelShape(Shape fxShape, double width, double height) {
        if (!(fxShape instanceof Rectangle fxRectangle)) { // Use pattern matching for instanceof
            throw new IllegalArgumentException("Expected Rectangle");
        }

        // Calculate normalized start/end coordinates based on JavaFX rectangle properties
        double startX = fxRectangle.getX() / width;
        double startY = fxRectangle.getY() / height;
        double endX = (fxRectangle.getX() + fxRectangle.getWidth()) / width;
        double endY = (fxRectangle.getY() + fxRectangle.getHeight()) / height;

        MyRectangle modelRectangle = new MyRectangle(
                startX, startY, endX, endY,
                fxRectangle.getRotate(), // Get rotation from JavaFX shape
                convertToModelColor((Color) fxRectangle.getStroke()), // Convert stroke color
                convertToModelColor((Color) fxRectangle.getFill()) // Convert fill color
        );

        return modelRectangle;
    }
}