package it.unisa.diem.sad.geoshapes.command;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;

/**
 * A concrete command that flips a list of shapes horizontally in the drawing model.
 * This command encapsulates the action of horizontal flipping, and it can be undone
 * (which effectively performs another horizontal flip, returning the shape to its original orientation).
 */
public class FlipHShapeCommand implements Command {

    // Private instance variables
    private final DrawingModel model;
    private final List<MyShape> shapes;

    /**
     * Constructs a new {@code FlipHShapeCommand}.
     *
     * @param model The {@link DrawingModel} on which the command will operate.
     * @param shapes The {@code List} of {@link MyShape} objects to be flipped horizontally.
     */
    public FlipHShapeCommand(DrawingModel model, List<MyShape> shapes) {
        this.model = model;
        this.shapes = shapes;
    }

    /**
     * Executes the command, flipping each shape in the list horizontally.
     */
    @Override
    public void execute() {
        for (MyShape shape : shapes) {
            model.flipHorizontal(shape);
        }
    }

    /**
     * Undoes the command. For a horizontal flip, undoing is equivalent to
     * performing another horizontal flip, returning the shape to its initial orientation.
     */
    @Override
    public void undo() {
        // Horizontal flip is its own inverse, so undoing simply re-executes the flip.
        execute();
    }
}