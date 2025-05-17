package com.example.geoshapes.observer;

import com.example.geoshapes.model.shapes.Shape;

public interface ShapeObserver {
    void update(Shape shape);
}