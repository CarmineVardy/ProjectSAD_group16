package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the bidirectional mapping between model shapes ({@link MyShape})
 * and their corresponding JavaFX view shapes ({@link Shape}).
 * This class ensures consistency between the logical representation of shapes
 * in the application's data model and their visual representation in the UI.
 */
public class ShapeMapping {

    private List<MyShape> modelShapes;
    private List<Shape> viewShapes;

    /**
     * Constructs a new ShapeMapping instance with empty lists for model and view shapes.
     * The lists will be populated as shapes are added or updated in the application.
     */
    public ShapeMapping() {
        this.modelShapes = new ArrayList<>();
        this.viewShapes = new ArrayList<>();
    }

    /**
     * Returns a new list containing all model shapes currently mapped.
     * The returned list is a copy to prevent external modifications to the internal state.
     *
     * @return A list of {@link MyShape} objects.
     */
    public List<MyShape> getModelShapes() {
        return new ArrayList<>(modelShapes);
    }

    /**
     * Returns a new list containing all JavaFX view shapes currently mapped.
     * The returned list is a copy to prevent external modifications to the internal state.
     *
     * @return A list of {@link Shape} objects.
     */
    public List<Shape> getViewShapes() {
        return new ArrayList<>(viewShapes);
    }

    /**
     * Retrieves the JavaFX view shape corresponding to a given model shape.
     *
     * @param modelShape The {@link MyShape} object for which to find the view shape.
     * @return The corresponding {@link Shape} object, or {@code null} if no mapping exists.
     */
    public Shape getViewShape(MyShape modelShape) {
        int index = modelShapes.indexOf(modelShape);
        return index != -1 ? viewShapes.get(index) : null;
    }

    /**
     * Retrieves the model shape corresponding to a given JavaFX view shape.
     *
     * @param viewShape The {@link Shape} object for which to find the model shape.
     * @return The corresponding {@link MyShape} object, or {@code null} if no mapping exists.
     */
    public MyShape getModelShape(Shape viewShape) {
        int index = viewShapes.indexOf(viewShape);
        return index != -1 ? modelShapes.get(index) : null;
    }

    /**
     * Rebuilds the entire mapping between model shapes and view shapes.
     * This method is typically used after significant changes to the model or view,
     * such as loading a new drawing or reordering shapes.
     *
     * @param orderedModelShapes A list of model shapes in their desired order.
     * @param orderedViewShapes  A list of view shapes corresponding to the model shapes, in the same order.
     * @throws IllegalArgumentException if the size of the model and view shape lists do not match.
     */
    public void rebuildMapping(List<MyShape> orderedModelShapes, List<Shape> orderedViewShapes) {
        if (orderedModelShapes.size() != orderedViewShapes.size()) {
            throw new IllegalArgumentException("The lists of model and view must have the same size.");
        }
        clear(); // Clear existing mappings
        for (int i = 0; i < orderedModelShapes.size(); i++) {
            modelShapes.add(orderedModelShapes.get(i));
            viewShapes.add(orderedViewShapes.get(i));
        }
    }

    /**
     * Clears all existing mappings between model and view shapes.
     */
    public void clear() {
        modelShapes.clear();
        viewShapes.clear();
    }

    /**
     * Returns the number of mapped shape pairs.
     *
     * @return The total number of shapes in the mapping.
     */
    public int size() {
        return modelShapes.size();
    }

    /**
     * Checks if the mapping is empty (i.e., contains no shape pairs).
     *
     * @return {@code true} if the mapping is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return modelShapes.isEmpty();
    }
}