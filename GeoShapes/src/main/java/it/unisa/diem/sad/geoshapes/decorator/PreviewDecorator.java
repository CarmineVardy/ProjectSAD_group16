package it.unisa.diem.sad.geoshapes.decorator;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

public class PreviewDecorator implements ShapeDecorator {

    private final Shape shape;

    private Paint originalFill;
    private double originalOpacity;

    public PreviewDecorator(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void applyDecoration() {
        storeOriginalProperties();

        shape.getStrokeDashArray().setAll(5.0, 4.0);
        shape.setOpacity(0.75);

        if (shape.getFill() instanceof Color) {
            Color currentFill = (Color) shape.getFill();
            shape.setFill(currentFill.brighter());
        }
    }

    private void storeOriginalProperties() {
        originalFill = shape.getFill();
        originalOpacity = shape.getOpacity();
    }

    @Override
    public void removeDecoration() {
        shape.setFill(originalFill);
        shape.getStrokeDashArray().clear();
        shape.setOpacity(originalOpacity);
    }

    @Override
    public Shape getDecoratedShape() {
        return shape;
    }
}