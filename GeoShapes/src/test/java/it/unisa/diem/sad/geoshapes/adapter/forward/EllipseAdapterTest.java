package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EllipseAdapterTest {

    private EllipseAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EllipseAdapter();
    }

    @Test
    void testColorConversionAndApplication() {
        MyColor borderMyColor = new MyColor(255 / 255.0, 0, 0);
        MyColor fillMyColor = new MyColor(0, 0, 255 / 255.0);

        MyEllipse modelEllipse = new MyEllipse(
                0.2, 0.2,
                0.8, 0.6,
                0.0,
                borderMyColor,
                fillMyColor
        );

        double canvasWidth = 200;
        double canvasHeight = 100;

        Shape fxShape = adapter.getFxShape(modelEllipse, canvasWidth, canvasHeight);
        assertInstanceOf(Ellipse.class, fxShape);

        Ellipse fxEllipse = (Ellipse) fxShape;

        Color expectedStroke = Color.rgb(255, 0, 0);
        Color expectedFill = Color.rgb(0, 0, 255);

        assertEquals(expectedStroke, fxEllipse.getStroke(), "Border color is not correct!");
        assertEquals(expectedFill, fxEllipse.getFill(), "Fill color is not correct!");
    }
}