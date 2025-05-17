package com.example.geoshapes.command;

import com.example.geoshapes.model.DrawingModel;
import com.example.geoshapes.strategy.ToolStrategy;
import com.example.geoshapes.model.shapes.Shape;

public class CreateShapeCommand implements Command {
    private final DrawingModel model;
    private final ToolStrategy strategy;

    public CreateShapeCommand(DrawingModel model, ToolStrategy strategy) {
        this.model = model;
        this.strategy = strategy;
    }

    @Override
    public void execute() {
        Shape shape = strategy.getFinalShape();
        model.addShape(shape);
    }
}