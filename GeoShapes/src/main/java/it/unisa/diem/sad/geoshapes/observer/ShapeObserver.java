package it.unisa.diem.sad.geoshapes.observer;

/**
 * Represents an observer in the Observer design pattern.
 * Implementations of this interface are notified when the state of a {@link ShapeSubject} changes.
 */
public interface ShapeObserver {
    /**
     * Called when the subject's state changes.
     * Observers should implement this method to update themselves based on the changes in the subject.
     */
    void update();
}