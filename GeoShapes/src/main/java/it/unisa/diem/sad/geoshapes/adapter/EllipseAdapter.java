package it.unisa.diem.sad.geoshapes.adapter;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;

import javafx.scene.shape.Shape;
import javafx.scene.shape.Ellipse;

public class EllipseAdapter implements ShapeAdapter {


    @Override
    public Shape getFxShape(MyShape modelShape, double width, double height) {
        if (!(modelShape instanceof MyEllipse)) {
            throw new IllegalArgumentException("Expected MyEllipse");
        }
        MyEllipse modelEllipse = (MyEllipse) modelShape;
        Ellipse fxEllipse = new Ellipse(
                modelEllipse.getCenterX() * width,
                modelEllipse.getCenterY() * height,
                modelEllipse.getRadiusX() * width,
                modelEllipse.getRadiusY() * height
        );
        fxEllipse.setStroke(convertToJavaFxColor(modelEllipse.getBorderColor()));
        fxEllipse.setFill(convertToJavaFxColor(modelEllipse.getFillColor()));
        fxEllipse.setStrokeWidth(2.0);

        return fxEllipse;
    }

}