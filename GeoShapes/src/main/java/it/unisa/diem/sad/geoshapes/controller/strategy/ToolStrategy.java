package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.List;

public interface ToolStrategy {

    void activate(Color borderColor, Color fillColor);
    void handleBorderColorChange(Color color);
    void handleFillColorChange(Color color);
    void handleBringToFront(ActionEvent actionEvent);
    void handleBringToTop(ActionEvent actionEvent);
    void handleSendToBack(ActionEvent actionEvent);
    void handleSendToBottom(ActionEvent actionEvent);
    void handleMousePressed(MouseEvent event);
    void handleMouseDragged(MouseEvent event);
    void handleMouseReleased(MouseEvent event);
    void handleMouseMoved(MouseEvent event);
    void handleCopy(Event event);
    void handleCut(Event event);
    void handleDelete(Event event);

    void reset();
}
