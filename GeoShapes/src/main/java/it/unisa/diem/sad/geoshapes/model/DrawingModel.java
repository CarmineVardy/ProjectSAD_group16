package it.unisa.diem.sad.geoshapes.model;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.observer.ShapeObserver;
import it.unisa.diem.sad.geoshapes.observer.ShapeSubject;

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

    public void modifyShape(MyShape oldShape, MyShape newShape) {
        oldShape.setStartX(newShape.getStartX());
        oldShape.setStartY(newShape.getStartY());
        oldShape.setEndX(newShape.getEndX());
        oldShape.setEndY(newShape.getEndY());
        oldShape.setBorderColor(newShape.getBorderColor());
        oldShape.setFillColor(newShape.getFillColor());
        notifyObservers("MODIFY", oldShape);

    }

    public void clearShapes() {
        shapes.clear();
        notifyObservers("CLEARALL", null);
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