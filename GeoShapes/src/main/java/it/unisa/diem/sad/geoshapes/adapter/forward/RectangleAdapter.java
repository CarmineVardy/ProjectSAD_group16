package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * An adapter for converting a model-level {@link MyRectangle} object
 * to a JavaFX {@link Rectangle} object. This class follows the Singleton pattern
 * to ensure only one instance is created.
 */
public class RectangleAdapter implements ShapeAdapter {

    // Private static final constants
    private static final RectangleAdapter INSTANCE = new RectangleAdapter();

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    RectangleAdapter() {
        // Private constructor
    }

    /**
     * Returns the singleton instance of the {@code RectangleAdapter}.
     *
     * @return The single instance of {@code RectangleAdapter}.
     */
    public static RectangleAdapter getInstance() {
        return INSTANCE;
    }

    /**
     * Converts a {@link MyRectangle} model shape into a JavaFX {@link Rectangle}.
     * The coordinates are scaled based on the provided width and height of the drawing area.
     *
     * @param modelShape The {@link MyShape} object (expected to be {@link MyRectangle}) to convert.
     * @param width The current width of the drawing area, used for scaling coordinates.
     * @param height The current height of the drawing area, used for scaling coordinates.
     * @return The JavaFX {@link Rectangle} representation of the model shape.
     * @throws IllegalArgumentException If the provided {@code modelShape} is not an instance of {@link MyRectangle}.
     */
    @Override
    public Shape getFxShape(MyShape modelShape, double width, double height) {
        if (!(modelShape instanceof MyRectangle myRectangle)) { // Use pattern matching for instanceof
            throw new IllegalArgumentException("Expected MyRectangle");
        }

        // Calculate position and dimensions based on scaled start/end coordinates
        double x = Math.min(myRectangle.getStartX(), myRectangle.getEndX()) * width;
        double y = Math.min(myRectangle.getStartY(), myRectangle.getEndY()) * height;
        double rectWidth = Math.abs(myRectangle.getEndX() - myRectangle.getStartX()) * width;
        double rectHeight = Math.abs(myRectangle.getEndY() - myRectangle.getStartY()) * height;

        Rectangle fxRectangle = new Rectangle(x, y, rectWidth, rectHeight);
        fxRectangle.setStroke(convertToJavaFxColor(myRectangle.getBorderColor())); // Apply border color
        fxRectangle.setFill(convertToJavaFxColor(myRectangle.getFillColor())); // Apply fill color
        fxRectangle.setStrokeWidth(2.0); // Set stroke width
        fxRectangle.setRotate(myRectangle.getRotation()); // Apply rotation from model
        return fxRectangle;
    }
}