package it.unisa.diem.sad.geoshapes.controller.strategy;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public interface ToolStrategy {

    void handlePressed(MouseEvent event);
    void handleDragged(MouseEvent event);
    void handleReleased(MouseEvent event);
    void handleBorderColorChange(Color color);
    void handleFillColorChange(Color color);
    void reset();

}