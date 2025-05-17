package com.example.geoshapes.model.shapes;

public class Ellipse implements Shape {
    private double centerX;
    private double centerY;
    private double radiusX;
    private double radiusY;
    private javafx.scene.shape.Ellipse javaFXEllipse;

    public Ellipse(double centerX, double centerY, double radiusX, double radiusY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
    }

    @Override
    public javafx.scene.shape.Shape create() {
        javaFXEllipse = new javafx.scene.shape.Ellipse(centerX, centerY, Math.abs(radiusX), Math.abs(radiusY));
        return javaFXEllipse;
    }

    @Override
    public void setEndPoint(double x, double y) {
        this.radiusX = Math.abs(x - centerX);
        this.radiusY = Math.abs(y - centerY);
        if (javaFXEllipse != null) {
            javaFXEllipse.setRadiusX(radiusX);
            javaFXEllipse.setRadiusY(radiusY);
        }
    }

    @Override
    public javafx.scene.shape.Shape getJavaFXShape() {
        if (javaFXEllipse == null) {
            create();
        }
        return javaFXEllipse;
    }
}