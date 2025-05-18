package com.example.geoshapes.controller.strategy;

import com.example.geoshapes.model.factory.EllipseFactory;
import com.example.geoshapes.model.factory.ShapeFactory;
import com.example.geoshapes.model.shapes.Shape;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Ellipse;

public class EllipseToolStrategy implements ToolStrategy {

    private Pane drawingArea;
    private final ShapeFactory factory;
    private javafx.scene.shape.Shape previewFxShape;
    private Shape currentModelShape;
    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public EllipseToolStrategy(Pane drawingArea) {
        this.drawingArea = drawingArea;
        this.factory = new EllipseFactory();
    }

    @Override
    public void handlePressed(MouseEvent event) {
        startX = event.getX();
        startY = event.getY();
        endX = startX;
        endY = startY;

        previewFxShape = new javafx.scene.shape.Ellipse(startX, startY, 0, 0);

        drawingArea.getChildren().add(previewFxShape);
    }

    @Override
    public void handleDragged(MouseEvent event) {
        if (previewFxShape != null) {

            endX = event.getX();
            endY = event.getY();

            ((javafx.scene.shape.Ellipse) previewFxShape).setCenterX((startX + endX) / 2);
            ((javafx.scene.shape.Ellipse) previewFxShape).setCenterY((startY + endY) / 2);
            ((javafx.scene.shape.Ellipse) previewFxShape).setRadiusX(Math.abs(endX - startX) / 2);
            ((javafx.scene.shape.Ellipse) previewFxShape).setRadiusY(Math.abs(endY - startY) / 2);
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