package it.unisa.diem.sad.geoshapes.command;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;
import java.util.ArrayList;

public class BringToTopCommand implements Command {
    private final DrawingModel model;
    private final List<MyShape> shapes;
    private final List<Integer> originalIndices;

    public BringToTopCommand(DrawingModel model, List<MyShape> shapes) {
        this.model = model;
        this.shapes = shapes;
        this.originalIndices = new ArrayList<>();
    }

    @Override
    public void execute() {
        originalIndices.clear();
        for (MyShape shape : shapes) {
            originalIndices.add(model.getShapes().indexOf(shape));
            model.bringToTop(shape);
        }
    }

    @Override
    public void undo() {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            model.moveToPosition(shapes.get(i), originalIndices.get(i));
        }
    }
}