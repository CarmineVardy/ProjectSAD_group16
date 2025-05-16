package com.example.geoshapes.controller;

import com.example.geoshapes.model.DrawingModel;
import com.example.geoshapes.model.factory.LineFactory;
import com.example.geoshapes.model.factory.ShapeFactory;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private ToggleGroup tools;

    @FXML
    private Canvas drawingCanvas;

    @FXML
    private StackPane canvasContainer;

    private DrawingModel drawingModel;
    private GraphicsContext gc;
    private ShapeFactory lineFactory;
    private double startX;
    private double startY;


    @FXML
    public void initialize() {
        drawingModel = new DrawingModel();
        lineFactory = new LineFactory();
        gc = drawingCanvas.getGraphicsContext2D();

        drawingCanvas.setOnMousePressed(this::handleMousePressed);
        drawingCanvas.setOnMouseDragged(this::handleMouseDragged);
        drawingCanvas.setOnMouseReleased(this::handleMouseReleased);

        setupCanvasResizeBinding();
    }

    private void setupCanvasResizeBinding() {
        canvasContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            drawingCanvas.setWidth(newVal.doubleValue());
            redraw();
        });
        canvasContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            drawingCanvas.setHeight(newVal.doubleValue());
            redraw();
        });
        drawingCanvas.setWidth(canvasContainer.getWidth());
        drawingCanvas.setHeight(canvasContainer.getHeight());
    }

    private void redraw() {
        gc.clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
        drawingModel.drawShapes(gc);
    }

    private void handleMousePressed(MouseEvent event) {
        if (isLineToolActive()) {
            drawingModel.setCurrentFactory(lineFactory);
            startX = event.getX();
            startY = event.getY();
            drawingModel.startDrawing(startX, startY);
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isLineToolActive() && drawingModel != null) {
            drawingModel.updateDrawing(event.getX(), event.getY());
            redraw();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (isLineToolActive() && drawingModel != null) {
            double endX = event.getX();
            double endY = event.getY();
            // Check if the start and end points are the same
            if (startX != endX || startY != endY) {
                drawingModel.endDrawing(endX, endY);
                redraw();
            }
        }
    }

    private boolean isLineToolActive() {
        ToggleButton selected = (ToggleButton) tools.getSelectedToggle();
        return selected != null && "Line".equals(selected.getText());
    }
}