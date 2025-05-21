package it.unisa.diem.sad.geoshapes.model.util;

import java.io.Serializable;

public class MyColor implements Serializable {

    private static final long serialVersionUID = 1L;

    private double red;
    private double green;
    private double blue;
    private double opacity;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyColor myColor = (MyColor) o;
        return Double.compare(myColor.red, red) == 0 &&
                Double.compare(myColor.green, green) == 0 &&
                Double.compare(myColor.blue, blue) == 0 &&
                Double.compare(myColor.opacity, opacity) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(red, green, blue, opacity);
    }


}