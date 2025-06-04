package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;

/**
 * An adapter for converting a JavaFX {@link Line} object
 * back to a model-level {@link MyLine} object. This class follows the Singleton pattern
 * to ensure only one instance is created.
 */
public class ReverseLineAdapter implements ReverseShapeAdapter {

    // Private static final constants
    private static final ReverseLineAdapter INSTANCE = new ReverseLineAdapter();

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private ReverseLineAdapter() {
        // Private constructor
    }

    /**
     * Returns the singleton instance of the {@code ReverseLineAdapter}.
     *
     * @return The single instance of {@code ReverseLineAdapter}.
     */
    public static ReverseLineAdapter getInstance() {
        return INSTANCE;
    }

    /**
     * Converts a JavaFX {@link Line} into a {@link MyLine} model shape.
     * The coordinates are scaled based on the provided width and height of the drawing area.
     *
     * @param fxShape The JavaFX {@link Shape} object (expected to be {@link Line}) to convert.
     * @param width The current width of the drawing area, used for scaling coordinates.
     * @param height The current height of the drawing area, used for scaling coordinates.
     * @return The {@link MyLine} model representation of the JavaFX shape.
     * @throws IllegalArgumentException If the provided {@code fxShape} is not an instance of {@link Line}.
     */
    @Override
    public MyShape getModelShape(Shape fxShape, double width, double height) {
        if (!(fxShape instanceof Line fxLine)) { // Use pattern matching for instanceof
            throw new IllegalArgumentException("Expected Line");
        }

        MyLine modelLine = new MyLine(
                fxLine.getStartX() / width, // Scale start X back to normalized
                fxLine.getStartY() / height, // Scale start Y back to normalized
                fxLine.getEndX() / width, // Scale end X back to normalized
                fxLine.getEndY() / height, // Scale end Y back to normalized
                fxLine.getRotate(), // Get rotation from JavaFX shape
                convertToModelColor((Color) fxLine.getStroke()) // Convert stroke color
        );

        return modelLine;
    }
}