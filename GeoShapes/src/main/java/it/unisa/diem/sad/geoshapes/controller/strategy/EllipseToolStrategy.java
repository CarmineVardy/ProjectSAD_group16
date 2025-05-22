package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.decorator.PreviewDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;

public class EllipseToolStrategy implements ToolStrategy {

    private final Pane drawingArea;
    private final InteractionCallback callback;

    private Shape previewFxShape;
    private ShapeDecorator previewDecorator;

    private Color borderColor;
    private Color fillColor;
    private double startX, startY, endX, endY;

    private static final double MIN_RADIUS = 1.0;

    public EllipseToolStrategy(Pane drawingArea, InteractionCallback callback) {
        this.drawingArea = drawingArea;
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

        drawingArea.setCursor(Cursor.CROSSHAIR);

        startX = event.getX();
        startY = event.getY();
        endX = startX;
        endY = startY;

        Ellipse ellipse = new Ellipse(startX, startY, 0, 0);
        ellipse.setStroke(borderColor);
        ellipse.setFill(fillColor);
        ellipse.setStrokeWidth(2.0);

        previewFxShape = ellipse;
        previewDecorator = new PreviewDecorator(previewFxShape);
        previewDecorator.applyDecoration();

        drawingArea.getChildren().add(previewFxShape);
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        endX = event.getX();
        endY = event.getY();

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
        drawingArea.setCursor(Cursor.DEFAULT);

        endX = event.getX();
        endY = event.getY();

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
