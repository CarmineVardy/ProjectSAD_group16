package it.unisa.diem.sad.geoshapes.decorator;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import java.util.ArrayList;
import java.util.List;

public class PreviewDecorator implements ShapeDecorator {
    private final Shape shape;

    // Original properties to restore
    private Paint originalStroke;
    private double originalStrokeWidth;
    private Paint originalFill;
    private List<Double> originalStrokeDashArray;
    private StrokeLineCap originalStrokeLineCap;
    private double originalOpacity;


    public PreviewDecorator(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void applyDecoration() {
        // Store original properties
        originalStroke = shape.getStroke();
        originalStrokeWidth = shape.getStrokeWidth();
        originalFill = shape.getFill();
        originalStrokeDashArray = new ArrayList<>(shape.getStrokeDashArray());
        originalStrokeLineCap = shape.getStrokeLineCap();
        originalOpacity = shape.getOpacity();

        // Apply preview styling
        shape.setStroke(Color.rgb(80, 80, 80, 0.9)); // Dark gray, slightly transparent stroke for visibility
        shape.getStrokeDashArray().setAll(5.0, 4.0);    // Dashed line
        shape.setStrokeLineCap(StrokeLineCap.BUTT);
        shape.setOpacity(0.75); // Overall opacity for preview

        Paint currentFill = shape.getFill();
        // For preview, often a very light, semi-transparent fill is good regardless of selected fill
        // This ensures the area is visible but clearly a preview.
        if (currentFill != null) { // If a fill is set (e.g. for Rectangle, Ellipse)
            if (currentFill instanceof Color) {
                Color c = (Color) currentFill;
                // Use a standard preview fill color but retain some of original color's alpha if it's not fully opaque
                shape.setFill(new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.min(c.getOpacity(),0.35)));
            } else {
                // For non-Color paints (gradients, etc.), use a standard light preview fill
                shape.setFill(new Color(0.85, 0.85, 0.85, 0.30)); // Light gray, 30% opacity
            }
        } else { // currentFill is null (e.g., typically for Line)
            // Do nothing, or apply a very transparent fill if you want lines to have a "preview area"
        }
    }

    @Override
    public void removeDecoration() {
        // Restore original properties
        shape.setStroke(originalStroke);
        shape.setStrokeWidth(originalStrokeWidth);
        shape.setFill(originalFill);
        shape.getStrokeDashArray().setAll(originalStrokeDashArray);
        shape.setStrokeLineCap(originalStrokeLineCap);
        shape.setOpacity(originalOpacity);
    }

    @Override
    public Shape getDecoratedShape() {
        return shape; // Returns the same shape instance that was modified
    }
}