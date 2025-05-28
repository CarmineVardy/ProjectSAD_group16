package it.unisa.diem.sad.geoshapes.controller.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;

public class BringToTopCommand implements Command {

    private final DrawingModel model;
    private final MyShape shape;
    private int originalIndex;

    public BringToTopCommand(DrawingModel model, MyShape shape) {
        this.model = model;
        this.shape = shape;
    }

    @Override
    public void execute() {
        // Salva l'indice originale prima di eseguire il comando
        originalIndex = model.getShapes().indexOf(shape);
        model.bringToTop(shape);
    }

    @Override
    public void undo() {
        // Usa un metodo del model per riportare alla posizione originale
        model.moveToPosition(shape, originalIndex);
    }
}