package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import it.unisa.diem.sad.geoshapes.model.shapes.MyPolygon;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PolygonAdapterTest {

    @Test
    public void testGetFxShapeWithValidPolygon(){
        List<Double> xPoints = List.of(0.2, 0.5, 0.8);
        List<Double> yPoints = List.of(0.2, 0.8, 0.2);

        MyColor borderColor = new MyColor(0.0, 0.0, 1.0);
        MyColor fillColor = new MyColor(1.0, 1.0, 0.0);

        MyPolygon modelPolygon = new MyPolygon(xPoints, yPoints, 0.0, borderColor, fillColor);

        double canvasWidth = 100.0;
        double canvasHeight = 200.0;

        Shape fxShape = PolygonAdapter.getInstance().getFxShape(modelPolygon, canvasWidth, canvasHeight);

        assertTrue(fxShape instanceof Polygon);
        Polygon fxPolygon = (Polygon) fxShape;

        List<Double> expectedPoints = List.of(
                0.2 * canvasWidth, 0.2 * canvasHeight,
                0.5 * canvasWidth, 0.8 * canvasHeight,
                0.8 * canvasWidth, 0.2 * canvasHeight
        );
        assertEquals(expectedPoints, fxPolygon.getPoints());

        assertEquals(Color.color(0.0, 0.0, 1.0, 1.0), fxPolygon.getStroke());
        assertEquals(Color.color(1.0, 1.0, 0.0, 1.0), fxPolygon.getFill());
        assertEquals(2.0, fxPolygon.getStrokeWidth());
    }

    @Test
    public void testPolygonWithTooFewPointsThrowsException() {
        List<Double> xPoints = List.of(0.1, 0.2);
        List<Double> yPoints = List.of(0.1, 0.2);
        MyColor borderColor = new MyColor(0.0, 0.0, 0.0);
        MyColor fillColor = new MyColor(1.0, 1.0, 1.0);

        assertThrows(IllegalArgumentException.class, () -> {
            new MyPolygon(xPoints, yPoints, 0.0, borderColor, fillColor);
        });
    }

    @Test
    public void testPolygonWithTooManyPointsThrowsException() {
        List<Double> xPoints = List.of(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);
        List<Double> yPoints = List.of(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);
        MyColor borderColor = new MyColor(0.0, 0.0, 0.0);
        MyColor fillColor = new MyColor(1.0, 1.0, 1.0);

        assertThrows(IllegalArgumentException.class, () -> {
            new MyPolygon(xPoints, yPoints, 0.0, borderColor, fillColor);
        });
    }

    @Test
    public void testPolygonConstructorWithMismatchedListsThrowsException() {
        List<Double> xPoints = List.of(0.1, 0.2, 0.3);
        List<Double> yPoints = List.of(0.1, 0.2); // mismatch
        MyColor borderColor = new MyColor(0.0, 0.0, 0.0);
        MyColor fillColor = new MyColor(1.0, 1.0, 1.0);

        assertThrows(IllegalArgumentException.class, () -> {
            new MyPolygon(xPoints, yPoints, 0.0, borderColor, fillColor);
        });
    }
}