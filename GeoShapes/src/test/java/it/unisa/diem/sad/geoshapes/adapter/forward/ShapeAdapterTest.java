package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShapeAdapterTest {

    private static class DummyShapeAdapter implements ShapeAdapter {
        @Override
        public javafx.scene.shape.Shape getFxShape(it.unisa.diem.sad.geoshapes.model.shapes.MyShape modelShape, double width, double height) {
            return null;
        }
    }

    private final ShapeAdapter adapter = new DummyShapeAdapter();

    @Test
    void testConvertToJavaFxColor_withValidMyColor() {
        MyColor myColor = new MyColor(0.2, 0.4, 0.6, 0.8);
        Color fxColor = adapter.convertToJavaFxColor(myColor);

        assertEquals(0.2, fxColor.getRed(), 1e-6);
        assertEquals(0.4, fxColor.getGreen(), 1e-6);
        assertEquals(0.6, fxColor.getBlue(), 1e-6);
        assertEquals(0.8, fxColor.getOpacity(), 1e-6);
    }

    @Test
    void testConvertToJavaFxColor_withNullMyColor() {
        Color fxColor = adapter.convertToJavaFxColor(null);

        assertEquals(Color.BLACK, fxColor);
    }
}