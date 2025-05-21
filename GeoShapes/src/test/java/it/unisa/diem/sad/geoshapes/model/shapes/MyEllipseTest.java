package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.factory.EllipseFactory;
import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyEllipseTest {

    @Test
    public void testEllipseCreation() {
        EllipseFactory factory = new EllipseFactory();
        MyShape shape = factory.createShape(15, 25, 45, 65, null, null);

        assertNotNull(shape, "ERROR: shape must not be null!");
        assertInstanceOf(MyEllipse.class, shape, "Shape should be an instance of Ellipse");

        MyEllipse ellipse = (MyEllipse) shape;
        assertEquals(15, ellipse.getStartX(), "Incorrect StartX");
        assertEquals(25, ellipse.getStartY(), "Incorrect StartY");
        assertEquals(45, ellipse.getEndX(), "Incorrect EndX");
        assertEquals(65, ellipse.getEndY(), "Incorrect EndY");
    }

    @Test
    public void testEllipseAddedToModel() {
        DrawingModel model = new DrawingModel();
        MyEllipse ellipse = new MyEllipse(0, 0, 50, 50, null, null);

        model.addShape(ellipse);

        assertEquals(1, model.getShapes().size(), "Model must contain 1 shape!");
        assertSame(ellipse, model.getShapes().get(0), "Model's shape is not equal to the same added line!");
    }

    @Test
    public void testCircleCreation() {
        EllipseFactory factory = new EllipseFactory();
        MyShape shape = factory.createShape(10, 10, 60, 60, null, null);

        assertNotNull(shape, "ERROR: shape must not be null!");
        assertInstanceOf(MyEllipse.class, shape, "Shape should be an instance of Ellipse");

        MyEllipse circle = (MyEllipse) shape;
        double width = circle.getEndX() - circle.getStartX();
        double height = circle.getEndY() - circle.getStartY();
    }
}
