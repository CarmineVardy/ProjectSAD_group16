package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.List;

public interface InteractionCallback {

    void onCreateShape(Shape shape);

    void onModifyShape(Shape shape);

    void onDeleteShape(Shape shape);

    void onCopyShape(List<Shape> shapes);

    void onCutShape(Shape shape);

    void onBringToFront(Shape shape);

    void onBringToTop(Shape shape);

    void onSendToBack(Shape shape);

    void onSendToBottom(Shape shape);

    void onShapeSelected(Shape shape);

    void onShapeDeselected();

    void onLineSelected(boolean selected);

    void onSelectionMenuOpened(double x, double y);

    void onShapesSelected(List<Shape> selectedJavaShapes);

    void onRotateShape(Shape selectedJavaFxShape, double oldAngle, double newAngle);


    void onCopyShapes(List<Shape> shapes);
    void onCutShapes(List<Shape> shapes);
    void onDeleteShapes(List<Shape> shapes);


    void onModifyGroup(List<Shape> oldStates, List<Shape> newStates);
}

