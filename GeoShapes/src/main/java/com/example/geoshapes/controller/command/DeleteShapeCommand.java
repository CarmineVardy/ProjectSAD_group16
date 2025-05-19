package com.example.geoshapes.controller.command;

import com.example.geoshapes.model.DrawingModel;
import com.example.geoshapes.model.shapes.MyShape;

public class DeleteShapeCommand implements Command{

    private final DrawingModel model;
    private final MyShape shape;

    public DeleteShapeCommand(DrawingModel model, MyShape shape) {
        this.model = model;
        this.shape = shape;
    }

    @Override
    public void execute() {
        model.removeShape(shape);

    }
}
