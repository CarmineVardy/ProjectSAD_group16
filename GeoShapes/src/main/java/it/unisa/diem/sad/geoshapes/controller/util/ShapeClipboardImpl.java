package it.unisa.diem.sad.geoshapes.controller.util;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ShapeClipboardImpl implements ShapeClipboard {

    private final List<MyShape> clipboard = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void copy(List<MyShape> shapes) {
        Objects.requireNonNull(shapes, "La lista di forme non può essere null");

        synchronized (clipboard) {
            clipboard.clear();
            for (MyShape shape : shapes) {
                if (shape != null) {
                    try {
                        clipboard.add(shape.cloneShape());
                    } catch (Exception e) {
                        throw new IllegalStateException("Errore durante la clonazione della forma", e);
                    }
                }
            }
        }
    }

    @Override
    public List<MyShape> paste() {
        List<MyShape> clones = new ArrayList<>();

        synchronized (clipboard) {
            if (clipboard.isEmpty()) {
                return Collections.emptyList();
            }

            for (MyShape shape : clipboard) {
                try {
                    clones.add(shape.cloneShape());
                } catch (Exception e) {
                    throw new IllegalStateException("Errore durante la clonazione della forma", e);
                }
            }
        }

        return Collections.unmodifiableList(clones);
    }

    @Override
    public boolean isEmpty() {
        synchronized (clipboard) {
            return clipboard.isEmpty();
        }
    }
}