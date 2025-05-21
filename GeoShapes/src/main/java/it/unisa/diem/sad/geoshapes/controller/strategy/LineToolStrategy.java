package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.decorator.PreviewDecorator;
import it.unisa.diem.sad.geoshapes.decorator.ShapeDecorator;
import it.unisa.diem.sad.geoshapes.model.factory.LineFactory;
import it.unisa.diem.sad.geoshapes.model.factory.ShapeFactory;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.Cursor;

public class LineToolStrategy implements ToolStrategy {

    private final Pane drawingArea;

    private final ColorPicker borderColorPicker;
    private final ColorPicker fillColorPicker;

    private final ShapeFactory factory;

    private Shape previewFxShape;

    private ShapeDecorator previewDecorator;

    private double startX, startY, endX, endY;

    private static final double MIN_LENGTH = 2.0; // Minimum length to consider a line valid

    private InteractionCallback callback;


    public LineToolStrategy(Pane drawingArea, ColorPicker borderColorPicker, ColorPicker fillColorPicker, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.borderColorPicker = borderColorPicker;
        this.fillColorPicker = fillColorPicker;
        this.factory = new LineFactory();
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

        previewFxShape = new Line(startX, startY, endX, endY);

        previewFxShape.setStroke(borderColorPicker.getValue());
        previewFxShape.setStrokeWidth(2.0);

        previewDecorator = new PreviewDecorator(previewFxShape);
        previewDecorator.applyDecoration();

        drawingArea.getChildren().add(previewFxShape);
    }

    @Override
    public void handleDragged(MouseEvent event) {
        endX = event.getX();
        endY = event.getY();
        ((Line) previewFxShape).setEndX(endX);
        ((Line) previewFxShape).setEndY(endY);
    }

    @Override
    public void handleReleased(MouseEvent event) {
        if (previewFxShape != null) {
            // Final coordinates
            endX = event.getX();
            endY = event.getY();

            double dx = endX - startX;
            double dy = endY - startY;
            double length = Math.sqrt(dx * dx + dy * dy);

            if (length >= MIN_LENGTH) {
                Color borderColor = borderColorPicker.getValue();
                Color fillColor = fillColorPicker.getValue(); // Usa il fill color picker invece di TRANSPARENT

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