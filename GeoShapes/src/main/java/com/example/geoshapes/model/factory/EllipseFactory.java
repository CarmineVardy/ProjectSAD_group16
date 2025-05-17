package com.example.geoshapes.model.factory;

import com.example.geoshapes.model.shapes.Ellipse;
import com.example.geoshapes.model.shapes.Shape;

public class EllipseFactory implements ShapeFactory {

    @Override
    public Shape createShape(double startX, double startY, double endX, double endY) {
        double centerX = (startX + endX) / 2;
        double centerY = (startY + endY) / 2;
        double radiusX = Math.abs(endX - startX) / 2;
        double radiusY = Math.abs(endY - startY) / 2;
        Ellipse ellipse = new Ellipse(centerX, centerY, radiusX, radiusY);
        return ellipse;
    }
}