package com.example.geoshapes.controller.command;

import com.example.geoshapes.model.DrawingModel;
import com.example.geoshapes.model.shapes.MyShape;

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