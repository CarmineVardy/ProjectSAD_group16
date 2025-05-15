package com.example.geoshapes.model.factory;

import com.example.geoshapes.model.shapes.Shape;
import javafx.scene.paint.Color;

public interface ShapeFactory {

    Shape createShape(double startX, double startY, Color borderColor, Color fillColor);

}
