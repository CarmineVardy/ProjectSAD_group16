package it.unisa.diem.sad.geoshapes.controller.decorator;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.Collections;
import java.util.List;


public class PreviewShapeDecorator implements ShapeDecorator {

    private final Shape shape;

    private Paint originalFill;
    private double originalOpacity;

    public PreviewShapeDecorator(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void applyDecoration() {
        storeOriginalProperties();
        shape.getStrokeDashArray().setAll(5.0, 4.0);
        shape.setOpacity(0.75);
        Color currentFill = (Color) shape.getFill();
        if (shape.getFill() != null) {
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

    @Override
    public List<Circle> getResizeHandles() {
        return Collections.emptyList();
    }

}