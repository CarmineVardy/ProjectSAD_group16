package com.example.geoshapes.model.util;

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

    // Scopo: verificare che due colori uguali siano considerati uguali (equals e hashCode)
    @Test
    public void testEqualsAndHashCode() {
        MyColor color1 = new MyColor(0.1, 0.2, 0.3, 0.4);
        MyColor color2 = new MyColor(0.1, 0.2, 0.3, 0.4);
        MyColor color3 = new MyColor(0.1, 0.2, 0.3, 1.0);

        assertEquals(color1, color2, "Color1 and Color2 should be equal");
        assertEquals(color1.hashCode(), color2.hashCode(), "Hash codes should match");

        assertNotEquals(color1, color3, "Color1 and Color3 should not be equal (opacity differs)");
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
