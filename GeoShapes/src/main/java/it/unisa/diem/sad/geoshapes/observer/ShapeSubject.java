package it.unisa.diem.sad.geoshapes.observer;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;

public interface ShapeSubject {
    void attach(ShapeObserver observer);

    void detach(ShapeObserver observer);

    void notifyObservers(String event, MyShape myShape);
}