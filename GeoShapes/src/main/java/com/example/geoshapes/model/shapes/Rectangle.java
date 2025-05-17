package com.example.geoshapes.model.shapes;

public class Rectangle implements Shape {
    private double startX;
    private double startY;
    private double width;
    private double height;
    private javafx.scene.shape.Rectangle javaFXRectangle;

    public Rectangle(double startX, double startY, double width, double height) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
    }

    @Override
    public javafx.scene.shape.Shape create() {
        javaFXRectangle = new javafx.scene.shape.Rectangle(getTopLeftX(), getTopLeftY(), Math.abs(width), Math.abs(height));
        return javaFXRectangle;
    }

    @Override
    public void setEndPoint(double x, double y) {
        this.width = x - startX;
        this.height = y - startY;
        if (javaFXRectangle != null) {
            javaFXRectangle.setX(getTopLeftX());
            javaFXRectangle.setY(getTopLeftY());
            javaFXRectangle.setWidth(Math.abs(width));
            javaFXRectangle.setHeight(Math.abs(height));
        }
    }

    @Override
    public javafx.scene.shape.Shape getJavaFXShape() {
        if (javaFXRectangle == null) {
            create();
        }
        return javaFXRectangle;
    }

    private double getTopLeftX() {
        return width < 0 ? startX + width : startX;
    }

    private double getTopLeftY() {
        return height < 0 ? startY + height : startY;
    }
}
