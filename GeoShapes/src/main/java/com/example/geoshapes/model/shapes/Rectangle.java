package com.example.geoshapes.model.shapes;

public class Rectangle implements Shape {

    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public Rectangle(double startX, double startY, double endX, double endY) {
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

    private double getTopLeftX() {
        return Math.min(startX, endX);
    }

    private double getTopLeftY() {
        return Math.min(startY, endY);
    }

    private double getWidth() {
        return Math.abs(endX - startX);
    }

    private double getHeight() {
        return Math.abs(endY - startY);
    }

    @Override
    public javafx.scene.shape.Shape getJavaFXShape() {
        return new javafx.scene.shape.Rectangle(getTopLeftX(), getTopLeftY(), getWidth(), getHeight());
    }
}