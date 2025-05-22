package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public interface InteractionCallback {

    void onCreateShape(Shape shape);

    void onChangeBorderColor(Shape shape, Color color);

    void onChangeFillColor(Shape shape, Color color);

    void onSelectionMenuOpened(Shape viewShape, double x, double y);

    void onResizeShape(Shape shape);

}
