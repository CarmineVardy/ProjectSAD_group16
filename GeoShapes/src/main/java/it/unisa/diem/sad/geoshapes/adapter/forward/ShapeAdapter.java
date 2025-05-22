package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.shape.Shape;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import javafx.scene.paint.Color;

public interface ShapeAdapter {
    Shape getFxShape(MyShape modelShape, double width, double height);
    void updateFxShape(MyShape modelShape, Shape fxShape);

    default Color convertToJavaFxColor(MyColor modelColor) {
        if (modelColor == null) {
            return Color.BLACK;
        }
        return new Color(
                modelColor.getRed(),
                modelColor.getGreen(),
                modelColor.getBlue(),
                modelColor.getOpacity()
        );
    }
}