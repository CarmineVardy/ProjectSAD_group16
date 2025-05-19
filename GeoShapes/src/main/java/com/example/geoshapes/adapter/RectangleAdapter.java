package com.example.geoshapes.adapter;

import com.example.geoshapes.model.shapes.MyRectangle;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class RectangleAdapter implements ShapeAdapter {

    private MyRectangle modelRectangle; // Adaptee
    private Rectangle fxRectangle; // Cache

    public RectangleAdapter(MyRectangle modelRectangle, Pane drawingArea) {
        this.modelRectangle = modelRectangle;
        this.fxRectangle = new Rectangle(
                modelRectangle.getTopLeftX() * drawingArea.getWidth(),
                modelRectangle.getTopLeftY() * drawingArea.getHeight(),
                modelRectangle.getWidth() * drawingArea.getWidth(),
                modelRectangle.getHeight() * drawingArea.getHeight()
        );

        fxRectangle.setStroke(convertToJavaFxColor(modelRectangle.getBorderColor()));
        fxRectangle.setFill(convertToJavaFxColor(modelRectangle.getFillColor()));
    }

    @Override
    public Shape getFxShape() {
        return fxRectangle;
    }

    public MyRectangle getModelRectangle() {
        return modelRectangle;
    }
}