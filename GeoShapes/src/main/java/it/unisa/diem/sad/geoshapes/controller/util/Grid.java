package it.unisa.diem.sad.geoshapes.controller.util;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Manages the display and behavior of a grid on the drawing canvas.
 * This utility class allows enabling/disabling the grid, setting its cell size,
 * and drawing the grid lines dynamically based on the canvas dimensions.
 */
public class Grid {

    // Private instance variables
    private final Canvas gridCanvas;
    private final Pane drawingArea;

    private boolean gridEnabled;
    private double cellSize;

    /**
     * Constructs a new {@code Grid} instance.
     * Initializes the grid as disabled with a default cell size of 20.0.
     * The grid canvas's dimensions are bound to the provided drawing area.
     *
     * @param drawingArea The {@link Pane} on which the grid will be drawn.
     */
    public Grid(Pane drawingArea) {
        this.gridEnabled = false;
        this.cellSize = 20.0;
        this.drawingArea = drawingArea;
        this.gridCanvas = new Canvas();
        // Bind canvas dimensions to the drawing area's dimensions
        gridCanvas.widthProperty().bind(drawingArea.widthProperty());
        gridCanvas.heightProperty().bind(drawingArea.heightProperty());
        // Add grid canvas as the first child to ensure it's at the back
        drawingArea.getChildren().add(0, gridCanvas);
    }

    /**
     * Checks if the grid is currently enabled.
     *
     * @return {@code true} if the grid is enabled, {@code false} otherwise.
     */
    public boolean isGridEnabled() {
        return gridEnabled;
    }

    /**
     * Returns the current cell size of the grid.
     *
     * @return The cell size in pixels.
     */
    public double getCellSize() {
        return cellSize;
    }

    /**
     * Sets whether the grid should be enabled or disabled.
     *
     * @param gridEnabled {@code true} to enable the grid, {@code false} to disable it.
     */
    public void setGridEnabled(boolean gridEnabled) {
        this.gridEnabled = gridEnabled;
    }

    /**
     * Sets the cell size for the grid.
     *
     * @param cellSize The new cell size in pixels.
     */
    public void setCellSize(double cellSize) {
        this.cellSize = cellSize;
    }

    /**
     * Draws the grid lines on the canvas.
     * The grid is only drawn if {@link #isGridEnabled()} returns {@code true}.
     * It clears the canvas before redrawing.
     */
    public void drawGrid() {
        GraphicsContext graphicsContext = gridCanvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        if (!isGridEnabled()) {
            return; // Do not draw if grid is disabled
        }

        double width = gridCanvas.getWidth();
        double height = gridCanvas.getHeight();

        graphicsContext.setStroke(Color.LIGHTGRAY);
        graphicsContext.setLineWidth(0.5);

        // Draw vertical lines
        for (double x = 0; x < width; x += cellSize) {
            graphicsContext.strokeLine(x, 0, x, height);
        }

        // Draw horizontal lines
        for (double y = 0; y < height; y += cellSize) {
            graphicsContext.strokeLine(0, y, width, y);
        }
    }

    /**
     * Returns the {@link Canvas} object on which the grid is drawn.
     *
     * @return The grid {@link Canvas}.
     */
    public Canvas getGridCanvas() {
        return gridCanvas;
    }
}