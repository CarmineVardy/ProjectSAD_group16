package it.unisa.diem.sad.geoshapes.command;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;

/**
 * A concrete command that modifies one or more existing shapes in the drawing model.
 * This command encapsulates the action of updating shape properties, and it can be undone
 * to revert the shapes to their previous state.
 */
public class ModifyShapeCommand implements Command {

    // Private instance variables
    private final DrawingModel model;
    private final List<MyShape> targetShapes;
    private final List<MyShape> oldShapes;
    private final List<MyShape> newShapes;

    /**
     * Constructs a new {@code ModifyShapeCommand}.
     *
     * @param model The {@link DrawingModel} on which the command will operate.
     * @param targetShapes A list of the actual {@link MyShape} objects in the model that are being modified.
     * @param oldShapes A list of {@link MyShape} objects representing the state of {@code targetShapes} before modification.
     * @param newShapes A list of {@link MyShape} objects representing the desired new state for {@code targetShapes}.
     */
    public ModifyShapeCommand(DrawingModel model, List<MyShape> targetShapes,
                              List<MyShape> oldShapes, List<MyShape> newShapes) {
        this.model = model;
        this.targetShapes = targetShapes;
        this.oldShapes = oldShapes;
        this.newShapes = newShapes;
    }

    /**
     * Executes the command, applying the new properties from {@code newShapes} to {@code targetShapes}
     * within the drawing model.
     * Shapes are modified in the order they appear in the {@code targetShapes} list.
     */
    @Override
    public void execute() {
        for (int i = 0; i < targetShapes.size(); i++) {
            model.modifyShape(targetShapes.get(i), newShapes.get(i));
        }
    }

    /**
     * Undoes the command, restoring the original properties from {@code oldShapes} to {@code targetShapes}
     * within the drawing model.
     * Shapes are restored in the order they appear in the {@code targetShapes} list.
     */
    @Override
    public void undo() {
        for (int i = 0; i < targetShapes.size(); i++) {
            model.modifyShape(targetShapes.get(i), oldShapes.get(i));
        }
    }
}