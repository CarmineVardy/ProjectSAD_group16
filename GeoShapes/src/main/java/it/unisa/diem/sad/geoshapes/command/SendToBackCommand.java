package it.unisa.diem.sad.geoshapes.command;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;

public class SendToBackCommand implements Command {
    private final DrawingModel model;
    private final List<MyShape> shapes;

    public SendToBackCommand(DrawingModel model, List<MyShape> shapes) {
        this.model = model;
        this.shapes = shapes;
    }

    @Override
    public void execute() {
        for (MyShape shape : shapes) {
            model.sendToBack(shape);
        }
    }

    @Override
    public void undo() {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            model.bringToFront(shapes.get(i));
        }
    }
}