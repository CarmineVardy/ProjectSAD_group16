
package it.unisa.diem.sad.geoshapes.controller.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;

public class RotateShapeCommand implements Command {
    private final DrawingModel model;
    private final MyShape shape;
    private final double oldAngle;
    private final double newAngle;

    /**
     * Costruttore per il comando di rotazione.
     * @param model Il modello che contiene le forme
     * @param shape La forma del modello da ruotare
     * @param oldAngle L'angolo originale
     * @param newAngle Il nuovo angolo di rotazione
     */
    public RotateShapeCommand(DrawingModel model, MyShape shape, double oldAngle, double newAngle) {
        this.model = model;
        this.shape = shape;
        this.oldAngle = oldAngle;
        this.newAngle = newAngle;
    }

    @Override
    public void execute() {
        shape.setRotation(newAngle);
        model.notifyObservers(); // Notifica i cambiamenti agli observer
    }

    @Override
    public void undo() {
        shape.setRotation(oldAngle);
        model.notifyObservers(); // Notifica i cambiamenti agli observer
    }
}