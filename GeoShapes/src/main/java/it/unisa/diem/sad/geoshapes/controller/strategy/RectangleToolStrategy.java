package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.decorator.PreviewDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import it.unisa.diem.sad.geoshapes.model.factory.RectangleFactory;
import it.unisa.diem.sad.geoshapes.model.factory.ShapeFactory;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;
import javafx.scene.Cursor;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class RectangleToolStrategy implements ToolStrategy {

    private final Pane drawingArea;
    private final ColorPicker borderColorPicker;
    private final ColorPicker fillColorPicker;
    private final ShapeFactory factory;

    private Shape previewFxShape;
    private ShapeDecorator previewDecorator;

    private double startX, startY, endX, endY;
    private static final double MIN_DIMENSION = 2.0; // Minimum width/height

    private InteractionCallback callback;

    public RectangleToolStrategy(Pane drawingArea, ColorPicker borderColorPicker, ColorPicker fillColorPicker, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.borderColorPicker = borderColorPicker;
        this.fillColorPicker = fillColorPicker;
        this.factory = new RectangleFactory();
        this.callback = callback;
    }

    @Override
    public void handlePressed(MouseEvent event) {
        reset();

        drawingArea.setCursor(Cursor.CROSSHAIR);

        startX = event.getX();
        startY = event.getY();
        endX = startX;
        endY = startY;

        previewFxShape = new Rectangle(startX, startY, 0, 0);

        previewFxShape.setStroke(borderColorPicker.getValue());
        previewFxShape.setFill(fillColorPicker.getValue());
        previewFxShape.setStrokeWidth(2.0);

        previewDecorator = new PreviewDecorator(previewFxShape);
        previewDecorator.applyDecoration();

        drawingArea.getChildren().add(previewFxShape);
    }

    @Override
    public void handleDragged(MouseEvent event) {
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


    @Override
    public void handleReleased(MouseEvent event) {
        if (previewFxShape != null) {
            endX = event.getX();
            endY = event.getY();

            double width = Math.abs(endX - startX);
            double height = Math.abs(endY - startY);

            if (width >= MIN_DIMENSION && height >= MIN_DIMENSION) {
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