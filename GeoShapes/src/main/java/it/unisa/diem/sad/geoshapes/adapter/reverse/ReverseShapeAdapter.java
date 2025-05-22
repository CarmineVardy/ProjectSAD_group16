package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;

public interface ReverseShapeAdapter {
    MyShape getModelShape(Shape fxShape, double width, double height);

    default MyColor convertToModelColor(Color fxColor) {
        if (fxColor == null) {
            return new MyColor(0, 0, 0, 1); // Default black
        }
        return new MyColor(
                fxColor.getRed(),
                fxColor.getGreen(),
                fxColor.getBlue(),
                fxColor.getOpacity()
        );
    }
}