package com.example.geoshapes.model;

import com.example.geoshapes.observer.ShapeObserver;
import com.example.geoshapes.observer.ShapeSubject;
import com.example.geoshapes.model.shapes.Shape;

import java.util.ArrayList;
import java.util.List;

public class DrawingModel implements ShapeSubject {

    private final List<Shape> shapes;
    private final List<ShapeObserver> observers;

    public DrawingModel() {
        shapes = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
        notifyObservers(shape);
    }

    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }

    @Override
    public void attach(ShapeObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(ShapeObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Shape shape) {
        for (ShapeObserver observer : observers) {
            observer.update(shape);
        }
    }
}