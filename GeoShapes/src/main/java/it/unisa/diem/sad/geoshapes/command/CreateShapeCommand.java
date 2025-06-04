package it.unisa.diem.sad.geoshapes.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;

import java.util.List;
import java.util.ArrayList;

public class CreateShapeCommand implements Command {

    private final DrawingModel model;
    private final List<MyShape> shapes;

    public CreateShapeCommand(DrawingModel model, MyShape shape) {
        this.model = model;
        this.shapes = new ArrayList<>();
        this.shapes.add(shape);
    }

    public CreateShapeCommand(DrawingModel model, List<MyShape> shapes) {
        this.model = model;
        this.shapes = new ArrayList<>(shapes); // Copia difensiva
    }

    @Override
    public void execute() {
        for (MyShape shape : shapes) {
            model.addShape(shape);
        }
    }

    @Override
    public void undo() {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            model.removeShape(shapes.get(i));
        }
    }
}