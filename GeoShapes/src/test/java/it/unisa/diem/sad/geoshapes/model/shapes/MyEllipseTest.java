package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyEllipseTest {

    /*@Test
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
    }*/

    /*@Test
    public void testEllipseAddedToModel() {
        DrawingModel model = new DrawingModel();
        MyEllipse ellipse = new MyEllipse(0, 0, 50, 50, null, null);

        model.addShape(ellipse);

        assertEquals(1, model.getShapes().size(), "Model must contain 1 shape!");
        assertSame(ellipse, model.getShapes().get(0), "Model's shape is not equal to the same added line!");
    }*/

    /*@Test
    public void testCircleCreation() {
        EllipseFactory factory = new EllipseFactory();
        MyShape shape = factory.createShape(10, 10, 60, 60, null, null);

        assertNotNull(shape, "ERROR: shape must not be null!");
        assertInstanceOf(MyEllipse.class, shape, "Shape should be an instance of Ellipse");

        MyEllipse circle = (MyEllipse) shape;
        double width = circle.getEndX() - circle.getStartX();
        double height = circle.getEndY() - circle.getStartY();
    }*/

    @Test
    public void testColorAssignment() {
        int r = 255;
        int g = 128;
        int b = 0;

        MyColor borderColor = new MyColor(r / 255.0, 0, 0); // Rosso
        MyColor fillColor = new MyColor(0, g / 255.0, 0);   // Verde

        MyEllipse ellipse = new MyEllipse(10, 20, 30, 40, borderColor, fillColor);

        assertEquals(borderColor, ellipse.getBorderColor(), "Border color should be correctly assigned");
        assertEquals(fillColor, ellipse.getFillColor(), "Fill color should be correctly assigned");
    }

    /*@Test
    public void testColorPersistenceAfterEndPointChange() {
        int r = 255;
        int g = 128;
        int b = 0;

        MyColor borderColor = new MyColor(0, 0, b / 255.0); // Blu
        MyColor fillColor = new MyColor(r / 255.0, g / 255.0, 0); // Giallo

        MyEllipse ellipse = new MyEllipse(0, 0, 50, 50, borderColor, fillColor);

        ellipse.setEndPoint(100, 100); // Modifica i punti

        // I colori devono restare invariati
        assertEquals(borderColor, ellipse.getBorderColor(), "Border color should persist");
        assertEquals(fillColor, ellipse.getFillColor(), "Fill color should persist");
    }*/

    @Test
    public void testTransparentFillColor() {
        MyColor border = new MyColor(0.0, 0.0, 0.0, 1.0);         // Nero opaco
        MyColor transparentFill = new MyColor(1.0, 0.0, 0.0, 0.0); // Trasparente

        MyEllipse elli = new MyEllipse(0, 0, 50, 50, border, transparentFill);

        assertEquals(transparentFill, elli.getFillColor(), "The transparent fill color must be stored correctly");
    }

    @Test
    void testFlipHorizontal_ellipse() {
        MyEllipse e = new MyEllipse(0.2, 0.4, 0.6, 0.8, new MyColor(0, 0, 0), new MyColor(1, 1, 1));
        e.flipHorizontal();
        assertEquals(0.2, e.getStartX(), 1e-6);
        assertEquals(0.6, e.getEndX(), 1e-6);
    }

    @Test
    void testFlipVertical_ellipse() {
        MyEllipse e = new MyEllipse(0.2, 0.4, 0.6, 0.8, new MyColor(0, 0, 0), new MyColor(1, 1, 1));
        e.flipVertical();
        assertEquals(0.4, e.getStartY(), 1e-6);
        assertEquals(0.8, e.getEndY(), 1e-6);
    }
}