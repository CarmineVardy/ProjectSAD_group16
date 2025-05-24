package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShapeMapping {

    private Map<MyShape, Shape> modelToView;

    public ShapeMapping() {
        this.modelToView = new LinkedHashMap<>();
    }

    public Shape getViewShape(MyShape modelShape) {
        return modelToView.get(modelShape);
    }

    public MyShape getModelShape(Shape viewShape) {
        for (Map.Entry<MyShape, Shape> entry : modelToView.entrySet()) {
            if (entry.getValue().equals(viewShape)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<MyShape> getModelShapes() {
        return new ArrayList<>(modelToView.keySet());
    }

    public List<Shape> getViewShapes() {
        return new ArrayList<>(modelToView.values());
    }

    public void register(MyShape modelShape, Shape viewShape) {
        modelToView.put(modelShape, viewShape);
    }

    public void unregister(MyShape modelShape) {
        modelToView.remove(modelShape);
    }

    public void updateViewMapping(MyShape modelShape, Shape newViewShape) {
        modelToView.put(modelShape, newViewShape);
    }

    public void clear() {
        modelToView.clear();
    }

}