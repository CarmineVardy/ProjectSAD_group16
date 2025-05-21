package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.decorator.PreviewDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import it.unisa.diem.sad.geoshapes.model.factory.EllipseFactory;
import it.unisa.diem.sad.geoshapes.model.factory.ShapeFactory;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
// Removed unused import javafx.scene.shape.Shape;

public class EllipseToolStrategy implements ToolStrategy {

    private final Pane drawingArea;
    private final ColorPicker borderColorPicker;
    private final ColorPicker fillColorPicker;
    private final ShapeFactory factory;

    private javafx.scene.shape.Shape previewFxShape;
    private ShapeDecorator previewDecorator;
    private MyShape currentModelMyShape;

    private double startX, startY, endX, endY;
    private static final double MIN_RADIUS = 1.0; // Minimum radius (half of MIN_DIMENSION)

    public EllipseToolStrategy(Pane drawingArea, ColorPicker borderColorPicker, ColorPicker fillColorPicker) {
        this.drawingArea = drawingArea;
        this.borderColorPicker = borderColorPicker;
        this.fillColorPicker = fillColorPicker;
        this.factory = new EllipseFactory();
    }

    @Override
    public void handlePressed(MouseEvent event) {
        reset();

        startX = event.getX();
        startY = event.getY();
        endX = startX;
        endY = startY;

        Ellipse rawEllipse = new Ellipse(startX, startY, 0, 0); // CenterX, CenterY, RadiusX, RadiusY
        rawEllipse.setStroke(borderColorPicker.getValue());
        rawEllipse.setFill(fillColorPicker.getValue());

        previewDecorator = new PreviewDecorator(rawEllipse);
        previewDecorator.applyDecoration();

        previewFxShape = rawEllipse;
        drawingArea.getChildren().add(previewFxShape);
    }

    @Override
    public void handleDragged(MouseEvent event) {
        if (previewFxShape instanceof Ellipse) {
            endX = event.getX();
            endY = event.getY();

            double centerX = (startX + endX) / 2;
            double centerY = (startY + endY) / 2;
            double radiusX = Math.abs(endX - startX) / 2;
            double radiusY = Math.abs(endY - startY) / 2;

            ((Ellipse) previewFxShape).setCenterX(centerX);
            ((Ellipse) previewFxShape).setCenterY(centerY);
            ((Ellipse) previewFxShape).setRadiusX(radiusX);
            ((Ellipse) previewFxShape).setRadiusY(radiusY);
        }
    }

    @Override
    public void handleReleased(MouseEvent event) {
        if (previewFxShape != null) {
            endX = event.getX();
            endY = event.getY();

            double radiusX = Math.abs(endX - startX) / 2;
            double radiusY = Math.abs(endY - startY) / 2;

            if (previewDecorator != null) {
                previewDecorator.removeDecoration();
            }
            drawingArea.getChildren().remove(previewFxShape);

            previewFxShape = null;
            previewDecorator = null;

            if (radiusX >= MIN_RADIUS && radiusY >= MIN_RADIUS) {
                Color borderColor = borderColorPicker.getValue();
                Color fillColor = fillColorPicker.getValue();

                // The factory likely expects the bounding box coordinates for the ellipse
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
                currentModelMyShape = null; // Ellipse is too small
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