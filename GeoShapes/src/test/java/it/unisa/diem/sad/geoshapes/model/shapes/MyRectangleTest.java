package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.util.MyColor;
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

    @Test
    public void testSetEndPointChangesSize() {
        MyRectangle rect = new MyRectangle(0, 0, 50, 50,
                new MyColor(0.0, 0.0, 0.0), new MyColor(1.0, 1.0, 1.0));

        //cambio le coordinate endX e endY
        rect.setEndPoint(100, 80);

        assertEquals(100, rect.getEndX(), 0.001);
        assertEquals(80, rect.getEndY(), 0.001);
    }
}
