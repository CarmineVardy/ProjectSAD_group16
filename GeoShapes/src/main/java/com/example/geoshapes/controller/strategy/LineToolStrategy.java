package com.example.geoshapes.controller.strategy;

import com.example.geoshapes.model.factory.LineFactory;
import com.example.geoshapes.model.factory.ShapeFactory;
import com.example.geoshapes.model.shapes.MyShape;
import com.example.geoshapes.model.util.MyColor;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class LineToolStrategy implements ToolStrategy {

    private Pane drawingArea;
    private ColorPicker borderColorPicker;
    private ColorPicker fillColorPicker;

    private ShapeFactory factory;

    private Shape previewFxShape;
    private MyShape currentModelMyShape;

    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public LineToolStrategy(Pane drawingArea, ColorPicker borderColorPicker, ColorPicker fillColorPicker) {
        this.drawingArea = drawingArea;
        this.fillColorPicker = fillColorPicker;
        this.borderColorPicker = borderColorPicker;
        this.factory = new LineFactory();
    }

    @Override
    public void handlePressed(MouseEvent event) {

        startX = event.getX();
        startY = event.getY();
        endX = startX;
        endY = startY;

        previewFxShape = new Line(startX, startY, startX, startY);
        previewFxShape.setStroke(borderColorPicker.getValue());

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

            Color borderColor = borderColorPicker.getValue();
            Color fillColor = fillColorPicker.getValue();
            currentModelMyShape = factory.createShape(startX/ drawingArea.getWidth(), startY/ drawingArea.getHeight(), endX/ drawingArea.getWidth(), endY/ drawingArea.getHeight(), new MyColor(borderColor.getRed(), borderColor.getGreen(),  borderColor.getBlue(), borderColor.getOpacity()), new MyColor(fillColor.getRed(),  fillColor.getGreen(),  fillColor.getBlue(),  fillColor.getOpacity()));

        }
    }

    public MyShape getFinalShape() {
        return currentModelMyShape;
    }
}