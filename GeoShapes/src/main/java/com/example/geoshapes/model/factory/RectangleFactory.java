package com.example.geoshapes.model.factory;

import com.example.geoshapes.model.shapes.Rectangle;
import com.example.geoshapes.model.shapes.Shape;

public class RectangleFactory implements ShapeFactory {

    @Override
    public Shape createShape(double startX, double startY, double endX, double endY) {
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);
        Rectangle rect = new Rectangle(x, y, width, height);
        return rect;
    }
}
