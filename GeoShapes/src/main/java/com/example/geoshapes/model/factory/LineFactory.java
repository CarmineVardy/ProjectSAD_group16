package com.example.geoshapes.model.factory;

import com.example.geoshapes.model.shapes.Line;
import com.example.geoshapes.model.shapes.Shape;
import javafx.scene.paint.Color;

public class LineFactory implements ShapeFactory {


    @Override
    public Shape createShape(double startX, double startY, Color borderColor, Color fillColor) {
        return new Line(startX, startY, borderColor);
    }
}
