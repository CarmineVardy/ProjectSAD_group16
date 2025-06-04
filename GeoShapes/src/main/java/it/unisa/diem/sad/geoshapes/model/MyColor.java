package it.unisa.diem.sad.geoshapes.model;

import java.io.Serializable;

/**
 * Represents a color in the GeoShapes application with red, green, blue, and opacity components.
 * This class is serializable to allow for persistence of color information.
 * Color component values (red, green, blue, opacity) must be between 0.0 and 1.0.
 */
public class MyColor implements Serializable {

    // Public static final constants
    private static final long serialVersionUID = 1L;

    // Private instance variables
    private double red;
    private double green;
    private double blue;
    private double opacity;

    /**
     * Constructs a new {@code MyColor} instance with specified RGB components and full opacity (1.0).
     *
     * @param red The red component of the color (0.0 to 1.0).
     * @param green The green component of the color (0.0 to 1.0).
     * @param blue The blue component of the color (0.0 to 1.0).
     * @throws IllegalArgumentException If any color component is outside the range of 0.0 to 1.0.
     */
    public MyColor(double red, double green, double blue) {
        this(red, green, blue, 1.0);
    }

    /**
     * Constructs a new {@code MyColor} instance with specified RGBA components.
     *
     * @param red The red component of the color (0.0 to 1.0).
     * @param green The green component of the color (0.0 to 1.0).
     * @param blue The blue component of the color (0.0 to 1.0).
     * @param opacity The opacity component of the color (0.0 to 1.0).
     * @throws IllegalArgumentException If any color component or opacity is outside the range of 0.0 to 1.0.
     */
    public MyColor(double red, double green, double blue, double opacity) {
        if (red < 0.0 || red > 1.0 ||
                green < 0.0 || green > 1.0 ||
                blue < 0.0 || blue > 1.0 ||
                opacity < 0.0 || opacity > 1.0) {
            throw new IllegalArgumentException("MyColor components must be between 0.0 and 1.0");
        }
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.opacity = opacity;
    }

    /**
     * Returns the red component of this color.
     *
     * @return The red component value (0.0 to 1.0).
     */
    public double getRed() {
        return red;
    }

    /**
     * Returns the green component of this color.
     *
     * @return The green component value (0.0 to 1.0).
     */
    public double getGreen() {
        return green;
    }

    /**
     * Returns the blue component of this color.
     *
     * @return The blue component value (0.0 to 1.0).
     */
    public double getBlue() {
        return blue;
    }

    /**
     * Returns the opacity component of this color.
     *
     * @return The opacity component value (0.0 to 1.0).
     */
    public double getOpacity() {
        return opacity;
    }
}