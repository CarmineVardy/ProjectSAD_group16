package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;

/**
 * Defines the common interface for "reverse" adapters, which convert
 * JavaFX {@link Shape} objects back into model-level {@link MyShape} objects.
 * This interface also provides a default method for converting JavaFX {@link Color}
 * to {@link MyColor}.
 */
public interface ReverseShapeAdapter {

    /**
     * Converts a JavaFX {@link Shape} object into its corresponding model-level {@link MyShape} representation.
     * The dimensions of the drawing area (width and height) are provided to allow
     * for proper scaling of coordinates from pixel space back to a normalized model space.
     *
     * @param fxShape The JavaFX {@link Shape} object.
     * @param width The current width of the drawing area, used for scaling coordinates.
     * @param height The current height of the drawing area, used for scaling coordinates.
     * @return The model-level {@link MyShape} representation of the JavaFX shape.
     * @throws IllegalArgumentException if the provided JavaFX shape is not of the expected type
     * for a specific adapter implementation.
     */
    MyShape getModelShape(Shape fxShape, double width, double height);

    /**
     * Converts a JavaFX {@link Color} object to a model-level {@link MyColor} object.
     * If the JavaFX color is {@code null}, it defaults to a black {@link MyColor}.
     *
     * @param fxColor The JavaFX {@link Color} object to convert.
     * @return The corresponding model-level {@link MyColor} object.
     */
    default MyColor convertToModelColor(Color fxColor) {
        if (fxColor == null) {
            return new MyColor(0, 0, 0, 1); // Default to black MyColor
        }
        return new MyColor(
                fxColor.getRed(),
                fxColor.getGreen(),
                fxColor.getBlue(),
                fxColor.getOpacity()
        );
    }
}