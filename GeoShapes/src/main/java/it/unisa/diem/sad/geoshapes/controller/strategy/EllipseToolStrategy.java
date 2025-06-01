package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.decorator.PreviewDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EllipseToolStrategy implements ToolStrategy {

    private final Pane drawingPane;
    private final InteractionCallback callback;
    private final Group zoomGroup;
    private Shape previewFxShape;
    private ShapeDecorator previewDecorator;

    private Color borderColor;
    private Color fillColor;
    private double startX, startY, endX, endY;

    private static final double MIN_RADIUS = 1.0;

    public EllipseToolStrategy(Pane drawingPane, InteractionCallback callback, Group zoomGroup) {
        this.drawingPane = drawingPane;
        this.callback = callback;
        this.zoomGroup=zoomGroup;
    }

    @Override
    public void activate(Color borderColor, Color fillColor) {
        this.borderColor = borderColor;
        this.fillColor = fillColor;
        callback.onLineSelected(false);
    }

    @Override
    public void handleBorderColorChange(Color color) {
        this.borderColor = color;
    }

    @Override
    public void handleFillColorChange(Color color) {
        this.fillColor = color;
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        if (previewFxShape != null) {
            reset();
        }

        drawingPane.setCursor(Cursor.CROSSHAIR);

        //Questo mi aiuta a convertire le coordinate del content zoommato a quelle della finestra
        Point2D localPoint = getTransformedCoordinates(event,drawingPane);
        startX = localPoint.getX();
        startY = localPoint.getY();
        endX = startX; // Inizializza endX ed endY con le stesse coordinate di start
        endY = startY;

        Ellipse ellipse = new Ellipse(startX, startY, 0, 0);
        ellipse.setStroke(borderColor);
        ellipse.setFill(fillColor);
        ellipse.setStrokeWidth(2.0);

        previewFxShape = ellipse;
        previewDecorator = new PreviewDecorator(previewFxShape);
        previewDecorator.applyDecoration();

        drawingPane.getChildren().add(previewFxShape);
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        if (previewFxShape == null) return; // Aggiunto per sicurezza

        //Questo mi aiuta a convertire le coordinate del content zoommato a quelle della finestra
        Point2D localPoint = getTransformedCoordinates(event,drawingPane);
        endX = localPoint.getX();
        endY = localPoint.getY();

        double centerX = (startX + endX) / 2;
        double centerY = (startY + endY) / 2;
        double radiusX = Math.abs(endX - startX) / 2;
        double radiusY = Math.abs(endY - startY) / 2;

        if (previewFxShape instanceof Ellipse ellipse) {
            ellipse.setCenterX(centerX);
            ellipse.setCenterY(centerY);
            ellipse.setRadiusX(radiusX);
            ellipse.setRadiusY(radiusY);
        }
    }

    @Override
    public void handleMouseReleased(MouseEvent event) {
        drawingPane.setCursor(Cursor.DEFAULT);

        //Questo mi aiuta a convertire le coordinate del content zoommato a quelle della finestra
        Point2D localPoint = getTransformedCoordinates(event,drawingPane);
        endX = localPoint.getX();
        endY = localPoint.getY();

        double radiusX = Math.abs(endX - startX) / 2;
        double radiusY = Math.abs(endY - startY) / 2;

        if (radiusX >= MIN_RADIUS && radiusY >= MIN_RADIUS) {
            callback.onCreateShape(previewFxShape);
        } else {
            reset();
        }
    }

    @Override
    public void handleMouseMoved(MouseEvent event) {
        // Nessun comportamento specifico richiesto per il movimento del mouse non trascinato
    }

    @Override
    public void handleBringToFront(ActionEvent actionEvent) {
    }

    @Override
    public void handleBringToTop(ActionEvent actionEvent) {
    }

    @Override
    public void handleSendToBack(ActionEvent actionEvent) {
    }

    @Override
    public void handleSendToBottom(ActionEvent actionEvent) {
    }

    @Override
    public void handleCopy(Event event) {
    }

    @Override
    public void handleCut(Event event) {
    }

    @Override
    public void handleDelete(Event event) {
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

    @Override
    public List<MyShape> getSelectedShapes() {
        return Collections.emptyList();
    }

}