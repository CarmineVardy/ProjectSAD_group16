package it.unisa.diem.sad.geoshapes.model;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.observer.ShapeObserver;
import it.unisa.diem.sad.geoshapes.observer.ShapeSubject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawingModel implements ShapeSubject {

    private final List<MyShape> shapes;
    private final List<ShapeObserver> observers;
    private static int idCounter = 0;
    private List<MyShape> selectedShapes = new ArrayList<>();

    public DrawingModel() {
        shapes = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public void addShape(MyShape myShape) {
        idCounter++;
        String shapeName = myShape.getShapeType() + " " + idCounter;
        myShape.setName(shapeName);

        shapes.add(myShape);
        notifyObservers();
    }

    public void removeShape(MyShape myShape) {
        shapes.remove(myShape);
        notifyObservers();
    }

    public void modifyShape(MyShape oldShape, MyShape newShape) {
        oldShape.setStartX(newShape.getStartX());
        oldShape.setStartY(newShape.getStartY());
        oldShape.setEndX(newShape.getEndX());
        oldShape.setEndY(newShape.getEndY());
        oldShape.setBorderColor(newShape.getBorderColor());
        oldShape.setFillColor(newShape.getFillColor());
        oldShape.setRotation(newShape.getRotation());

        notifyObservers();
    }

    public void bringToFront(MyShape myShape) {
        if (shapes.contains(myShape)) {
            int currentIndex = shapes.indexOf(myShape);

            if (currentIndex < shapes.size() - 1) {
                shapes.remove(currentIndex);
                shapes.add(currentIndex + 1, myShape);

                notifyObservers();
            } else {
                System.out.println("Shape is already at the front");
            }
        } else {
            System.out.println("Shape not found in the model");
        }
    }

    public void sendToBack(MyShape myShape) {
        if (shapes.contains(myShape)) {
            int currentIndex = shapes.indexOf(myShape);

            if (currentIndex > 0) {
                shapes.remove(currentIndex);
                shapes.add(currentIndex - 1, myShape);

                notifyObservers();
            } else {
                System.out.println("Shape is already at the back");
            }
        } else {
            System.out.println("Shape not found in the model");
        }
    }

    public void bringToTop(MyShape myShape) {
        if (shapes.contains(myShape)) {
            shapes.remove(myShape);
            shapes.add(myShape); // Aggiunge alla fine
            notifyObservers();
        } else {
            System.out.println("Shape not found in the model");
        }
    }

    public void sendToBottom(MyShape myShape) {
        if (shapes.contains(myShape)) {
            shapes.remove(myShape);
            shapes.add(0, myShape); // Inserisce all'inizio
            notifyObservers();
        } else {
            System.out.println("Shape not found in the model");
        }
    }

    public void moveToPosition(MyShape myShape, int targetIndex) {
        if (shapes.contains(myShape) && targetIndex >= 0 && targetIndex < shapes.size()) {
            shapes.remove(myShape);
            shapes.add(targetIndex, myShape);
            notifyObservers();
        } else {
            System.out.println("Invalid position or shape not found");
        }
    }

    public void clearShapes() {
        shapes.clear();
        notifyObservers();
    }

    public void printAllShapes() {
        System.out.println("Forme nel modello:");
        for (int i = shapes.size() - 1; i >= 0; i--) {
            System.out.println(shapes.get(i));
        }

    }

    public List<MyShape> getShapes() {
        return new ArrayList<>(shapes);
    }

    public List<MyShape> getShapesReversed() {
        List<MyShape> reversedShapes = new ArrayList<>(shapes);
        Collections.reverse(reversedShapes);
        return reversedShapes;
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
    public void notifyObservers() {
        for (ShapeObserver observer : observers) {
            observer.update();
        }
    }

    public List<MyShape> getSelectedShapes() {
        return selectedShapes;
    }
}