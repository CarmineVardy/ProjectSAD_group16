package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Ellipse;
import javafx.scene.paint.Color;

public class ReverseEllipseAdapter implements ReverseShapeAdapter {

    private static final ReverseEllipseAdapter INSTANCE = new ReverseEllipseAdapter();

    private ReverseEllipseAdapter() {}

    public static ReverseEllipseAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public MyShape getModelShape(Shape fxShape, double width, double height) {
        if (!(fxShape instanceof Ellipse)) {
            throw new IllegalArgumentException("Expected Ellipse");
        }

        Ellipse fxEllipse = (Ellipse) fxShape;

        // Converti da centerX, centerY, radiusX, radiusY a startX, startY, endX, endY
        double centerX = fxEllipse.getCenterX() / width;
        double centerY = fxEllipse.getCenterY() / height;
        double radiusX = fxEllipse.getRadiusX() / width;
        double radiusY = fxEllipse.getRadiusY() / height;

        double startX = centerX - radiusX;
        double startY = centerY - radiusY;
        double endX = centerX + radiusX;
        double endY = centerY + radiusY;
        double rotation = fxEllipse.getRotate();

        MyEllipse modelEllipse = new MyEllipse(
                startX, startY, endX, endY,rotation,
                convertToModelColor((Color) fxEllipse.getStroke()),
                convertToModelColor((Color) fxEllipse.getFill())
        );

        return modelEllipse;
    }
}