package com.example.geoshapes.model.factory;

import com.example.geoshapes.model.shapes.MyLine;
import com.example.geoshapes.model.shapes.MyShape;
import com.example.geoshapes.model.util.MyColor;

public class LineFactory implements ShapeFactory {

    @Override
    public MyShape createShape(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor) {
        MyLine myLine = new MyLine(startX, startY, endX, endY, borderMyColor);
        return myLine;
    }
}