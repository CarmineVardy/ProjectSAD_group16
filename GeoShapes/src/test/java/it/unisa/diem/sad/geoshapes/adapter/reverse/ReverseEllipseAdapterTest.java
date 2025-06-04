package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReverseEllipseAdapterTest {

    private final ReverseEllipseAdapter adapter = ReverseEllipseAdapter.getInstance();

    @Test
    void testGetModelShape_validEllipse() {
        double width = 1000.0;
        double height = 800.0;

        Ellipse fxEllipse = new Ellipse();
        fxEllipse.setCenterX(500);
        fxEllipse.setCenterY(400);
        fxEllipse.setRadiusX(200);
        fxEllipse.setRadiusY(100);
        fxEllipse.setRotate(30.0);
        fxEllipse.setStroke(Color.RED);
        fxEllipse.setFill(Color.BLUE);

        // Azione
        MyShape result = adapter.getModelShape(fxEllipse, width, height);

        // Verifica
        assertInstanceOf(MyEllipse.class, result);
        MyEllipse model = (MyEllipse) result;

        assertEquals(0.3, model.getStartX(), 1e-6);
        assertEquals(0.375, model.getStartY(), 1e-6);
        assertEquals(0.7, model.getEndX(), 1e-6);
        assertEquals(0.625, model.getEndY(), 1e-6);

        assertEquals(30.0, model.getRotation(), 1e-6);

        assertEquals(new MyColor(1.0, 0.0, 0.0, 1.0), model.getBorderColor());
        assertEquals(new MyColor(0.0, 0.0, 1.0, 1.0), model.getFillColor());
    }

    @Test
    void testGetModelShape_invalidShape_throwsException() {
        javafx.scene.shape.Rectangle fxRect = new javafx.scene.shape.Rectangle();

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                adapter.getModelShape(fxRect, 1000, 800));

        assertEquals("Expected Ellipse", ex.getMessage());
    }
}