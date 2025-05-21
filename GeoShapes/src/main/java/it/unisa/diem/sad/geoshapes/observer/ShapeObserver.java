package it.unisa.diem.sad.geoshapes.observer;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;

public interface ShapeObserver {
    void update(String event, MyShape myShape);
}