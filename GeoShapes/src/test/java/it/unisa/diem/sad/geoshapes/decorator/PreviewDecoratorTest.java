package it.unisa.diem.sad.geoshapes.decorator;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class PreviewDecoratorTest {

    private Shape testShape;
    private PreviewDecorator decorator;

    // Original properties to verify restoration
    private Color originalStrokeColor;
    private double originalStrokeWidth;
    private Color originalFillColor;
    private double originalOpacity;

    @BeforeEach
    public void setUp() {
        // Create a rectangle as the test shape
        testShape = new Rectangle(0, 0, 100, 50);

        // Set initial properties
        originalStrokeColor = Color.BLACK;
        originalStrokeWidth = 2.0;
        originalFillColor = Color.BLUE;
        originalOpacity = 1.0;

        testShape.setStroke(originalStrokeColor);
        testShape.setStrokeWidth(originalStrokeWidth);
        testShape.setFill(originalFillColor);
        testShape.setOpacity(originalOpacity);

        // Create the decorator
        decorator = new PreviewDecorator(testShape);
    }

    @AfterEach
    public void tearDown() {
        testShape = null;
        decorator = null;
    }

    // ====== Tests for applyDecoration() method ======

    @Test
    public void testApplyDecoration_StrokeChanges() {
        // Apply the decoration
        decorator.applyDecoration();

        // Verify stroke color is changed as expected
        assertEquals(Color.rgb(80, 80, 80, 0.9), testShape.getStroke());
    }

    @Test
    public void testApplyDecoration_DashArrayChanges() {
        // Apply the decoration
        decorator.applyDecoration();

        // Verify that dash array is set to [5.0, 4.0]
        assertEquals(Arrays.asList(5.0, 4.0), testShape.getStrokeDashArray());
    }

    @Test
    public void testApplyDecoration_OpacityChanges() {
        // Apply the decoration
        decorator.applyDecoration();

        // Verify opacity is changed to 0.75
        assertEquals(0.75, testShape.getOpacity(), 0.001);
    }

    @Test
    public void testApplyDecoration_FillChanges() {
        // Apply the decoration
        decorator.applyDecoration();

        // Verify fill is changed as expected
        Color expectedFill = new Color(originalFillColor.getRed(),
                originalFillColor.getGreen(),
                originalFillColor.getBlue(),
                Math.min(originalFillColor.getOpacity(), 0.35));
        assertEquals(expectedFill, testShape.getFill());
    }

    @Test
    public void testApplyDecoration_StrokeLineCapChanges() {
        // Set original stroke line cap to something different
        testShape.setStrokeLineCap(StrokeLineCap.ROUND);

        // Apply the decoration
        decorator.applyDecoration();

        // Verify stroke line cap is changed to BUTT
        assertEquals(StrokeLineCap.BUTT, testShape.getStrokeLineCap());
    }

    // ====== Tests for removeDecoration() method ======

    @Test
    public void testRemoveDecoration_StrokeRestoration() {
        // Apply and then remove decoration
        decorator.applyDecoration();
        decorator.removeDecoration();

        // Verify stroke color is restored
        assertEquals(originalStrokeColor, testShape.getStroke());
    }

    @Test
    public void testRemoveDecoration_StrokeWidthRestoration() {
        // Apply and then remove decoration
        decorator.applyDecoration();
        decorator.removeDecoration();

        // Verify stroke width is restored
        assertEquals(originalStrokeWidth, testShape.getStrokeWidth(), 0.001);
    }

    @Test
    public void testRemoveDecoration_FillRestoration() {
        // Apply and then remove decoration
        decorator.applyDecoration();
        decorator.removeDecoration();

        // Verify fill is restored
        assertEquals(originalFillColor, testShape.getFill());
    }

    @Test
    public void testRemoveDecoration_OpacityRestoration() {
        // Apply and then remove decoration
        decorator.applyDecoration();
        decorator.removeDecoration();

        // Verify opacity is restored
        assertEquals(originalOpacity, testShape.getOpacity(), 0.001);
    }

    @Test
    public void testRemoveDecoration_DashArrayRestoration() {
        // Set original dash array
        testShape.getStrokeDashArray().setAll(1.0, 2.0, 3.0);

        // Apply and then remove decoration
        decorator.applyDecoration();
        decorator.removeDecoration();

        // Verify dash array is restored
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), testShape.getStrokeDashArray());
    }

    @Test
    public void testRemoveDecoration_StrokeLineCapRestoration() {
        // Set original stroke line cap
        testShape.setStrokeLineCap(StrokeLineCap.ROUND);

        // Apply and then remove decoration
        decorator.applyDecoration();
        decorator.removeDecoration();

        // Verify stroke line cap is restored
        assertEquals(StrokeLineCap.ROUND, testShape.getStrokeLineCap());
    }

    // ====== Tests for getDecoratedShape() method ======

    @Test
    public void testGetDecoratedShape_ReturnsSameInstance() {
        // Verify that the method returns the same shape instance
        assertSame(testShape, decorator.getDecoratedShape());
    }

    @Test
    public void testGetDecoratedShape_AfterApplyDecoration() {
        // Apply decoration
        decorator.applyDecoration();

        // Verify that the method still returns the same shape instance
        assertSame(testShape, decorator.getDecoratedShape());
    }

    @Test
    public void testGetDecoratedShape_AfterRemoveDecoration() {
        // Apply and remove decoration
        decorator.applyDecoration();
        decorator.removeDecoration();

        // Verify that the method still returns the same shape instance
        assertSame(testShape, decorator.getDecoratedShape());
    }

    // ====== Additional Integration Tests ======

    @Test
    public void testDecorationCycle_MultipleApplyRemove() {
        // Apply and remove decoration multiple times
        for (int i = 0; i < 3; i++) {
            decorator.applyDecoration();

            // Verify some decoration properties are applied
            assertEquals(Color.rgb(80, 80, 80, 0.9), testShape.getStroke());
            assertEquals(0.75, testShape.getOpacity(), 0.001);

            decorator.removeDecoration();

            // Verify original properties are restored
            assertEquals(originalStrokeColor, testShape.getStroke());
            assertEquals(originalOpacity, testShape.getOpacity(), 0.001);
        }
    }

    @Test
    public void testWithNullFill() {
        // Set fill to null
        testShape.setFill(null);

        // Apply decoration (should handle null fill without exceptions)
        decorator.applyDecoration();

        // Verify decoration was applied
        assertEquals(Color.rgb(80, 80, 80, 0.9), testShape.getStroke());

        // Remove decoration
        decorator.removeDecoration();

        // Verify fill is still null
        assertNull(testShape.getFill());
    }

    @Test
    public void testWithCustomGradientFill() {
        // Create a LinearGradient as a non-Color fill
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.RED), new Stop(1, Color.BLUE)
        );
        testShape.setFill(gradient);

        // Apply decoration
        decorator.applyDecoration();

        // Verify fill is changed to light gray for non-Color fills
        assertEquals(new Color(0.85, 0.85, 0.85, 0.30), testShape.getFill());

        // Remove decoration
        decorator.removeDecoration();

        // Verify original fill is restored
        assertSame(gradient, testShape.getFill());
    }
}