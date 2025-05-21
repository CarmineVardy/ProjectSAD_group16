package it.unisa.diem.sad.geoshapes.perstistence;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PersistenceService {

    private File currentFile;

    public PersistenceService() {
        this.currentFile = null;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }

    public void saveDrawing(List<MyShape> shapes, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(shapes);
        }
    }

    public List<MyShape> loadDrawing(File file) throws IOException, ClassNotFoundException {
        this.currentFile = file;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object data = ois.readObject();
            if (!(data instanceof List<?>)) {
                if (data == null) {
                    return new ArrayList<>();
                }
                throw new IOException("File content is not a List of Shapes.");
            }
            List<?> rawList = (List<?>) data;
            List<MyShape> shapeList = new ArrayList<>(rawList.size());
            for (Object item : rawList) {
                if (item instanceof MyShape) {
                    shapeList.add((MyShape) item);
                } else {
                    throw new IOException("File contains non-MyShape data in the list.");
                }
            }
            return shapeList;
        }
    }
}