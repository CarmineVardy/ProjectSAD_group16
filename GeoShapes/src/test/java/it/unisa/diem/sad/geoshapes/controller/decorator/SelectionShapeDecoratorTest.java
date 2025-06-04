package it.unisa.diem.sad.geoshapes.controller.decorator;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SelectionShapeDecoratorTest {

    private Rectangle testShape;
    private SelectionShapeDecorator selectionDecorator;

    private Color originalFill;

    @BeforeEach
    public void setup() {
        testShape = new Rectangle(10, 10, 100, 50);
        testShape.setFill(Color.web("#808080")); // grigio medio, per verificare il brightening
        testShape.setStroke(Color.BLACK);
        testShape.setOpacity(1.0);
        originalFill = (Color) testShape.getFill();
        selectionDecorator = new SelectionShapeDecorator(testShape);
    }

    @Test
    public void testRemoveDecorationRestoresOriginalProperties() {
        Color originalStroke = (Color) testShape.getStroke();
        double originalOpacity = testShape.getOpacity();

        selectionDecorator.applyDecoration();
        selectionDecorator.removeDecoration();

        assertEquals(originalStroke, testShape.getStroke(), "stroke must be restored");
        assertEquals(originalFill, testShape.getFill(), "fill must be restored");
        assertEquals(originalOpacity, testShape.getOpacity(), "opacity must be restored");
        assertTrue(testShape.getStrokeDashArray().isEmpty(), "stroke dash array must be cleared");
    }
}
