package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.input.MouseEvent;

public interface ToolStrategy {

    void handlePressed(MouseEvent event);
    void handleDragged(MouseEvent event);
    void handleReleased(MouseEvent event);
    MyShape getFinalShape();

}