package it.unisa.diem.sad.geoshapes.controller.decorator;

import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PreviewShapeDecoratorTest {

    private Shape rectangle;
    private Shape circle;
    private Shape gradientShape;
    private PreviewShapeDecorator rectangleDecorator;
    private PreviewShapeDecorator circleDecorator;
    private PreviewShapeDecorator gradientDecorator;

    @BeforeEach
    public void setup() {
        rectangle = new Rectangle(10, 10, 100, 50);
        rectangle.setFill(Color.DARKBLUE);
        rectangle.setStroke(Color.BLACK);
        rectangle.setOpacity(1.0);
        rectangleDecorator = new PreviewShapeDecorator(rectangle);

        circle = new Circle(50, 50, 25);
        circle.setFill(Color.RED);
        circle.setStroke(Color.DARKGRAY);
        circle.setOpacity(0.8);
        circleDecorator = new PreviewShapeDecorator(circle);

        gradientShape = new Rectangle(0, 0, 200, 100);
        Stop[] stops = new Stop[] {
                new Stop(0, Color.RED),
                new Stop(1, Color.BLUE)
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, null, stops);
        gradientShape.setFill(gradient);
        gradientShape.setOpacity(0.9);
        gradientDecorator = new PreviewShapeDecorator(gradientShape);
    }

    @Test
    public void testApplyDecoration_DashArray() {
        rectangleDecorator.applyDecoration();

        assertFalse(rectangle.getStrokeDashArray().isEmpty());
        assertEquals(5.0, rectangle.getStrokeDashArray().get(0), 0.001);
        assertEquals(4.0, rectangle.getStrokeDashArray().get(1), 0.001);
        assertEquals(2, rectangle.getStrokeDashArray().size());
    }

    @Test
    public void testApplyDecoration_FillColor() {
        Color originalRectFill = (Color) rectangle.getFill();
        rectangleDecorator.applyDecoration();
        Color newRectFill = (Color) rectangle.getFill();
        assertEquals(originalRectFill.brighter(), newRectFill);

        Color originalCircleFill = (Color) circle.getFill();
        circleDecorator.applyDecoration();
        Color newCircleFill = (Color) circle.getFill();
        assertEquals(originalCircleFill.brighter(), newCircleFill);
    }



    @Test
    public void testRemoveDecoration_DashArray() {
        rectangleDecorator.applyDecoration();
        rectangleDecorator.removeDecoration();
        assertTrue(rectangle.getStrokeDashArray().isEmpty());

        circleDecorator.applyDecoration();
        circleDecorator.removeDecoration();
        assertTrue(circle.getStrokeDashArray().isEmpty());
    }

    @Test
    public void testRemoveDecoration_FillColor() {
        Color originalRectFill = (Color) rectangle.getFill();
        rectangleDecorator.applyDecoration();
        rectangleDecorator.removeDecoration();
        assertEquals(originalRectFill, rectangle.getFill());

        Color originalCircleFill = (Color) circle.getFill();
        circleDecorator.applyDecoration();
        circleDecorator.removeDecoration();
        assertEquals(originalCircleFill, circle.getFill());
    }

    @Test
    public void testRemoveDecoration_MultipleApplies() {
        Shape testRectangle = new Rectangle(10, 10, 100, 50);
        testRectangle.setFill(Color.DARKBLUE);
        testRectangle.setStroke(Color.BLACK);
        testRectangle.setOpacity(1.0);
        PreviewShapeDecorator multipleApplyDecorator = new PreviewShapeDecorator(testRectangle);

        Color trueOriginalFill = (Color) testRectangle.getFill();

        multipleApplyDecorator.applyDecoration();
        multipleApplyDecorator.applyDecoration();

        multipleApplyDecorator.removeDecoration();

        assertEquals(trueOriginalFill.brighter(), testRectangle.getFill());
        assertEquals(0.75, testRectangle.getOpacity(), 0.001);
        assertTrue(testRectangle.getStrokeDashArray().isEmpty());
    }

    @Test
    public void testGetDecoratedShape_Rectangle() {
        assertSame(rectangle, rectangleDecorator.getDecoratedShape());
    }

    @Test
    public void testGetDecoratedShape_Circle() {
        assertSame(circle, circleDecorator.getDecoratedShape());
    }

    @Test
    public void testGetDecoratedShape_GradientShape() {
        assertSame(gradientShape, gradientDecorator.getDecoratedShape());
    }

    @Test
    public void testGetDecoratedShape_AfterDecoration() {
        rectangleDecorator.applyDecoration();
        assertSame(rectangle, rectangleDecorator.getDecoratedShape());

        rectangleDecorator.removeDecoration();
        assertSame(rectangle, rectangleDecorator.getDecoratedShape());
    }
}