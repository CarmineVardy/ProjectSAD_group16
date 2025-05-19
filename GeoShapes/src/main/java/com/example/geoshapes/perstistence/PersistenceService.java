package com.example.geoshapes.service;

import com.example.geoshapes.model.shapes.MyShape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.ArrayList;

public class PersistenceService {


    public void saveDrawing(File file, List<MyShape> myShapes) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(myShapes);
        }
    }

    @SuppressWarnings("unchecked") // Per il cast da Object a List<MyShape>
    public List<MyShape> loadDrawing(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object data = ois.readObject();
            if (data instanceof List) {
                // Verifica che tutti gli elementi siano MyShape (opzionale, ma più sicuro)
                for (Object item : (List<?>) data) {
                    if (!(item instanceof MyShape)) {
                        throw new IOException("File contains non-MyShape data in the list.");
                    }
                }
                return (List<MyShape>) data;
            } else if (data == null) {
                return new ArrayList<>(); // File vuoto o solo null scritto
            }
            throw new IOException("File content is not a List of Shapes.");
        }
    }
}
