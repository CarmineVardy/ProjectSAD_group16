package it.unisa.diem.sad.geoshapes.controller.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;

public class CreateShapeCommand implements Command {
    private final DrawingModel model;
    private final MyShape newMyShape;

    public CreateShapeCommand(DrawingModel model, MyShape newMyShape) {
        this.model = model;
        this.newMyShape = newMyShape;
    }

    @Override
    public void execute() {
        model.addShape(newMyShape);
    }
}