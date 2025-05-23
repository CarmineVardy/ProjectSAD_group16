package it.unisa.diem.sad.geoshapes.controller.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;


public class ModifyShapeCommand implements Command {

    private final DrawingModel model;
    private final MyShape oldShape;
    private final MyShape newShape;

    public ModifyShapeCommand( DrawingModel model, MyShape oldShape, MyShape newShape){
        this.model = model;
        this.oldShape = oldShape;
        this.newShape = newShape;

    }

    @Override
    public void execute() {
        model.modifyShape(oldShape, newShape);
    }




}