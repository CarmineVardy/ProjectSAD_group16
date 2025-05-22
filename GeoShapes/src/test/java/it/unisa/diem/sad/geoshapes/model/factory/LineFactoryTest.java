/*
package it.unisa.diem.sad.geoshapes.model.factory;

import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LineFactoryTest {

    // Scopo: verificare che la factory crei correttamente un oggetto MyLine
    @Test
    public void testCreateLineShape() {
        LineFactory factory = new LineFactory();

        double startX = 10.0;
        double startY = 20.0;
        double endX = 30.0;
        double endY = 40.0;

        MyColor borderColor = new MyColor(0.0, 0.0, 1.0); // Blu
        MyColor fillColor = new MyColor(1.0, 0.0, 0.0);   // Rosso (ignorato dalla linea)

        MyShape shape = factory.createShape(startX, startY, endX, endY, borderColor, fillColor);

        assertNotNull(shape, "Shape must not be null!");
        assertInstanceOf(MyLine.class, shape, "Shape must be an instance of MyLine");

        MyLine line = (MyLine) shape;
        assertEquals(startX, line.getStartX(), "Incorrect StartX");
        assertEquals(startY, line.getStartY(), "Incorrect StartY");
        assertEquals(endX, line.getEndX(), "Incorrect EndX");
        assertEquals(endY, line.getEndY(), "Incorrect EndY");
        assertEquals(borderColor, line.getBorderColor(), "Incorrect border color");
    }
}
*/