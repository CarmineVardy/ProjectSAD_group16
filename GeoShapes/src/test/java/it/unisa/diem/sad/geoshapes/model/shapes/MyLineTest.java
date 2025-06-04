package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyLineTest {

    //Scopo: verificare che una Linea venga effettivamente aggiunta al modello DrawingModel
    @Test
    public void testLineAddedToModel() {
        DrawingModel model = new DrawingModel();
        MyLine line = new MyLine(5, 5, 100, 100, 0.0, null);

        model.addShape(line);

        assertEquals(1, model.getShapes().size(), "Model must contain 1 shape!");
        assertSame(line, model.getShapes().get(0), "Model's shape is not equal to the same added line!");
    }
}
