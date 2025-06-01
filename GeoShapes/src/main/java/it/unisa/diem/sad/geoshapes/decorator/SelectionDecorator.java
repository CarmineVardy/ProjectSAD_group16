package it.unisa.diem.sad.geoshapes.decorator;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;

public class SelectionDecorator implements ShapeDecorator {

    private final Shape decoratedShape;
    private Color originalStrokeColor;
    private double originalStrokeWidth;
    private StrokeType originalStrokeType;
    private double originalOpacity;
    private Paint originalFill;
    private Circle rotationHandle;
    private static final double ROTATION_HANDLE_OFFSET = 30;

    private Pane drawingArea;

    private final List<Circle> allHandles;
    private final List<Shape> selectionBorders;
    private static final double HANDLE_SIZE = 8;

    // Flag per tenere traccia dello stato della decorazione (attiva/disattiva)
    private boolean isActive = false;

    public SelectionDecorator(Shape shape) {
        this.decoratedShape = shape;
        this.allHandles = new ArrayList<>();
        this.selectionBorders = new ArrayList<>();
    }

    @Override
    public void applyDecoration() {
        // Se la decorazione è già attiva, non fare nulla
        if (isActive) {
            return;
        }

        if (decoratedShape.getParent() instanceof Pane) {
            this.drawingArea = (Pane) decoratedShape.getParent();
        } else {
            return;
        }

        storeOriginalProperties();

        decoratedShape.setStroke(Color.GREEN);
        decoratedShape.setStrokeWidth(originalStrokeWidth + 0.5);
        decoratedShape.setStrokeType(StrokeType.OUTSIDE);

        if (originalFill instanceof Color originalColor) {
            if (originalColor.getOpacity() > 0.0) {
                Color newColor = new Color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), 0.7);
                decoratedShape.setFill(newColor);
            }
        }

        createAndAddDecorations();
        isActive = true; // Imposta lo stato su attivo
    }

    private void storeOriginalProperties() {
        originalStrokeColor = (Color) decoratedShape.getStroke();
        System.out.println("\nCOLOR ORIGINAL" + originalStrokeColor.toString());
        originalStrokeWidth = decoratedShape.getStrokeWidth();
        originalStrokeType = decoratedShape.getStrokeType();
        originalOpacity = decoratedShape.getOpacity();
        originalFill = decoratedShape.getFill();
    }

    @Override
    public void removeDecoration() {
        // Se la decorazione non è attiva, non fare nulla
        if (!isActive) {
            return;
        }

        // Ripristina le proprietà originali solo se erano state modificate
        decoratedShape.setStroke(originalStrokeColor);
        decoratedShape.setStrokeWidth(originalStrokeWidth);
        decoratedShape.setStrokeType(originalStrokeType);
        decoratedShape.setFill(originalFill);
        decoratedShape.setOpacity(originalOpacity);

        removeDecorationsFromPane();
        isActive = false; // Imposta lo stato su disattivo
    }

    @Override
    public Shape getDecoratedShape() {
        return decoratedShape;
    }

    @Override
    public List<Circle> getResizeHandles() {
        return new ArrayList<>(allHandles);
    }

    public List<Shape> getSelectionBorders() {
        return new ArrayList<>(selectionBorders);
    }

    private void removeDecorationsFromPane() {
        if (drawingArea != null) {
            drawingArea.getChildren().removeAll(allHandles);
            drawingArea.getChildren().removeAll(selectionBorders);
        }
        allHandles.clear();
        selectionBorders.clear();
        rotationHandle = null;
    }

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

        decoratedShape.toFront();

        if (!(decoratedShape instanceof Line)) {
            Rectangle selectionRect = new Rectangle(shapeLocalX, shapeLocalY, shapeLocalWidth, shapeLocalHeight);
            selectionRect.setStroke(Color.DODGERBLUE);
            selectionRect.setStrokeWidth(2);
            selectionRect.getStrokeDashArray().addAll(5.0, 5.0);
            selectionRect.setFill(Color.TRANSPARENT);

            selectionRect.setRotate(shapeRotateAngle);
            selectionRect.setTranslateX(translateX);
            selectionRect.setTranslateY(translateY);

            selectionBorders.add(selectionRect);
            drawingArea.getChildren().add(selectionRect);
            selectionRect.toFront();
        }

        double circleRadius = HANDLE_SIZE / 2;

        Point2D rotationHandleLocalPos = new Point2D(shapeLocalCenterX, shapeLocalY - ROTATION_HANDLE_OFFSET);
        Point2D finalRotationHandlePos = rotateTransform.transform(rotationHandleLocalPos);

        rotationHandle = new Circle(finalRotationHandlePos.getX() + translateX, finalRotationHandlePos.getY() + translateY, circleRadius, Color.DARKORANGE);
        rotationHandle.setStroke(Color.WHITE);
        rotationHandle.setStrokeWidth(1);
        rotationHandle.setUserData("ROTATION");
        allHandles.add(rotationHandle);
        drawingArea.getChildren().add(rotationHandle);

        if (decoratedShape instanceof Line fxLine) {
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
            handleStart.setUserData("NORTH_WEST");
            allHandles.add(handleStart);
            drawingArea.getChildren().add(handleStart);

            Circle handleEnd = new Circle(transformedEnd.getX() + translateX, transformedEnd.getY() + translateY, circleRadius, Color.BLUE);
            handleEnd.setStroke(Color.WHITE);
            handleEnd.setStrokeWidth(1);
            handleEnd.setUserData("SOUTH_EAST");
            allHandles.add(handleEnd);
            drawingArea.getChildren().add(handleEnd);

        } else if (decoratedShape instanceof Rectangle || decoratedShape instanceof Ellipse) {
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
                handle.setUserData(handleTypes[i]);
                allHandles.add(handle);
                drawingArea.getChildren().add(handle);
            }
        } else {
            System.err.println("Unsupported shape type for resize handles: " + decoratedShape.getClass().getSimpleName());
        }

        for (Circle handle : allHandles) {
            handle.toFront();
        }
    }



    public void deactivateDecoration() {
        if (!isActive) {
            return; // Already deactivated
        }
        // Restore original properties (already done in removeDecoration, but good to be explicit if this were a standalone method)
        decoratedShape.setStroke(originalStrokeColor);
        decoratedShape.setStrokeWidth(originalStrokeWidth);
        decoratedShape.setStrokeType(originalStrokeType);
        decoratedShape.setFill(originalFill);
        decoratedShape.setOpacity(originalOpacity);

        // Remove the visual elements
        removeDecorationsFromPane();
        isActive = false; // Mark as deactivated
    }


    public void activateDecoration() {
        if (isActive) {
            return; // Already active
        }

        // Re-check parent in case it changed since last deactivation
        if (decoratedShape.getParent() instanceof Pane) {
            this.drawingArea = (Pane) decoratedShape.getParent();
        } else {
            System.err.println("Cannot activate decoration: The decorated shape is not currently part of a Pane.");
            return;
        }


        decoratedShape.setStroke(Color.GREEN);
        decoratedShape.setStrokeWidth(originalStrokeWidth + 0.5);
        decoratedShape.setStrokeType(StrokeType.OUTSIDE);

        if (originalFill instanceof Color originalColor) {
            if (originalColor.getOpacity() > 0.0) {
                Color newColor = new Color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), 0.7);
                decoratedShape.setFill(newColor);
            }
        }

        // Recreate and add the visual elements
        createAndAddDecorations();
        isActive = true; // Mark as active
    }
}
