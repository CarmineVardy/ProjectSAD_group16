package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
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

    private final Pane drawingArea;
    private final ShapeMapping shapeMapping;

    private ShapeDecorator currentDecorator;

    private MyShape selectedModelShape;
    private Shape selectedJavaFxShape;

    private InteractionCallback callback;


    public SelectionToolStrategy(Pane drawingArea, ShapeMapping shapeMapping, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.shapeMapping = shapeMapping;
        this.callback = callback;
    }

    @Override
    public void handlePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        Shape shapeAtPosition = findShapeAt(x, y);

        if (shapeAtPosition == null) {
            reset();
            return;
        }

        if (shapeAtPosition != selectedJavaFxShape) {
            reset();
            selectedJavaFxShape = shapeAtPosition;
            selectedModelShape = shapeMapping.getModelShape(selectedJavaFxShape);

            currentDecorator = new SelectionDecorator(selectedJavaFxShape);
            currentDecorator.applyDecoration();
        }

        if (event.getButton() == MouseButton.SECONDARY && selectedJavaFxShape != null) {
            callback.onSelectionMenuOpened(selectedJavaFxShape, selectedModelShape, event.getX(), event.getY());
        }
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
    public void reset() {
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            currentDecorator = null;
        }
        selectedModelShape = null;
        selectedJavaFxShape = null;

        callback.onSelectionMenuClosed();
    }

    private Shape findShapeAt(double x, double y) {
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


}