package it.unisa.diem.sad.geoshapes.controller.util;

public class GridSettings {

    private boolean gridEnabled;
    private double cellSize;

    public GridSettings() {
        this.gridEnabled = false;
        this.cellSize = 20.0;
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
}
