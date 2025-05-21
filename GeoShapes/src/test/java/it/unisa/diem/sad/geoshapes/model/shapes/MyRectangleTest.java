package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.factory.RectangleFactory;
import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyRectangleTest {

    @Test
    public void testRectangleCreation() {
        RectangleFactory factory = new RectangleFactory();
        MyShape shape = factory.createShape(10, 20, 30, 40, null, null);

        assertNotNull(shape, "ERROR: shape must not be null!");
        assertInstanceOf(MyRectangle.class, shape, "Shape should be an instance of Rectangle");

        MyRectangle rectangle = (MyRectangle) shape;
        assertEquals(10, rectangle.getStartX(), "Incorrect StartX");
        assertEquals(20, rectangle.getStartY(), "Incorrect StartY");
        assertEquals(30, rectangle.getEndX(), "Incorrect EndX");
        assertEquals(40, rectangle.getEndY(), "Incorrect EndY");
    }

    @Test
    public void testRectangleAddedToModel() {
        DrawingModel model = new DrawingModel();
        MyRectangle rectangle = new MyRectangle(5, 5, 100, 100, null, null);

        model.addShape(rectangle);

        assertEquals(1, model.getShapes().size(), "Model must contain 1 shape!");
        assertSame(rectangle, model.getShapes().get(0), "Model's shape is not equal to the same added line!");
    }
}