package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class ShapeMapping {

    private List<MyShape> modelShapes;
    private List<Shape> viewShapes;

    public ShapeMapping() {
        this.modelShapes = new ArrayList<>();
        this.viewShapes = new ArrayList<>();
    }

    public Shape getViewShape(MyShape modelShape) {
        int index = modelShapes.indexOf(modelShape);
        return index != -1 ? viewShapes.get(index) : null;
    }

    public MyShape getModelShape(Shape viewShape) {
        int index = viewShapes.indexOf(viewShape);
        return index != -1 ? modelShapes.get(index) : null;
    }

    public List<MyShape> getModelShapes() {
        return new ArrayList<>(modelShapes);
    }

    public List<Shape> getViewShapes() {
        return new ArrayList<>(viewShapes);
    }

    public void register(MyShape modelShape, Shape viewShape) {
        modelShapes.add(modelShape);
        viewShapes.add(viewShape);
    }

    public void unregister(MyShape modelShape) {
        int index = modelShapes.indexOf(modelShape);
        if (index != -1) {
            modelShapes.remove(index);
            viewShapes.remove(index);
        }
    }

    public void updateViewMapping(MyShape modelShape, Shape newViewShape) {
        int index = modelShapes.indexOf(modelShape);
        if (index != -1) {
            viewShapes.set(index, newViewShape);
        }
    }

    public void clear() {
        modelShapes.clear();
        viewShapes.clear();
    }

    public void moveShape(MyShape modelShape, int newIndex) {
        int currentIndex = modelShapes.indexOf(modelShape);

        if (currentIndex == -1) {
            System.out.println("Shape not found in mapping");
            return;
        }

        if (newIndex < 0 || newIndex >= modelShapes.size() || currentIndex == newIndex) {
            return; // Nessun movimento necessario
        }

        // Sposta in entrambe le liste
        MyShape tempModel = modelShapes.remove(currentIndex);
        Shape tempView = viewShapes.remove(currentIndex);

        modelShapes.add(newIndex, tempModel);
        viewShapes.add(newIndex, tempView);
    }

    public void bringToFront(MyShape modelShape) {
        int currentIndex = modelShapes.indexOf(modelShape);
        if (currentIndex != -1 && currentIndex < modelShapes.size() - 1) {
            moveShape(modelShape, currentIndex + 1);
        }
    }

    public void sendToBack(MyShape modelShape) {
        int currentIndex = modelShapes.indexOf(modelShape);
        if (currentIndex > 0) {
            moveShape(modelShape, currentIndex - 1);
        }
    }


}