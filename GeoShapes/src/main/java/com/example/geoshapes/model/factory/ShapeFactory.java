package com.example.geoshapes.model.factory;

import com.example.geoshapes.model.shapes.MyShape;
import com.example.geoshapes.model.util.MyColor;

public interface ShapeFactory {

    MyShape createShape(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor);

}

