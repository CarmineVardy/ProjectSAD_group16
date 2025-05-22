/*
package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.factory.LineFactory;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyLineTest {

    //Scopo: verificare che la linea venga creata correttamente dalla factory
    @Test
    public void testLineCreation() {
        LineFactory factory = new LineFactory();
        MyShape shape = factory.createShape(10, 20, 30, 40, null, null);

        assertNotNull(shape, "ERROR: shape must not be null!");
        assertInstanceOf(MyLine.class, shape, "Shape should be an instance of Line");

        MyLine line = (MyLine) shape;
        assertEquals(10, line.getStartX(), "Incorrect StartX");
        assertEquals(20, line.getStartY(), "Incorrect StartY");
        assertEquals(30, line.getEndX(), "Incorrect EndX");
        assertEquals(40, line.getEndY(), "Incorrect EndY");
    }

    //Scopo: verificare che una Linea venga effettivamente aggiunta al modello DrawingModel
    @Test
    public void testLineAddedToModel() {
        DrawingModel model = new DrawingModel();
        MyLine line = new MyLine(5, 5, 100, 100, null);

        model.addShape(line);

        assertEquals(1, model.getShapes().size(), "Model must contain 1 shape!");
        assertSame(line, model.getShapes().get(0), "Model's shape is not equal to the same added line!");
    }

    @Test   //controlla che il colore venga memorizzato correttamente
    public void testBorderColorIsStoredCorrectly() {
        MyColor border = new MyColor(0.5, 0.4, 0.3);
        MyLine line = new MyLine(0, 0, 1, 1, border);
        assertEquals(border, line.getBorderColor(), "Border color must match the assigned one");
    }

    @Test   //Verifica che setEndPoint() aggiorni i valori finali
    public void testSetEndPointModifiesEndCoordinates() {
        MyLine line = new MyLine(0.2, 0.2, 0.5, 0.5, new MyColor(0, 0, 0));
        line.setEndPoint(0.9, 0.8);
        assertEquals(0.9, line.getEndX(), 0.001);
        assertEquals(0.8, line.getEndY(), 0.001);
    }

    @Test   //Verifica le coordinate iniziali
    public void testGetStartCoordinates() {
        MyLine line = new MyLine(0.1, 0.1, 0.9, 0.9, new MyColor(1, 0, 0));
        assertEquals(0.1, line.getStartX(), 0.001);
        assertEquals(0.1, line.getStartY(), 0.001);
    }
}
*/