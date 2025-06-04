package it.unisa.diem.sad.geoshapes.command;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;
import java.util.ArrayList;

/**
 * A concrete command that brings a list of shapes to the very top of the drawing order.
 * This command encapsulates the action of changing the Z-order of shapes within the {@link DrawingModel},
 * and it can be undone by restoring the shapes to their original positions.
 */
public class BringToTopCommand implements Command {

    // Private instance variables
    private final DrawingModel model;
    private final List<MyShape> shapes;
    private final List<Integer> originalIndices;

    /**
     * Constructs a new {@code BringToTopCommand}.
     *
     * @param model The {@link DrawingModel} on which the command will operate.
     * @param shapes The {@code List} of {@link MyShape} objects to bring to the top.
     */
    public BringToTopCommand(DrawingModel model, List<MyShape> shapes) {
        this.model = model;
        this.shapes = shapes;
        this.originalIndices = new ArrayList<>();
    }

    /**
     * Executes the command, bringing each shape in the list to the very top of the drawing order.
     * The original indices of the shapes are stored to enable undo functionality.
     */
    @Override
    public void execute() {
        originalIndices.clear();
        // Store original indices before moving shapes
        for (MyShape shape : shapes) {
            originalIndices.add(model.getShapes().indexOf(shape));
            model.bringToTop(shape);
        }
    }

    /**
     * Undoes the command, restoring each shape to its original position in the drawing order.
     * Shapes are restored in reverse order of processing to maintain correct Z-order.
     */
    @Override
    public void undo() {
        // Undo in reverse order to correctly restore z-order changes if multiple shapes were moved.
        for (int i = shapes.size() - 1; i >= 0; i--) {
            model.moveToPosition(shapes.get(i), originalIndices.get(i));
        }
    }
}