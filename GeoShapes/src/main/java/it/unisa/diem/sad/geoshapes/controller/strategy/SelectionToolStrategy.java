package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.controller.ShapeMapping;
import it.unisa.diem.sad.geoshapes.decorator.SelectionDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class SelectionToolStrategy implements ToolStrategy {

    private final Pane drawingArea;
    private final ShapeMapping shapeMapping;
    private final InteractionCallback callback;

    private Shape selectedJavaFxShape;
    private ShapeDecorator currentDecorator;

    private Color borderColor;
    private Color fillColor;

    private double startX;
    private double startY;
    private double initialTranslateX;
    private double initialTranslateY;

    public SelectionToolStrategy(Pane drawingArea, ShapeMapping shapeMapping, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.shapeMapping = shapeMapping;
        this.callback = callback;
    }

    @Override
    public void activate(Color borderColor, Color fillColor) {
        this.borderColor = borderColor;
        this.fillColor = fillColor;
    }

    @Override
    public void handleBorderColorChange(Color color) {
        if (selectedJavaFxShape != null) {
            callback.onChangeBorderColor(selectedJavaFxShape, color);
        }
    }

    @Override
    public void handleFillColorChange(Color color) {
        if (selectedJavaFxShape != null) {
            callback.onChangeFillColor(selectedJavaFxShape, color);
        }
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        startX = event.getX();
        startY = event.getY();

        Shape shapeAtPosition = shapeMapping.getViewShapeAt(startX, startY);

        // Se clicco su un'altra figura o sul vuoto, rimuovo decorazione da quella precedente
        if (shapeAtPosition != selectedJavaFxShape) {
            if (currentDecorator != null) {
                currentDecorator.removeDecoration();
                currentDecorator = null;
            }
            selectedJavaFxShape = null;
        }

        // Se ho cliccato su una nuova figura, applico decorazione
        if (shapeAtPosition != null) {
            selectedJavaFxShape = shapeAtPosition;
            currentDecorator = new SelectionDecorator(selectedJavaFxShape);
            currentDecorator.applyDecoration();

            initialTranslateX = selectedJavaFxShape.getTranslateX();
            initialTranslateY = selectedJavaFxShape.getTranslateY();

            if (event.getButton() == MouseButton.SECONDARY) {
                callback.onSelectionMenuOpened(selectedJavaFxShape, event.getX(), event.getY());
            }
        }
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        if (selectedJavaFxShape != null) {
            double deltaX = event.getX() - startX;
            double deltaY = event.getY() - startY;
            selectedJavaFxShape.setTranslateX(initialTranslateX + deltaX);
            selectedJavaFxShape.setTranslateY(initialTranslateY + deltaY);
        }
    }

    @Override
    public void handleMouseReleased(MouseEvent event) {
        // Intenzionalmente vuoto: tutto è già gestito in drag
    }

    @Override
    public void handleMouseMoved(MouseEvent event) {
        if (shapeMapping.getViewShapeAt(event.getX(), event.getY()) != null) {
            drawingArea.setCursor(Cursor.HAND);
        } else {
            drawingArea.setCursor(Cursor.DEFAULT);
        }
    }

    @Override
    public void reset() {
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            currentDecorator = null;
        }
        selectedJavaFxShape = null;
    }


}
