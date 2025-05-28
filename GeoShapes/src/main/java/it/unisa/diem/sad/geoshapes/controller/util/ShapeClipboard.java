package it.unisa.diem.sad.geoshapes.controller.util;

import javafx.beans.property.BooleanProperty;
import javafx.scene.shape.Shape;
import java.util.List;

public interface ShapeClipboard {

    void copy(List<Shape> shapes);

    List<Shape> paste();

    boolean isEmpty();

    BooleanProperty emptyProperty();
}