package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RectangleAdapterTest {

    @Test
    public void testColorAppliedCorrectlyToFxShape() {
        MyColor border = new MyColor(0.0, 1.0, 0.0); // Verde
        MyColor fill = new MyColor(1.0, 1.0, 0.0);   // Giallo

        MyRectangle modelRect = new MyRectangle(0.1, 0.1, 0.3, 0.3, 0.0, border, fill);
        RectangleAdapter adapter = new RectangleAdapter();

        Shape fxShape = adapter.getFxShape(modelRect, 100, 100);

        assertInstanceOf(Rectangle.class, fxShape);
        Rectangle fxRect = (Rectangle) fxShape;

        assertEquals(Color.color(border.getRed(), border.getGreen(), border.getBlue(), border.getOpacity()), fxRect.getStroke());
        assertEquals(Color.color(fill.getRed(), fill.getGreen(), fill.getBlue(), fill.getOpacity()), fxRect.getFill());
    }
}