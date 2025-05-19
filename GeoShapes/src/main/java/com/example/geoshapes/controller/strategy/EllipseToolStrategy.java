package com.example.geoshapes.controller.strategy;

import com.example.geoshapes.model.factory.EllipseFactory;
import com.example.geoshapes.model.factory.ShapeFactory;
import com.example.geoshapes.model.shapes.MyShape;
import com.example.geoshapes.model.util.MyColor;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;

public class EllipseToolStrategy implements ToolStrategy {

    private Pane drawingArea;
    private ColorPicker borderColorPicker;
    private ColorPicker fillColorPicker;

    private final ShapeFactory factory;

    private Shape previewFxShape;
    private MyShape currentModelMyShape;

    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public EllipseToolStrategy(Pane drawingArea, ColorPicker borderColorPicker, ColorPicker fillColorPicker) {
        this.drawingArea = drawingArea;
        this.borderColorPicker = borderColorPicker;
        this.fillColorPicker = fillColorPicker;
        this.factory = new EllipseFactory();
    }

    @Override
    public void handlePressed(MouseEvent event) {
        startX = event.getX();
        startY = event.getY();
        endX = startX;
        endY = startY;

        previewFxShape = new Ellipse(startX, startY, 0, 0);
        previewFxShape.setStroke(borderColorPicker.getValue());
        previewFxShape.setFill(fillColorPicker.getValue());

        drawingArea.getChildren().add(previewFxShape);
    }

    @Override
    public void handleDragged(MouseEvent event) {
        if (previewFxShape != null) {

            endX = event.getX();
            endY = event.getY();

            ((Ellipse) previewFxShape).setCenterX((startX + endX) / 2);
            ((Ellipse) previewFxShape).setCenterY((startY + endY) / 2);
            ((Ellipse) previewFxShape).setRadiusX(Math.abs(endX - startX) / 2);
            ((Ellipse) previewFxShape).setRadiusY(Math.abs(endY - startY) / 2);
        }
    }

    @Override
    public void handleReleased(MouseEvent event) {
        if (previewFxShape != null) {

            endX = event.getX();
            endY = event.getY();

            drawingArea.getChildren().remove(previewFxShape);
            previewFxShape = null;

            Color borderColor = borderColorPicker.getValue();
            Color fillColor = fillColorPicker.getValue();
            currentModelMyShape = factory.createShape(startX / drawingArea.getWidth(), startY / drawingArea.getHeight(), endX / drawingArea.getWidth(), endY / drawingArea.getHeight(), new MyColor(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), borderColor.getOpacity()), new MyColor(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), fillColor.getOpacity()));
        }
    }

    public MyShape getFinalShape() {
        return currentModelMyShape;
    }
}