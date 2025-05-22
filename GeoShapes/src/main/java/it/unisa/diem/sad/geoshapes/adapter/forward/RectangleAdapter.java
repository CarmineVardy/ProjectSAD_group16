package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Rectangle;

public class RectangleAdapter implements ShapeAdapter {

    private static final RectangleAdapter INSTANCE = new RectangleAdapter();

    private RectangleAdapter() {}

    public static RectangleAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public Shape getFxShape(MyShape modelShape, double width, double height) {
        if (!(modelShape instanceof MyRectangle)) {
            throw new IllegalArgumentException("Expected MyRectangle");
        }
        MyRectangle modelRectangle = (MyRectangle) modelShape;

        // Calcola posizione e dimensioni da startX, startY, endX, endY
        double x = Math.min(modelRectangle.getStartX(), modelRectangle.getEndX()) * width;
        double y = Math.min(modelRectangle.getStartY(), modelRectangle.getEndY()) * height;
        double rectWidth = Math.abs(modelRectangle.getEndX() - modelRectangle.getStartX()) * width;
        double rectHeight = Math.abs(modelRectangle.getEndY() - modelRectangle.getStartY()) * height;

        Rectangle fxRectangle = new Rectangle(x, y, rectWidth, rectHeight);
        fxRectangle.setStroke(convertToJavaFxColor(modelRectangle.getBorderColor()));
        fxRectangle.setFill(convertToJavaFxColor(modelRectangle.getFillColor()));
        fxRectangle.setStrokeWidth(2.0);
        return fxRectangle;
    }
}