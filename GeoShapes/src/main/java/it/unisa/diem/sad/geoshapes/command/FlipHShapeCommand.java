package it.unisa.diem.sad.geoshapes.command;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;

public class FlipHShapeCommand implements Command {
    private final DrawingModel model;
    private final List<MyShape> shapes;

    public FlipHShapeCommand(DrawingModel model, List<MyShape> shapes) {
        this.model = model;
        this.shapes = shapes;
    }

    @Override
    public void execute() {
        for (MyShape shape : shapes) {
            model.flipHorizontal(shape);
        }
    }

    @Override
    public void undo() {
        execute();
    }
}