package com.example.geoshapes.model.factory;

import com.example.geoshapes.model.shapes.Rectangle;
import com.example.geoshapes.model.shapes.Shape;

public class RectangleFactory implements ShapeFactory {

    @Override
    public Shape createShape(double startX, double startY, double endX, double endY) {

        Rectangle rect = new Rectangle(startX, startY, endX, endY);
        return rect;
    }

}