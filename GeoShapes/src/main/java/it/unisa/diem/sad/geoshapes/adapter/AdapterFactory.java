package it.unisa.diem.sad.geoshapes.adapter;

import it.unisa.diem.sad.geoshapes.adapter.forward.EllipseAdapter;
import it.unisa.diem.sad.geoshapes.adapter.forward.LineAdapter;
import it.unisa.diem.sad.geoshapes.adapter.forward.PolygonAdapter;
import it.unisa.diem.sad.geoshapes.adapter.forward.RectangleAdapter;
import it.unisa.diem.sad.geoshapes.adapter.forward.ShapeAdapter;
import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import it.unisa.diem.sad.geoshapes.model.shapes.MyPolygon;
import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.adapter.reverse.ReverseEllipseAdapter;
import it.unisa.diem.sad.geoshapes.adapter.reverse.ReverseLineAdapter;
import it.unisa.diem.sad.geoshapes.adapter.reverse.ReversePolygonAdapter;
import it.unisa.diem.sad.geoshapes.adapter.reverse.ReverseRectangleAdapter;
import it.unisa.diem.sad.geoshapes.adapter.reverse.ReverseShapeAdapter;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A factory class responsible for providing adapters to convert between
 * model-level {@link MyShape} objects and JavaFX {@link Shape} objects,
 * and vice-versa. It also handles cloning of shapes with an offset.
 * This centralizes the conversion logic for different shape types.
 */
public class AdapterFactory {

    // Private instance variables
    private final Map<Class<? extends MyShape>, ShapeAdapter> forwardAdapters;
    private final Map<Class<? extends Shape>, ReverseShapeAdapter> reverseAdapters;

    /**
     * Constructs a new {@code AdapterFactory} and initializes
     * its maps of forward and reverse adapters for supported shape types.
     */
    public AdapterFactory() {
        this.forwardAdapters = initializeForwardAdapters();
        this.reverseAdapters = initializeReverseAdapters();
    }

    /**
     * Converts a model-level {@link MyShape} into a JavaFX {@link Shape}.
     * It selects the appropriate forward adapter based on the model shape's type.
     *
     * @param modelShape The {@link MyShape} object to convert.
     * @param width The current width of the drawing area, used for scaling coordinates.
     * @param height The current height of the drawing area, used for scaling coordinates.
     * @return The corresponding JavaFX {@link Shape} object, or {@code null} if the input is {@code null}.
     * @throws IllegalArgumentException If no adapter is found for the given model shape type.
     */
    public Shape convertToJavaFx(MyShape modelShape, double width, double height) {
        if (modelShape == null) {
            return null;
        }

        ShapeAdapter adapter = forwardAdapters.get(modelShape.getClass());
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter found for shape type: " + modelShape.getClass().getSimpleName());
        }

