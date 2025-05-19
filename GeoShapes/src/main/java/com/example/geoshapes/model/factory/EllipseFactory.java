package com.example.geoshapes.model.factory;

import com.example.geoshapes.model.shapes.MyEllipse;
import com.example.geoshapes.model.shapes.MyShape;
import com.example.geoshapes.model.util.MyColor;

public class EllipseFactory implements ShapeFactory {

    @Override
    public MyShape createShape(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor) {

        MyEllipse myEllipse = new MyEllipse(startX, startY, endX, endY, borderMyColor, fillMyColor);
        return myEllipse;
    }
}