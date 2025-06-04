package it.unisa.diem.sad.geoshapes.command;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;

/**
 * A concrete command that flips a list of shapes vertically in the drawing model.
 * This command encapsulates the action of vertical flipping, and it can be undone
 * (which effectively performs another vertical flip, returning the shape to its original orientation).
 */
public class FlipVShapeCommand implements Command {

    // Private instance variables
    private final DrawingModel model;
    private final List<MyShape> shapes;

    /**
     * Constructs a new {@code FlipVShapeCommand}.
     *
     * @param model The {@link DrawingModel} on which the command will operate.
     * @param shapes The {@code List} of {@link MyShape} objects to be flipped vertically.
     */
    public FlipVShapeCommand(DrawingModel model, List<MyShape> shapes) {
        this.model = model;
        this.shapes = shapes;
    }

    /**
     * Executes the command, flipping each shape in the list vertically.
     */
    @Override
    public void execute() {
        for (MyShape shape : shapes) {
            model.flipVertical(shape);
        }
    }

    /**
     * Undoes the command. For a vertical flip, undoing is equivalent to
     * performing another vertical flip, returning the shape to its initial orientation.
     */
    @Override
    public void undo() {
        // Vertical flip is its own inverse, so undoing simply re-executes the flip.
        execute();
    }
}