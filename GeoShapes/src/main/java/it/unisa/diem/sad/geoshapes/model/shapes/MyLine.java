package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

/**
 * Represents a line shape in the GeoShapes application.
 * This class extends {@link MyShape} and is specifically designed
 * for line segments, therefore it does not have a fill color.
 */
public class MyLine extends MyShape {

    /**
     * Constructs a new {@code MyLine} with specified starting and ending coordinates,
     * rotation, and border color.
     * The fill color is implicitly {@code null} for a line.
     *
     * @param startX The starting X-coordinate of the line.
     * @param startY The starting Y-coordinate of the line.
     * @param endX The ending X-coordinate of the line.
     * @param endY The ending Y-coordinate of the line.
     * @param rotation The rotation angle of the line in degrees.
     * @param borderMyColor The border color of the line.
     */
    public MyLine(double startX, double startY, double endX, double endY, double rotation, MyColor borderMyColor) {
        super(startX, startY, endX, endY, rotation, borderMyColor, null);
    }

    /**
     * Returns {@code null} as lines do not have a fill color.
     *
     * @return Always returns {@code null}.
     */
    @Override
    public MyColor getFillColor() {
        return null;
    }

    /**
     * This method does nothing as lines do not have a fill color.
     *
     * @param color The color to set (ignored).
     */
    @Override
    public void setFillColor(MyColor color) {
        // Lines do not have a fill color, so this method does nothing.
    }

    /**
     * Returns the string representation of the shape type, which is "Line".
     *
     * @return The string "Line".
     */
    @Override
    public String getShapeType() {
        return "Line";
    }

    /**
     * Flips the line horizontally by reflecting its X-coordinates relative to its center.
     */
    @Override
    public void flipHorizontal() {
        double centerX = (getStartX() + getEndX()) / 2.0;
        double newStartX = 2 * centerX - getStartX();
        double newEndX   = 2 * centerX - getEndX();
        setStartX(newStartX);
        setEndX(newEndX);
        // Rotation is typically adjusted based on coordinate system, but for lines
        // if only coordinates change, the visual flip might be handled by the new coordinates directly.
        // No explicit rotation adjustment for lines during flip is needed based on typical implementations.
    }

    /**
     * Flips the line vertically by reflecting its Y-coordinates relative to its center.
     */
    @Override
    public void flipVertical() {
        double centerY = (getStartY() + getEndY()) / 2.0;
        double newStartY = 2 * centerY - getStartY();
        double newEndY   = 2 * centerY - getEndY();
        setStartY(newStartY);
        setEndY(newEndY);
        // Similar to horizontal flip, rotation adjustment is often not explicitly
        // needed for lines when only coordinates change.
    }
}