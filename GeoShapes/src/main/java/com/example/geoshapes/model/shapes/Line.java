package com.example.geoshapes.model.shapes;

public class Line implements Shape {

    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public Line(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public void setEndPoint(double x, double y) {
        this.endX = x;
        this.endY = y;
    }

    @Override
    public javafx.scene.shape.Shape getJavaFXShape() {
        return new javafx.scene.shape.Line(startX, startY, endX, endY);
    }

}
