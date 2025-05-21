package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.util.MyColor;

public class MyLine implements MyShape {

    private static final long serialVersionUID = 1L; // IMPORTANTE per la serializzazione

    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private MyColor borderColor;

    public MyLine(double startX, double startY, double endX, double endY, MyColor borderMyColor) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.borderColor = borderMyColor;
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

    @Override
    public void setEndPoint(double x, double y) {
        this.endX = x;
        this.endY = y;
    }


}
