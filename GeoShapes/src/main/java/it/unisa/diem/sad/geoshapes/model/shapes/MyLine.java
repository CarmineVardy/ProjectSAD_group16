package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

public class MyLine extends MyShape {

    public MyLine(double startX, double startY, double endX, double endY, double rotation, MyColor borderMyColor) {
        super(startX, startY, endX, endY, rotation, borderMyColor, null);
    }

    @Override
    public MyColor getFillColor() {
        return null;
    }

    @Override
    public void setFillColor(MyColor color) {
    }

    @Override
    public String getShapeType() {
        return "Line";
    }

    @Override
    public void flipHorizontal() {
        double centerX = (getStartX() + getEndX()) / 2.0;
        double newStartX = 2 * centerX - getStartX();
        double newEndX   = 2 * centerX - getEndX();
        setStartX(newStartX);
        setEndX(newEndX);
    }

    @Override
    public void flipVertical() {
        double centerY = (getStartY() + getEndY()) / 2.0;
        double newStartY = 2 * centerY - getStartY();
        double newEndY   = 2 * centerY - getEndY();
        setStartY(newStartY);
        setEndY(newEndY);
    }

}

