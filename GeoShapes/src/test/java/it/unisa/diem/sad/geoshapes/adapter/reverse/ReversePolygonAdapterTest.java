package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.shapes.MyPolygon;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ReversePolygonAdapterTest {

    @Test
    public void testGetModelShapeWithValidPolygon() {
        Polygon fxPolygon = new Polygon();
        fxPolygon.getPoints().addAll(
                10.0, 20.0,
                30.0, 40.0,
                50.0, 60.0
        );
        fxPolygon.setRotate(25.0);
        fxPolygon.setStroke(Color.rgb(100, 150, 200, 0.5));
        fxPolygon.setFill(Color.rgb(50, 100, 150, 0.8));

        double width = 100.0;
        double height = 100.0;

        ReversePolygonAdapter adapter = ReversePolygonAdapter.getInstance();
        MyPolygon modelPolygon = (MyPolygon) adapter.getModelShape(fxPolygon, width, height);

        assertEquals(Arrays.asList(0.1, 0.3, 0.5), modelPolygon.getXPoints());
        assertEquals(Arrays.asList(0.2, 0.4, 0.6), modelPolygon.getYPoints());
        assertEquals(25.0, modelPolygon.getRotation(), 0.0001);
    }

    @Test
    public void testGetModelShapeWithInvalidShape() {
        Shape wrongShape = new Circle(50, 50, 10);
        ReversePolygonAdapter adapter = ReversePolygonAdapter.getInstance();

        assertThrows(IllegalArgumentException.class, () ->
                adapter.getModelShape(wrongShape, 100.0, 100.0));
    }

    @Test
    public void testGetModelShapeWithOddNumberOfPoints() {
        Polygon fxPolygon = new Polygon();
        fxPolygon.getPoints().addAll(
                10.0, 20.0,
                30.0
        );

        ReversePolygonAdapter adapter = ReversePolygonAdapter.getInstance();

        assertThrows(IllegalArgumentException.class, () ->
                adapter.getModelShape(fxPolygon, 100.0, 100.0));
    }
}