package com.example.geoshapes.observer;

import com.example.geoshapes.model.shapes.MyShape;

public interface ShapeObserver {
    void update(String event, MyShape myShape);
}