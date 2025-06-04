package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

public class MyRectangle extends MyShape {


    public MyRectangle(double startX, double startY, double endX, double endY, double rotation, MyColor borderMyColor, MyColor fillMyColor) {
        super(startX, startY, endX, endY, rotation, borderMyColor, fillMyColor);
    }

    @Override
    public String getShapeType() {
        return "Rectangle";
    }

    @Override
    public void flipHorizontal() {
        double centerX = (getStartX() + getEndX()) / 2.0;
        double newStartX = centerX - (getEndX() - centerX);
        double newEndX = centerX - (getStartX() - centerX);

        setStartX(Math.min(newStartX, newEndX));
        setEndX(Math.max(newStartX, newEndX));

        this.rotation = -this.rotation;
        this.rotation = this.rotation % 360;
        if (this.rotation > 180) {
            this.rotation -= 360;
        } else if (this.rotation <= -180) {
            this.rotation += 360;
        }
    }

    @Override
    public void flipVertical() {
        double centerY = (getStartY() + getEndY()) / 2.0;

        double newStartY = centerY - (getEndY() - centerY);
        double newEndY = centerY - (getStartY() - centerY);

        setStartY(Math.min(newStartY, newEndY));
        setEndY(Math.max(newStartY, newEndY));

        this.rotation = 180 - this.rotation;
        if (this.rotation > 180.0) {
            this.rotation -= 360.0;
        } else if (this.rotation <= -180.0) {
            this.rotation += 360.0;
        }
    }



}