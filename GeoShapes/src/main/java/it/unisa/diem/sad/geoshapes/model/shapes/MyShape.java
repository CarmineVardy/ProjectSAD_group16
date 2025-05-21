package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.util.MyColor;

import java.io.Serializable;

public interface MyShape extends Serializable {

    void setEndPoint(double x, double y);

    MyColor getBorderColor();

    MyColor getFillColor();

    void setBorderColor(MyColor color);

    void setFillColor(MyColor color);

}