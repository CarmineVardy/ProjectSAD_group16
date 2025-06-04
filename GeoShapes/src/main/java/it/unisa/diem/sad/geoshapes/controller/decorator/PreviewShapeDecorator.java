package it.unisa.diem.sad.geoshapes.controller.decorator;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.Collections;
import java.util.List;

/**
 * A concrete decorator that applies a "preview" style to a JavaFX {@link Shape}.
 * This decorator is typically used to visually indicate a shape that is currently being
 * drawn or about to be added to the canvas, making it semi-transparent and dashed.
 */
public class PreviewShapeDecorator implements ShapeDecorator {

    // Private instance variables
    private final Shape decoratedShape; // Renamed for clarity on decorated object

    private Paint originalFill;
    private double originalOpacity;

    /**
     * Constructs a new {@code PreviewShapeDecorator} for the given shape.
     *
     * @param shape The JavaFX {@link Shape} to be decorated.
     */
    public PreviewShapeDecorator(Shape shape) {
        this.decoratedShape = shape;
    }

    /**
     * Applies the preview decoration to the shape.
     * This involves setting a dashed stroke, reducing opacity, and brightening the fill color.
     * The original properties are stored before applying the decoration to allow for restoration.
     */
    @Override
    public void applyDecoration() {
        storeOriginalProperties();
        decoratedShape.getStrokeDashArray().setAll(5.0, 4.0);
        decoratedShape.setOpacity(0.75);

        // Brighten the fill color if it's not null and is a Color instance
        if (decoratedShape.getFill() instanceof Color currentFill) {
            decoratedShape.setFill(currentFill.brighter());
        }
    }

    /**
     * Removes the preview decoration, restoring the shape to its original appearance.
     * The original fill, opacity, and stroke dash array are reverted.
     */
    @Override
    public void removeDecoration() {
        decoratedShape.setFill(originalFill);
        decoratedShape.getStrokeDashArray().clear();
        decoratedShape.setOpacity(originalOpacity);
    }

    /**
     * Returns the decorated JavaFX {@link Shape} object.
     *
     * @return The {@link Shape} instance that this decorator is wrapping.
     */
    @Override
    public Shape getDecoratedShape() {
        return decoratedShape;
    }

    /**
     * Returns an empty list of resize handles, as preview shapes do not typically
     * have interactive resize handles.
     *
     * @return An empty {@code List} of {@link Circle} objects.
     */
    @Override
    public List<Circle> getResizeHandles() {
        return Collections.emptyList();
    }

    // Private methods

    /**
     * Stores the original fill color and opacity of the decorated shape.
     * This method is called once when the decoration is first applied to allow for proper restoration.
     */
    private void storeOriginalProperties() {
        originalFill = decoratedShape.getFill();
        originalOpacity = decoratedShape.getOpacity();
    }
}