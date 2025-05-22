package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class ReverseRectangleAdapter implements ReverseShapeAdapter {

    private static final ReverseRectangleAdapter INSTANCE = new ReverseRectangleAdapter();

    private ReverseRectangleAdapter() {}

    public static ReverseRectangleAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public MyShape getModelShape(Shape fxShape, double width, double height) {
        if (!(fxShape instanceof Rectangle)) {
            throw new IllegalArgumentException("Expected Rectangle");
        }

        Rectangle fxRectangle = (Rectangle) fxShape;

        // Converti da x, y, width, height a startX, startY, endX, endY
        double startX = fxRectangle.getX() / width;
        double startY = fxRectangle.getY() / height;
        double endX = (fxRectangle.getX() + fxRectangle.getWidth()) / width;
        double endY = (fxRectangle.getY() + fxRectangle.getHeight()) / height;

        MyRectangle modelRectangle = new MyRectangle(
                startX, startY, endX, endY,
                convertToModelColor((Color) fxRectangle.getStroke()),
                convertToModelColor((Color) fxRectangle.getFill())
        );

        return modelRectangle;
    }
}