package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

public class MyLine extends MyShape {

    public MyLine(double startX, double startY, double endX, double endY,double rotation, MyColor borderMyColor) {
        super(startX, startY, endX, endY,rotation, borderMyColor, null);
    }

    @Override
    public MyColor getFillColor() {
        return null; // Le linee non hanno colore di riempimento
    }

    @Override
    public void setFillColor(MyColor color) {
        // Nessuna operazione, le linee non hanno colore di riempimento
    }

    @Override
    public String getShapeType() {
        return "Line";
    }

    @Override
    public void moveBy(double dx, double dy) {
        this.setStartX(this.getStartX() + dx);
        this.setStartY(this.getStartY() + dy);
        this.setEndX(this.getEndX() + dx);
        this.setEndY(this.getEndY() + dy);
    }

    //MIRRORING
    @Override
    public void flipHorizontal() {
        System.out.println("--- DEBUG: MyLine.flipHorizontal() ---");
        System.out.println("BEFORE flipHorizontal:");
        System.out.println("  startX: " + getStartX() + ", startY: " + getStartY());
        System.out.println("  endX:   " + getEndX()   + ", endY:   " + getEndY());

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

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}

