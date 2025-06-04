package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

/**
 * An adapter for converting a model-level {@link MyLine} object
 * to a JavaFX {@link Line} object. This class follows the Singleton pattern
 * to ensure only one instance is created.
 */
public class LineAdapter implements ShapeAdapter {

    // Private static final constants
    private static final LineAdapter INSTANCE = new LineAdapter();

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    LineAdapter() {
        // Private constructor
    }

    /**
     * Returns the singleton instance of the {@code LineAdapter}.
     *
     * @return The single instance of {@code LineAdapter}.
     */
    public static LineAdapter getInstance() {
        return INSTANCE;
    }

    /**
     * Converts a {@link MyLine} model shape into a JavaFX {@link Line}.
     * The coordinates are scaled based on the provided width and height of the drawing area.
     *
     * @param modelShape The {@link MyShape} object (expected to be {@link MyLine}) to convert.
     * @param width The current width of the drawing area, used for scaling coordinates.
     * @param height The current height of the drawing area, used for scaling coordinates.
     * @return The JavaFX {@link Line} representation of the model shape.
     * @throws IllegalArgumentException If the provided {@code modelShape} is not an instance of {@link MyLine}.
     */
    @Override
    public Shape getFxShape(MyShape modelShape, double width, double height) {
        if (!(modelShape instanceof MyLine myLine)) { // Use pattern matching for instanceof
            throw new IllegalArgumentException("Expected MyLine");
        }
        Line fxLine = new Line(
                myLine.getStartX() * width, // Scale start X
                myLine.getStartY() * height, // Scale start Y
                myLine.getEndX() * width, // Scale end X
                myLine.getEndY() * height // Scale end Y
        );
        fxLine.setStroke(convertToJavaFxColor(myLine.getBorderColor())); // Apply border color
        fxLine.setStrokeWidth(2.0); // Set stroke width
        fxLine.setRotate(myLine.getRotation()); // Apply rotation from model

        return fxLine;
    }
}