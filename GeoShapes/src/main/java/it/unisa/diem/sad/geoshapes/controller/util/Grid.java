package it.unisa.diem.sad.geoshapes.controller.util;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Grid {

    private final Canvas gridCanvas;
    private final Pane drawingArea;

    private boolean gridEnabled;
    private double cellSize;

    public Grid(Pane drawingArea) {
        this.gridEnabled = false;
        this.cellSize = 20.0;
        this.drawingArea = drawingArea;
        this.gridCanvas = new Canvas();
        gridCanvas.widthProperty().bind(drawingArea.widthProperty());
        gridCanvas.heightProperty().bind(drawingArea.heightProperty());
        drawingArea.getChildren().add(0, gridCanvas);
    }

    public boolean isGridEnabled() {
        return gridEnabled;
    }

    public double getCellSize() {
        return cellSize;
    }

    public void setGridEnabled(boolean gridEnabled) {
        this.gridEnabled = gridEnabled;
    }

    public void setCellSize(double cellSize) {
        this.cellSize = cellSize;
    }

    public void drawGrid() {
        GraphicsContext gc = gridCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        if (!isGridEnabled()) return;

        double width = gridCanvas.getWidth();
        double height = gridCanvas.getHeight();

        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);

        for (double x = 0; x < width; x += cellSize) {
            gc.strokeLine(x, 0, x, height);
        }

        for (double y = 0; y < height; y += cellSize) {
            gc.strokeLine(0, y, width, y);
        }
    }

    public Canvas getGridCanvas() {
        return gridCanvas;
    }
}
