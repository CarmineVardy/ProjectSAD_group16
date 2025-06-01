package it.unisa.diem.sad.geoshapes.controller.command;

import it.unisa.diem.sad.geoshapes.model.shapes.MyText;

public class MoveTextCommand implements Command {
    private final MyText model;
    private final double oldX, oldY;
    private final double newX, newY;

    public MoveTextCommand(MyText model, double oldX, double oldY, double newX, double newY) {
        this.model = model;
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
    }

    @Override
    public void execute() {
        model.setStartX(newX);
        model.setStartY(newY);
        model.setEndX(newX);
        model.setEndY(newY);
        System.out.println("✔ MoveTextCommand.execute: new=(" + newX + "," + newY + ")");
    }

    @Override
    public void undo() {
        model.setStartX(oldX);
        model.setStartY(oldY);
        model.setEndX(oldX);
        model.setEndY(oldY);
        System.out.println("↩ MoveTextCommand.undo: restored=(" + oldX + "," + oldY + ")");
    }
}