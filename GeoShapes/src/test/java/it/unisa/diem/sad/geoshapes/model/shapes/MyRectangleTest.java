package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyRectangleTest {

    @Test   //verifica che i colori passati al costruttore siano memorizzati correttamente
    public void testColorChangePersists() {
        MyColor border = new MyColor(1.0, 0.0, 0.0); // Rosso
        MyColor fill = new MyColor(0.0, 0.0, 1.0);   // Blu

        MyRectangle rect = new MyRectangle(10, 10, 100, 100, border, fill);

        assertEquals(border, rect.getBorderColor(), "Border color must match the assigned red");
        assertEquals(fill, rect.getFillColor(), "Fill color must match the assigned blue");
    }

    /*@Test
    public void testSetEndPointChangesSize() {
        MyRectangle rect = new MyRectangle(0, 0, 50, 50,
                new MyColor(0.0, 0.0, 0.0), new MyColor(1.0, 1.0, 1.0));

        //cambio le coordinate endX e endY
        rect.setEndPoint(100, 80);

        assertEquals(100, rect.getEndX(), 0.001);
        assertEquals(80, rect.getEndY(), 0.001);
    }*/

    @Test
    public void testTransparentFillColor() {
        MyColor border = new MyColor(0.0, 0.0, 0.0, 1.0);         // Nero opaco
        MyColor transparentFill = new MyColor(1.0, 0.0, 0.0, 0.0); // Trasparente

        MyRectangle rect = new MyRectangle(0, 0, 50, 50, border, transparentFill);

        assertEquals(transparentFill, rect.getFillColor(), "The transparent fill color should be stored correctly");
    }

    @Test
    void testFlipHorizontal_rectangle() {
        MyRectangle r = new MyRectangle(0.2, 0.4, 0.6, 0.8, null, null);
        r.flipHorizontal();
        assertEquals(0.2, r.getStartX(), 1e-6);
        assertEquals(0.6, r.getEndX(), 1e-6);
    }

    @Test
    void testFlipVertical_rectangle() {
        MyRectangle rect = new MyRectangle(0.2, 0.4, 0.6, 0.8, null, null);
        rect.flipVertical();

        assertEquals(0.4, rect.getStartY(), 1e-6);
        assertEquals(0.8, rect.getEndY(), 1e-6);
    }
}