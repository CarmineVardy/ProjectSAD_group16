package com.example.geoshapes.adapter;

import com.example.geoshapes.model.shapes.MyEllipse;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape; // JavaFX MyShape

public class EllipseAdapter implements ShapeAdapter {

    private MyEllipse modelEllipse;
    private Ellipse fxEllipse;

    public EllipseAdapter(MyEllipse modelEllipse, Pane drawingArea) {
        this.modelEllipse = modelEllipse;
        this.fxEllipse = new Ellipse(
                modelEllipse.getCenterX() * drawingArea.getWidth(),
                modelEllipse.getCenterY() * drawingArea.getHeight(),
                modelEllipse.getRadiusX() * drawingArea.getWidth(),
                modelEllipse.getRadiusY() * drawingArea.getHeight()
        );
        fxEllipse.setStroke(convertToJavaFxColor(modelEllipse.getBorderColor()));
        fxEllipse.setFill(convertToJavaFxColor(modelEllipse.getFillColor()));
    }

    @Override
    public Shape getFxShape() {
        return fxEllipse;
    }

    public MyEllipse getModelEllipse() {
        return modelEllipse;
    }
}