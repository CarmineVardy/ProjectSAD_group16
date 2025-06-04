package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a polygon shape in the GeoShapes application.
 * This class extends {@link MyShape} and supports both regular and irregular polygons
 * defined by a list of points. It also provides specific implementations for geometric operations.
 */
public class MyPolygon extends MyShape {

    // Private instance variables
    private List<Double> xPoints;
    private List<Double> yPoints;
    private int numberOfPoints; // Renamed from nPoints for clarity

    /**
     * Constructs a new {@code MyPolygon} with specified lists of X and Y coordinates,
     * rotation, border color, and fill color.
     * The polygon must have between 3 and 8 vertices.
     *
     * @param xPoints A list of X-coordinates for the polygon's vertices.
     * @param yPoints A list of Y-coordinates for the polygon's vertices.
     * @param rotation The rotation angle of the polygon in degrees.
     * @param borderColor The border color of the polygon.
     * @param fillColor The fill color of the polygon.
     * @throws IllegalArgumentException If {@code xPoints} and {@code yPoints} do not have the same size,
     * or if the number of vertices is not between 3 and 8.
     */
    public MyPolygon(List<Double> xPoints, List<Double> yPoints, double rotation, MyColor borderColor, MyColor fillColor) {
        super(calculateMinX(xPoints), calculateMinY(yPoints),
                calculateMaxX(xPoints), calculateMaxY(yPoints),
                rotation, borderColor, fillColor);

        if (xPoints.size() != yPoints.size()) {
            throw new IllegalArgumentException("xPoints and yPoints must have the same size");
        }
        if (xPoints.size() < 3 || xPoints.size() > 8) {
            throw new IllegalArgumentException("Polygon must have between 3 and 8 vertices");
        }

        this.xPoints = new ArrayList<>(xPoints);
        this.yPoints = new ArrayList<>(yPoints);
        this.numberOfPoints = xPoints.size();
    }

    /**
     * Constructs a new regular {@code MyPolygon} based on a bounding box,
     * a specified number of vertices, rotation, border color, and fill color.
     * The polygon must have between 3 and 8 vertices.
     *
     * @param startX The starting X-coordinate of the bounding box.
     * @param startY The starting Y-coordinate of the bounding box.
     * @param endX The ending X-coordinate of the bounding box.
     * @param endY The ending Y-coordinate of the bounding box.
     * @param numberOfVertices The desired number of vertices for the regular polygon (3 to 8).
     * @param rotation The rotation angle of the polygon in degrees.
     * @param borderColor The border color of the polygon.
     * @param fillColor The fill color of the polygon.
     * @throws IllegalArgumentException If the number of vertices is not between 3 and 8.
     */
    public MyPolygon(double startX, double startY, double endX, double endY,
                     int numberOfVertices, double rotation, MyColor borderColor, MyColor fillColor) {
        super(startX, startY, endX, endY, rotation, borderColor, fillColor);

        if (numberOfVertices < 3 || numberOfVertices > 8) {
            throw new IllegalArgumentException("Polygon must have between 3 and 8 vertices");
        }

        this.numberOfPoints = numberOfVertices;
        this.xPoints = new ArrayList<>();
        this.yPoints = new ArrayList<>();

        generateRegularPolygon(startX, startY, endX, endY, numberOfVertices);
    }

    // Public methods

    /**
     * Returns a new list containing the X-coordinates of the polygon's vertices.
     *
     * @return A {@code List} of X-coordinates.
     */
    @Override
    public List<Double> getXPoints() {
        return new ArrayList<>(xPoints);
    }

    /**
     * Returns a new list containing the Y-coordinates of the polygon's vertices.
     *
     * @return A {@code List} of Y-coordinates.
     */
    @Override
    public List<Double> getYPoints() {
        return new ArrayList<>(yPoints);
    }

    /**
     * Returns the number of vertices in the polygon.
     *
     * @return The number of vertices.
     */
    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    /**
     * Sets the coordinates of the polygon's vertices.
     * Updates the bounding box coordinates (startX, startY, endX, endY) based on the new points.
     * The new lists of points must have the same size and the number of vertices must be between 3 and 8.
     *
     * @param xPoints A new list of X-coordinates for the polygon's vertices.
     * @param yPoints A new list of Y-coordinates for the polygon's vertices.
     * @throws IllegalArgumentException If {@code xPoints} and {@code yPoints} do not have the same size,
     * or if the number of vertices is not between 3 and 8.
     */
    @Override
    public void setPoints(List<Double> xPoints, List<Double> yPoints) {
        if (xPoints.size() != yPoints.size()) {
            throw new IllegalArgumentException("xPoints and yPoints must have the same size");
        }
        if (xPoints.size() < 3 || xPoints.size() > 8) {
            throw new IllegalArgumentException("Polygon must have between 3 and 8 vertices");
        }

        this.xPoints = new ArrayList<>(xPoints);
        this.yPoints = new ArrayList<>(yPoints);
        this.numberOfPoints = xPoints.size();

        this.startX = calculateMinX(xPoints);
        this.startY = calculateMinY(yPoints);
        this.endX = calculateMaxX(xPoints);
        this.endY = calculateMaxY(yPoints);
    }

