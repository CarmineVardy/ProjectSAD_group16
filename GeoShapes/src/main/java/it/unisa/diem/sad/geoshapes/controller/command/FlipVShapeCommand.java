package it.unisa.diem.sad.geoshapes.controller.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;

public class FlipVShapeCommand implements Command {
    private final DrawingModel model;
    private final List<MyShape> shapes;

    public FlipVShapeCommand(DrawingModel model, List<MyShape> shapes) {
        this.model = model;
        this.shapes = shapes;
    }

    @Override
    public void execute() {

        model.flipVertical(shapes);
    }

    @Override
    public void undo() {
        execute();
    }
}