package it.unisa.diem.sad.geoshapes.model.shapes;

import java.io.Serializable;

public interface MyShape extends Serializable {

    void setEndPoint(double x, double y);

    void setStartPoint(double x, double y);
}