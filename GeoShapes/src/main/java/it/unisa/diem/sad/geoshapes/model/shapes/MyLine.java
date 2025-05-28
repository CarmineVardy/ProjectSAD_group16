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

}
