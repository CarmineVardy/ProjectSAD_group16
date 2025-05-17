package com.example.geoshapes.strategy;

import com.example.geoshapes.model.factory.RectangleFactory;
import com.example.geoshapes.model.factory.ShapeFactory;
import com.example.geoshapes.model.shapes.Shape;
import javafx.scene.input.MouseEvent;

public class RectangleToolStrategy implements ToolStrategy {

    private final ShapeFactory factory;
    private Shape currentShape;
    private boolean drawing = false;

    public RectangleToolStrategy() {
        this.factory = new RectangleFactory();
    }

    @Override
    public void handlePressed(MouseEvent event) {
        currentShape = factory.createShape(event.getX(), event.getY(), event.getX(), event.getY());
        drawing = true;
    }

    @Override
    public void handleDragged(MouseEvent event) {
        if (drawing) {
            currentShape.setEndPoint(event.getX(), event.getY());
        }
    }

    @Override
    public void handleReleased(MouseEvent event) {
        if (drawing) {
            currentShape.setEndPoint(event.getX(), event.getY());
            drawing = false;
        }
    }

    public Shape getFinalShape() {
        return currentShape;
    }

}