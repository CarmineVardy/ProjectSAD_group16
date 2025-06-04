package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Shape;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import javafx.scene.paint.Color;

/**
 * Defines the common interface for "forward" adapters, which convert
 * model-level {@link MyShape} objects into JavaFX {@link Shape} objects
 * for display in the UI.
 * This interface also provides a default method for converting {@link MyColor}
 * to JavaFX {@link Color}.
 */
public interface ShapeAdapter {

    /**
     * Converts a model-level {@link MyShape} into its corresponding JavaFX {@link Shape} representation.
     * The dimensions of the drawing area (width and height) are provided to allow
     * for proper scaling of coordinates from a normalized model space to pixel space.
     *
     * @param modelShape The {@link MyShape} object from the model.
     * @param width The width of the drawing area in pixels.
     * @param height The height of the drawing area in pixels.
     * @return The JavaFX {@link Shape} representation of the model shape.
     * @throws IllegalArgumentException if the provided model shape is not of the expected type
     * for a specific adapter implementation.
     */
    Shape getFxShape(MyShape modelShape, double width, double height);

    /**
     * Converts a model-level {@link MyColor} object to a JavaFX {@link Color} object.
     * If the model color is {@code null}, it defaults to JavaFX black.
     *
     * @param modelColor The {@link MyColor} object to convert.
     * @return The corresponding JavaFX {@link Color} object.
     */
    default Color convertToJavaFxColor(MyColor modelColor) {
        if (modelColor == null) {
            return Color.BLACK; // Default to black if model color is null
        }
        return new Color(
                modelColor.getRed(),
                modelColor.getGreen(),
                modelColor.getBlue(),
                modelColor.getOpacity()
        );
    }
}