package it.unisa.diem.sad.geoshapes.controller.util;

import it.unisa.diem.sad.geoshapes.adapter.AdapterFactory;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Clipboard{

    private final List<MyShape> clipboard;
    private AdapterFactory adapterFactory;
    private final BooleanProperty emptyProperty;
    private static final double OFFSETX = 0.025;
    private static final double OFFSETY = 0.025;

    public Clipboard(AdapterFactory adapterFactory ) {
        this.clipboard = new ArrayList<>();
        this.adapterFactory = adapterFactory;
        this.emptyProperty = new SimpleBooleanProperty(true);
    }

    public void copy(List<MyShape> shapes) {
        if (shapes == null) {
            throw new IllegalArgumentException("Shape list cannot be null");
        }
        clipboard.clear();
        for (MyShape shape : shapes) {
            if (shape != null) {
                clipboard.add(shape);
            }
        }
        updateEmptyProperty();
    }

    public List<MyShape> paste() {
        if (clipboard.isEmpty()) {
            return Collections.emptyList();
        }

        List<MyShape> pastedShapes = new ArrayList<>();

        for (MyShape shape : clipboard) {
            pastedShapes.add(adapterFactory.cloneWithOffset(shape, OFFSETX, OFFSETY));
        }
        clipboard.clear();
        clipboard.addAll(pastedShapes);
        return Collections.unmodifiableList(pastedShapes);
    }

    public boolean isEmpty() {
        return clipboard.isEmpty();
    }

    public BooleanProperty emptyProperty() {
        return emptyProperty;
    }

    private void updateEmptyProperty() {
        emptyProperty.set(clipboard.isEmpty());
    }
}