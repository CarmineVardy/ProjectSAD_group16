package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.ShapeMapping;
import it.unisa.diem.sad.geoshapes.decorator.SelectionDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

import java.util.List;

public class SelectionToolStrategy implements ToolStrategy {

    private final Pane drawingArea; // Still needed for findShapeAt
    private final ShapeMapping shapeMapping;

    private ShapeDecorator currentDecorator;
    private MyShape selectedModelShape;
    private Shape selectedJavaFxShape;

    public SelectionToolStrategy(Pane drawingArea, ShapeMapping shapeMapping) {
        this.drawingArea = drawingArea;
        this.shapeMapping = shapeMapping;
    }

    @Override
    public void handlePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        Shape targetViewShape = findShapeAt(x, y);
        MyShape targetModelShape = (targetViewShape != null) ? shapeMapping.getModelShape(targetViewShape) : null;

        // Case 1: Right-click on the currently selected shape.
        // The strategy does not change the selection state here.
        // The controller will query getSelectedModelShape() and decide to show the context menu.
        if (event.getButton() == MouseButton.SECONDARY &&
                targetModelShape != null &&
                selectedModelShape == targetModelShape) {
            return; // Do nothing to selection state, controller handles menu visibility
        }

        // Case 2: Any other click (left click, or right click on a new shape/empty area).
        // First, clear any existing selection decoration and state if:
        // a) a different shape is clicked
        // b) empty space is clicked
        // c) it's a primary click (which always aims to select anew or deselect)
        if (selectedModelShape != null && (targetModelShape != selectedModelShape || event.getButton() == MouseButton.PRIMARY)) {
            clearCurrentSelectionInternally();
        }

        // If we cleared due to primary click, or if it's a secondary click on a new shape,
        // and a valid model shape was under the cursor, select it.
        if (targetModelShape != null && selectedModelShape == null) { // select if a shape was clicked and nothing is selected (or was just deselected)
            selectedModelShape = targetModelShape;
            selectedJavaFxShape = targetViewShape;
            if (selectedJavaFxShape != null) {
                currentDecorator = new SelectionDecorator(selectedJavaFxShape);
                currentDecorator.applyDecoration();
            }
        }
        // If targetModelShape is null (empty space) or unmapped, selection remains cleared.
    }

    private void clearCurrentSelectionInternally() {
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            currentDecorator = null;
        }
        selectedModelShape = null;
        selectedJavaFxShape = null;
    }

    /**
     * Resets the selection state. Called by the controller when changing tools
     * or when a shape is deleted externally.
     */
    public void resetSelection() {
        clearCurrentSelectionInternally();
    }


    @Override
    public void handleDragged(MouseEvent event) {
        // Future drag functionality for moving/resizing selected shape
    }

    @Override
    public void handleReleased(MouseEvent event) {
        // Future release functionality
    }

    @Override
    public MyShape getFinalShape() {
        // For SelectionTool, this might not be directly used for creation,
        // but the interface requires it. It returns the selected shape.
        return selectedModelShape;
    }

    public MyShape getSelectedModelShape() {
        return selectedModelShape;
    }

    public Shape getSelectedJavaFxShape() {
        return selectedJavaFxShape;
    }

    private Shape findShapeAt(double x, double y) {
        // Iterate from top-most shapes to bottom
        List<javafx.scene.Node> children = drawingArea.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            javafx.scene.Node node = children.get(i);
            if (node instanceof Shape) {
                Shape shape = (Shape) node;
                if (shape.isVisible() && shape.contains(x, y)) {
                    return shape;
                }
            }
        }
        return null;
    }

    /**
     * Updates the JavaFX shape reference and re-applies decoration if a shape
     * was selected and its view component is recreated (e.g., during a full redraw).
     *
     * @param newFxShape The new JavaFX Shape instance corresponding to the selected MyShape.
     */
    public void updateSelectedFxShape(Shape newFxShape) {
        if (selectedModelShape != null) { // If a MyShape was indeed selected
            if (currentDecorator != null) {
                // Attempt to remove decoration from the old FxShape if it's still somehow relevant,
                // though typically it's already removed from scene. More importantly, nullify old decorator.
                currentDecorator.removeDecoration(); // This might do nothing if old shape is gone
            }

            selectedJavaFxShape = newFxShape; // Update to the new JavaFX shape

            if (selectedJavaFxShape != null) { // If a valid new FxShape is provided
                currentDecorator = new SelectionDecorator(selectedJavaFxShape);
                currentDecorator.applyDecoration();
            } else {
                // The selected MyShape no longer has a valid view. Clear selection.
                currentDecorator = null;
                selectedModelShape = null; // MyShape is effectively deselected as it has no view
            }
        }
    }
}