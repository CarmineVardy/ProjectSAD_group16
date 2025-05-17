package com.example.geoshapes.model.factory;

import com.example.geoshapes.model.shapes.Line;
import com.example.geoshapes.model.shapes.Shape;

public class LineFactory implements ShapeFactory {

    @Override
    public Shape createShape(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        return line;
    }
}