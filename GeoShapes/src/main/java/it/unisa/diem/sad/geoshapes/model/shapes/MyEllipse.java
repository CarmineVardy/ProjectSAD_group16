package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import javafx.scene.shape.Ellipse;

public class MyEllipse extends MyShape {

    private Ellipse ellipse;

    public MyEllipse(double startX, double startY, double endX, double endY, MyColor borderMyColor, MyColor fillMyColor) {
        super(startX, startY, endX, endY, borderMyColor, fillMyColor);
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
}