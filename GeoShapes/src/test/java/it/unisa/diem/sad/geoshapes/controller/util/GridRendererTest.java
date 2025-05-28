/*package it.unisa.diem.sad.geoshapes.controller.util;

import it.unisa.diem.sad.geoshapes.controller.util.GridSettings;
import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GridRendererTest {

    @BeforeEach
    void initToolkit() {
        new JFXPanel(); // Inizializza JavaFX Toolkit
    }

    @Test
    void testDrawGridEnabled() {
        GridSettings settings = new GridSettings();
        settings.setGridEnabled(true);
        settings.setCellSize(10);

        Pane drawingArea = new Pane();
        GridRenderer renderer = new GridRenderer(drawingArea, settings);

        // Non lancia eccezioni e si disegna correttamente
        renderer.drawGrid();
    }

    @Test
    void testDrawGridDisabled() {
        GridSettings settings = new GridSettings(); // gridEnabled = false

        Pane drawingArea = new Pane();
        GridRenderer renderer = new GridRenderer(drawingArea, settings);

        renderer.drawGrid(); // Non dovrebbe disegnare nulla (ma nemmeno crashare)
    }
}*/
