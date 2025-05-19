package com.example.geoshapes.adapter;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import com.example.geoshapes.model.util.MyColor;

public interface ShapeAdapter {

    Shape getFxShape();

    default Color convertToJavaFxColor(MyColor modelColor) {
        return new Color(
                modelColor.getRed(),
                modelColor.getGreen(),
                modelColor.getBlue(),
                modelColor.getOpacity()
        );
    }
}