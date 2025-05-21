package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.util.MyColor;

public class MyRectangle implements MyShape {

    private static final long serialVersionUID = 1L; // IMPORTANTE per la serializzazione

    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private MyColor borderMyColor;
    private MyColor fillMyColor;

    public MyRectangle(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.borderMyColor = borderMyColor;
        this.fillMyColor = fillMyColor;
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

    public double getTopLeftX() {
        return Math.min(startX, endX);
    }

    public double getTopLeftY() {
        return Math.min(startY, endY);
    }

    public double getWidth() {
        return Math.abs(endX - startX);
    }

    public double getHeight() {
        return Math.abs(endY - startY);
    }

    public MyColor getBorderColor() {
        return borderMyColor;
    }

    public MyColor getFillColor() {
        return fillMyColor;
    }


}