package it.unisa.diem.sad.geoshapes.adapter;

import it.unisa.diem.sad.geoshapes.adapter.forward.*;
import it.unisa.diem.sad.geoshapes.model.shapes.*;
import it.unisa.diem.sad.geoshapes.adapter.reverse.*;
import javafx.scene.text.Text;
import javafx.scene.shape.*;
import java.util.HashMap;
import java.util.Map;

public class AdapterFactory {

    private final Map<Class<? extends MyShape>, ShapeAdapter> forwardAdapters;

    private final Map<Class<? extends Shape>, ReverseShapeAdapter> reverseAdapters;

    public AdapterFactory() {
        this.forwardAdapters = initializeForwardAdapters();
        this.reverseAdapters = initializeReverseAdapters();
    }

    private Map<Class<? extends MyShape>, ShapeAdapter> initializeForwardAdapters() {
        Map<Class<? extends MyShape>, ShapeAdapter> adapters = new HashMap<>();
        // Usa i singleton invece di creare nuove istanze
        adapters.put(MyLine.class, LineAdapter.getInstance());
        adapters.put(MyRectangle.class, RectangleAdapter.getInstance());
        adapters.put(MyEllipse.class, EllipseAdapter.getInstance());
        adapters.put(MyText.class, TextAdapter.getInstance());
        return adapters;
    }

    private Map<Class<? extends Shape>, ReverseShapeAdapter> initializeReverseAdapters() {
        Map<Class<? extends Shape>, ReverseShapeAdapter> adapters = new HashMap<>();
        // Usa i singleton invece di creare nuove istanze
        adapters.put(Line.class, ReverseLineAdapter.getInstance());
        adapters.put(Rectangle.class, ReverseRectangleAdapter.getInstance());
        adapters.put(Ellipse.class, ReverseEllipseAdapter.getInstance());
        adapters.put(Text.class, ReverseTextAdapter.getInstance());
        return adapters;
    }

    public Shape convertToJavaFx(MyShape modelShape, double width, double height) {
        if (modelShape == null) {
            return null;
        }
        ShapeAdapter adapter = forwardAdapters.get(modelShape.getClass());
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter found for shape type: " + modelShape.getClass().getSimpleName());
        }
        return adapter.getFxShape(modelShape, width, height);
    }

    public MyShape convertToModel(Shape fxShape, double width, double height) {
        if (fxShape == null) {
            return null;
        }
        ReverseShapeAdapter adapter = reverseAdapters.get(fxShape.getClass());
        if (adapter == null) {
            throw new IllegalArgumentException("No reverse adapter found for JavaFX shape type: " + fxShape.getClass().getSimpleName());
        }
        return adapter.getModelShape(fxShape, width, height);
    }

    public MyShape cloneWithOffset(MyShape original, double offsetX, double offsetY) {
        if (original == null) {
            return null;
        }

        // Clona la forma originale
        MyShape cloned = original.clone();

        // Applica l'offset alle coordinate normalizzate
        cloned.setStartX(cloned.getStartX() + offsetX);
        cloned.setStartY(cloned.getStartY() + offsetY);
        cloned.setEndX(cloned.getEndX() + offsetX);
        cloned.setEndY(cloned.getEndY() + offsetY);

        return cloned;
    }


}