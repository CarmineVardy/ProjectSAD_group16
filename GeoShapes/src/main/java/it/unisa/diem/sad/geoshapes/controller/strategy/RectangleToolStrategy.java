package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.decorator.PreviewDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import it.unisa.diem.sad.geoshapes.model.factory.RectangleFactory;
import it.unisa.diem.sad.geoshapes.model.factory.ShapeFactory;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
// Removed unused import javafx.scene.shape.Shape;

public class RectangleToolStrategy implements ToolStrategy {

    private final Pane drawingArea;
    private final ColorPicker borderColorPicker;
    private final ColorPicker fillColorPicker;
    private final ShapeFactory factory;

    private javafx.scene.shape.Shape previewFxShape;
    private ShapeDecorator previewDecorator;
    private MyShape currentModelMyShape;

    private double startX, startY, endX, endY;
    private static final double MIN_DIMENSION = 2.0; // Minimum width/height

    public RectangleToolStrategy(Pane drawingArea, ColorPicker borderColorPicker, ColorPicker fillColorPicker) {
        this.drawingArea = drawingArea;
        this.borderColorPicker = borderColorPicker;
        this.fillColorPicker = fillColorPicker;
        this.factory = new RectangleFactory();
    }

    @Override
    public void handlePressed(MouseEvent event) {
        reset();

        startX = event.getX();
        startY = event.getY();
        endX = startX;
        endY = startY;

        Rectangle rawRectangle = new Rectangle(startX, startY, 0, 0);
        rawRectangle.setStroke(borderColorPicker.getValue());
        rawRectangle.setFill(fillColorPicker.getValue());

        previewDecorator = new PreviewDecorator(rawRectangle);
        previewDecorator.applyDecoration();

        previewFxShape = rawRectangle;
        drawingArea.getChildren().add(previewFxShape);
    }

    @Override
    public void handleDragged(MouseEvent event) {
        if (previewFxShape instanceof Rectangle) {
            endX = event.getX();
            endY = event.getY();

            double x = Math.min(startX, endX);
            double y = Math.min(startY, endY);
            double width = Math.abs(endX - startX);
            double height = Math.abs(endY - startY);

            ((Rectangle) previewFxShape).setX(x);
            ((Rectangle) previewFxShape).setY(y);
            ((Rectangle) previewFxShape).setWidth(width);
            ((Rectangle) previewFxShape).setHeight(height);
        }
    }

    @Override
    public void handleReleased(MouseEvent event) {
        if (previewFxShape != null) {
            endX = event.getX();
            endY = event.getY();

            double width = Math.abs(endX - startX);
            double height = Math.abs(endY - startY);

            if (previewDecorator != null) {
                previewDecorator.removeDecoration();
            }
            drawingArea.getChildren().remove(previewFxShape);

            previewFxShape = null;
            previewDecorator = null;

            if (width >= MIN_DIMENSION && height >= MIN_DIMENSION) {
                Color borderColor = borderColorPicker.getValue();
                Color fillColor = fillColorPicker.getValue();

                // Ensure startX, startY are top-left for the model if factory expects that
                double modelStartX = Math.min(startX, endX);
                double modelStartY = Math.min(startY, endY);
                double modelEndX = Math.max(startX, endX);
                double modelEndY = Math.max(startY, endY);

                currentModelMyShape = factory.createShape(
                        modelStartX / drawingArea.getWidth(), modelStartY / drawingArea.getHeight(),
                        modelEndX / drawingArea.getWidth(), modelEndY / drawingArea.getHeight(),
                        new MyColor(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), borderColor.getOpacity()),
                        new MyColor(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), fillColor.getOpacity())
                );
            } else {
                currentModelMyShape = null; // Rectangle is too small
            }
        }
    }

    @Override
    public MyShape getFinalShape() {
        MyShape shapeToReturn = currentModelMyShape;
        currentModelMyShape = null; // Consume
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
        startX = 0; startY = 0; endX = 0; endY = 0;
    }
}