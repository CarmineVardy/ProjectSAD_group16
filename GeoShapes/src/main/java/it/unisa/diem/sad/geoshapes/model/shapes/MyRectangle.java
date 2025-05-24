package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

public class MyRectangle extends MyShape {

    public MyRectangle(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor) {
        super(startX, startY, endX, endY, borderMyColor, fillMyColor);
    }

    public double getX() {
        return Math.min(this.startX, this.endX);
    }

    // Aggiunto setter per X
    public void setX(double x) {
        double currentWidth = getWidth();
        this.startX = x;
        this.endX = x + currentWidth;
    }

    public double getY() {
        return Math.min(this.startY, this.endY);
    }

    // Aggiunto setter per Y
    public void setY(double y) {
        double currentHeight = getHeight();
        this.startY = y;
        this.endY = y + currentHeight;
    }

    public double getWidth() {
        return Math.abs(this.endX - this.startX);
    }

    // Aggiunto setter per Width
    public void setWidth(double width) {
        double currentX = getX();
        // Presupponiamo che la larghezza si estenda a destra (endX > startX)
        this.startX = currentX;
        this.endX = currentX + width;
    }

    public double getHeight() {
        return Math.abs(this.endY - this.startY);
    }

    // Aggiunto setter per Height
    public void setHeight(double height) {
        double currentY = getY();
        // Presupponiamo che l'altezza si estenda verso il basso (endY > startY)
        this.startY = currentY;
        this.endY = currentY + height;
    }

    @Override
    public String getShapeType() {
        return "Rectangle";
    }
}