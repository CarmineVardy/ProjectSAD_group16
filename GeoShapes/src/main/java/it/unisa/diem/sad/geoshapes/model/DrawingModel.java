package it.unisa.diem.sad.geoshapes.model;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.observer.ShapeObserver;
import it.unisa.diem.sad.geoshapes.observer.ShapeSubject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the drawing model, managing a collection of geometric shapes.
 * This class acts as the subject in the Observer design pattern, notifying
 * registered observers when the collection of shapes changes.
 */
public class DrawingModel implements ShapeSubject {

    private final List<MyShape> shapes;
    private final List<ShapeObserver> observers;
    private static int idCounter = 0;

    /**
     * Constructs a new {@code DrawingModel}.
     * Initializes empty lists for shapes and observers.
     */
    public DrawingModel() {
        shapes = new ArrayList<>();
        observers = new ArrayList<>();
    }

    /**
     * Returns an unmodifiable list of all shapes in the model.
     *
     * @return A new {@code ArrayList} containing all shapes.
     */
    public List<MyShape> getShapes() {
        return new ArrayList<>(shapes);
    }

    /**
     * Returns an unmodifiable list of all shapes in the model in reverse order.
     *
     * @return A new {@code ArrayList} containing all shapes in reverse order.
     */
    public List<MyShape> getShapesReversed() {
        List<MyShape> reversedShapes = new ArrayList<>(shapes);
        Collections.reverse(reversedShapes);
        return reversedShapes;
    }

    /**
     * Adds a new shape to the drawing model.
     * A unique name is assigned to the shape based on its type and an internal counter.
     * Observers are notified after the shape is added.
     *
     * @param myShape The {@link MyShape} object to add.
     */
    public void addShape(MyShape myShape) {
        idCounter++;
        String shapeName = myShape.getShapeType() + " " + idCounter;
        myShape.setName(shapeName);

        shapes.add(myShape);
        notifyObservers();
    }

    /**
     * Modifies an existing shape in the model with new properties from another shape.
     * The type of the old and new shapes must be the same.
     * Observers are notified after the shape is modified.
     *
     * @param oldShape The original {@link MyShape} to be modified.
     * @param newShape The {@link MyShape} containing the new properties.
     * @throws IllegalArgumentException If the types of the old and new shapes are different.
     */
    public void modifyShape(MyShape oldShape, MyShape newShape) {
        if (!oldShape.getClass().equals(newShape.getClass())) {
            throw new IllegalArgumentException("Cannot modify shape: different types");
        }

        oldShape.setStartX(newShape.getStartX());
        oldShape.setStartY(newShape.getStartY());
        oldShape.setEndX(newShape.getEndX());
        oldShape.setEndY(newShape.getEndY());
        oldShape.setBorderColor(newShape.getBorderColor());
        oldShape.setFillColor(newShape.getFillColor());
        oldShape.setRotation(newShape.getRotation());
        oldShape.setPoints(newShape.getXPoints(), newShape.getYPoints());

        notifyObservers();
    }

    /**
     * Moves a specified shape one level forward in the drawing order (towards the top).
     * Observers are notified if the shape's position changes.
     *
     * @param myShape The {@link MyShape} to bring to front.
     */
    public void bringToFront(MyShape myShape) {
        if (shapes.contains(myShape)) {
            int currentIndex = shapes.indexOf(myShape);

            if (currentIndex < shapes.size() - 1) {
                shapes.remove(currentIndex);
                shapes.add(currentIndex + 1, myShape);

                notifyObservers();
            } else {
                System.out.println("Shape is already at the front"); // Debugging message
            }
        } else {
            System.out.println("Shape not found in the model"); // Debugging message
        }
    }

    /**
     * Moves a specified shape one level backward in the drawing order (towards the bottom).
     * Observers are notified if the shape's position changes.
     *
     * @param myShape The {@link MyShape} to send to back.
     */
    public void sendToBack(MyShape myShape) {
        if (shapes.contains(myShape)) {
            int currentIndex = shapes.indexOf(myShape);

            if (currentIndex > 0) {
                shapes.remove(currentIndex);
                shapes.add(currentIndex - 1, myShape);

                notifyObservers();
            } else {
                System.out.println("Shape is already at the back"); // Debugging message
            }
        } else {
            System.out.println("Shape not found in the model"); // Debugging message
        }
    }

