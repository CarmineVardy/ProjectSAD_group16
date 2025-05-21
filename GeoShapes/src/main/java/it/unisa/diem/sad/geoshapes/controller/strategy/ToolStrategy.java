package it.unisa.diem.sad.geoshapes.controller.strategy;

import javafx.scene.input.MouseEvent;

public interface ToolStrategy {

    void handlePressed(MouseEvent event);
    void handleDragged(MouseEvent event);
    void handleReleased(MouseEvent event);
    void reset();

}