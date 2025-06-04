package it.unisa.diem.sad.geoshapes.adapter.forward;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Ellipse;

public class EllipseAdapter implements ShapeAdapter {

    private static final EllipseAdapter INSTANCE = new EllipseAdapter();

    private EllipseAdapter() {}

    public static EllipseAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public Shape getFxShape(MyShape modelShape, double width, double height) {
        if (!(modelShape instanceof MyEllipse)) {
            throw new IllegalArgumentException("Expected MyEllipse");
        }
        MyEllipse modelEllipse = (MyEllipse) modelShape;

        double centerX = (modelEllipse.getStartX() + modelEllipse.getEndX()) / 2.0 * width;
        double centerY = (modelEllipse.getStartY() + modelEllipse.getEndY()) / 2.0 * height;
        double radiusX = Math.abs(modelEllipse.getEndX() - modelEllipse.getStartX()) / 2.0 * width;
        double radiusY = Math.abs(modelEllipse.getEndY() - modelEllipse.getStartY()) / 2.0 * height;

        Ellipse fxEllipse = new Ellipse(centerX, centerY, radiusX, radiusY);
        fxEllipse.setStroke(convertToJavaFxColor(modelEllipse.getBorderColor()));
        fxEllipse.setFill(convertToJavaFxColor(modelEllipse.getFillColor()));
        fxEllipse.setStrokeWidth(2.0);
        fxEllipse.setRotate(modelEllipse.getRotation());
        return fxEllipse;
    }


}