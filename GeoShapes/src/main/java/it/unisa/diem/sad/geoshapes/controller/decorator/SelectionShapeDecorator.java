package it.unisa.diem.sad.geoshapes.controller.decorator;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap; // Explicitly import HashMap

/**
 * A concrete decorator that applies a "selection" style to a JavaFX {@link Shape}.
 * This decorator visually highlights a selected shape with a colored border and adds
 * interactive resize handles and a rotation handle, allowing for manipulation.
 */
public class SelectionShapeDecorator implements ShapeDecorator {

    // Public static final constants
    private static final double ROTATION_HANDLE_OFFSET = 15;
    private static final double HANDLE_SIZE = 8;

    // Private instance variables
    private final Shape decoratedShape;
    private Color originalStrokeColor;
    private double originalStrokeWidth;
    private StrokeType originalStrokeType;
    private double originalOpacity;
    private Paint originalFill;
    private Circle rotationHandle;
    private Pane drawingArea;

    private final List<Circle> resizeHandles; // Renamed from allHandles
    private final List<Shape> selectionBoundingBoxes; // Renamed from selectionBorders

    private boolean isDecorationActive; // Renamed from isActive

    /**
     * Constructs a new {@code SelectionShapeDecorator} for the given shape.
     *
     * @param shape The JavaFX {@link Shape} to be decorated.
     */
    public SelectionShapeDecorator(Shape shape) {
        this.decoratedShape = shape;
        this.resizeHandles = new ArrayList<>();
        this.selectionBoundingBoxes = new ArrayList<>();
    }

