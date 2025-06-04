package it.unisa.diem.sad.geoshapes.controller.strategy;

import javafx.event.Event;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.List;

public interface ToolStrategy {

    void activate(Color lineBorderColor, Color rectangleBorderColor, Color rectangleFillColor, Color ellipseBorderColor, Color ellipseFillColor, Color polygonBorderColor, Color polygonFillColor, Color textBorderColor, Color textFillColor, Color textColor, int polygonVertices, boolean regularPolygon, int fontSize);
    void handleMousePressed(MouseEvent event);
    void handleMouseDragged(MouseEvent event);
    void handleMouseReleased(MouseEvent event);
    void handleMouseMoved(MouseEvent event);
    void handleLineBorderColorChange(Color color);
    void handleRectangleBorderColorChange(Color color);
    void handleRectangleFillColorChange(Color color);
    void handleEllipseBorderColorChange(Color color);
    void handleEllipseFillColorChange(Color color);
    void handlePolygonBorderColorChange(Color color);
    void handlePolygonFillColorChange(Color color);
    void handleTextBorderColorChange(Color color);
    void handleTextFillColorChange(Color color);
    void handleTextColorChange(Color color);
    void handlePolygonVerticesChange(int polygonVertices);
    void handleRegularPolygon(boolean regularPolygon);
    void handleFontSizeChange(int fontSize);
    void handleKeyPressed(KeyEvent event);
    void handleKeyTyped(KeyEvent event);
    void handleBorderColorChange(Color color);
    void handleFillColorChange(Color color);
    void handleTextColorMenuChange(Color color);
    void handleFontSizeMenuChange(int fontSize);
    List<Shape> getSelectedShapes();
    void reset();

}
