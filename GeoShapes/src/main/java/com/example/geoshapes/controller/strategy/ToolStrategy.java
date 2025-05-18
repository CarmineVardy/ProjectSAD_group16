package com.example.geoshapes.controller.strategy;

import com.example.geoshapes.model.shapes.Shape;
import javafx.scene.input.MouseEvent;

public interface ToolStrategy {

    void handlePressed(MouseEvent event);
    void handleDragged(MouseEvent event);
    void handleReleased(MouseEvent event);

    Shape getFinalShape();

}