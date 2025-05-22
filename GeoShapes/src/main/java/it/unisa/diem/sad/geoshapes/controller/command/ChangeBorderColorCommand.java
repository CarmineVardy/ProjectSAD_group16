package it.unisa.diem.sad.geoshapes.controller.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.MyColor;

public class ChangeBorderColorCommand implements Command{

    private final DrawingModel model;
    private final MyShape shape;
    private final MyColor oldColor;
    private final MyColor newColor;

    public ChangeBorderColorCommand(DrawingModel model, MyShape shape, MyColor color) {
        this.model = model;
        this.shape = shape;
        this.oldColor = shape.getBorderColor();
        this.newColor = color;
    }

    @Override
    public void execute() {
        model.changeBorderColor(shape, newColor);
    }

}
