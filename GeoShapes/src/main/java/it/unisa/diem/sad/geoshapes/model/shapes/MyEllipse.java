package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import javafx.scene.shape.Ellipse;

public class MyEllipse extends MyShape {
    private double centerX, centerY;
    private Ellipse ellipse;

    public MyEllipse(double startX, double startY, double endX, double endY, double rotation, MyColor borderMyColor, MyColor fillMyColor) {
        super(startX, startY, endX, endY,rotation, borderMyColor, fillMyColor);

        this.centerX = (startX + endX) / 2;
        this.centerY = (startY + endY) / 2;
        double radiusX = Math.abs(endX - startX) / 2;
        double radiusY = Math.abs(endY - startY) / 2;

        this.ellipse = new Ellipse(centerX, centerY, radiusX, radiusY);
        this.ellipse.setStroke(borderMyColor.toJavaFXColor());
        this.ellipse.setFill(fillMyColor.toJavaFXColor());
    }

    @Override
    public String getShapeType() {
        return "Ellipse";
    }

    @Override
    public void moveBy(double dx, double dy) {
        ellipse.setCenterX(ellipse.getCenterX() + dx);
        ellipse.setCenterY(ellipse.getCenterY() + dy);
    }

    public Ellipse getEllipse() {
        return ellipse;
    }

    public void setEllipse(Ellipse ellipse) {
        this.ellipse = ellipse;
    }

    @Override
    public void flipHorizontal() {
        System.out.println("ELLIPSE FLIP HORIZONTAL - Before: startX=" + startX + ", endX=" + endX + ", rotation=" + rotation);

        // Calcola il centro dell'ellisse
        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;

        // Riflette le coordinate rispetto al centro orizzontale
        double newStartX = 2 * centerX - startX;
        double newEndX = 2 * centerX - endX;

        this.startX = newStartX;
        this.endX = newEndX;

        // Gestisce la rotazione: per l'ellisse il flip orizzontale inverte l'angolo
        if (Math.abs(rotation) > 1e-6) {
            this.rotation = -rotation;
            // Normalizza l'angolo tra -180 e 180
            while (this.rotation > 180.0) this.rotation -= 360.0;
            while (this.rotation < -180.0) this.rotation += 360.0;
        }

        System.out.println("ELLIPSE FLIP HORIZONTAL - After: startX=" + startX + ", endX=" + endX + ", rotation=" + rotation);
    }

    @Override
    public void flipVertical() {
        System.out.println("ELLIPSE FLIP VERTICAL - Before: startY=" + startY + ", endY=" + endY + ", rotation=" + rotation);

        // Calcola il centro dell'ellisse
        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;

        // Riflette le coordinate rispetto al centro verticale
        double newStartY = 2 * centerY - startY;
        double newEndY = 2 * centerY - endY;

        this.startY = newStartY;
        this.endY = newEndY;

        // Gestisce la rotazione: per l'ellisse il flip verticale modifica l'angolo
        if (Math.abs(rotation) > 1e-6) {
            this.rotation = 180.0 - rotation;
            // Normalizza l'angolo tra -180 e 180
            while (this.rotation > 180.0) this.rotation -= 360.0;
            while (this.rotation < -180.0) this.rotation += 360.0;
        }

        System.out.println("ELLIPSE FLIP VERTICAL - After: startY=" + startY + ", endY=" + endY + ", rotation=" + rotation);
    }


    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}