package it.unisa.diem.sad.geoshapes.command;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;

/**
 * A concrete command that deletes one or more shapes from the drawing model.
 * This command encapsulates the action of removing shapes, and it can be undone
 * to restore the deleted shapes.
 */
public class DeleteShapeCommand implements Command {

    // Private instance variables
    private final DrawingModel model;
    private final List<MyShape> shapes;

    /**
     * Constructs a new {@code DeleteShapeCommand}.
     *
     * @param model The {@link DrawingModel} on which the command will operate.
     * @param shapes The {@code List} of {@link MyShape} objects to be deleted.
     */
    public DeleteShapeCommand(DrawingModel model, List<MyShape> shapes) {
        this.model = model;
        this.shapes = shapes;
    }

    /**
     * Executes the command, removing each shape in the list from the drawing model.
     * The shapes are removed in their original order within the list.
     */
    @Override
    public void execute() {
        for (MyShape shape : shapes) {
            model.removeShape(shape);
        }
    }

    /**
     * Undoes the command, re-adding all previously deleted shapes to the drawing model.
     * The shapes are re-added in reverse order to ensure correct Z-order if dependencies exist.
     */
    @Override
    public void undo() {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            model.addShape(shapes.get(i));
        }
    }
}