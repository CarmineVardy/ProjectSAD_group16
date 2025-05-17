package com.example.geoshapes.model.factory;

import com.example.geoshapes.model.shapes.Shape;

public interface ShapeFactory {

    Shape createShape(double startX, double startY, double endX, double endY);

}

