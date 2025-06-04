package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

public class MyEllipse extends MyShape {

    public MyEllipse(double startX, double startY, double endX, double endY, double rotation, MyColor borderMyColor, MyColor fillMyColor) {
        super(startX, startY, endX, endY, rotation, borderMyColor, fillMyColor);
    }

    @Override
    public String getShapeType() {
        return "Ellipse";
    }

    @Override
    public void flipHorizontal() {
        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;

        double newStartX = 2 * centerX - startX;
        double newEndX = 2 * centerX - endX;

        this.startX = newStartX;
        this.endX = newEndX;

        if (Math.abs(rotation) > 1e-6) {
            this.rotation = -rotation;
            while (this.rotation > 180.0) this.rotation -= 360.0;
            while (this.rotation < -180.0) this.rotation += 360.0;
        }

    }

    @Override
    public void flipVertical() {
        System.out.println("ELLIPSE FLIP VERTICAL - Before: startY=" + startY + ", endY=" + endY + ", rotation=" + rotation);

        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;

        double newStartY = 2 * centerY - startY;
        double newEndY = 2 * centerY - endY;

        this.startY = newStartY;
        this.endY = newEndY;

        if (Math.abs(rotation) > 1e-6) {
            this.rotation = 180.0 - rotation;
            while (this.rotation > 180.0) this.rotation -= 360.0;
            while (this.rotation < -180.0) this.rotation += 360.0;
        }

    }



}