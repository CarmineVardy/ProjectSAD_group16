package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class ShapeMapping {

    // Manteniamo due liste parallele per efficienza nelle ricerche bidirezionali
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

    public void clear() {
        modelShapes.clear();
        viewShapes.clear();
    }

    public void rebuildMapping(List<MyShape> orderedModelShapes, List<Shape> orderedViewShapes) {
        if (orderedModelShapes.size() != orderedViewShapes.size()) {
            throw new IllegalArgumentException("The lists of model and view must have same size");
        }

        clear();

        for (int i = 0; i < orderedModelShapes.size(); i++) {
            modelShapes.add(orderedModelShapes.get(i));
            viewShapes.add(orderedViewShapes.get(i));
        }
    }

    public int size() {
        return modelShapes.size();
    }

    public boolean isEmpty() {
        return modelShapes.isEmpty();
    }
}