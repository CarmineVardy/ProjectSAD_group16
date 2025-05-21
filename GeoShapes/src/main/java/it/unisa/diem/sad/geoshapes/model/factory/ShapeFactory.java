package it.unisa.diem.sad.geoshapes.model.factory;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;

public interface ShapeFactory {

    MyShape createShape(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor);

}

