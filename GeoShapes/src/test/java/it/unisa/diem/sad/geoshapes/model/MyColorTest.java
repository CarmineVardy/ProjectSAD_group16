package it.unisa.diem.sad.geoshapes.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyColorTest {

    // Scopo: verificare la corretta creazione di un colore RGB
    @Test
    public void testRGBConstructor() {
        MyColor color = new MyColor(0.2, 0.4, 0.6);
        assertEquals(0.2, color.getRed(), 1e-9);
        assertEquals(0.4, color.getGreen(), 1e-9);
        assertEquals(0.6, color.getBlue(), 1e-9);
        assertEquals(1.0, color.getOpacity(), 1e-9);
    }
}
