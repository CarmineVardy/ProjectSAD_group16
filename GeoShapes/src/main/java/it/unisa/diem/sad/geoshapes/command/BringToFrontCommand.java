package it.unisa.diem.sad.geoshapes.command;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;

/**
 * A concrete command that brings a list of shapes one level forward in the drawing order.
 * This command encapsulates the action of changing the Z-order of shapes within the {@link DrawingModel},
 * and it can be undone.
 */
public class BringToFrontCommand implements Command {

    // Private instance variables
    private final DrawingModel model;
    private final List<MyShape> shapes;

    /**
     * Constructs a new {@code BringToFrontCommand}.
     *
     * @param model The {@link DrawingModel} on which the command will operate.
     * @param shapes The {@code List} of {@link MyShape} objects to bring to the front.
     */
    public BringToFrontCommand(DrawingModel model, List<MyShape> shapes) {
        this.model = model;
        this.shapes = shapes;
    }

    /**
     * Executes the command, bringing each shape in the list one level forward in the drawing order.
     * The shapes are processed in their original order within the list.
     */
    @Override
    public void execute() {
        for (MyShape shape : shapes) {
            model.bringToFront(shape);
        }
    }

    /**
     * Undoes the command, sending each shape back one level from its new position.
     * The shapes are undone in reverse order to maintain correct Z-order.
     */
    @Override
    public void undo() {
        // Undo in reverse order to correctly revert z-order changes if multiple shapes were moved.
        for (int i = shapes.size() - 1; i >= 0; i--) {
            model.sendToBack(shapes.get(i));
        }
    }
}