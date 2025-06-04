package it.unisa.diem.sad.geoshapes.adapter.reverse;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyPolygon;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class ReversePolygonAdapter implements ReverseShapeAdapter {
    private static final ReversePolygonAdapter INSTANCE = new ReversePolygonAdapter();

    private ReversePolygonAdapter() {}

    public static ReversePolygonAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public MyShape getModelShape(Shape fxShape, double width, double height) {
        if (!(fxShape instanceof Polygon)) {
            throw new IllegalArgumentException("Expected Polygon");
        }

        Polygon fxPolygon = (Polygon) fxShape;
        List<Double> points = fxPolygon.getPoints();

        if (points.size() % 2 != 0) {
            throw new IllegalArgumentException("Invalid polygon points");
        }

        List<Double> xPoints = new ArrayList<>();
        List<Double> yPoints = new ArrayList<>();

        for (int i = 0; i < points.size(); i += 2) {
            xPoints.add(points.get(i) / width);
            yPoints.add(points.get(i + 1) / height);
        }

        MyPolygon modelPolygon = new MyPolygon(
                xPoints,
                yPoints,
                fxPolygon.getRotate(),
                convertToModelColor((Color) fxPolygon.getStroke()),
                convertToModelColor((Color) fxPolygon.getFill())
        );

        return modelPolygon;
    }
}