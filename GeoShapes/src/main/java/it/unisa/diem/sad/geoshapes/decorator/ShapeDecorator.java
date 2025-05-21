package it.unisa.diem.sad.geoshapes.decorator;

import javafx.scene.shape.Shape;


public interface ShapeDecorator {

    void applyDecoration();
    void removeDecoration();
    Shape getDecoratedShape();
}