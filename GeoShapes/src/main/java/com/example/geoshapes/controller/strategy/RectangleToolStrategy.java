package com.example.geoshapes.controller.strategy;

import com.example.geoshapes.model.factory.RectangleFactory;
import com.example.geoshapes.model.factory.ShapeFactory;
import com.example.geoshapes.model.shapes.Shape;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class RectangleToolStrategy implements ToolStrategy {

    private Pane drawingArea;
    private final ShapeFactory factory;
    private javafx.scene.shape.Shape previewFxShape;
    private Shape currentModelShape;
    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public RectangleToolStrategy(Pane drawingArea) {
        this.drawingArea = drawingArea;
        this.factory = new RectangleFactory();
    }

    @Override
    public void handlePressed(MouseEvent event) {
        startX = event.getX();
        startY = event.getY();
        endX = startX;
        endY = startY;

        previewFxShape = new javafx.scene.shape.Rectangle(startX, startY, 0, 0);;

        drawingArea.getChildren().add(previewFxShape);
    }

    @Override
    public void handleDragged(MouseEvent event) {
        if (previewFxShape != null) {

            endX = event.getX();
            endY = event.getY();

            ((javafx.scene.shape.Rectangle) previewFxShape).setX(Math.min(startX, endX));
            ((javafx.scene.shape.Rectangle) previewFxShape).setY(Math.min(startY, endY));
            ((javafx.scene.shape.Rectangle) previewFxShape).setWidth(Math.abs(endX - startX));
            ((javafx.scene.shape.Rectangle) previewFxShape).setHeight(Math.abs(endY - startY));
        }
    }

    @Override
    public void handleReleased(MouseEvent event) {
        if (previewFxShape != null) {

            endX = event.getX();
            endY = event.getY();

            drawingArea.getChildren().remove(previewFxShape);
            previewFxShape = null;

            currentModelShape = factory.createShape(startX, startY, endX, endY);

        }
    }

    public Shape getFinalShape() {
        return currentModelShape;
    }
}