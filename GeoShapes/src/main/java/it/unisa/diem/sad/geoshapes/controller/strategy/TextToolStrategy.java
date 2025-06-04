package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.Collections;
import java.util.List;

public class TextToolStrategy implements ToolStrategy{

    private final Pane drawingArea;
    private final InteractionCallback callback;

    private Color borderColor;
    private Color fillColor;
    private Color textColor;
    private int fontSize;

    public TextToolStrategy(Pane drawingArea, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.callback = callback;
    }

    @Override
    public void activate(Color lineBorderColor, Color rectangleBorderColor, Color rectangleFillColor, Color ellipseBorderColor, Color ellipseFillColor, Color polygonBorderColor, Color polygonFillColor, Color textBorderColor, Color textFillColor, Color textColor, int polygonVertices, boolean regularPolygon, int fontSize) {
        this.borderColor = textBorderColor;
        this.fillColor = textFillColor;
        this.textColor = textColor;
        this.fontSize = fontSize;
    }

    @Override
    public void handleMousePressed(MouseEvent event) {

    }

    @Override
    public void handleMouseDragged(MouseEvent event) {

    }

    @Override
    public void handleMouseReleased(MouseEvent event) {

    }

    @Override
    public void handleMouseMoved(MouseEvent event) {

    }

    @Override
    public void handleLineBorderColorChange(Color color) {

    }

    @Override
    public void handleRectangleBorderColorChange(Color color) {

    }

    @Override
    public void handleRectangleFillColorChange(Color color) {

    }

    @Override
    public void handleEllipseBorderColorChange(Color color) {

    }

    @Override
    public void handleEllipseFillColorChange(Color color) {

    }

    @Override
    public void handlePolygonBorderColorChange(Color color) {

    }

    @Override
    public void handlePolygonFillColorChange(Color color) {

    }

    @Override
    public void handleTextBorderColorChange(Color color) {
        this.borderColor = color;
    }

    @Override
    public void handleTextFillColorChange(Color color) {
        this.fillColor = color;
    }

    @Override
    public void handleTextColorChange(Color color) {
        this.textColor = color;
    }

    @Override
    public void handlePolygonVerticesChange(int polygonVertices) {

    }

    @Override
    public void handleRegularPolygon(boolean regularPolygon) {

    }

    @Override
    public void handleFontSizeChange(int fontSize) {
        this.fontSize = fontSize;

    }

    @Override
    public void handleKeyPressed(KeyEvent event) {

    }

    @Override
    public void handleKeyTyped(KeyEvent event) {

    }

    @Override
    public void handleBorderColorChange(Color color) {

    }

    @Override
    public void handleFillColorChange(Color color) {

    }

    @Override
    public void handleTextColorMenuChange(Color color) {

    }

    @Override
    public void handleFontSizeMenuChange(int fontSize) {

    }

    @Override
    public List<Shape> getSelectedShapes() {
        return Collections.emptyList();
    }

    @Override
    public void reset() {

    }
}
