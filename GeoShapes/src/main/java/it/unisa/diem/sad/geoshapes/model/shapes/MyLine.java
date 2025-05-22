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

     public void setStartX(double startX) { this.startX = startX; }
     public void setStartY(double startY) { this.startY = startY; }
     public void setEndX(double endX) { this.endX = endX; }
     public void setEndY(double endY) { this.endY = endY; }
}