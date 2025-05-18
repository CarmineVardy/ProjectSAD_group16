package com.example.geoshapes.controller.command;

import com.example.geoshapes.model.DrawingModel;
import com.example.geoshapes.controller.strategy.ToolStrategy;
import com.example.geoshapes.model.shapes.Shape;

public class CreateShapeCommand implements Command {
    private final DrawingModel model;
    private final Shape newShape;

    public CreateShapeCommand(DrawingModel model, Shape newShape) {
        this.model = model;
        this.newShape = newShape;
    }

    @Override
    public void execute() {
        model.addShape(newShape);
    }
}