package it.unisa.diem.sad.geoshapes.controller;

import javafx.scene.shape.Shape;

import java.util.List;


public interface InteractionCallback {

    void onCreateShape(Shape shape);

    void onModifyShapes(List<Shape> shape);

    void onSelectionMenuOpened(double x, double y);

    void onChangeShapeSelected();

}
