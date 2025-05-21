package it.unisa.diem.sad.geoshapes.adapter;

import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LineAdapterTest {

    // Scopo: verificare che l'adapter converta correttamente un oggetto MyLine in un oggetto Line di JavaFX
    @Test
    public void testGetFxShapeCorrectConversion() {
        LineAdapter adapter = new LineAdapter();

        MyColor borderColor = new MyColor(1.0, 0.0, 0.0); // Rosso
        MyLine myLine = new MyLine(0.1, 0.2, 0.3, 0.4, borderColor);

        double width = 500.0;
        double height = 400.0;

        Shape shape = adapter.getFxShape(myLine, width, height);

        assertNotNull(shape, "Shape must not be null");
        assertInstanceOf(Line.class, shape, "Shape must be instance of Line");

        Line fxLine = (Line) shape;

        // Coordinate convertite
        assertEquals(0.1 * width, fxLine.getStartX(), 1e-9, "Incorrect StartX");
        assertEquals(0.2 * height, fxLine.getStartY(), 1e-9, "Incorrect StartY");
        assertEquals(0.3 * width, fxLine.getEndX(), 1e-9, "Incorrect EndX");
        assertEquals(0.4 * height, fxLine.getEndY(), 1e-9, "Incorrect EndY");

        // Colore convertito
        Color expectedColor = new Color(1.0, 0.0, 0.0, 1.0);
        assertEquals(expectedColor, fxLine.getStroke(), "Incorrect stroke color");
    }

    // Scopo: verificare che venga lanciata un’eccezione se l’oggetto passato non è un’istanza di MyLine
    @Test
    public void testGetFxShapeThrowsExceptionIfNotMyLine() {
        LineAdapter adapter = new LineAdapter();

        // Usa una forma concreta diversa da MyLine, ad esempio MyEllipse
        MyShape fakeShape = new MyEllipse(0, 0, 1, 1, null, null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            adapter.getFxShape(fakeShape, 100, 100);
        });

        assertEquals("Expected MyLine", exception.getMessage(), "Exception message should match");
    }
}
