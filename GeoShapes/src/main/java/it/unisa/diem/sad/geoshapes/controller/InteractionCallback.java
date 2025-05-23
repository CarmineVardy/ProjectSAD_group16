package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public interface InteractionCallback {

    void onCreateShape(Shape shape);

    void onDeleteShape(Shape shape);

    void onModifyShape(Shape shape);

    void onSelectionMenuOpened(Shape viewShape, double x, double y);

}
