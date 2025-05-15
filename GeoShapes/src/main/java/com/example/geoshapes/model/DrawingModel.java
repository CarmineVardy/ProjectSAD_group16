package com.example.geoshapes.model;

import com.example.geoshapes.model.factory.ShapeFactory;
import com.example.geoshapes.model.shapes.Shape;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class DrawingModel {
    
    private List<Shape> shapes;
    private Shape currentShape;
    private ShapeFactory currentFactory;
    private Color borderColor;
    private Color fillColor;

    public DrawingModel() {
        this.shapes = new ArrayList<>();
        this.borderColor = Color.BLACK;
        this.fillColor = Color.TRANSPARENT;
    }

    public void setCurrentFactory(ShapeFactory currentFactory) {
        this.currentFactory = currentFactory;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public void startDrawing(double x, double y) {
        if(currentFactory != null)
            currentShape = currentFactory. createShape(x,y,borderColor,fillColor);
    }

    public void updateDrawing(double x, double y) {
        if(currentShape != null){
            currentShape.setEndPoint(x,y);
        }
    }

    public void endDrawing(double x, double y) {
        currentShape.setEndPoint(x,y);
        shapes.add(currentShape);
        currentShape = null;
    }

    public void drawShapes(GraphicsContext gc){

        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        for (Shape shape : shapes) {
            shape.draw(gc);
        }

        if(currentShape != null){
            currentShape.drawPreview(gc);
        }
    }


}
