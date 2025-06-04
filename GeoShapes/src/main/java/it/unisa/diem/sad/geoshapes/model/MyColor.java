package it.unisa.diem.sad.geoshapes.model;

import java.io.Serializable;

public class MyColor implements Serializable {

    private static final long serialVersionUID = 1L;

    private double red;
    private double green;
    private double blue;
    private double opacity;

    //Constructor for RGB
    public MyColor(double red, double green, double blue) {
        this(red, green, blue, 1.0);
    }

    // Constructor for RGBA
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

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
        return blue;
    }

    public double getOpacity() {
        return opacity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MyColor other = (MyColor) obj;
        return Double.compare(red, other.red) == 0 &&
                Double.compare(green, other.green) == 0 &&
                Double.compare(blue, other.blue) == 0 &&
                Double.compare(opacity, other.opacity) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(red, green, blue, opacity);
    }

}