package com.example.geoshapes.model.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ellipse extends Shape {
    private double x, y, width, height;

    public Ellipse(double startX, double startY, Color borderColor, Color fillColor) {
        super(startX, startY, borderColor, fillColor);
        this.startX = startX;
        this.startY = startY;
        this.x = startX;
        this.y = startY;
        this.width = 0;
        this.height = 0;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(borderColor);
        gc.setFill(fillColor);
        gc.strokeOval(x, y, width, height);
        gc.fillOval(x, y, width, height);
    }

    @Override
    public void drawPreview(GraphicsContext gc) {
        gc.setStroke(borderColor);
        gc.setFill(fillColor);
        gc.strokeOval(x, y, width, height);
        gc.fillOval(x, y, width, height);
    }

    @Override
    public void setEndPoint(double endX, double endY) {
        this.x = Math.min(startX, endX);
        this.y = Math.min(startY, endY);
        this.width = Math.abs(endX - startX);
        this.height = Math.abs(endY - startY);
    }
}
