package com.example.geoshapes.model.factory;

import com.example.geoshapes.model.shapes.MyShape;
import com.example.geoshapes.model.shapes.MyRectangle;
import com.example.geoshapes.model.util.MyColor;

public class RectangleFactory implements ShapeFactory {

    @Override
    public MyShape createShape(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor) {

        MyRectangle rect = new MyRectangle(startX, startY, endX, endY, borderMyColor, fillMyColor);
        return rect;
    }

}