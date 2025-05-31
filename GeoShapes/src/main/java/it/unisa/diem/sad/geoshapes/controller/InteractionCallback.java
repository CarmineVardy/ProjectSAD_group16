package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public interface InteractionCallback {

    void onCreateShape(Shape shape);

    void onModifyShape(Shape shape);

    void onDeleteShape(Shape shape);

    void onCopyShape(Shape shape);

    void onCutShape(Shape shape);

    void onBringToFront(Shape shape);

    void onBringToTop(Shape shape);

    void onSendToBack(Shape shape);

    void onSendToBottom(Shape shape);

    void onShapeSelected(Shape shape);

    void onShapeDeselected();

    void onLineSelected(boolean selected);

    void onSelectionMenuOpened(double x, double y);


    void onRotateShape(Shape selectedJavaFxShape, double oldAngle, double newAngle);
}
