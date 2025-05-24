package it.unisa.diem.sad.geoshapes.model.util;

import it.unisa.diem.sad.geoshapes.model.MyColor;
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
        assertEquals(1.0, color.getOpacity(), 1e-9); // default opacity
    }

    // Scopo: verificare la creazione di un colore RGBA
    @Test
    public void testRGBAConstructor() {
        MyColor color = new MyColor(0.1, 0.3, 0.5, 0.8);
        assertEquals(0.1, color.getRed(), 1e-9);
        assertEquals(0.3, color.getGreen(), 1e-9);
        assertEquals(0.5, color.getBlue(), 1e-9);
        assertEquals(0.8, color.getOpacity(), 1e-9);
    }

    // Scopo: verificare che vengano lanciati errori per valori non validi
    @Test
    public void testInvalidColorValues() {
        assertThrows(IllegalArgumentException.class, () -> new MyColor(-0.1, 0.2, 0.3));
        assertThrows(IllegalArgumentException.class, () -> new MyColor(0.1, 1.1, 0.3));
        assertThrows(IllegalArgumentException.class, () -> new MyColor(0.1, 0.2, 1.2, 0.9));
        assertThrows(IllegalArgumentException.class, () -> new MyColor(0.1, 0.2, 0.3, -0.5));
    }
}
