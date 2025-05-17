package com.example.geoshapes.observer;

import com.example.geoshapes.model.shapes.Shape;
import java.util.ArrayList;
import java.util.List;

public interface ShapeSubject {
    void attach(ShapeObserver observer);
    void detach(ShapeObserver observer);
    void notifyObservers(Shape shape);
}