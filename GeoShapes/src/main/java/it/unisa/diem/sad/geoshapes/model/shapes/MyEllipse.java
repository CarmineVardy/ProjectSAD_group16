package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;

/**
 * Represents an ellipse shape in the GeoShapes application.
 * This class extends {@link MyShape} and provides specific implementations
 * for geometric operations like flipping.
 */
public class MyEllipse extends MyShape {

    /**
     * Constructs a new {@code MyEllipse} with specified starting and ending coordinates,
     * rotation, border color, and fill color.
     *
     * @param startX The starting X-coordinate of the ellipse's bounding box.
     * @param startY The starting Y-coordinate of the ellipse's bounding box.
     * @param endX The ending X-coordinate of the ellipse's bounding box.
     * @param endY The ending Y-coordinate of the ellipse's bounding box.
     * @param rotation The rotation angle of the ellipse in degrees.
     * @param borderMyColor The border color of the ellipse.
     * @param fillMyColor The fill color of the ellipse.
     */
    public MyEllipse(double startX, double startY, double endX, double endY, double rotation, MyColor borderMyColor, MyColor fillMyColor) {
        super(startX, startY, endX, endY, rotation, borderMyColor, fillMyColor);
    }

    /**
     * Returns the string representation of the shape type, which is "Ellipse".
     *
     * @return The string "Ellipse".
     */
    @Override
    public String getShapeType() {
        return "Ellipse";
    }

    /**
     * Flips the ellipse horizontally by reflecting its X-coordinates
     * relative to its horizontal center and negating its rotation.
     */
    @Override
    public void flipHorizontal() {
        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;

        double newStartX = 2 * centerX - startX;
        double newEndX = 2 * centerX - endX;

        this.startX = newStartX;
        this.endX = newEndX;

        // Adjust rotation for horizontal flip, ensuring angle is within -180 to 180 degrees
        if (Math.abs(rotation) > 1e-6) { // Check if rotation is significant
            this.rotation = -rotation;
            while (this.rotation > 180.0) this.rotation -= 360.0;
            while (this.rotation < -180.0) this.rotation += 360.0;
        }

    }

    /**
     * Flips the ellipse vertically by reflecting its Y-coordinates
     * relative to its vertical center and adjusting its rotation by 180 degrees.
     */
    @Override
    public void flipVertical() {

        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;

        double newStartY = 2 * centerY - startY;
        double newEndY = 2 * centerY - endY;

        this.startY = newStartY;
        this.endY = newEndY;

        // Adjust rotation for vertical flip, ensuring angle is within -180 to 180 degrees
        if (Math.abs(rotation) > 1e-6) { // Check if rotation is significant
            this.rotation = 180.0 - rotation;
            while (this.rotation > 180.0) this.rotation -= 360.0;
            while (this.rotation < -180.0) this.rotation += 360.0;
        }

    }



}