/*
package it.unisa.diem.sad.geoshapes.persistence;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import it.unisa.diem.sad.geoshapes.perstistence.PersistenceService;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceServiceTest {

    private PersistenceService persistenceService;
    private File tempFile; // file temporaneo per il test

    // static perché altrimenti non potrebbe essere serializzata
    public static class TestShape implements MyShape {
        private String id;
        private double x, y;

        public TestShape(String id) {
            this.id = id;
        }

        @Override
        public void setEndPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public MyColor getBorderColor() {
            return null;
        }

        @Override
        public MyColor getFillColor() {
            return null;
        }

        @Override
        public void setBorderColor(MyColor color) {

        }

        @Override
        public void setFillColor(MyColor color) {

        }

        public String getId() {
            return id;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof TestShape)) return false;
            TestShape other = (TestShape) obj;
            return Objects.equals(id, other.id) &&
                    Double.compare(x, other.x) == 0 &&
                    Double.compare(y, other.y) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, x, y);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        persistenceService = new PersistenceService();
        tempFile = File.createTempFile("testDrawing", ".geo");
        tempFile.deleteOnExit();
    }

    @Test
    void testSaveAndLoadDrawing() throws Exception {
        List<MyShape> originalShapes = new ArrayList<>();
        TestShape shape1 = new TestShape("circle-1");
        shape1.setEndPoint(10, 10);
        TestShape shape2 = new TestShape("ellipse-1");
        shape2.setEndPoint(20, 30);
        originalShapes.add(shape1);
        originalShapes.add(shape2);

        persistenceService.saveDrawing(originalShapes, tempFile); //Salva la lista di forme nel file temporaneo
        List<MyShape> loadedShapes = persistenceService.loadDrawing(tempFile); //Ricarica la lista di forme dal file

        //Verifica che il numero e il contenuto delle forme caricate sia identico a quello salvato
        assertEquals(originalShapes.size(), loadedShapes.size(), "Number of shapes should match");
        assertEquals(originalShapes, loadedShapes, "Shapes should match after loading");
    }

    @Test
    void testLoadInvalidFileFormat() throws Exception {
        try (var writer = new java.io.FileWriter(tempFile)) {
            writer.write("Not a serialized shape");
        }

        assertThrows(IOException.class, () -> {
            persistenceService.loadDrawing(tempFile);
        });
    }

    //Cancella il file temporaneo se ancora esiste
    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }
}*/