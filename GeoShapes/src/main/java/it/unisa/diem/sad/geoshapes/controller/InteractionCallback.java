package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public interface InteractionCallback {

    void onCreateShape(Shape shape);

    void onChangeBorderColor(MyShape shape, Color color);

    void onChangeFillColor(MyShape shape, Color color);

    void onSelectionMenuOpened(Shape viewShape, MyShape selectedModelShape, double x, double y);

    void onResizeShape(Shape fxShape, Bounds initialFxBounds, Bounds finalFxBounds);

}
