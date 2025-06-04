package it.unisa.diem.sad.geoshapes.observer;

/**
 * Represents a subject in the Observer design pattern.
 * Implementations of this interface maintain a list of {@link ShapeObserver} objects
 * and notify them of state changes.
 */
public interface ShapeSubject {
    /**
     * Registers an observer to be notified of changes.
     *
     * @param observer The {@link ShapeObserver} to attach.
     */
    void attach(ShapeObserver observer);

    /**
     * Unregisters an observer, so it will no longer be notified of changes.
     *
     * @param observer The {@link ShapeObserver} to detach.
     */
    void detach(ShapeObserver observer);

    /**
     * Notifies all registered observers about a state change.
     */
    void notifyObservers();
}