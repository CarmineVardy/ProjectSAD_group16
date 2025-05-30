package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import javafx.scene.shape.Ellipse;

public class MyEllipse extends MyShape {
    private double centerX, centerY;
    private Ellipse ellipse;

    public MyEllipse(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor) {
        super(startX, startY, endX, endY, borderMyColor, fillMyColor);

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
}