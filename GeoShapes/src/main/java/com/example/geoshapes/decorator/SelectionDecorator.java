package com.example.geoshapes.decorator;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;


public class SelectionDecorator implements ShapeDecorator {
    private final Shape shape;

    // Original properties to restore later
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

        // Set fill opacity to 70% while maintaining the original fill color
        if (originalFill instanceof Color) {
            Color originalColor = (Color) originalFill;
            Color newColor = new Color(
                    originalColor.getRed(),
                    originalColor.getGreen(),
                    originalColor.getBlue(),
                    0.7
            );
            shape.setFill(newColor);
        }
    }

    @Override
    public void removeDecoration() {
        // Restore original properties
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