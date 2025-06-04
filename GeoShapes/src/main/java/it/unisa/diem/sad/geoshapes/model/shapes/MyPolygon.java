package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import java.util.ArrayList;
import java.util.List;

public class MyPolygon extends MyShape {

    private List<Double> xPoints;
    private List<Double> yPoints;
    private int nPoints;

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
        this.nPoints = xPoints.size();
    }

    public MyPolygon(double startX, double startY, double endX, double endY,
                     int nPoints, double rotation, MyColor borderColor, MyColor fillColor) {
        super(startX, startY, endX, endY, rotation, borderColor, fillColor);

        if (nPoints < 3 || nPoints > 8) {
            throw new IllegalArgumentException("Polygon must have between 3 and 8 vertices");
        }

        this.nPoints = nPoints;
        this.xPoints = new ArrayList<>();
        this.yPoints = new ArrayList<>();

        generateRegularPolygon(startX, startY, endX, endY, nPoints);
    }

    private void generateRegularPolygon(double startX, double startY, double endX, double endY, int nPoints) {
        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;
        double radiusX = Math.abs(endX - startX) / 2.0;
        double radiusY = Math.abs(endY - startY) / 2.0;

        for (int i = 0; i < nPoints; i++) {
            double angle = 2.0 * Math.PI * i / nPoints - Math.PI / 2;
            double x = centerX + radiusX * Math.cos(angle);
            double y = centerY + radiusY * Math.sin(angle);
            xPoints.add(x);
            yPoints.add(y);
        }
    }

    private static double calculateMinX(List<Double> xPoints) {
        return xPoints.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
    }

    private static double calculateMinY(List<Double> yPoints) {
        return yPoints.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
    }

    private static double calculateMaxX(List<Double> xPoints) {
        return xPoints.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    }

    private static double calculateMaxY(List<Double> yPoints) {
        return yPoints.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    }

    @Override
    public List<Double> getXPoints() {
        return new ArrayList<>(xPoints);
    }

    @Override
    public List<Double> getYPoints() {
        return new ArrayList<>(yPoints);
    }

    public int getNPoints() {
        return nPoints;
    }

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
        this.nPoints = xPoints.size();

        this.startX = calculateMinX(xPoints);
        this.startY = calculateMinY(yPoints);
        this.endX = calculateMaxX(xPoints);
        this.endY = calculateMaxY(yPoints);
    }

    @Override
    public String getShapeType() {
        return "Polygon";
    }


    @Override
    public MyPolygon clone() {
        MyPolygon cloned = (MyPolygon) super.clone();
        cloned.xPoints = new ArrayList<>(this.xPoints);
        cloned.yPoints = new ArrayList<>(this.yPoints);
        return cloned;
    }

    @Override
    public void flipHorizontal() {
        double centerX = (getStartX() + getEndX()) / 2.0;

        for (int i = 0; i < xPoints.size(); i++) {
            double newX = 2 * centerX - xPoints.get(i);
            xPoints.set(i, newX);
        }

        double newStartX = 2 * centerX - getEndX();
        double newEndX = 2 * centerX - getStartX();
        setStartX(Math.min(newStartX, newEndX));
        setEndX(Math.max(newStartX, newEndX));

        this.rotation = -this.rotation;
        this.rotation = this.rotation % 360;
        if (this.rotation > 180) {
            this.rotation -= 360;
        } else if (this.rotation <= -180) {
            this.rotation += 360;
        }
    }

    @Override
    public void flipVertical() {
        double centerY = (getStartY() + getEndY()) / 2.0;

        for (int i = 0; i < yPoints.size(); i++) {
            double newY = 2 * centerY - yPoints.get(i);
            yPoints.set(i, newY);
        }

        double newStartY = 2 * centerY - getEndY();
        double newEndY = 2 * centerY - getStartY();
        setStartY(Math.min(newStartY, newEndY));
        setEndY(Math.max(newStartY, newEndY));

        this.rotation = 180 - this.rotation;
        if (this.rotation > 180.0) {
            this.rotation -= 360.0;
        } else if (this.rotation <= -180.0) {
            this.rotation += 360.0;
        }
    }

}