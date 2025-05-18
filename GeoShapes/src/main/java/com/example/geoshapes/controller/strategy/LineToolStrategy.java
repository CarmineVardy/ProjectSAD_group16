package com.example.geoshapes.controller.strategy;

import com.example.geoshapes.model.factory.LineFactory;
import com.example.geoshapes.model.factory.ShapeFactory;
import com.example.geoshapes.model.shapes.Shape;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class LineToolStrategy implements ToolStrategy {

    private Pane drawingArea;
    private ShapeFactory factory;
    private javafx.scene.shape.Shape previewFxShape;
    private Shape currentModelShape;
    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public LineToolStrategy(Pane drawingArea) {
        this.drawingArea = drawingArea;
        this.factory = new LineFactory();
    }

    @Override
    public void handlePressed(MouseEvent event) {

        startX = event.getX();
        startY = event.getY();
        endX = startX;
        endY = startY;

        previewFxShape = new javafx.scene.shape.Line(startX, startY, startX, startY);

        drawingArea.getChildren().add(previewFxShape);
    }

    @Override
    public void handleDragged(MouseEvent event) {
        if (previewFxShape != null) {

            endX = event.getX();
            endY = event.getY();

            ((javafx.scene.shape.Line) previewFxShape).setEndX(endX);
            ((javafx.scene.shape.Line) previewFxShape).setEndY(endY);
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