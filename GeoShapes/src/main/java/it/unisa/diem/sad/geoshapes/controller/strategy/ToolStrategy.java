package it.unisa.diem.sad.geoshapes.controller.strategy;

import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public interface ToolStrategy {

    void activate(Color borderColor, Color fillColor);
    void handleMousePressed(MouseEvent event);
    void handleMouseDragged(MouseEvent event);
    void handleMouseReleased(MouseEvent event);
    void handleMouseMoved(MouseEvent event);
    void handleBorderColorChange(Color color);
    void handleFillColorChange(Color color);
    void handleBringToFront(ActionEvent actionEvent);
    void handleSendToBack(ActionEvent actionEvent);
    void reset();

    default Point2D getTransformedCoordinates(MouseEvent event, Group zoomGroup) {
        Point2D scenePoint = new Point2D(event.getSceneX(), event.getSceneY());
        
        return zoomGroup.sceneToLocal(scenePoint);
    }

}