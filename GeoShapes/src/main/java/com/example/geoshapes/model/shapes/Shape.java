package com.example.geoshapes.model.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Shape {

    protected double startX;
    protected double startY;
    protected double endX;
    protected double endY;
    protected Color borderColor;
    protected Color fillColor;
    protected double borderWidth;

    public Shape(double startX, double startY, Color borderColor, Color fillColor) {
        this.startX = startX;
        this.startY = startY;
        this.borderColor = borderColor;
        this.fillColor = fillColor;
        this.borderWidth = 1;
    }

    public void setEndPoint(double x, double y){
        this.endX = x;
        this.endY = y;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public abstract void draw(GraphicsContext gc);
    public abstract void drawPreview(GraphicsContext gc);

}
