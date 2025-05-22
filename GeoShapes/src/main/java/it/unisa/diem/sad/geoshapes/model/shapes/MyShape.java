package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import java.io.Serializable;

public abstract class MyShape implements Serializable {

    private static final long serialVersionUID = 1L;

    protected double startX;
    protected double startY;
    protected double endX;
    protected double endY;
    protected MyColor borderColor;
    protected MyColor fillColor;

    public MyShape(double startX, double startY, double endX, double endY, MyColor borderColor, MyColor fillColor) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.borderColor = borderColor;
        this.fillColor = fillColor;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
    }

    public MyColor getBorderColor() {
        return borderColor;
    }

    public MyColor getFillColor() {
        return fillColor;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public void setBorderColor(MyColor color) {
        this.borderColor = color;
    }

    public void setFillColor(MyColor color) {
        this.fillColor = color; // Comportamento di default, MyLine lo sovrascriverà
    }

}