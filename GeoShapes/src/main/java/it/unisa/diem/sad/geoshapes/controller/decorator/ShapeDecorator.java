package it.unisa.diem.sad.geoshapes.controller.decorator;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import java.util.List;

public interface ShapeDecorator {
    void applyDecoration();
    void removeDecoration();
    Shape getDecoratedShape();
    List<Circle> getResizeHandles();
}