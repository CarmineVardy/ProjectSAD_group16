package it.unisa.diem.sad.geoshapes.controller.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;

public class SendToBackCommand implements Command{

    private final DrawingModel model;
    private final MyShape shape;

    public SendToBackCommand(DrawingModel model, MyShape shape) {
        this.model = model;
        this.shape = shape;
    }

    @Override
    public void execute() {
        model.sendToBack(shape);
    }

    @Override
    public void undo() {
        model.bringToFront(shape);
    }
}