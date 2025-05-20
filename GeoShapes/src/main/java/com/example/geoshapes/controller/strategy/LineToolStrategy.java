package com.example.geoshapes.controller.strategy;

import com.example.geoshapes.decorator.PreviewDecorator;
import com.example.geoshapes.decorator.ShapeDecorator;
import com.example.geoshapes.model.factory.LineFactory;
import com.example.geoshapes.model.factory.ShapeFactory;
import com.example.geoshapes.model.shapes.MyShape;
import com.example.geoshapes.model.util.MyColor;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
// Removed unused import javafx.scene.shape.Shape;

public class LineToolStrategy implements ToolStrategy {

    private final Pane drawingArea;
    private final ColorPicker borderColorPicker;
    // fillColorPicker is not typically used for Line, but kept for consistency if model changes
    private final ColorPicker fillColorPicker;
    private final ShapeFactory factory;

    private javafx.scene.shape.Shape previewFxShape; // This will be the decorated shape
    private ShapeDecorator previewDecorator;
    private MyShape currentModelMyShape;

    private double startX, startY, endX, endY;
    private static final double MIN_LENGTH = 2.0; // Minimum length to consider a line valid


    public LineToolStrategy(Pane drawingArea, ColorPicker borderColorPicker, ColorPicker fillColorPicker) {
        this.drawingArea = drawingArea;
        this.borderColorPicker = borderColorPicker;
        this.fillColorPicker = fillColorPicker; // Retained for interface consistency or future use
        this.factory = new LineFactory();
    }

    @Override
    public void handlePressed(MouseEvent event) {
        reset(); // Clear any previous unfinished drawing

        startX = event.getX();
        startY = event.getY();
        endX = startX; // Initialize end to start
        endY = startY;

        Line rawLine = new Line(startX, startY, endX, endY);
        rawLine.setStroke(borderColorPicker.getValue());
        // Line typically doesn't have a fill. If it did, set it here before decoration.
        // rawLine.setFill(null); or fillColorPicker.getValue() if applicable

        previewDecorator = new PreviewDecorator(rawLine);
        previewDecorator.applyDecoration();

        previewFxShape = rawLine; // Or previewDecorator.getDecoratedShape();
        drawingArea.getChildren().add(previewFxShape);
    }

    @Override
    public void handleDragged(MouseEvent event) {
        if (previewFxShape instanceof Line) {
            endX = event.getX();
            endY = event.getY();
            ((Line) previewFxShape).setEndX(endX);
            ((Line) previewFxShape).setEndY(endY);
        }
    }

    @Override
    public void handleReleased(MouseEvent event) {
        if (previewFxShape != null) {
            // Final coordinates
            endX = event.getX();
            endY = event.getY();

            // Calculate length
            double dx = endX - startX;
            double dy = endY - startY;
            double length = Math.sqrt(dx * dx + dy * dy);

            // Remove preview from drawing area
            if (previewDecorator != null) {
                previewDecorator.removeDecoration(); // Clean up decoration
            }
            drawingArea.getChildren().remove(previewFxShape);

            previewFxShape = null;
            previewDecorator = null;

            if (length >= MIN_LENGTH) { // Check for minimum length
                Color borderColor = borderColorPicker.getValue();
                // For Line, fill color is often ignored or transparent in the model/adapter
                Color fillColor = Color.TRANSPARENT; // Default for Line unless model explicitly uses fillColorPicker
                // Color fillColor = fillColorPicker.getValue(); // If lines can have fill

                currentModelMyShape = factory.createShape(
                        startX / drawingArea.getWidth(), startY / drawingArea.getHeight(),
                        endX / drawingArea.getWidth(), endY / drawingArea.getHeight(),
                        new MyColor(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), borderColor.getOpacity()),
                        new MyColor(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), fillColor.getOpacity())
                );
            } else {
                currentModelMyShape = null; // Line is too short, not created
            }
        }
    }

    @Override
    public MyShape getFinalShape() {
        MyShape shapeToReturn = currentModelMyShape;
        currentModelMyShape = null; // Consume the shape
        return shapeToReturn;
    }

    public void reset() {
        if (previewFxShape != null) {
            if (previewDecorator != null) {
                previewDecorator.removeDecoration();
            }
            drawingArea.getChildren().remove(previewFxShape);
        }
        previewFxShape = null;
        previewDecorator = null;
        currentModelMyShape = null;
        // Reset coordinates if desired, though handlePressed usually re-initializes them
        startX = 0; startY = 0; endX = 0; endY = 0;
    }
}