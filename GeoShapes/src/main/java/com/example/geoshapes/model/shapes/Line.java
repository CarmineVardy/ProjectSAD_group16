package com.example.geoshapes.model.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Line extends Shape {

    public Line(double startX, double startY,  Color borderColor) {
        super(startX, startY, borderColor, null);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(borderColor);
        gc.setLineWidth(borderWidth);
        gc.strokeLine(startX, startY, endX, endY);
    }

    public void drawPreview(GraphicsContext gc){
        draw(gc);
    }





}
