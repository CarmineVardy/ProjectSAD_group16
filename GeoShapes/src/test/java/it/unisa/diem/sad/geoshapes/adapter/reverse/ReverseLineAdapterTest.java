package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReverseLineAdapterTest {

    @Test
    public void testGetModelShapeWithValidLine() {
        Line fxLine = new Line(20, 30, 80, 90);
        fxLine.setStroke(Color.rgb(100, 150, 200, 0.5));
        fxLine.setRotate(15.0);

        double width = 100.0;
        double height = 100.0;

        ReverseLineAdapter adapter = ReverseLineAdapter.getInstance();
        MyLine modelLine = (MyLine) adapter.getModelShape(fxLine, width, height);

        assertEquals(0.2, modelLine.getStartX(), 0.0001);
        assertEquals(0.3, modelLine.getStartY(), 0.0001);
        assertEquals(0.8, modelLine.getEndX(), 0.0001);
        assertEquals(0.9, modelLine.getEndY(), 0.0001);
        assertEquals(15.0, modelLine.getRotation(), 0.0001);
    }

    @Test
    public void testGetModelShapeWithInvalidShape() {
        Shape wrongShape = new Circle(50, 50, 10); // not a Line
        ReverseLineAdapter adapter = ReverseLineAdapter.getInstance();

        assertThrows(IllegalArgumentException.class, () ->
                adapter.getModelShape(wrongShape, 100.0, 100.0));
    }
}