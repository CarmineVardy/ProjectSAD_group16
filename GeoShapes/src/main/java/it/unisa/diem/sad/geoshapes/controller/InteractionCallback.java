package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public interface InteractionCallback {

    void onDeleteShape(MyShape shape);

    void onCreateShape(MyShape shape);

    void onSelectionMenuOpened(Shape viewShape, MyShape modelShape, double x, double y);

    void onSelectionMenuClosed();

    void onChangeBorderColor(MyShape modelShape, Color color);

    void onChangeFillColor(MyShape modelShape, Color color);

}
