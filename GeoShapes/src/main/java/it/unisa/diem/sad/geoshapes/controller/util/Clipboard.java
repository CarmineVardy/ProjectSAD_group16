package it.unisa.diem.sad.geoshapes.controller.util;

import it.unisa.diem.sad.geoshapes.adapter.AdapterFactory;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the copy and paste functionality for shapes within the GeoShapes application.
 * It holds a list of {@link MyShape} objects that have been copied and provides methods
 * for pasting cloned versions of these shapes with a slight offset.
 */
public final class Clipboard { // Class made final as per convention for utility classes with no subclasses.

    // Public static final constants
    private static final double OFFSET_X = 0.025; // Offset for X-coordinate when pasting
    private static final double OFFSET_Y = 0.025; // Offset for Y-coordinate when pasting

    // Private instance variables
    private final List<MyShape> clipboardContent; // Renamed from 'clipboard' for clarity
    private final AdapterFactory adapterFactory; // Made final as it's typically injected and not reassigned
    private final BooleanProperty emptyProperty;

    /**
     * Constructs a new {@code Clipboard} instance.
     *
     * @param adapterFactory The {@link AdapterFactory} to use for cloning shapes during paste operations.
     */
    public Clipboard(AdapterFactory adapterFactory) {
        this.clipboardContent = new ArrayList<>();
        this.adapterFactory = adapterFactory;
        this.emptyProperty = new SimpleBooleanProperty(true);
    }

    /**
     * Copies the provided list of shapes to the clipboard.
     * Any existing content in the clipboard is cleared before adding the new shapes.
     * The clipboard stores references to the original shapes.
     *
     * @param shapes The {@code List} of {@link MyShape} objects to copy. Cannot be null.
     * @throws IllegalArgumentException If the provided shape list is null.
     */
    public void copy(List<MyShape> shapes) {
        if (shapes == null) {
            throw new IllegalArgumentException("Shape list cannot be null");
        }
        clipboardContent.clear();
        for (MyShape shape : shapes) {
            if (shape != null) {
                clipboardContent.add(shape);
            }
        }
        updateEmptyProperty();
    }

    /**
     * Pastes the shapes currently in the clipboard onto the drawing area.
     * Each shape is cloned and offset by {@code OFFSET_X} and {@code OFFSET_Y}
     * relative to its original coordinates when copied.
     * The clipboard content itself is updated with the newly pasted (offset) shapes.
     *
     * @return An unmodifiable {@code List} of the newly pasted {@link MyShape} objects.
     * Returns an empty list if the clipboard is empty.
     */
    public List<MyShape> paste() {
        if (clipboardContent.isEmpty()) {
            return Collections.emptyList();
        }

        List<MyShape> pastedShapes = new ArrayList<>();

        // Clone each shape from clipboard and apply offset
        for (MyShape shape : clipboardContent) {
            pastedShapes.add(adapterFactory.cloneWithOffset(shape, OFFSET_X, OFFSET_Y));
        }

        // Update clipboard with the newly pasted shapes (for subsequent pastes)
        clipboardContent.clear();
        clipboardContent.addAll(pastedShapes);

        return Collections.unmodifiableList(pastedShapes);
    }

    /**
     * Checks if the clipboard is currently empty.
     *
     * @return {@code true} if the clipboard contains no shapes, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return clipboardContent.isEmpty();
    }

    /**
     * Returns a {@link BooleanProperty} that indicates whether the clipboard is empty.
     * This property can be used for UI binding to enable/disable paste actions.
     *
     * @return The {@code BooleanProperty} representing the empty status of the clipboard.
     */
    public BooleanProperty emptyProperty() {
        return emptyProperty;
    }

    // Private methods

    /**
     * Updates the {@code emptyProperty} based on the current state of the clipboard content.
     * This method should be called whenever the {@code clipboardContent} list changes.
     */
    private void updateEmptyProperty() {
        emptyProperty.set(clipboardContent.isEmpty());
    }
}