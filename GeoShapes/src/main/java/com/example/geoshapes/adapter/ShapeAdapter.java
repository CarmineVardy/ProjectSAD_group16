package com.example.geoshapes.adapter;

import com.example.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Shape;

import com.example.geoshapes.model.util.MyColor;
import javafx.scene.paint.Color;

public interface ShapeAdapter {

    Shape getFxShape(MyShape modelShape, double width, double height);

    default Color convertToJavaFxColor(MyColor modelColor) {
        return new Color(
                modelColor.getRed(),
                modelColor.getGreen(),
                modelColor.getBlue(),
                modelColor.getOpacity()
        );
    }
}