    /**
     * Returns the string representation of the shape type, which is "Polygon".
     *
     * @return The string "Polygon".
     */
    @Override
    public String getShapeType() {
        return "Polygon";
    }

    /**
     * Creates and returns a deep copy of this {@code MyPolygon} instance.
     * The lists of X and Y coordinates are also cloned.
     *
     * @return A cloned {@code MyPolygon} instance.
     */
    @Override
    public MyPolygon clone() {
        MyPolygon cloned = (MyPolygon) super.clone();
        cloned.xPoints = new ArrayList<>(this.xPoints);
        cloned.yPoints = new ArrayList<>(this.yPoints);
        return cloned;
    }

    /**
     * Flips the polygon horizontally by reflecting each vertex's X-coordinate
     * relative to the horizontal center of the polygon's bounding box and adjusting its rotation.
     */
    @Override
    public void flipHorizontal() {
        double centerX = (getStartX() + getEndX()) / 2.0;

        for (int i = 0; i < xPoints.size(); i++) {
            double newX = 2 * centerX - xPoints.get(i);
            xPoints.set(i, newX);
        }

        // Update bounding box after flipping points
        double newStartX = 2 * centerX - getEndX();
        double newEndX = 2 * centerX - getStartX();
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
     * Flips the polygon vertically by reflecting each vertex's Y-coordinate
     * relative to the vertical center of the polygon's bounding box and adjusting its rotation.
     */
    @Override
    public void flipVertical() {
        double centerY = (getStartY() + getEndY()) / 2.0;

        for (int i = 0; i < yPoints.size(); i++) {
            double newY = 2 * centerY - yPoints.get(i);
            yPoints.set(i, newY);
        }

        // Update bounding box after flipping points
        double newStartY = 2 * centerY - getEndY();
        double newEndY = 2 * centerY - getStartY();
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

    // Private static helper methods

    /**
     * Calculates the minimum X-coordinate from a list of X-coordinates.
     *
     * @param xPoints The list of X-coordinates.
     * @return The minimum X-coordinate, or 0.0 if the list is empty.
     */
    private static double calculateMinX(List<Double> xPoints) {
        return xPoints.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
    }

    /**
     * Calculates the minimum Y-coordinate from a list of Y-coordinates.
     *
     * @param yPoints The list of Y-coordinates.
     * @return The minimum Y-coordinate, or 0.0 if the list is empty.
     */
    private static double calculateMinY(List<Double> yPoints) {
        return yPoints.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
    }

    /**
     * Calculates the maximum X-coordinate from a list of X-coordinates.
     *
     * @param xPoints The list of X-coordinates.
     * @return The maximum X-coordinate, or 0.0 if the list is empty.
     */
    private static double calculateMaxX(List<Double> xPoints) {
        return xPoints.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    }

    /**
     * Calculates the maximum Y-coordinate from a list of Y-coordinates.
     *
     * @param yPoints The list of Y-coordinates.
     * @return The maximum Y-coordinate, or 0.0 if the list is empty.
     */
    private static double calculateMaxY(List<Double> yPoints) {
        return yPoints.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    }

    // Private methods

    /**
     * Generates the vertices for a regular polygon based on a given bounding box
     * and number of sides. The vertices are stored in {@code xPoints} and {@code yPoints}.
     *
     * @param startX The starting X-coordinate of the bounding box.
     * @param startY The starting Y-coordinate of the bounding box.
     * @param endX The ending X-coordinate of the bounding box.
     * @param endY The ending Y-coordinate of the bounding box.
     * @param numberOfSides The number of sides for the regular polygon.
     */
    private void generateRegularPolygon(double startX, double startY, double endX, double endY, int numberOfSides) {
        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;
        double radiusX = Math.abs(endX - startX) / 2.0;
        double radiusY = Math.abs(endY - startY) / 2.0;

        for (int i = 0; i < numberOfSides; i++) {
            double angle = 2.0 * Math.PI * i / numberOfSides - Math.PI / 2; // Start from top
            double x = centerX + radiusX * Math.cos(angle);
            double y = centerY + radiusY * Math.sin(angle);
            xPoints.add(x);
            yPoints.add(y);
        }
    }
}