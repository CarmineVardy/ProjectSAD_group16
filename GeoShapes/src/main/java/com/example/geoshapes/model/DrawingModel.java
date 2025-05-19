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
        notifyObservers("CREATE", myShape);
    }

    public void removeShape(MyShape myShape) {
        shapes.remove(myShape);
        notifyObservers("DELETE", myShape);
    }

    /*
    public modifyShape(MyShape myShape){

        //notifyObservers("MODIFY", myShape);
    }
    */


    public void clearShapes() {
        shapes.clear();
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
    public void notifyObservers(String event, MyShape myShape) {
        for (ShapeObserver observer : observers) {
            observer.update(event, myShape);
        }
    }
}