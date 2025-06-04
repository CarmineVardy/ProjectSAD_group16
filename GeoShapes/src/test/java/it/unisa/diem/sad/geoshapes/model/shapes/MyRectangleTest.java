package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyRectangleTest {

    @Test
    public void testColorChangePersists() {
        MyColor border = new MyColor(1.0, 0.0, 0.0); // Rosso
        MyColor fill = new MyColor(0.0, 0.0, 1.0);   // Blu

        MyRectangle rect = new MyRectangle(10, 10, 100, 100, 0.0, border, fill);

        assertEquals(border, rect.getBorderColor(), "Border color must match the assigned red");
        assertEquals(fill, rect.getFillColor(), "Fill color must match the assigned blue");
    }

    @Test
    public void testSetEndPointChangesSize() {
        MyRectangle rect = new MyRectangle(0.0, 0.0, 50.0, 50.0, 0.0,
                new MyColor(0.0, 0.0, 0.0), new MyColor(1.0, 1.0, 1.0));

        // Verifica dimensioni iniziali
        double initialWidth = Math.abs(rect.getEndX() - rect.getStartX());
        double initialHeight = Math.abs(rect.getEndY() - rect.getStartY());
        assertEquals(50.0, initialWidth, 0.01);
        assertEquals(50.0, initialHeight, 0.01);

        // Cambia l'end point
        rect.setEndX(80.0);
        rect.setEndY(120.0);

        double newWidth = Math.abs(rect.getEndX() - rect.getStartX());
        double newHeight = Math.abs(rect.getEndY() - rect.getStartY());

        assertEquals(80.0, newWidth, 0.01);
        assertEquals(120.0, newHeight, 0.01);
    }

    @Test
    public void testFlipHorizontalChangesCoordinatesCorrectly() {
        MyRectangle rect = new MyRectangle(10.0, 0.0, 30.0, 20.0, 45.0,
                new MyColor(0, 0, 0), new MyColor(1, 1, 1));

        double originalWidth = Math.abs(rect.getEndX() - rect.getStartX());

        rect.flipHorizontal();

        double flippedWidth = Math.abs(rect.getEndX() - rect.getStartX());

        // La larghezza deve rimanere invariata
        assertEquals(originalWidth, flippedWidth, 0.01);

        // La rotazione viene negata
        assertEquals(-45.0, rect.getRotation(), 0.01);  // solo se hai getRotation()
    }

    @Test
    public void testFlipVerticalChangesCoordinatesCorrectly() {
        MyRectangle rect = new MyRectangle(0, 10, 20, 30, 90,
                new MyColor(0, 0, 0), new MyColor(1, 1, 1));

        rect.flipVertical();

        assertEquals(20, Math.abs(rect.getEndY() - rect.getStartY()), 0.01);

        // Calcolo della rotazione attesa come negazione normalizzata
        double expectedRotation = -90 % 360;
        if (expectedRotation > 180) {
            expectedRotation -= 360;
        } else if (expectedRotation <= -180) {
            expectedRotation += 360;
        }

        assertEquals(expectedRotation, rect.getRotation(), 0.01);
    }
}