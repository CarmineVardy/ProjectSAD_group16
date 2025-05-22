package it.unisa.diem.sad.geoshapes.controller.strategy;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public interface ToolStrategy {

    void handleMousePressed(MouseEvent event);
    void handleMouseDragged(MouseEvent event);
    void handleMouseReleased(MouseEvent event);
    void handleMouseMoved(MouseEvent event);
    void handleBorderColorChange(Color color);
    void handleFillColorChange(Color color);

    void reset();

}