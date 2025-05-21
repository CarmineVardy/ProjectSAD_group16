package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.util.MyColor;

public class MyEllipse implements MyShape {

    private static final long serialVersionUID = 1L; // IMPORTANTE per la serializzazione

    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private MyColor borderColor;
    private MyColor fillColor;

    public MyEllipse(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.borderColor = borderMyColor;
        this.fillColor = fillMyColor;
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


    @Override
    public void setEndPoint(double x, double y) {
        this.endX = x;
        this.endY = y;
    }

    public double getCenterX() {
        return (startX + endX) / 2;
    }

    public double getCenterY() {
        return (startY + endY) / 2;
    }

    public double getRadiusX() {
        return Math.abs(endX - startX) / 2;
    }

    public double getRadiusY() {
        return Math.abs(endY - startY) / 2;
    }

    public MyColor getBorderColor() {
        return borderColor;
    }

    public MyColor getFillColor() {
        return fillColor;
    }

    @Override
    public void setStartPoint(double x, double y) {
        this.startX = x;
        this.startY = y;
    }

    @Override
    public MyShape clone() {
        return new MyEllipse(startX, startY, endX, endY, borderColor, fillColor);
    }

}