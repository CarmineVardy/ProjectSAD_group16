package com.example.geoshapes.model.shapes;

import com.example.geoshapes.model.DrawingModel;
import com.example.geoshapes.model.factory.LineFactory;
import com.example.geoshapes.model.shapes.MyLine;
import com.example.geoshapes.model.shapes.MyShape;
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
}
