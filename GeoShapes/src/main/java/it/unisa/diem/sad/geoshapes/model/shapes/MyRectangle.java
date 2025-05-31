package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

public class MyRectangle extends MyShape {

    private double x, y, width, height,rotation;

    public MyRectangle(double startX, double startY, double endX, double endY, double rotation,MyColor borderMyColor, MyColor fillMyColor) {
        super(startX, startY, endX, endY, rotation,borderMyColor, fillMyColor);

        this.rotation = rotation;
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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    //MIRRORING
    @Override
    public void flipHorizontal() {
        double centerX = (getStartX() + getEndX()) / 2;
        double newStartX = 2 * centerX - getEndX();
        double newEndX = 2 * centerX - getStartX();
        setStartX(Math.min(newStartX, newEndX));
        setEndX(Math.max(newStartX, newEndX));
    }

    @Override
    public void flipVertical() {
        double centerY = (getStartY() + getEndY()) / 2;
        double newStartY = 2 * centerY - getEndY();
        double newEndY = 2 * centerY - getStartY();
        setStartY(Math.min(newStartY, newEndY));
        setEndY(Math.max(newStartY, newEndY));
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

}