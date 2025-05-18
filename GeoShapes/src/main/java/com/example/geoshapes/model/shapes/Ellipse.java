package com.example.geoshapes.model.shapes;

public class Ellipse implements Shape {

    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public Ellipse(double startX, double startY, double endX, double endY) {
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

    private double getCenterX() {
        return (startX + endX) / 2;
    }

    private double getCenterY() {
        return (startY + endY) / 2;
    }

    private double getRadiusX() {
        return Math.abs(endX - startX) / 2;
    }

    private double getRadiusY() {
        return Math.abs(endY - startY) / 2;
    }

    @Override
    public javafx.scene.shape.Shape getJavaFXShape() {
        return new javafx.scene.shape.Ellipse(getCenterX(), getCenterY(), getRadiusX(), getRadiusY());
    }
}