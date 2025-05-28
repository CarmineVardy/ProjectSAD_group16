package it.unisa.diem.sad.geoshapes.controller.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ShapeClipboardImpl implements ShapeClipboard {

    private final List<Shape> clipboard;
    private final BooleanProperty emptyProperty;
    private static final double OFFSET = 20.0;

    public ShapeClipboardImpl() {
        this.clipboard = new ArrayList<>();
        this.emptyProperty = new SimpleBooleanProperty(true);
    }

    @Override
    public void copy(List<Shape> shapes) {
        if (shapes == null) {
            throw new IllegalArgumentException("Shape list cannot be null");
        }

        clipboard.clear();
        for (Shape shape : shapes) {
            if (shape != null) {
                clipboard.add(cloneShape(shape));
            }
        }

        // Aggiorna la property
        updateEmptyProperty();
    }

    @Override
    public List<Shape> paste() {
        if (clipboard.isEmpty()) {
            return Collections.emptyList();
        }

        List<Shape> pastedShapes = new ArrayList<>();

        // Clona le forme dalla clipboard e applica l'offset
        for (Shape shape : clipboard) {
            Shape cloned = cloneShape(shape);
            applyOffset(cloned, OFFSET);
            pastedShapes.add(cloned);
        }

        // Sostituisce la clipboard con le forme appena incollate
        // In modo che il prossimo incolla abbia offset rispetto a queste
        clipboard.clear();
        clipboard.addAll(pastedShapes);

        // La property rimane false perché la clipboard non è vuota
        // (contiene le nuove forme incollate)

        return Collections.unmodifiableList(pastedShapes);
    }

    @Override
    public boolean isEmpty() {
        return clipboard.isEmpty();
    }

    public BooleanProperty emptyProperty() {
        return emptyProperty;
    }

    private void updateEmptyProperty() {
        emptyProperty.set(clipboard.isEmpty());
    }

    private Shape cloneShape(Shape original) {
        if (original instanceof Rectangle) {
            Rectangle rect = (Rectangle) original;
            Rectangle clone = new Rectangle(rect.getWidth(), rect.getHeight());
            clone.setX(rect.getX());
            clone.setY(rect.getY());
            copyCommonProperties(original, clone);
            return clone;

        } else if (original instanceof Ellipse) {
            Ellipse ellipse = (Ellipse) original;
            Ellipse clone = new Ellipse(ellipse.getRadiusX(), ellipse.getRadiusY());
            clone.setCenterX(ellipse.getCenterX());
            clone.setCenterY(ellipse.getCenterY());
            copyCommonProperties(original, clone);
            return clone;

        } else if (original instanceof Line) {
            Line line = (Line) original;
            Line clone = new Line(line.getStartX(), line.getStartY(),
                    line.getEndX(), line.getEndY());
            copyCommonProperties(original, clone);
            return clone;
        }

        throw new IllegalArgumentException("Unsupported shape type: " + original.getClass());
    }

    private void copyCommonProperties(Shape original, Shape clone) {
        clone.setFill(original.getFill());
        clone.setStroke(original.getStroke());
        clone.setStrokeWidth(original.getStrokeWidth());
        clone.setOpacity(original.getOpacity());
    }

    private void applyOffset(Shape shape, double offset) {
        if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;
            rect.setX(rect.getX() + offset);
            rect.setY(rect.getY() + offset);

        } else if (shape instanceof Ellipse) {
            Ellipse ellipse = (Ellipse) shape;
            ellipse.setCenterX(ellipse.getCenterX() + offset);
            ellipse.setCenterY(ellipse.getCenterY() + offset);

        } else if (shape instanceof Line) {
            Line line = (Line) shape;
            line.setStartX(line.getStartX() + offset);
            line.setStartY(line.getStartY() + offset);
            line.setEndX(line.getEndX() + offset);
            line.setEndY(line.getEndY() + offset);
        }
    }
}