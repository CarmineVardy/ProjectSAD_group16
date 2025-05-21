package it.unisa.diem.sad.geoshapes.adapter;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;

import javafx.scene.shape.Shape;
import javafx.scene.shape.Rectangle;

public class RectangleAdapter implements ShapeAdapter {

    @Override
    public Shape getFxShape(MyShape modelShape, double width, double height) {

        if (!(modelShape instanceof MyRectangle)) {
            throw new IllegalArgumentException("Expected MyRectangle");
        }
        MyRectangle modelRectangle = (MyRectangle) modelShape;
        Rectangle fxRectangle = new Rectangle(
                modelRectangle.getTopLeftX() * width,
                modelRectangle.getTopLeftY() * height,
                modelRectangle.getWidth() * width,
                modelRectangle.getHeight() * height
        );
        fxRectangle.setStroke(convertToJavaFxColor(modelRectangle.getBorderColor()));
        fxRectangle.setFill(convertToJavaFxColor(modelRectangle.getFillColor()));
        fxRectangle.setStrokeWidth(2.0);

        return fxRectangle;
    }

}