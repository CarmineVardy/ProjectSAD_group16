package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.decorator.PreviewDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.Cursor;

public class LineToolStrategy implements ToolStrategy {

    private final Pane drawingArea;
    private final InteractionCallback callback;

    private Shape previewFxShape;
    private ShapeDecorator previewDecorator;

    private Color borderColor;
    private double startX, startY, endX, endY;

    private static final double MIN_LENGTH = 2.0;

    public LineToolStrategy(Pane drawingArea, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.callback = callback;
    }

    @Override
    public void activate(Color borderColor, Color fillColor) {
        this.borderColor = borderColor;
    }

    @Override
    public void handleBorderColorChange(Color color) {
        this.borderColor = color;
    }

    @Override
    public void handleFillColorChange(Color color) {

    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        if (previewFxShape != null) {
            reset();
        }

        drawingArea.setCursor(Cursor.CROSSHAIR);

        Point2D localPoint = drawingArea.parentToLocal(event.getX(), event.getY());
        startX = localPoint.getX();
        startY = localPoint.getY();
        endX = startX;
        endY = startY;

        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(borderColor);
        line.setStrokeWidth(2.0);

        previewFxShape = line;
        previewDecorator = new PreviewDecorator(previewFxShape);
        previewDecorator.applyDecoration();

        drawingArea.getChildren().add(previewFxShape);
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        if (previewFxShape == null) return;

        //Questo mi aiuta a convertire le coordinate del content zoommato a quelle della finestra
        Point2D localPoint = drawingArea.parentToLocal(event.getX(), event.getY());
        endX = localPoint.getX();
        endY = localPoint.getY();

        if (previewFxShape instanceof Line) {
            Line line = (Line) previewFxShape;
            line.setEndX(endX);
            line.setEndY(endY);
        }
    }

    @Override
    public void handleMouseReleased(MouseEvent event) {
        drawingArea.setCursor(Cursor.DEFAULT);

        //Questo mi aiuta a convertire le coordinate del content zoommato a quelle della finestra
        Point2D localPoint = drawingArea.parentToLocal(event.getX(), event.getY());
        endX = localPoint.getX();
        endY = localPoint.getY();

        double dx = endX - startX;
        double dy = endY - startY;
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length >= MIN_LENGTH) {
            drawingArea.getChildren().remove(previewFxShape);
            callback.onCreateShape(previewFxShape);
        } else {
            reset();
        }
    }

    @Override
    public void handleMouseMoved(MouseEvent event) {
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
