package it.unisa.diem.sad.geoshapes.command;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;

/**
 * A concrete command that sends a list of shapes one level backward in the drawing order.
 * This command encapsulates the action of changing the Z-order of shapes within the {@link DrawingModel},
 * and it can be undone.
 */
public class SendToBackCommand implements Command {

    // Private instance variables
    private final DrawingModel model;
    private final List<MyShape> shapes;

    /**
     * Constructs a new {@code SendToBackCommand}.
     *
     * @param model The {@link DrawingModel} on which the command will operate.
     * @param shapes The {@code List} of {@link MyShape} objects to send to the back.
     */
    public SendToBackCommand(DrawingModel model, List<MyShape> shapes) {
        this.model = model;
        this.shapes = shapes;
    }

    /**
     * Executes the command, sending each shape in the list one level backward in the drawing order.
     * The shapes are processed in their original order within the list.
     */
    @Override
    public void execute() {
        for (MyShape shape : shapes) {
            model.sendToBack(shape);
        }
    }

    /**
     * Undoes the command, bringing each shape forward one level from its new position.
     * The shapes are undone in reverse order to maintain correct Z-order.
     */
    @Override
    public void undo() {
        // Undo in reverse order to correctly revert z-order changes if multiple shapes were moved.
        for (int i = shapes.size() - 1; i >= 0; i--) {
            model.bringToFront(shapes.get(i));
        }
    }
}