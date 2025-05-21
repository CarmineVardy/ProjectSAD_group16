package it.unisa.diem.sad.geoshapes.model.factory;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;

public class RectangleFactory implements ShapeFactory {

    @Override
    public MyShape createShape(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor) {

        MyRectangle rect = new MyRectangle(startX, startY, endX, endY, borderMyColor, fillMyColor);
        return rect;
    }

}