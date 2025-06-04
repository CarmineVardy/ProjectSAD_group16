package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReverseRectangleAdapterTest {

    private final ReverseRectangleAdapter adapter = ReverseRectangleAdapter.getInstance();

    @Test
    void testGetModelShape_validRectangle() {
        double width = 1000.0;
        double height = 800.0;

        Rectangle fxRectangle = new Rectangle();
        fxRectangle.setX(200);
        fxRectangle.setY(160);
        fxRectangle.setWidth(400);
        fxRectangle.setHeight(480);
        fxRectangle.setRotate(60.0);
        fxRectangle.setStroke(Color.RED);
        fxRectangle.setFill(Color.BLUE);

        MyShape result = adapter.getModelShape(fxRectangle, width, height);

        assertInstanceOf(MyRectangle.class, result);
        MyRectangle model = (MyRectangle) result;

        assertEquals(0.2, model.getStartX(), 1e-6);
        assertEquals(0.2, model.getStartY(), 1e-6);
        assertEquals(0.6, model.getEndX(), 1e-6);
        assertEquals(0.8, model.getEndY(), 1e-6);
        assertEquals(60.0, model.getRotation(), 1e-6);

        assertEquals(new MyColor(1.0, 0.0, 0.0, 1.0), model.getBorderColor());
        assertEquals(new MyColor(0.0, 0.0, 1.0, 1.0), model.getFillColor());
    }

    @Test
    void testGetModelShape_invalidShape_throwsException() {
        Circle fxCircle = new Circle();

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                adapter.getModelShape(fxCircle, 1000, 800));

        assertEquals("Expected Rectangle", ex.getMessage());
    }
}