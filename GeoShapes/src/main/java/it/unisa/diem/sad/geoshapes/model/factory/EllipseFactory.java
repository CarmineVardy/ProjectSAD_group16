package it.unisa.diem.sad.geoshapes.model.factory;

import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;

public class EllipseFactory implements ShapeFactory {

    @Override
    public MyShape createShape(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor) {

        MyEllipse myEllipse = new MyEllipse(startX, startY, endX, endY, borderMyColor, fillMyColor);
        return myEllipse;
    }
}