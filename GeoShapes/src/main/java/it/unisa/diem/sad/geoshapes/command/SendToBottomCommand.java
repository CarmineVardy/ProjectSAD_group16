package it.unisa.diem.sad.geoshapes.command;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;
import java.util.ArrayList;

/**
 * A concrete command that sends a list of shapes to the very bottom of the drawing order.
 * This command encapsulates the action of changing the Z-order of shapes within the {@link DrawingModel},
 * and it can be undone by restoring the shapes to their original positions.
 */
public class SendToBottomCommand implements Command {

    // Private instance variables
    private final DrawingModel model;
    private final List<MyShape> shapes;
    private final List<Integer> originalIndices; // Stores original positions for undo

    /**
     * Constructs a new {@code SendToBottomCommand}.
     *
     * @param model The {@link DrawingModel} on which the command will operate.
     * @param shapes The {@code List} of {@link MyShape} objects to send to the bottom.
     */
    public SendToBottomCommand(DrawingModel model, List<MyShape> shapes) {
        this.model = model;
        this.shapes = shapes;
        this.originalIndices = new ArrayList<>();
    }

    /**
     * Executes the command, sending each shape in the list to the very bottom of the drawing order.
     * The original indices of the shapes are stored to enable undo functionality.
     */
    @Override
    public void execute() {
        originalIndices.clear(); // Clear previous state before new execution
        // Store original indices before moving shapes
        for (MyShape shape : shapes) {
            originalIndices.add(model.getShapes().indexOf(shape));
            model.sendToBottom(shape);
        }
    }

    /**
     * Undoes the command, restoring each shape to its original position in the drawing order.
     * Shapes are restored in reverse order of their removal to maintain correct Z-order.
     */
    @Override
    public void undo() {
        // Undo in reverse order to correctly restore z-order changes if multiple shapes were moved.
        for (int i = shapes.size() - 1; i >= 0; i--) {
            model.moveToPosition(shapes.get(i), originalIndices.get(i));
        }
    }
}