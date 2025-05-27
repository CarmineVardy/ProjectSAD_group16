package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.decorator.PreviewDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.Cursor;

public class LineToolStrategy implements ToolStrategy {

    private final Pane drawingPane;
    private final Group zoomGroup;
    private final InteractionCallback callback;

    private Shape previewFxShape;
    private ShapeDecorator previewDecorator;

    private Color borderColor;
    private double startX, startY, endX, endY;

    private static final double MIN_LENGTH = 2.0;

    public LineToolStrategy(Group zoomGroup, Pane drawingArea ,InteractionCallback callback) {
        this.drawingPane = drawingArea;
        this.zoomGroup=zoomGroup;
        this.callback = callback;
    }

    @Override
    public void activate(Color borderColor, Color fillColor) {
        this.borderColor = borderColor;
        callback.onLineSelected(true);
    }

    @Override
    public void handleBorderColorChange(Color color) {
        this.borderColor = color;
    }

    @Override
    public void handleFillColorChange(Color color) {

    }

    @Override
    public void handleBringToFront(ActionEvent actionEvent) {

    }

    @Override
    public void handleSendToBack(ActionEvent actionEvent) {

    }


    @Override
    public void handleMousePressed(MouseEvent event) {
            if (previewFxShape != null) {
                reset();
            }

            zoomGroup.setCursor(Cursor.CROSSHAIR);

            Point2D localPoint = getTransformedCoordinates(event, zoomGroup);
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

            zoomGroup.getChildren().add(previewFxShape);  // Cambiato da drawingPane a zoomGroup
        }


    @Override
    public void handleMouseDragged(MouseEvent event) {
        if (previewFxShape == null) return;

        Point2D localPoint = getTransformedCoordinates(event,zoomGroup);
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
        drawingPane.setCursor(Cursor.DEFAULT);

        Point2D localPoint = getTransformedCoordinates(event,zoomGroup);
        endX = localPoint.getX();
        endY = localPoint.getY();

        double dx = endX - startX;
        double dy = endY - startY;
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length >= MIN_LENGTH) {
            drawingPane.getChildren().remove(previewFxShape);
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
            drawingPane.getChildren().remove(previewFxShape);
            previewFxShape = null;
        }
    }

}
