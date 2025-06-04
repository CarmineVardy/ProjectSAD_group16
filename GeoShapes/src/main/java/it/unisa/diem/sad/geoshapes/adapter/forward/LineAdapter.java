package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Line;

public class LineAdapter implements ShapeAdapter {

    private static final LineAdapter INSTANCE = new LineAdapter();

    public LineAdapter() {
    }

    public static LineAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public Shape getFxShape(MyShape modelShape, double width, double height) {
        if (!(modelShape instanceof MyLine modelLine)) {
            throw new IllegalArgumentException("Expected MyLine");
        }
        Line fxLine = new Line(
                modelLine.getStartX() * width,
                modelLine.getStartY() * height,
                modelLine.getEndX() * width,
                modelLine.getEndY() * height

        );
        fxLine.setStroke(convertToJavaFxColor(modelLine.getBorderColor()));
        fxLine.setStrokeWidth(2.0);
        fxLine.setRotate(modelLine.getRotation());

        return fxLine;
    }

}