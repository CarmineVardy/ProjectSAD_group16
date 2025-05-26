package it.unisa.diem.sad.geoshapes.model.util;

public class GridSettings {
    private boolean gridEnabled = false;
    private double cellSize = 20.0;

    public boolean isGridEnabled() {
        return gridEnabled;
    }

    public void setGridEnabled(boolean gridEnabled) {
        this.gridEnabled = gridEnabled;
    }

    public double getCellSize() {
        return cellSize;
    }

    public void setCellSize(double cellSize) {
        this.cellSize = cellSize;
    }
}