        // Get the JavaFX shape and then apply its rotation
        Shape fxShape = adapter.getFxShape(modelShape, width, height);
        fxShape.setRotate(modelShape.getRotation()); // Ensure rotation from model is applied
        return fxShape;
    }

    /**
     * Converts a JavaFX {@link Shape} into a model-level {@link MyShape}.
     * It selects the appropriate reverse adapter based on the JavaFX shape's type.
     *
     * @param fxShape The JavaFX {@link Shape} object to convert.
     * @param width The current width of the drawing area, used for scaling coordinates.
     * @param height The current height of the drawing area, used for scaling coordinates.
     * @return The corresponding {@link MyShape} object, or {@code null} if the input is {@code null}.
     * @throws IllegalArgumentException If no reverse adapter is found for the given JavaFX shape type.
     */
    public MyShape convertToModel(Shape fxShape, double width, double height) {
        if (fxShape == null) {
            return null;
        }

        ReverseShapeAdapter adapter = reverseAdapters.get(fxShape.getClass());
        if (adapter == null) {
            throw new IllegalArgumentException("No reverse adapter found for JavaFX shape type: " + fxShape.getClass().getSimpleName());
        }

        // Get the model shape and then apply its rotation
        MyShape modelShape = adapter.getModelShape(fxShape, width, height);
        modelShape.setRotation(fxShape.getRotate()); // Ensure rotation from JavaFX shape is applied
        return modelShape;
    }

    /**
     * Creates a clone of an original {@link MyShape} and applies an offset to its coordinates.
     * This is useful for "paste" operations to slightly move the new shape from the original.
     *
     * @param original The original {@link MyShape} to clone.
     * @param offsetX The X-coordinate offset to apply to the cloned shape.
     * @param offsetY The Y-coordinate offset to apply to the cloned shape.
     * @return A cloned {@link MyShape} with adjusted coordinates, or {@code null} if the original is {@code null}.
     */
    public MyShape cloneWithOffset(MyShape original, double offsetX, double offsetY) {
        if (original == null) {
            return null;
        }

        MyShape cloned = original.clone(); // Perform a shallow clone first

        // Adjust start and end coordinates
        cloned.setStartX(cloned.getStartX() + offsetX);
        cloned.setStartY(cloned.getStartY() + offsetY);
        cloned.setEndX(cloned.getEndX() + offsetX);
        cloned.setEndY(cloned.getEndY() + offsetY);

        // Adjust points for polygon-like shapes (if applicable)
        List<Double> currentXPoints = cloned.getXPoints();
        List<Double> currentYPoints = cloned.getYPoints();

        // Check if the shape has points (e.g., MyPolygon) before iterating
        if (!currentXPoints.isEmpty() && !currentYPoints.isEmpty()) {
            List<Double> newXPoints = new ArrayList<>();
            List<Double> newYPoints = new ArrayList<>();

            for (int i = 0; i < currentXPoints.size(); i++) {
                newXPoints.add(currentXPoints.get(i) + offsetX);
                newYPoints.add(currentYPoints.get(i) + offsetY);
            }
            cloned.setPoints(newXPoints, newYPoints); // Update the points
        }

        // Note: Rotation is already handled by the clone() method if it performs a deep copy of primitives,
        // but explicitly setting it again for clarity and to ensure consistency in case of future changes to clone().
        cloned.setRotation(cloned.getRotation());

        return cloned;
    }

    // Private methods

    /**
     * Initializes the map of forward adapters, which convert model shapes to JavaFX shapes.
     *
     * @return A {@code Map} linking {@link MyShape} subclasses to their respective {@link ShapeAdapter} instances.
     */
    private Map<Class<? extends MyShape>, ShapeAdapter> initializeForwardAdapters() {
        Map<Class<? extends MyShape>, ShapeAdapter> adapters = new HashMap<>();
        adapters.put(MyLine.class, LineAdapter.getInstance());
        adapters.put(MyRectangle.class, RectangleAdapter.getInstance());
        adapters.put(MyEllipse.class, EllipseAdapter.getInstance());
        adapters.put(MyPolygon.class, PolygonAdapter.getInstance());
        // adapters.put(MyText.class, TextAdapter.getInstance()); // TextAdapter is commented out
        return adapters;
    }

    /**
     * Initializes the map of reverse adapters, which convert JavaFX shapes back to model shapes.
     *
     * @return A {@code Map} linking JavaFX {@link Shape} subclasses to their respective {@link ReverseShapeAdapter} instances.
     */
    private Map<Class<? extends Shape>, ReverseShapeAdapter> initializeReverseAdapters() {
        Map<Class<? extends Shape>, ReverseShapeAdapter> adapters = new HashMap<>();
        adapters.put(Line.class, ReverseLineAdapter.getInstance());
        adapters.put(Rectangle.class, ReverseRectangleAdapter.getInstance());
        adapters.put(Ellipse.class, ReverseEllipseAdapter.getInstance());
        adapters.put(Polygon.class, ReversePolygonAdapter.getInstance());
        // adapters.put(TextShapeWrapper.class, ReverseTextAdapter.getInstance()); // ReverseTextAdapter is commented out
        return adapters;
    }
}