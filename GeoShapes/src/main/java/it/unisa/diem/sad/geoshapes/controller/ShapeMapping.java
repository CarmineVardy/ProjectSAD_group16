package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Shape;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ShapeMapping {

    private Map<MyShape, Shape> modelToView;
    private Map<Shape, MyShape> viewToModel;

    public ShapeMapping() {
        this.modelToView = new HashMap<>();
        this.viewToModel = new HashMap<>();
    }

    public Shape getViewShape(MyShape modelShape) {
        return modelToView.get(modelShape);
    }

    public MyShape getModelShape(Shape viewShape) {
        return viewToModel.get(viewShape);
    }

    public boolean hasViewShape(MyShape modelShape) {
        return modelToView.containsKey(modelShape);
    }

    public boolean hasModelShape(Shape viewShape) {
        return viewToModel.containsKey(viewShape);
    }

    public void clear() {
        modelToView.clear();
        viewToModel.clear();
    }

    public void register(MyShape modelShape, Shape viewShape) {
        modelToView.put(modelShape, viewShape);
        viewToModel.put(viewShape, modelShape);
    }

    public void updateViewMapping(MyShape modelShape, Shape newViewShape) {
        Shape oldViewShape = modelToView.get(modelShape);
        if (oldViewShape != null) {
            viewToModel.remove(oldViewShape);
        }
        modelToView.put(modelShape, newViewShape);
        viewToModel.put(newViewShape, modelShape);
    }

    public Set<MyShape> getAllModelShapes() {
        return modelToView.keySet();
    }

    public Set<Shape> getAllViewShapes() {
        return viewToModel.keySet();
    }

    public void unregister(MyShape modelShape) {
        Shape viewShape = modelToView.get(modelShape);
        if (viewShape != null) {
            modelToView.remove(modelShape);
            viewToModel.remove(viewShape);
        }
    }

    public void unregister(Shape viewShape) {
        MyShape modelShape = viewToModel.get(viewShape);
        if (modelShape != null) {
            viewToModel.remove(viewShape);
            modelToView.remove(modelShape);
        }
    }
}