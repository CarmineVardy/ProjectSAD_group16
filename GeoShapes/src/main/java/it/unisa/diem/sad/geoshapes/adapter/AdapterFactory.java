package it.unisa.diem.sad.geoshapes.adapter;

import it.unisa.diem.sad.geoshapes.adapter.forward.EllipseAdapter;
import it.unisa.diem.sad.geoshapes.adapter.forward.LineAdapter;
import it.unisa.diem.sad.geoshapes.adapter.forward.RectangleAdapter;
import it.unisa.diem.sad.geoshapes.adapter.forward.PolygonAdapter;
import it.unisa.diem.sad.geoshapes.adapter.forward.ShapeAdapter;
import it.unisa.diem.sad.geoshapes.model.shapes.*;
import it.unisa.diem.sad.geoshapes.adapter.reverse.*;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
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
        adapters.put(MyLine.class, LineAdapter.getInstance());
        adapters.put(MyRectangle.class, RectangleAdapter.getInstance());
        adapters.put(MyEllipse.class, EllipseAdapter.getInstance());
        adapters.put(MyPolygon.class, PolygonAdapter.getInstance());
        return adapters;
    }

    private Map<Class<? extends Shape>, ReverseShapeAdapter> initializeReverseAdapters() {
        Map<Class<? extends Shape>, ReverseShapeAdapter> adapters = new HashMap<>();
        adapters.put(Line.class, ReverseLineAdapter.getInstance());
        adapters.put(Rectangle.class, ReverseRectangleAdapter.getInstance());
        adapters.put(Ellipse.class, ReverseEllipseAdapter.getInstance());
        adapters.put(Polygon.class, ReversePolygonAdapter.getInstance());
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

        Shape fxShape = adapter.getFxShape(modelShape, width, height);
        fxShape.setRotate(modelShape.getRotation());
        return fxShape;
    }

    public MyShape convertToModel(Shape fxShape, double width, double height) {
        if (fxShape == null) {
            return null;
        }

        ReverseShapeAdapter adapter = reverseAdapters.get(fxShape.getClass());
        if (adapter == null) {
            throw new IllegalArgumentException("No reverse adapter found for JavaFX shape type: " + fxShape.getClass().getSimpleName());
        }

        MyShape modelShape = adapter.getModelShape(fxShape, width, height);
        modelShape.setRotation(fxShape.getRotate());
        return modelShape;
    }

    public MyShape cloneWithOffset(MyShape original, double offsetX, double offsetY) {
        if (original == null) {
            return null;
        }

        MyShape cloned = original.clone();

        cloned.setStartX(cloned.getStartX() + offsetX);
        cloned.setStartY(cloned.getStartY() + offsetY);
        cloned.setEndX(cloned.getEndX() + offsetX);
        cloned.setEndY(cloned.getEndY() + offsetY);
        cloned.setRotation(original.getRotation());

        return cloned;
    }
}