package it.unisa.diem.sad.geoshapes.adapter;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;

import javafx.scene.shape.Shape;
import javafx.scene.shape.Line;

public class LineAdapter implements ShapeAdapter {


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

        return fxLine;

    }

}