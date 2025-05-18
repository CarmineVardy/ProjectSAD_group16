package com.example.geoshapes.model.factory;

import com.example.geoshapes.model.shapes.Ellipse;
import com.example.geoshapes.model.shapes.Shape;

public class EllipseFactory implements ShapeFactory {

    @Override
    public Shape createShape(double startX, double startY, double endX, double endY) {

        Ellipse ellipse = new Ellipse(startX, startY, endX, endY);
        return ellipse;
    }
}