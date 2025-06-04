package it.unisa.diem.sad.geoshapes.controller.decorator;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import java.util.List;

/**
 * Defines the common interface for shape decorators in the Decorator design pattern.
 * This interface allows adding new functionalities or visual elements to existing
 * JavaFX {@link Shape} objects without modifying their core structure.
 */
public interface ShapeDecorator {
    /**
     * Applies the specific decoration to the shape.
     * This method should add any visual elements or modify properties
     * of the decorated shape to reflect the decoration.
     */
    void applyDecoration();

    /**
     * Removes the applied decoration from the shape, restoring its original state.
     * This method should remove any added visual elements and revert any modified properties.
     */
    void removeDecoration();

    /**
     * Returns the underlying JavaFX {@link Shape} object that is being decorated.
     *
     * @return The decorated {@link Shape} instance.
     */
    Shape getDecoratedShape();

    /**
     * Returns a list of {@link Circle} objects that serve as resize handles for the decorated shape.
     * If the decorator does not provide resize handles, an empty list should be returned.
     *
     * @return A {@code List} of {@link Circle} objects representing the resize handles.
     */
    List<Circle> getResizeHandles();
}