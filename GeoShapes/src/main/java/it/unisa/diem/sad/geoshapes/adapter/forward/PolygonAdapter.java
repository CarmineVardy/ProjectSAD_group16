package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyPolygon;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Polygon;
import java.util.List;

public class PolygonAdapter implements ShapeAdapter {
    private static final PolygonAdapter INSTANCE = new PolygonAdapter();

    private PolygonAdapter() {}

    public static PolygonAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public Shape getFxShape(MyShape modelShape, double width, double height) {
        if (!(modelShape instanceof MyPolygon)) {
            throw new IllegalArgumentException("Expected MyPolygon");
        }

        MyPolygon modelPolygon = (MyPolygon) modelShape;
        Polygon fxPolygon = new Polygon();

        List<Double> xPoints = modelPolygon.getXPoints();
        List<Double> yPoints = modelPolygon.getYPoints();

        for (int i = 0; i < xPoints.size(); i++) {
            fxPolygon.getPoints().add(xPoints.get(i) * width);
            fxPolygon.getPoints().add(yPoints.get(i) * height);
        }

        fxPolygon.setStroke(convertToJavaFxColor(modelPolygon.getBorderColor()));
        fxPolygon.setFill(convertToJavaFxColor(modelPolygon.getFillColor()));
        fxPolygon.setStrokeWidth(2.0);

        return fxPolygon;
    }
}