package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

public class MyRectangle extends MyShape {

    public MyRectangle(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor) {
        super(startX, startY, endX, endY, borderMyColor, fillMyColor);
    }

    @Override
    public String getShapeType() {
        return "Rectangle";
    }

    @Override
    public void moveBy(double dx, double dy) {
        setStartX(getStartX() + dx);
        setEndX(getEndX() + dx);
        setStartY(getStartY() + dy);
        setEndY(getEndY() + dy);
    }
}