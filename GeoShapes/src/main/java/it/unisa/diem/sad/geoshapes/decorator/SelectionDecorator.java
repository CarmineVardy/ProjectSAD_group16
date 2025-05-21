package it.unisa.diem.sad.geoshapes.decorator;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;


public class SelectionDecorator implements ShapeDecorator {
    private final Shape shape;

    private Color originalStrokeColor;
    private double originalStrokeWidth;
    private StrokeType originalStrokeType;
    private double originalOpacity;
    private Paint originalFill;

    public SelectionDecorator(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void applyDecoration() {
        // Store original properties
        storeOriginalProperties();

        // Apply selection styling
        shape.setStroke(Color.GREEN);
        shape.setStrokeWidth(originalStrokeWidth + 0.5);
        shape.setStrokeType(StrokeType.OUTSIDE);

        if (originalFill instanceof Color originalColor) {
            if (originalColor.getOpacity() > 0.0) {
                Color newColor = new Color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), 0.7);
                shape.setFill(newColor);
            }
        }
    }

    @Override
    public void removeDecoration() {

        shape.setStroke(originalStrokeColor);
        shape.setStrokeWidth(originalStrokeWidth);
        shape.setStrokeType(originalStrokeType);
        shape.setFill(originalFill);
        shape.setOpacity(originalOpacity);
    }



    @Override
    public Shape getDecoratedShape() {
        return shape;
    }

    private void storeOriginalProperties() {
        originalStrokeColor = (Color) shape.getStroke();
        originalStrokeWidth = shape.getStrokeWidth();
        originalStrokeType = shape.getStrokeType();
        originalOpacity = shape.getOpacity();
        originalFill = shape.getFill();
    }
}