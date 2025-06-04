package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * An abstract base class for all geometric shapes in the GeoShapes application.
 * This class provides common properties and behaviors for shapes, such as
 * position, dimensions (start and end coordinates), rotation, and colors.
 * It is designed to be serializable for persistence and cloneable for easy duplication.
 */
public abstract class MyShape implements Serializable, Cloneable {

    // Public static final constants
    private static final long serialVersionUID = 1L;

    // Protected instance variables
    protected String name;
    protected double startX;
    protected double startY;
    protected double endX;
    protected double endY;
    protected double rotation;
    protected MyColor borderColor;
    protected MyColor fillColor;

    /**
     * Constructs a new {@code MyShape} with specified initial coordinates, rotation, and colors.
     * The shape's name will be set automatically based on its type unless explicitly changed.
     *
     * @param startX The starting X-coordinate of the shape.
     * @param startY The starting Y-coordinate of the shape.
     * @param endX The ending X-coordinate of the shape.
     * @param endY The ending Y-coordinate of the shape.
     * @param rotation The rotation angle of the shape in degrees.
     * @param borderColor The border color of the shape.
     * @param fillColor The fill color of the shape.
     */
    public MyShape(double startX, double startY, double endX, double endY, double rotation, MyColor borderColor, MyColor fillColor) {
        this.name = null; // Name is assigned by DrawingModel when added
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.rotation = rotation;
        this.borderColor = borderColor;
        this.fillColor = fillColor;
    }

    // Public methods

    /**
     * Returns the name of the shape. If no custom name is set, a default name
     * based on the shape type will be returned.
     *
     * @return The name of the shape.
     */
    public String getName() {
        return name != null ? name : "Unnamed " + getShapeType();
    }

    /**
     * Returns the starting X-coordinate of the shape.
     *
     * @return The starting X-coordinate.
     */
    public double getStartX() {
        return startX;
    }

    /**
     * Returns the starting Y-coordinate of the shape.
     *
     * @return The starting Y-coordinate.
     */
    public double getStartY() {
        return startY;
    }

    /**
     * Returns the ending X-coordinate of the shape.
     *
     * @return The ending X-coordinate.
     */
    public double getEndX() {
        return endX;
    }

    /**
     * Returns the ending Y-coordinate of the shape.
     *
     * @return The ending Y-coordinate.
     */
    public double getEndY() {
        return endY;
    }

    /**
     * Returns the rotation angle of the shape.
     *
     * @return The rotation angle in degrees.
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * Returns the border color of the shape.
     *
     * @return The {@link MyColor} representing the border color.
     */
    public MyColor getBorderColor() {
        return borderColor;
    }

    /**
     * Returns the fill color of the shape.
     *
     * @return The {@link MyColor} representing the fill color.
     */
    public MyColor getFillColor() {
        return fillColor;
    }

    /**
     * Sets the name of the shape.
     *
     * @param name The new name for the shape.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the starting X-coordinate of the shape.
     *
     * @param startX The new starting X-coordinate.
     */
    public void setStartX(double startX) {
        this.startX = startX;
    }

    /**
     * Sets the starting Y-coordinate of the shape.
     *
     * @param startY The new starting Y-coordinate.
     */
    public void setStartY(double startY) {
        this.startY = startY;
    }

    /**
     * Sets the ending X-coordinate of the shape.
     *
     * @param endX The new ending X-coordinate.
     */
    public void setEndX(double endX) {
        this.endX = endX;
    }

    /**
     * Sets the ending Y-coordinate of the shape.
     *
     * @param endY The new ending Y-coordinate.
     */
    public void setEndY(double endY) {
        this.endY = endY;
    }

    /**
     * Sets the rotation angle of the shape.
     *
     * @param rotation The new rotation angle in degrees.
     */
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    /**
     * Sets the border color of the shape.
     *
     * @param color The new {@link MyColor} for the border.
     */
    public void setBorderColor(MyColor color) {
        this.borderColor = color;
    }

    /**
     * Sets the fill color of the shape.
     *
     * @param color The new {@link MyColor} for the fill.
     */
    public void setFillColor(MyColor color) {
        this.fillColor = color;
    }

    /**
     * Sets the coordinates of the points defining the shape.
     * This method is primarily used by polygon-like shapes.
     * Default implementation does nothing.
     *
     * @param xPoints A list of X-coordinates.
     * @param yPoints A list of Y-coordinates.
     */
    public void setPoints(List<Double> xPoints, List<Double> yPoints) {
        // Default implementation does nothing, to be overridden by specific shape types (e.g., Polygon)
    }

    /**
     * Returns an unmodifiable list of X-coordinates defining the shape's points.
     * This method is primarily used by polygon-like shapes.
     * Default implementation returns an empty list.
     *
     * @return An unmodifiable list of X-coordinates.
     */
    public List<Double> getXPoints() {
        return Collections.emptyList();
    }

    /**
     * Returns an unmodifiable list of Y-coordinates defining the shape's points.
     * This method is primarily used by polygon-like shapes.
     * Default implementation returns an empty list.
     *
     * @return An unmodifiable list of Y-coordinates.
     */
    public List<Double> getYPoints() {
        return Collections.emptyList();
    }

    /**
     * Creates and returns a copy of this object. The precise meaning of "copy" may depend
     * on the class of the object. The general intent is that for any object {@code x},
     * the expression:
     * {@code x.clone() != x} will be true, and
     * {@code x.clone().getClass() == x.getClass()} will be true.
     *
     * @return A clone of this instance.
     * @throws AssertionError if the cloning operation is not supported,
     * which indicates an unexpected error in the class hierarchy.
     */
    @Override
    public MyShape clone() {
        try {
            return (MyShape) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should ideally not happen as MyShape implements Cloneable
            throw new AssertionError("Cloning not supported for " + this.getClass().getSimpleName(), e);
        }
    }

    /**
     * Returns a string representation of the shape, primarily its name.
     *
     * @return The name of the shape.
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Returns the type of the shape as a {@code String} (e.g., "Rectangle", "Line").
     *
     * @return A {@code String} representing the shape type.
     */
    public abstract String getShapeType();

    /**
     * Flips the shape horizontally.
     * The implementation of this method will vary depending on the specific shape type.
     */
    public abstract void flipHorizontal();

    /**
     * Flips the shape vertically.
     * The implementation of this method will vary depending on the specific shape type.
     */
    public abstract void flipVertical();
}