package it.unisa.diem.sad.geoshapes.controller.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;

public class ModifyShapeCommand implements Command {

    private final DrawingModel model;
    private final MyShape targetShape;
    private final MyShape oldShape;
    private final MyShape newShape;

    public ModifyShapeCommand(DrawingModel model, MyShape targetShape, MyShape oldShape, MyShape newShape) {
        this.model = model;
        this.targetShape = targetShape;
        this.oldShape = oldShape;
        this.newShape = newShape;
    }

    @Override
    public void execute() {
        model.modifyShape(targetShape, newShape);
        System.out.println("\nCOMMAND  ANGOLO vechio:  " +oldShape.getRotation());
        System.out.println("\nCOMMAND  ANGOLO nuovo:  " +newShape.getRotation());

    }

    @Override
    public void undo() {
        model.modifyShape(targetShape, oldShape);
    }
}

