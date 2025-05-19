package com.example.geoshapes.observer;

import com.example.geoshapes.model.shapes.MyShape;

public interface ShapeSubject {
    void attach(ShapeObserver observer);

    void detach(ShapeObserver observer);

    void notifyObservers(String event, MyShape myShape);
}