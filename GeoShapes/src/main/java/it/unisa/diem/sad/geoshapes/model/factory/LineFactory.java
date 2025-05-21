package it.unisa.diem.sad.geoshapes.model.factory;

import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;

public class LineFactory implements ShapeFactory {

    @Override
    public MyShape createShape(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor) {
        MyLine myLine = new MyLine(startX, startY, endX, endY, borderMyColor);
        return myLine;
    }
}