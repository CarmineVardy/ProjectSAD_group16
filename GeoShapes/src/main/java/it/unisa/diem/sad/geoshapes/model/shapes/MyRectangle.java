package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

public class MyRectangle extends MyShape {

    // La rotazione è l'unica proprietà specifica di MyRectangle,
    // dato che startX, startY, endX, endY sono in MyShape.
    // Le proprietà x, y, width, height sono state rimosse perché ridondanti.
    private double rotation;

    public MyRectangle(double startX, double startY, double endX, double endY, double rotation, MyColor borderMyColor, MyColor fillMyColor) {
        super(startX, startY, endX, endY, rotation, borderMyColor, fillMyColor);
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

    @Override
    public void flipHorizontal() {
        // Calcola il centro della bounding box attuale (indipendentemente dalla rotazione)
        double centerX = (getStartX() + getEndX()) / 2.0;

        // Rifletti le coordinate X rispetto al centro
        double newStartX = centerX - (getEndX() - centerX); // Il nuovo startX è il vecchio endX riflesso
        double newEndX = centerX - (getStartX() - centerX); // Il nuovo endX è il vecchio startX riflesso

        // Imposta le nuove coordinate (normalizzando per mantenere startX <= endX)
        setStartX(Math.min(newStartX, newEndX));
        setEndX(Math.max(newStartX, newEndX));

        // Inverti il segno della rotazione per riflettere l'orientamento
        this.rotation = -this.rotation;
        // Normalizza l'angolo per mantenerlo in un intervallo ragionevole, ad es. tra -360 e 360
        this.rotation = this.rotation % 360;
        if (this.rotation > 180) {
            this.rotation -= 360;
        } else if (this.rotation <= -180) {
            this.rotation += 360;
        }
    }

    @Override
    public void flipVertical() {
        System.out.println("Rectangle FLIP VERTICAL - Before: startX=" + startX + ", endX=" + endX + ", rotation=" + rotation);
        double centerY = (getStartY() + getEndY()) / 2.0;

        double newStartY = centerY - (getEndY() - centerY); // Il nuovo startY è il vecchio endY riflesso
        double newEndY = centerY - (getStartY() - centerY); // Il nuovo endY è il vecchio startY riflesso

        setStartY(Math.min(newStartY, newEndY));
        setEndY(Math.max(newStartY, newEndY));

        this.rotation = 180 - this.rotation;
        if (this.rotation > 180.0) {
            this.rotation -= 360.0;
        } else if (this.rotation <= -180.0) {
            this.rotation += 360.0;
        }
    }


    @Override
    public MyShape clone() {
        return new MyRectangle(getStartX(), getStartY(), getEndX(), getEndY(),
                this.rotation, this.borderColor,this.fillColor);

    }


    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}