package it.unisa.diem.sad.geoshapes.controller.util;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GridRenderer {
    private final Canvas gridCanvas;
    private final Pane drawingArea;
    private final GridSettings gridSettings;

    public GridRenderer(Pane drawingArea, GridSettings gridSettings) {
        this.drawingArea = drawingArea;
        this.gridSettings = gridSettings;
        this.gridCanvas = new Canvas();
        gridCanvas.widthProperty().bind(drawingArea.widthProperty());
        gridCanvas.heightProperty().bind(drawingArea.heightProperty());
        drawingArea.getChildren().add(0, gridCanvas);
    }

    public void drawGrid() {
        GraphicsContext gc = gridCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        if (!gridSettings.isGridEnabled()) return;

        double width = gridCanvas.getWidth();
        double height = gridCanvas.getHeight();
        double cellSize = gridSettings.getCellSize();

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
