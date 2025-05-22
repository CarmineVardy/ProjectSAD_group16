package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.decorator.PreviewDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import it.unisa.diem.sad.geoshapes.model.factory.EllipseFactory;
import it.unisa.diem.sad.geoshapes.model.factory.ShapeFactory;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;
import javafx.scene.Cursor;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;

public class EllipseToolStrategy implements ToolStrategy {

    private final Pane drawingArea;
    private final ColorPicker borderColorPicker;
    private final ColorPicker fillColorPicker;
    private final ShapeFactory factory;

    private Shape previewFxShape;
    private ShapeDecorator previewDecorator;

    private double startX, startY, endX, endY;
    private static final double MIN_RADIUS = 1.0; // Minimum radius (half of MIN_DIMENSION)

    private InteractionCallback callback;

    public EllipseToolStrategy(Pane drawingArea, ColorPicker borderColorPicker, ColorPicker fillColorPicker, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.borderColorPicker = borderColorPicker;
        this.fillColorPicker = fillColorPicker;
        this.factory = new EllipseFactory();
        this.callback = callback;
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        reset();

        drawingArea.setCursor(Cursor.CROSSHAIR);

        startX = event.getX();
        startY = event.getY();
        endX = startX;
        endY = startY;

        previewFxShape = new Ellipse(startX, startY, 0, 0); // CenterX, CenterY, RadiusX, RadiusY
        previewFxShape.setStroke(borderColorPicker.getValue());
        previewFxShape.setFill(fillColorPicker.getValue());
        previewFxShape.setStrokeWidth(2.0);

        previewDecorator = new PreviewDecorator(previewFxShape);
        previewDecorator.applyDecoration();

        drawingArea.getChildren().add(previewFxShape);
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
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

    @Override
    public void handleMouseReleased(MouseEvent event) {
        if (previewFxShape != null) {
            endX = event.getX();
            endY = event.getY();

            double radiusX = Math.abs(endX - startX) / 2;
            double radiusY = Math.abs(endY - startY) / 2;

            if (radiusX >= MIN_RADIUS && radiusY >= MIN_RADIUS) {
                Color borderColor = borderColorPicker.getValue();
                Color fillColor = fillColorPicker.getValue();


                MyColor borderMyColor = new MyColor(
                        borderColor.getRed(),
                        borderColor.getGreen(),
                        borderColor.getBlue(),
                        borderColor.getOpacity()
                );

                MyColor fillMyColor = new MyColor(
                        fillColor.getRed(),
                        fillColor.getGreen(),
                        fillColor.getBlue(),
                        fillColor.getOpacity()
                );

                MyShape newShape = factory.createShape(
                        startX / drawingArea.getWidth(),
                        startY / drawingArea.getHeight(),
                        endX / drawingArea.getWidth(),
                        endY / drawingArea.getHeight(),
                        borderMyColor,
                        fillMyColor
                );

                callback.onCreateShape(newShape);
            }

            reset();
        }
    }

    @Override
    public void handleMouseMoved(MouseEvent event) {
    }

    @Override
    public void handleBorderColorChange(Color color) {

    }

    @Override
    public void handleFillColorChange(Color color) {

    }

    @Override
    public void reset() {
        drawingArea.setCursor(Cursor.DEFAULT);
        if (previewFxShape != null) {
            if (previewDecorator != null) {
                previewDecorator.removeDecoration();
            }
            drawingArea.getChildren().remove(previewFxShape);
        }
        previewFxShape = null;
        previewDecorator = null;
        startX = 0;
        startY = 0;
        endX = 0;
        endY = 0;
    }
}