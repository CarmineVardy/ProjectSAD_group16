package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Line;

public class LineAdapter implements ShapeAdapter {

    private static final LineAdapter INSTANCE = new LineAdapter();

    private LineAdapter() {}

    public static LineAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public Shape getFxShape(MyShape modelShape, double width, double height) {
        if (!(modelShape instanceof MyLine)) {
            throw new IllegalArgumentException("Expected MyLine");
        }
        MyLine modelLine = (MyLine) modelShape;
        Line fxLine = new Line(
                modelLine.getStartX() * width,
                modelLine.getStartY() * height,
                modelLine.getEndX() * width,
                modelLine.getEndY() * height
        );
        fxLine.setStroke(convertToJavaFxColor(modelLine.getBorderColor()));
        fxLine.setStrokeWidth(2.0);
        return fxLine;
    }

    @Override
    public void updateFxShape(MyShape modelShape, Shape fxShape) {
        if (!(modelShape instanceof MyLine myLine) || !(fxShape instanceof Line fxLine)) {
            throw new IllegalArgumentException("Type mismatch for LineAdapter update.");
        }
        fxLine.setStartX(myLine.getStartX());
        fxLine.setStartY(myLine.getStartY());
        fxLine.setEndX(myLine.getEndX());
        fxLine.setEndY(myLine.getEndY());
        fxLine.setStroke(convertToJavaFxColor(myLine.getBorderColor()));
    }
}