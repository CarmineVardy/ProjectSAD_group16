package it.unisa.diem.sad.geoshapes.controller;

import javafx.scene.shape.Shape;

import java.util.List;

/**
 * Defines a callback interface for handling user interactions and events
 * related to shape creation, modification, and selection within the drawing application.
 * This allows different components (e.g., tool strategies) to communicate back to the
 * main controller or other relevant parts of the application.
 */
public interface InteractionCallback {

    /**
     * Called when a new JavaFX {@link Shape} has been created by a tool and
     * is ready to be added to the drawing model.
     *
     * @param shape The newly created JavaFX {@link Shape} object.
     */
    void onCreateShape(Shape shape);

    /**
     * Called when properties of one or more JavaFX {@link Shape} objects have been modified
     * and these changes need to be reflected in the drawing model.
     *
     * @param shapes A {@code List} of JavaFX {@link Shape} objects whose properties have changed.
     */
    void onModifyShapes(List<Shape> shapes);

    /**
     * Called when a context menu for selection-related actions should be opened.
     *
     * @param x The screen X-coordinate where the context menu should appear.
     * @param y The screen Y-coordinate where the context menu should appear.
     */
    void onSelectionMenuOpened(double x, double y);

    /**
     * Called when the selection of shapes on the drawing area has changed.
     * This method can be used to update UI elements dependent on the current selection.
     */
    void onChangeShapeSelected();
}