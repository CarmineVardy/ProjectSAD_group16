package it.unisa.diem.sad.geoshapes.model.util;

import it.unisa.diem.sad.geoshapes.controller.util.GridSettings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GridSettingsTest {

    @Test
    void testInitialState() {
        GridSettings settings = new GridSettings();
        assertFalse(settings.isGridEnabled());
        assertEquals(20.0, settings.getCellSize());
    }

    @Test
    void testEnableGrid() {
        GridSettings settings = new GridSettings();
        settings.setGridEnabled(true);
        assertTrue(settings.isGridEnabled());
    }

    @Test
    void testSetCellSize() {
        GridSettings settings = new GridSettings();
        settings.setCellSize(50.0);
        assertEquals(50.0, settings.getCellSize());
    }
}