    /**
     * Moves a specified shape to the very top of the drawing order.
     * Observers are notified if the shape's position changes.
     *
     * @param myShape The {@link MyShape} to bring to top.
     */
    public void bringToTop(MyShape myShape) {
        if (shapes.contains(myShape)) {
            shapes.remove(myShape);
            shapes.add(myShape);
            notifyObservers();
        } else {
            System.out.println("Shape not found in the model");
        }
    }

    /**
     * Moves a specified shape to the very bottom of the drawing order.
     * Observers are notified if the shape's position changes.
     *
     * @param myShape The {@link MyShape} to send to bottom.
     */
    public void sendToBottom(MyShape myShape) {
        if (shapes.contains(myShape)) {
            shapes.remove(myShape);
            shapes.add(0, myShape);
            notifyObservers();
        } else {
            System.out.println("Shape not found in the model");
        }
    }

    /**
     * Moves a specified shape to a target position in the drawing order.
     * Observers are notified if the shape's position changes.
     *
     * @param myShape The {@link MyShape} to move.
     * @param targetIndex The desired index for the shape in the list.
     */
    public void moveToPosition(MyShape myShape, int targetIndex) {
        if (shapes.contains(myShape) && targetIndex >= 0 && targetIndex < shapes.size()) {
            shapes.remove(myShape);
            shapes.add(targetIndex, myShape);
            notifyObservers();
        } else {
            System.out.println("Invalid position or shape not found"); // Debugging message
        }
    }

    /**
     * Flips the specified shape horizontally.
     * Observers are notified after the shape is flipped.
     *
     * @param myShape The {@link MyShape} to flip horizontally.
     */
    public void flipHorizontal(MyShape myShape) {
        if (myShape == null) {
            return;
        }
        if (shapes.contains(myShape)) {
            myShape.flipHorizontal();
        }

        notifyObservers();
    }

    /**
     * Flips the specified shape vertically.
     * Observers are notified after the shape is flipped.
     *
     * @param myShape The {@link MyShape} to flip vertically.
     */
    public void flipVertical(MyShape myShape) {
        if (myShape == null) {
            return;
        }
        if (shapes.contains(myShape)) {
            myShape.flipVertical();
        }

        notifyObservers();
    }

    /**
     * Removes a specified shape from the drawing model.
     * Observers are notified after the shape is removed.
     *
     * @param myShape The {@link MyShape} object to remove.
     */
    public void removeShape(MyShape myShape) {
        shapes.remove(myShape);
        notifyObservers();
    }

    /**
     * Clears all shapes from the drawing model.
     * Observers are notified after all shapes are cleared.
     */
    public void clearShapes() {
        shapes.clear();
        notifyObservers();
    }

    /**
     * Prints all shapes currently in the model to the console.
     * Shapes are printed in reverse order of their appearance in the internal list.
     */
    public void printAllShapes() {
        System.out.println("Forme nel modello:");
        for (int i = shapes.size() - 1; i >= 0; i--) {
            System.out.println(shapes.get(i));
        }

    }

    /**
     * Attaches a {@link ShapeObserver} to this subject.
     * The observer will be notified of future changes in the model.
     *
     * @param observer The {@link ShapeObserver} to attach.
     */
    @Override
    public void attach(ShapeObserver observer) {
        observers.add(observer);
    }

    /**
     * Detaches a {@link ShapeObserver} from this subject.
     * The observer will no longer be notified of changes.
     *
     * @param observer The {@link ShapeObserver} to detach.
     */
    @Override
    public void detach(ShapeObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all attached {@link ShapeObserver}s that the model has changed.
     */
    @Override
    public void notifyObservers() {
        for (ShapeObserver observer : observers) {
            observer.update();
        }
    }

}