package it.unisa.diem.sad.geoshapes.persistence;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.MyColor;
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
    public static class TestShape extends MyShape {
        private String id;

        public TestShape(String id) {
            super(0, 0, 0, 0, null, null);
            this.id = id;
        }

        public void setEndPoint(double x, double y) {
            this.setEndX(x);
            this.setEndY(y);
        }

        public String getId() {
            return id;
        }

        @Override
        public MyColor getBorderColor() {
            return super.getBorderColor();
        }

        @Override
        public MyColor getFillColor() {
            return super.getFillColor();
        }

        @Override
        public String getShapeType() {
            return "";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof TestShape)) return false;

            TestShape other = (TestShape) obj;
            return Objects.equals(id, other.id) &&
                    Double.compare(getEndX(), other.getEndX()) == 0 &&
                    Double.compare(getEndY(), other.getEndY()) == 0 &&
                    Objects.equals(getBorderColor(), other.getBorderColor()) &&
                    Objects.equals(getFillColor(), other.getFillColor());
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, getEndX(), getEndY(), getBorderColor(), getFillColor());
        }

        @Override
        public void moveBy(double dx, double dy) {}
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

        persistenceService.saveDrawing(originalShapes, tempFile);
        List<MyShape> loadedShapes = persistenceService.loadDrawing(tempFile);

        assertEquals(originalShapes.size(), loadedShapes.size(), "Number of shapes must match");

        for (int i = 0; i < originalShapes.size(); i++) {
            TestShape original = (TestShape) originalShapes.get(i);
            TestShape loaded = (TestShape) loadedShapes.get(i);

            assertEquals(original.getId(), loaded.getId(), "ID must match");
            assertEquals(original.getEndX(), loaded.getEndX(), "EndX must match");
            assertEquals(original.getEndY(), loaded.getEndY(), "EndY must match");
            assertEquals(original, loaded, "Shapes must be equal");
        }
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
}
