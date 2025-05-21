package it.unisa.diem.sad.geoshapes.decorator;


import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class SelectionDecoratorTest {

    private Rectangle testShape;
    private SelectionDecorator selectionDecorator;

    @BeforeEach
    public void setup() {
        // Create a test shape
        testShape = new Rectangle(10, 10, 100, 50);
        testShape.setFill(Color.WHITE);
        testShape.setStroke(Color.BLACK);
        testShape.setStrokeWidth(1.0);
        testShape.setStrokeType(StrokeType.INSIDE);
        testShape.setOpacity(1.0);

        // Create decorators
        selectionDecorator = new SelectionDecorator(testShape);
    }

    @Test
    public void testSelectionDecorator() {
        // Store original properties
        Color originalStroke = (Color) testShape.getStroke();
        double originalStrokeWidth = testShape.getStrokeWidth();
        StrokeType originalStrokeType = testShape.getStrokeType();
        Paint originalFill = testShape.getFill();
        double originalOpacity = testShape.getOpacity();

        // Apply selection decoration
        selectionDecorator.applyDecoration();

        // Verify decoration was applied
        assertEquals(Color.GREEN, testShape.getStroke(), "Stroke color should be GREEN");
        assertEquals(originalStrokeWidth + 0.5, testShape.getStrokeWidth(), "Stroke width should be increased by 0.5");
        assertEquals(StrokeType.OUTSIDE, testShape.getStrokeType(), "Stroke type should be OUTSIDE");

        // Check fill has opacity of 0.7 if it's a Color
        if (testShape.getFill() instanceof Color) {
            Color fillColor = (Color) testShape.getFill();
            assertEquals(0.7, fillColor.getOpacity(), 0.01, "Fill opacity should be 0.7");
        }

        // Remove decoration
        selectionDecorator.removeDecoration();

        // Verify original properties were restored
        assertEquals(originalStroke, testShape.getStroke(), "Original stroke color should be restored");
        assertEquals(originalStrokeWidth, testShape.getStrokeWidth(), "Original stroke width should be restored");
        assertEquals(originalStrokeType, testShape.getStrokeType(), "Original stroke type should be restored");
        assertEquals(originalFill, testShape.getFill(), "Original fill should be restored");
        assertEquals(originalOpacity, testShape.getOpacity(), "Original opacity should be restored");
    }

}