package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

public class MyEllipse extends MyShape {

    public MyEllipse(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor) {
        super(startX, startY, endX, endY, borderMyColor, fillMyColor);
    }

    public double getCenterX() {
        return (this.startX + this.endX) / 2.0;
    }

    public void setCenterX(double centerX) {
        double currentRadiusX = getRadiusX();
        this.startX = centerX - currentRadiusX;
        this.endX = centerX + currentRadiusX;
    }

    public double getCenterY() {
        return (this.startY + this.endY) / 2.0;
    }

    public void setCenterY(double centerY) {
        double currentRadiusY = getRadiusY();
        this.startY = centerY - currentRadiusY;
        this.endY = centerY + currentRadiusY;
    }

    public double getRadiusX() {
        return Math.abs(this.endX - this.startX) / 2.0;
    }

    public void setRadiusX(double radiusX) {
        double currentCenterX = getCenterX();
        this.startX = currentCenterX - radiusX;
        this.endX = currentCenterX + radiusX;
    }

    public double getRadiusY() {
        return Math.abs(this.endY - this.startY) / 2.0;
    }

    public void setRadiusY(double radiusY) {
        double currentCenterY = getCenterY();
        this.startY = currentCenterY - radiusY;
        this.endY = currentCenterY + radiusY;
    }
}