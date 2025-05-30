package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

public class MyLine extends MyShape {

    public MyLine(double startX, double startY, double endX, double endY, MyColor borderMyColor) {
        super(startX, startY, endX, endY, borderMyColor, null);
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
        double centerX = (getStartX() + getEndX()) / 2.0;

        double newStartX = 2 * centerX - getStartX();
        double newEndX   = 2 * centerX - getEndX();

        setStartX(newStartX);
        setEndX(newEndX);
        // Y invariato
    }

    @Override
    public void flipVertical() {
        double centerY = (getStartY() + getEndY()) / 2.0;

        double newStartY = 2 * centerY - getStartY();
        double newEndY   = 2 * centerY - getEndY();

        setStartY(newStartY);
        setEndY(newEndY);
        // X invariato
    }
}
