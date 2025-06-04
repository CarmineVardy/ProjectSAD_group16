package it.unisa.diem.sad.geoshapes.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;

import java.util.List;
import java.util.ArrayList;

/**
 * A concrete command that creates and adds one or more shapes to the drawing model.
 * This command encapsulates the action of adding shapes, and it can be undone.
 */
public class CreateShapeCommand implements Command {

    // Private instance variables
    private final DrawingModel model;
    private final List<MyShape> shapes;

    /**
     * Constructs a {@code CreateShapeCommand} to add a single shape to the model.
     *
     * @param model The {@link DrawingModel} on which the command will operate.
     * @param shape The single {@link MyShape} object to be added.
     */
    public CreateShapeCommand(DrawingModel model, MyShape shape) {
        this.model = model;
        this.shapes = new ArrayList<>();
        this.shapes.add(shape);
    }

    /**
     * Constructs a {@code CreateShapeCommand} to add multiple shapes to the model.
     * A defensive copy of the provided list of shapes is created.
     *
     * @param model The {@link DrawingModel} on which the command will operate.
     * @param shapes The {@code List} of {@link MyShape} objects to be added.
     */
    public CreateShapeCommand(DrawingModel model, List<MyShape> shapes) {
        this.model = model;
        this.shapes = new ArrayList<>(shapes); // Defensive copy
    }

    /**
     * Executes the command, adding all shapes in the list to the drawing model.
     * The shapes are added in their original order within the list.
     */
    @Override
    public void execute() {
        for (MyShape shape : shapes) {
            model.addShape(shape);
        }
    }

    /**
     * Undoes the command, removing all shapes that were added during execution from the drawing model.
     * The shapes are removed in reverse order to ensure correct state if dependencies exist.
     */
    @Override
    public void undo() {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            model.removeShape(shapes.get(i));
        }
    }
}