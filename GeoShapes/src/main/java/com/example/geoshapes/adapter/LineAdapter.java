package com.example.geoshapes.adapter;

import com.example.geoshapes.model.shapes.MyLine;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class LineAdapter implements ShapeAdapter {

    private MyLine modelLine;
    private Line fxLine;

    public LineAdapter(MyLine modelLine, Pane drawingArea) {
        this.modelLine = modelLine;

        this.fxLine = new Line(
                modelLine.getStartX() * drawingArea.getWidth(),
                modelLine.getStartY() * drawingArea.getHeight(),
                modelLine.getEndX() * drawingArea.getWidth(),
                modelLine.getEndY() * drawingArea.getHeight()
        );
        fxLine.setStroke(convertToJavaFxColor(modelLine.getBorderColor()));
    }

    @Override
    public Shape getFxShape() {
        return fxLine;
    }

    public MyLine getModelLine() {
        return modelLine;
    }
}