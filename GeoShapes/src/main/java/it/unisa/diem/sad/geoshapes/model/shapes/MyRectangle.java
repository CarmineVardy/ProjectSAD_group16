package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

/**
 * Represents a rectangle shape in the GeoShapes application.
 * This class extends {@link MyShape} and provides specific implementations
 * for geometric operations like flipping.
 */
public class MyRectangle extends MyShape {

    /**
     * Constructs a new {@code MyRectangle} with specified starting and ending coordinates,
     * rotation, border color, and fill color.
     *
     * @param startX The starting X-coordinate of the rectangle.
     * @param startY The starting Y-coordinate of the rectangle.
     * @param endX The ending X-coordinate of the rectangle.
     * @param endY The ending Y-coordinate of the rectangle.
     * @param rotation The rotation angle of the rectangle in degrees.
     * @param borderMyColor The border color of the rectangle.
     * @param fillMyColor The fill color of the rectangle.
     */
    public MyRectangle(double startX, double startY, double endX, double endY, double rotation, MyColor borderMyColor, MyColor fillMyColor) {
        super(startX, startY, endX, endY, rotation, borderMyColor, fillMyColor);
    }

    /**
     * Returns the string representation of the shape type, which is "Rectangle".
     *
     * @return The string "Rectangle".
     */
    @Override
    public String getShapeType() {
        return "Rectangle";
    }

    /**
     * Flips the rectangle horizontally by reflecting its X-coordinates
     * relative to its horizontal center and negating its rotation.
     */
    @Override
    public void flipHorizontal() {
        double centerX = (getStartX() + getEndX()) / 2.0;
        double newStartX = centerX - (getEndX() - centerX);
        double newEndX = centerX - (getStartX() - centerX);

        setStartX(Math.min(newStartX, newEndX));
        setEndX(Math.max(newStartX, newEndX));

        // Adjust rotation for horizontal flip, ensuring angle is within -180 to 180 degrees
        this.rotation = -this.rotation;
        this.rotation = this.rotation % 360;
        if (this.rotation > 180) {
            this.rotation -= 360;
        } else if (this.rotation <= -180) {
            this.rotation += 360;
        }
    }

    /**
     * Flips the rectangle vertically by reflecting its Y-coordinates
     * relative to its vertical center and adjusting its rotation by 180 degrees.
     */
    @Override
    public void flipVertical() {
        double centerY = (getStartY() + getEndY()) / 2.0;

        double newStartY = centerY - (getEndY() - centerY);
        double newEndY = centerY - (getStartY() - centerY);

        setStartY(Math.min(newStartY, newEndY));
        setEndY(Math.max(newStartY, newEndY));

        // Adjust rotation for vertical flip, ensuring angle is within -180 to 180 degrees
        this.rotation = 180 - this.rotation;
        if (this.rotation > 180.0) {
            this.rotation -= 360.0;
        } else if (this.rotation <= -180.0) {
            this.rotation += 360.0;
        }
    }
}