    /**
     * Applies the selection decoration to the shape.
     * This involves changing the shape's stroke and fill, and adding
     * resize and rotation handles to the drawing area.
     */
    @Override
    public void applyDecoration() {
        if (isDecorationActive) {
            return;
        }

        if (decoratedShape.getParent() instanceof Pane) {
            this.drawingArea = (Pane) decoratedShape.getParent();
        } else {
            // If the shape is not attached to a Pane, it cannot be decorated.
            System.err.println("Decorated shape is not attached to a Pane. Cannot apply decoration.");
            return;
        }

        // Store original properties only the first time decoration is applied for this instance.
        if (originalStrokeColor == null) {
            storeOriginalProperties();
        }

        decoratedShape.setStroke(Color.GREEN);
        decoratedShape.setStrokeWidth(originalStrokeWidth + 0.5);
        decoratedShape.setStrokeType(StrokeType.OUTSIDE);

        // Make the fill slightly transparent if it wasn't already transparent
        if (originalFill instanceof Color originalColor) {
            if (originalColor.getOpacity() > 0.0) {
                Color newColor = new Color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), 0.7);
                decoratedShape.setFill(newColor);
            }
        }

        createAndAddDecorations();
        isDecorationActive = true;
        System.out.println("DEBUG: Applied decoration for " + decoratedShape.hashCode() + ". Stroke set to: " + decoratedShape.getStroke());
    }

    /**
     * Removes the selection decoration, restoring the shape to its original appearance.
     * All added handles and bounding boxes are removed from the drawing area.
     */
    @Override
    public void removeDecoration() {
        if (!isDecorationActive) {
            return;
        }

        // Restore properties from the stored original values
        decoratedShape.setStroke(originalStrokeColor);
        decoratedShape.setStrokeWidth(originalStrokeWidth);
        decoratedShape.setStrokeType(originalStrokeType);
        decoratedShape.setFill(originalFill);
        decoratedShape.setOpacity(originalOpacity);

        removeDecorationsFromPane();
        isDecorationActive = false;
        System.out.println("DEBUG: Removed decoration for " + decoratedShape.hashCode() + ". Restored stroke to: " + originalStrokeColor);
    }

    /**
     * Returns the underlying JavaFX {@link Shape} object that is being decorated.
     *
     * @return The decorated {@link Shape} instance.
     */
    @Override
    public Shape getDecoratedShape() {
        return decoratedShape;
    }

    /**
     * Returns a list of {@link Circle} objects that serve as resize handles for the decorated shape.
     *
     * @return A {@code List} of {@link Circle} objects representing the resize handles.
     */
    @Override
    public List<Circle> getResizeHandles() {
        return new ArrayList<>(resizeHandles);
    }

    /**
     * Returns a list of {@link Shape} objects that form the selection bounding boxes around the decorated shape.
     *
     * @return A {@code List} of {@link Shape} objects representing the selection borders.
     */
    public List<Shape> getSelectionBoundingBoxes() { // Renamed from getSelectionBorders
        return new ArrayList<>(selectionBoundingBoxes);
    }

    /**
     * Deactivates the decoration, similar to {@link #removeDecoration()},
     * restoring the shape to its original appearance and removing all added visual elements.
     */
    public void deactivateDecoration() {
        if (!isDecorationActive) {
            return;
        }
        decoratedShape.setStroke(originalStrokeColor);
        decoratedShape.setStrokeWidth(originalStrokeWidth);
        decoratedShape.setStrokeType(originalStrokeType);
        decoratedShape.setFill(originalFill);
        decoratedShape.setOpacity(originalOpacity);

        removeDecorationsFromPane();
        isDecorationActive = false;
    }

    /**
     * Updates the stored original stroke color for the decorated shape.
     * This is useful when the stroke color of the shape is changed while it is selected,
     * ensuring that the correct color is restored upon de-selection.
     *
     * @param newColor The new color to be stored as the original stroke color.
     */
    public void updateOriginalStrokeColor(Color newColor) {
        this.originalStrokeColor = newColor;
    }

    // Private methods

    /**
     * Stores the current properties of the decorated shape (stroke color, width, type, opacity, and fill).
     * This method is called only once when {@link #applyDecoration()} is first invoked for an instance.
     */
    private void storeOriginalProperties() {
        // Ensure the stroke is a Color before casting, otherwise use a default.
        Paint currentStrokePaint = decoratedShape.getStroke();
        if (currentStrokePaint instanceof Color) {
            this.originalStrokeColor = (Color) currentStrokePaint;
        } else {
            // If not a Color (e.g., null or LinearGradient), use a default color.
            this.originalStrokeColor = Color.BLACK; // Or another appropriate default for the application
        }

        this.originalStrokeWidth = decoratedShape.getStrokeWidth();
        this.originalStrokeType = decoratedShape.getStrokeType();
        this.originalOpacity = decoratedShape.getOpacity();
        this.originalFill = decoratedShape.getFill();

        System.out.println("DEBUG: Stored original properties for " + decoratedShape.hashCode() + ": Stroke=" + this.originalStrokeColor + ", Fill=" + this.originalFill);
    }

    /**
     * Removes all decoration elements (handles and bounding boxes) from the drawing area.
     * Also clears the internal lists of handles and bounding boxes.
     */
    private void removeDecorationsFromPane() {
        if (drawingArea != null) {
            drawingArea.getChildren().removeAll(resizeHandles);
            drawingArea.getChildren().removeAll(selectionBoundingBoxes);
        }
        // This call might be redundant if the original properties are restored right after.
        // Keeping it for now as per "Do not change any logic or functionality".
        restoreOriginalProperties();
        resizeHandles.clear();
        selectionBoundingBoxes.clear();
        rotationHandle = null;
    }

    /**
     * Creates the visual decoration elements (selection rectangle, resize handles, rotation handle)
     * and adds them to the drawing area.
     */
    private void createAndAddDecorations() {
        if (drawingArea == null) {
            System.err.println("Drawing area is null. Cannot add decorations.");
            return;
        }

        Bounds localBounds = decoratedShape.getLayoutBounds();
        double shapeRotateAngle = decoratedShape.getRotate();
        double translateX = decoratedShape.getTranslateX();
        double translateY = decoratedShape.getTranslateY();

        double shapeLocalX = localBounds.getMinX();
        double shapeLocalY = localBounds.getMinY();
        double shapeLocalWidth = localBounds.getWidth();
        double shapeLocalHeight = localBounds.getHeight();

        double shapeLocalCenterX = shapeLocalX + shapeLocalWidth / 2;
        double shapeLocalCenterY = shapeLocalY + shapeLocalHeight / 2;

        Rotate rotateTransform = new Rotate(shapeRotateAngle, shapeLocalCenterX, shapeLocalCenterY);

        decoratedShape.toFront(); // Bring the decorated shape to front

        double circleRadius = HANDLE_SIZE / 2;

        if (decoratedShape instanceof Line fxLine) {
            // Resize handles for lines: at start and end points
            Point2D startPointLocal = new Point2D(fxLine.getStartX(), fxLine.getStartY());
            Point2D endPointLocal = new Point2D(fxLine.getEndX(), fxLine.getEndY());

            double lineCenterX = (fxLine.getStartX() + fxLine.getEndX()) / 2;
            double lineCenterY = (fxLine.getStartY() + fxLine.getEndY()) / 2;
            Rotate lineRotateTransform = new Rotate(shapeRotateAngle, lineCenterX, lineCenterY);

            Point2D transformedStart = lineRotateTransform.transform(startPointLocal);
            Point2D transformedEnd = lineRotateTransform.transform(endPointLocal);

            Circle handleStart = new Circle(transformedStart.getX() + translateX, transformedStart.getY() + translateY, circleRadius, Color.BLUE);
            handleStart.setStroke(Color.WHITE);
            handleStart.setStrokeWidth(1);
            handleStart.setUserData("NORTH_WEST"); // Using string literal as a handle identifier
            resizeHandles.add(handleStart);
            drawingArea.getChildren().add(handleStart);

            Circle handleEnd = new Circle(transformedEnd.getX() + translateX, transformedEnd.getY() + translateY, circleRadius, Color.BLUE);
            handleEnd.setStroke(Color.WHITE);
            handleEnd.setStrokeWidth(1);
            handleEnd.setUserData("SOUTH_EAST"); // Using string literal as a handle identifier
            resizeHandles.add(handleEnd);
            drawingArea.getChildren().add(handleEnd);

            // For Line, no selection rectangle or rotation handle is created.
        } else if (decoratedShape instanceof Rectangle || decoratedShape instanceof Ellipse || decoratedShape instanceof Polygon) {
            // Selection rectangle for Rectangle, Ellipse, and Polygon
            Rectangle selectionRect = new Rectangle(shapeLocalX, shapeLocalY, shapeLocalWidth, shapeLocalHeight);
            selectionRect.setStroke(Color.DODGERBLUE);
            selectionRect.setStrokeWidth(2);
            selectionRect.getStrokeDashArray().addAll(5.0, 5.0);
            selectionRect.setFill(Color.TRANSPARENT);

            selectionRect.setRotate(shapeRotateAngle);
            selectionRect.setTranslateX(translateX);
            selectionRect.setTranslateY(translateY);

            selectionBoundingBoxes.add(selectionRect);
            drawingArea.getChildren().add(selectionRect);
            selectionRect.toFront(); // Bring the selection rectangle to front

            // Rotation handle (based on bounding box)
            Point2D rotationHandleLocalPos = new Point2D(shapeLocalCenterX, shapeLocalY - ROTATION_HANDLE_OFFSET);
            Point2D finalRotationHandlePos = rotateTransform.transform(rotationHandleLocalPos);

            rotationHandle = new Circle(finalRotationHandlePos.getX() + translateX, finalRotationHandlePos.getY() + translateY, circleRadius, Color.DARKORANGE);
            rotationHandle.setStroke(Color.WHITE);
            rotationHandle.setStrokeWidth(1);
            rotationHandle.setUserData("ROTATION"); // Using string literal as a handle identifier
            resizeHandles.add(rotationHandle);
            drawingArea.getChildren().add(rotationHandle);


            // Resize handles (based on bounding box)
            String[] handleTypes = {
                    "NORTH_WEST", "NORTH", "NORTH_EAST",
                    "WEST", "EAST",
                    "SOUTH_WEST", "SOUTH", "SOUTH_EAST"
            };

            Point2D[] localHandlePositions = {
                    new Point2D(shapeLocalX, shapeLocalY),
                    new Point2D(shapeLocalX + shapeLocalWidth / 2, shapeLocalY),
                    new Point2D(shapeLocalX + shapeLocalWidth, shapeLocalY),

                    new Point2D(shapeLocalX, shapeLocalY + shapeLocalHeight / 2),
                    new Point2D(shapeLocalX + shapeLocalWidth, shapeLocalY + shapeLocalHeight / 2),

                    new Point2D(shapeLocalX, shapeLocalY + shapeLocalHeight),
                    new Point2D(shapeLocalX + shapeLocalWidth / 2, shapeLocalY + shapeLocalHeight),
                    new Point2D(shapeLocalX + shapeLocalWidth, shapeLocalY + shapeLocalHeight)
            };

            for (int i = 0; i < handleTypes.length; i++) {
                Point2D localPos = localHandlePositions[i];
                Point2D finalPos = rotateTransform.transform(localPos);

                Circle handle = new Circle(finalPos.getX() + translateX, finalPos.getY() + translateY, circleRadius, Color.BLUE);
                handle.setStroke(Color.WHITE);
                handle.setStrokeWidth(1);
                handle.setUserData(handleTypes[i]); // Using string literal as a handle identifier
                resizeHandles.add(handle);
                drawingArea.getChildren().add(handle);
            }
        } else {
            System.err.println("Unsupported shape type for resize handles: " + decoratedShape.getClass().getSimpleName());
        }

        // Bring all handles (those that have been added) to the front
        for (Circle handle : resizeHandles) {
            handle.toFront();
        }
        // If the rotation handle was created (for Rect/Ellipse), bring it to the front
        if (rotationHandle != null) {
            rotationHandle.toFront();
        }
    }

    /**
     * Restores the decorated shape's properties to their original values.
     * This method is typically called internally after decoration is removed.
     */
    private void restoreOriginalProperties() {
        if (originalStrokeColor == null) {
            originalStrokeColor = Color.BLACK; // Default if no original was captured
        }
        decoratedShape.setStroke(originalStrokeColor);
        decoratedShape.setStrokeWidth(originalStrokeWidth);
        decoratedShape.setStrokeType(originalStrokeType);
        decoratedShape.setFill(originalFill);
        decoratedShape.setOpacity(originalOpacity);
    }
}