package it.unisa.diem.sad.geoshapes.controller.util;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.List;

public interface ShapeClipboard {

    //Copia una lista di forme nella clipboard.
    void copy(List<MyShape> shapes);

    //Restituisce una nuova lista contenente copie delle forme nella clipboard.
    List<MyShape> paste();

    boolean isEmpty();
}