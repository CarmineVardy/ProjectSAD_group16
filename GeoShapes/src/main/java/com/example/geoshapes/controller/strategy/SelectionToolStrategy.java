package com.example.geoshapes.controller.strategy;

import com.example.geoshapes.model.shapes.MyShape;
import com.example.geoshapes.decorator.ShapeDecorator;
import com.example.geoshapes.decorator.SelectionDecorator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class SelectionToolStrategy implements ToolStrategy {

    private final Pane drawingArea;

    private ShapeDecorator currentDecorator;

    private MyShape selectedModelShape;
    private Shape selectedJavaFxShape;


    public SelectionToolStrategy(Pane drawingArea) {
        this.drawingArea = drawingArea;
    }

    @Override
    public void handlePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            currentDecorator = null;
            selectedJavaFxShape = null;
            selectedModelShape = null;
        }

        Shape clickedShape = findShapeAt(x, y);

        if (clickedShape != null) {
            currentDecorator = new SelectionDecorator(clickedShape);
            currentDecorator.applyDecoration();
            selectedJavaFxShape = clickedShape;
        }
    }

    @Override
    public void handleDragged(MouseEvent event) {
        // Drag functionality could be implemented here
    }

    @Override
    public void handleReleased(MouseEvent event) {
        // Release functionality could be implemented here
    }

    @Override
    public MyShape getFinalShape() {
        return selectedModelShape;
    }


    private Shape findShapeAt(double x, double y) {

        for (int i = drawingArea.getChildren().size() - 1; i >= 0; i--) {
            if (drawingArea.getChildren().get(i) instanceof Shape) {
                Shape shape = (Shape) drawingArea.getChildren().get(i);
                if (shape.contains(x, y)) {
                    return shape;
                }
            }
        }
        return null;
    }


}