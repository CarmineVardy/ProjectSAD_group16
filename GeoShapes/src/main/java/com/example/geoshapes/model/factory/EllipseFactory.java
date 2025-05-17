package com.example.geoshapes.model.factory;

import com.example.geoshapes.model.shapes.Ellipse;
import com.example.geoshapes.model.shapes.Shape;
import javafx.scene.paint.Color;

public class EllipseFactory implements ShapeFactory {

    @Override
    public Shape createShape(double startX, double startY, Color borderColor, Color fillColor) {
        return new Ellipse(startX, startY, borderColor, fillColor);
    }
}
