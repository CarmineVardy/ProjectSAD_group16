package it.unisa.diem.sad.geoshapes.controller.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;

public class ChangeBorderColorCommand implements Command{

    private final DrawingModel model;
    private final MyShape shape;
    private final MyColor color;

    public ChangeBorderColorCommand(DrawingModel model, MyShape shape, MyColor color) {
        this.model = model;
        this.shape = shape;
        this.color = color;
    }

    @Override
    public void execute() {
        model.changeBorderColor(shape, color);
    }

}
