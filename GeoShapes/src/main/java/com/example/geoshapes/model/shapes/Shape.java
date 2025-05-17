package com.example.geoshapes.model.shapes;

public interface Shape {
    javafx.scene.shape.Shape create();
    void setEndPoint(double x, double y);
    javafx.scene.shape.Shape getJavaFXShape();
}