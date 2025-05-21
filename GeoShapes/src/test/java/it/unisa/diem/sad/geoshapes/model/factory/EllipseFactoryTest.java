package it.unisa.diem.sad.geoshapes.model.factory;

import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EllipseFactoryTest {

    // Scopo: verificare che la factory crei correttamente un oggetto MyEllipse
    @Test
    public void testCreateEllipseShape() {
        EllipseFactory factory = new EllipseFactory();

        double startX = 5.0;
        double startY = 10.0;
        double endX = 50.0;
        double endY = 70.0;

        MyColor borderColor = new MyColor(0.5, 0.0, 0.5); // Viola
        MyColor fillColor = new MyColor(0.0, 1.0, 1.0);   // Azzurro

        MyShape shape = factory.createShape(startX, startY, endX, endY, borderColor, fillColor);

        assertNotNull(shape, "Shape must not be null!");
        assertInstanceOf(MyEllipse.class, shape, "Shape must be an instance of MyEllipse");

        MyEllipse ellipse = (MyEllipse) shape;
        assertEquals(startX, ellipse.getStartX(), "Incorrect StartX");
        assertEquals(startY, ellipse.getStartY(), "Incorrect StartY");
        assertEquals(endX, ellipse.getEndX(), "Incorrect EndX");
        assertEquals(endY, ellipse.getEndY(), "Incorrect EndY");
        assertEquals(borderColor, ellipse.getBorderColor(), "Incorrect border color");
        assertEquals(fillColor, ellipse.getFillColor(), "Incorrect fill color");
    }
}
