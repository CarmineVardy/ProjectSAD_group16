package com.example.geoshapes.model.shapes;

public class Line implements Shape {
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private javafx.scene.shape.Line javaFXLine;

    public Line(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public javafx.scene.shape.Shape create() {
        javaFXLine = new javafx.scene.shape.Line(startX, startY, endX, endY);
        return javaFXLine;
    }

    @Override
    public void setEndPoint(double x, double y) {
        this.endX = x;
        this.endY = y;
        if (javaFXLine != null) {
            javaFXLine.setEndX(endX);
            javaFXLine.setEndY(endY);
        }
    }

    @Override
    public javafx.scene.shape.Shape getJavaFXShape() {
        if (javaFXLine == null) {
            create();
        }
        return javaFXLine;
    }
}
