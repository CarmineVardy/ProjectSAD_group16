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
        // Per forme ruotate, dobbiamo:
        // 1. Calcolare il centro della forma
        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;

        // 2. Calcolare la larghezza e altezza
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        // 3. Il flip orizzontale di una forma ruotata corrisponde a
        //    cambiare il segno della rotazione e riflettere
        this.rotation = -this.rotation;

        // Mantieni le dimensioni ma inverti l'orientamento orizzontale
        // Questo dipende da come interpreti il "flip" di una forma ruotata
    }

    @Override
    public void flipVertical() {
        // Simile al flip orizzontale ma per l'asse verticale
        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;

        // Per il flip verticale, modifica la rotazione di conseguenza
        this.rotation = 180 - this.rotation;
        if (this.rotation > 180.0) {
            this.rotation -= 360.0;
        } else if (this.rotation < -180.0) {
            this.rotation += 360.0;
        }
    }

    private void flipHorizontalWithRotation() {
        // Per forme ruotate, dobbiamo:
        // 1. Calcolare il centro della forma
        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;

        // 2. Calcolare la larghezza e altezza
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        // 3. Il flip orizzontale di una forma ruotata corrisponde a
        //    cambiare il segno della rotazione e riflettere
        this.rotation = -this.rotation;

        // Mantieni le dimensioni ma inverti l'orientamento orizzontale
        // Questo dipende da come interpreti il "flip" di una forma ruotata
    }

    private void flipVerticalWithRotation() {
        // Simile al flip orizzontale ma per l'asse verticale
        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;

        // Per il flip verticale, modifica la rotazione di conseguenza
        this.rotation = 180 - this.rotation;
        if (this.rotation > 180.0) {
            this.rotation -= 360.0;
        } else if (this.rotation < -180.0) {
            this.rotation += 360.0;
        }
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

}