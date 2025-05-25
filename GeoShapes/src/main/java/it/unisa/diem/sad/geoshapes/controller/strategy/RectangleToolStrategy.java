package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.decorator.PreviewDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class RectangleToolStrategy implements ToolStrategy {

    private final Pane drawingPane;
    private final InteractionCallback callback;

    private Shape previewFxShape;
    private ShapeDecorator previewDecorator;

    private Color borderColor;
    private Color fillColor;
    private double startX, startY, endX, endY;

    private static final double MIN_DIMENSION = 2.0;

    public RectangleToolStrategy(Pane drawingPane, InteractionCallback callback) {
        this.drawingPane = drawingPane;
        this.callback = callback;
    }

    @Override
    public void activate(Color borderColor, Color fillColor) {
        this.borderColor = borderColor;
        this.fillColor = fillColor;
    }

    @Override
    public void handleBorderColorChange(Color color) {
        this.borderColor = color;
        if (previewFxShape instanceof Rectangle rect) {
            rect.setStroke(this.borderColor);
        }
    }

    @Override
    public void handleFillColorChange(Color color) {
        this.fillColor = color;
        if (previewFxShape instanceof Rectangle rect) {
            rect.setFill(this.fillColor);
        }
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        if (previewFxShape != null) {
            reset();
        }

        drawingPane.setCursor(Cursor.CROSSHAIR);

        Point2D localPoint = drawingPane.parentToLocal(event.getX(), event.getY());
        startX = localPoint.getX();
        startY = localPoint.getY();
        endX = startX; // Inizializza endX e endY con le stesse coordinate di start
        endY = startY;

        Rectangle rect = new Rectangle(startX, startY, 0, 0);
        rect.setStroke(borderColor);
        rect.setFill(fillColor);
        rect.setStrokeWidth(2.0);

        previewFxShape = rect;
        previewDecorator = new PreviewDecorator(previewFxShape);
        previewDecorator.applyDecoration();

        drawingPane.getChildren().add(previewFxShape);
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        if (previewFxShape == null) return; // Aggiunto per sicurezza

        //Questo mi aiuta a convertire le coordinate del content zoommato a quelle della finestra
        Point2D localPoint = drawingPane.parentToLocal(event.getX(), event.getY());
        endX = localPoint.getX();
        endY = localPoint.getY();

        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        if (previewFxShape instanceof Rectangle rect) {
            rect.setX(x);
            rect.setY(y);
            rect.setWidth(width);
            rect.setHeight(height);
        }
    }

    @Override
    public void handleMouseReleased(MouseEvent event) {
        drawingPane.setCursor(Cursor.DEFAULT);

        //Questo mi aiuta a convertire le coordinate del content zoommato a quelle della finestra
        Point2D localPoint = drawingPane.parentToLocal(event.getX(), event.getY());
        endX = localPoint.getX();
        endY = localPoint.getY();

        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        if (width >= MIN_DIMENSION && height >= MIN_DIMENSION) {
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