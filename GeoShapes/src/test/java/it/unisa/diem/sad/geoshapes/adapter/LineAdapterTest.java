package it.unisa.diem.sad.geoshapes.adapter;

import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LineAdapterTest {

    @Test   //Verifica che l’adattatore crei correttamente una Line
    public void testLineIsCorrectlyAdapted() {
        MyColor border = new MyColor(1.0, 0.0, 0.0); // Rosso
        MyLine modelLine = new MyLine(0.1, 0.2, 0.7, 0.8, border);

        LineAdapter adapter = new LineAdapter();
        Shape fxShape = adapter.getFxShape(modelLine, 100, 100);

        assertInstanceOf(Line.class, fxShape);
        Line fxLine = (Line) fxShape;

        assertEquals(10.0, fxLine.getStartX(), 0.001);
        assertEquals(20.0, fxLine.getStartY(), 0.001);
        assertEquals(70.0, fxLine.getEndX(), 0.001);
        assertEquals(80.0, fxLine.getEndY(), 0.001);
        //Verifica che le coordinate siano state scalate correttamente

        Color expectedStroke = Color.color(border.getRed(), border.getGreen(), border.getBlue(), border.getOpacity());
        assertEquals(expectedStroke, fxLine.getStroke());
    }

    @Test   //Verifica che venga lanciata un'eccezione con un tipo errato
    public void testThrowsExceptionForWrongShapeType() {
        ShapeAdapter adapter = new LineAdapter();

        MyShape fakeShape = new MyShape() {
            @Override
            public void setEndPoint(double x, double y) {
                // Non serve implementare nulla
            }

            @Override
            public MyColor getBorderColor() {
                return null;
            }

            @Override
            public MyColor getFillColor() {
                return null;
            }

            @Override
            public void setBorderColor(MyColor color) {

            }

            @Override
            public void setFillColor(MyColor color) {

            }
        };

        assertThrows(IllegalArgumentException.class, () -> {
            adapter.getFxShape(fakeShape, 100, 100);
        });
    }
}
