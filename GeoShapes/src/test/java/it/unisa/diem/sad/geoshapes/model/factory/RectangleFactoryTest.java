package it.unisa.diem.sad.geoshapes.model.factory;

import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RectangleFactoryTest {

    // Scopo: verificare che la factory crei correttamente un oggetto MyRectangle
    @Test
    public void testCreateRectangleShape() {
        RectangleFactory factory = new RectangleFactory();

        double startX = 15.0;
        double startY = 25.0;
        double endX = 45.0;
        double endY = 65.0;

        MyColor borderColor = new MyColor(0.0, 1.0, 0.0); // Verde
        MyColor fillColor = new MyColor(1.0, 1.0, 0.0);   // Giallo

        MyShape shape = factory.createShape(startX, startY, endX, endY, borderColor, fillColor);

        assertNotNull(shape, "Shape must not be null!");
        assertInstanceOf(MyRectangle.class, shape, "Shape must be an instance of MyRectangle");

        MyRectangle rectangle = (MyRectangle) shape;
        assertEquals(startX, rectangle.getStartX(), "Incorrect StartX");
        assertEquals(startY, rectangle.getStartY(), "Incorrect StartY");
        assertEquals(endX, rectangle.getEndX(), "Incorrect EndX");
        assertEquals(endY, rectangle.getEndY(), "Incorrect EndY");
        assertEquals(borderColor, rectangle.getBorderColor(), "Incorrect border color");
        assertEquals(fillColor, rectangle.getFillColor(), "Incorrect fill color");
    }
}

