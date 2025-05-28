package it.unisa.diem.sad.geoshapes.observer;

public interface ShapeSubject {
    void attach(ShapeObserver observer);
    void detach(ShapeObserver observer);
    void notifyObservers();
}