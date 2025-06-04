package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.controller.decorator.PreviewShapeDecorator;
import it.unisa.diem.sad.geoshapes.controller.decorator.ShapeDecorator;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.Collections;
import java.util.List;

public class RectangleToolStrategy implements ToolStrategy{

    private final Pane drawingArea;
    private final InteractionCallback callback;

    private Rectangle previewFxShape;
    private ShapeDecorator previewDecorator;

    private double startX, startY, endX, endY;
    private static final double MIN_DIMENSION = 2.0;

    private Color borderColor;
    private Color fillColor;


    public RectangleToolStrategy(Pane drawingArea, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.callback = callback;
    }

    @Override
    public void activate(Color lineBorderColor, Color rectangleBorderColor, Color rectangleFillColor, Color ellipseBorderColor, Color ellipseFillColor, Color polygonBorderColor, Color polygonFillColor, Color textBorderColor, Color textFillColor, Color textColor, int polygonVertices, boolean regularPolygon, int fontSize) {
        this.borderColor = rectangleBorderColor;
        this.fillColor = rectangleFillColor;
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        if (previewFxShape != null) {
            reset();
        }

        drawingArea.setCursor(Cursor.CROSSHAIR);
        Point2D localPoint = drawingArea.sceneToLocal(event.getSceneX(), event.getSceneY());
        startX = localPoint.getX();
        startY = localPoint.getY();
        endX = startX;
        endY = startY;

        previewFxShape = new Rectangle(startX, startY, 0, 0);
        previewFxShape.setStroke(borderColor);
        previewFxShape.setFill(fillColor);
        previewFxShape.setStrokeWidth(2.0);

        previewDecorator = new PreviewShapeDecorator(previewFxShape);
        previewDecorator.applyDecoration();

        drawingArea.getChildren().add(previewFxShape);

    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        if (previewFxShape == null) return;

        Point2D localPoint = drawingArea.sceneToLocal(event.getSceneX(), event.getSceneY());
        endX = localPoint.getX();
        endY = localPoint.getY();

        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        previewFxShape.setX(x);
        previewFxShape.setY(y);
        previewFxShape.setWidth(width);
        previewFxShape.setHeight(height);

    }

    @Override
    public void handleMouseReleased(MouseEvent event) {
        drawingArea.setCursor(Cursor.DEFAULT);

        Point2D localPoint = drawingArea.sceneToLocal(event.getSceneX(), event.getSceneY());
        endX = localPoint.getX();
        endY = localPoint.getY();

        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        if (width >= MIN_DIMENSION && height >= MIN_DIMENSION) {
            callback.onCreateShape(previewFxShape);
        } else {
            reset();
        }

    }

    @Override
    public void handleMouseMoved(MouseEvent event) {

    }

    @Override
    public void handleLineBorderColorChange(Color color) {

    }

    @Override
    public void handleRectangleBorderColorChange(Color color) {
        this.borderColor = color;
    }

    @Override
    public void handleRectangleFillColorChange(Color color) {
        this.fillColor = color;
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

    }

    @Override
    public void handleTextFillColorChange(Color color) {

    }

    @Override
    public void handleTextColorChange(Color color) {

    }

    @Override
    public void handlePolygonVerticesChange(int polygonVertices) {

    }

    @Override
    public void handleRegularPolygon(boolean regularPolygon) {

    }

    @Override
    public void handleFontSizeChange(int fontSize) {

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
        if (previewDecorator != null) {
            previewDecorator.removeDecoration();
            previewDecorator = null;
        }
        if (previewFxShape != null) {
            drawingArea.getChildren().remove(previewFxShape);
            previewFxShape = null;
        }

    }
}
