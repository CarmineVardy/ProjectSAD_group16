package com.example.geoshapes.model;

import com.example.geoshapes.model.shapes.MyShape;
import com.example.geoshapes.observer.ShapeObserver;
import com.example.geoshapes.observer.ShapeSubject;

import java.util.ArrayList;
import java.util.List;

public class DrawingModel implements ShapeSubject {

    private final List<MyShape> shapes;
    private final List<ShapeObserver> observers;

    public DrawingModel() {
        shapes = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public void addShape(MyShape myShape) {
        shapes.add(myShape);
        notifyObservers(myShape);
    }

    public void clearShapes() {
        shapes.clear();
        // Notifica gli observer che il modello è stato pulito.
        // Un modo semplice è notificare con un "null" o un oggetto speciale,
        // oppure aggiungere un metodo specifico all'observer.
        // Per ora, il controller gestirà la pulizia della vista direttamente.
        // Potremmo aggiungere: notifyClear(); se avessimo un metodo onClear() in ShapeObserver.
    }

    public List<MyShape> getShapes() {
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
    public void notifyObservers(MyShape myShape) {
        for (ShapeObserver observer : observers) {
            observer.update(myShape);
        }
    }
}