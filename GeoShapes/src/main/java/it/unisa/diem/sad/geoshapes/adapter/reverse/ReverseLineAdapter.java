package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;

public class ReverseLineAdapter implements ReverseShapeAdapter {

    private static final ReverseLineAdapter INSTANCE = new ReverseLineAdapter();

    private ReverseLineAdapter() {}

    public static ReverseLineAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public MyShape getModelShape(Shape fxShape, double width, double height) {
        if (!(fxShape instanceof Line)) {
            throw new IllegalArgumentException("Expected Line");
        }

        Line fxLine = (Line) fxShape;
        MyLine modelLine = new MyLine(
                fxLine.getStartX() / width,
                fxLine.getStartY() / height,
                fxLine.getEndX() / width,
                fxLine.getEndY() / height,
                convertToModelColor((Color) fxLine.getStroke())
        );


        return modelLine;
    }
}