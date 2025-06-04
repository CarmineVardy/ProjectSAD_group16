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

public class SelectionShapeDecorator implements ShapeDecorator {

    private final Shape decoratedShape;
    private Color originalStrokeColor;
    private double originalStrokeWidth;
    private StrokeType originalStrokeType;
    private double originalOpacity;
    private Paint originalFill;
    private Circle rotationHandle;
    private static final double ROTATION_HANDLE_OFFSET = 15;

    private Pane drawingArea;

    private final List<Circle> allHandles;
    private final List<Shape> selectionBorders;
    private static final double HANDLE_SIZE = 8;

    private boolean isActive = false;

    public SelectionShapeDecorator(Shape shape) {
        this.decoratedShape = shape;
        this.allHandles = new ArrayList<>();
        this.selectionBorders = new ArrayList<>();
    }

    @Override
    public void applyDecoration() {
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
        isActive = true;
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
        if (!isActive) {
            return;
        }

        decoratedShape.setStroke(originalStrokeColor);
        decoratedShape.setStrokeWidth(originalStrokeWidth);
        decoratedShape.setStrokeType(originalStrokeType);
        decoratedShape.setFill(originalFill);
        decoratedShape.setOpacity(originalOpacity);

        removeDecorationsFromPane();
        isActive = false;
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
        restoreOriginalProperties();
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

        double circleRadius = HANDLE_SIZE / 2;

        if (decoratedShape instanceof Line fxLine) {
            // Handle di ridimensionamento per la linea: alle estremità (start e end point)
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

            // Per la Linea, NON si crea né il rettangolo di selezione né l'handle di rotazione.

        } else if (decoratedShape instanceof Rectangle || decoratedShape instanceof Ellipse || decoratedShape instanceof Polygon) {
            // Rettangolo di selezione per Rectangle e Ellipse
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

            // Handle di rotazione per Rectangle e Ellipse (basato sulla bounding box)
            Point2D rotationHandleLocalPos = new Point2D(shapeLocalCenterX, shapeLocalY - ROTATION_HANDLE_OFFSET);
            Point2D finalRotationHandlePos = rotateTransform.transform(rotationHandleLocalPos);

            rotationHandle = new Circle(finalRotationHandlePos.getX() + translateX, finalRotationHandlePos.getY() + translateY, circleRadius, Color.DARKORANGE);
            rotationHandle.setStroke(Color.WHITE);
            rotationHandle.setStrokeWidth(1);
            rotationHandle.setUserData("ROTATION");
            allHandles.add(rotationHandle);
            drawingArea.getChildren().add(rotationHandle);


            // Handle di ridimensionamento per Rectangle e Ellipse (basato sulla bounding box)
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

        // Porta tutti gli handle (quelli che sono stati aggiunti) in primo piano
        for (Circle handle : allHandles) {
            handle.toFront();
        }
        // Se l'handle di rotazione è stato creato (per Rect/Ellipse), portalo in primo piano
        if (rotationHandle != null) {
            rotationHandle.toFront();
        }
    }


    public void deactivateDecoration() {
        if (!isActive) {
            return;
        }
        decoratedShape.setStroke(originalStrokeColor);
        decoratedShape.setStrokeWidth(originalStrokeWidth);
        decoratedShape.setStrokeType(originalStrokeType);
        decoratedShape.setFill(originalFill);
        decoratedShape.setOpacity(originalOpacity);

        removeDecorationsFromPane();
        isActive = false;
    }


    public void activateDecoration() {
        if (isActive) {
            return;
        }

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

        createAndAddDecorations();
        isActive = true;
    }


    private void restoreOriginalProperties() {
        decoratedShape.setStroke(originalStrokeColor);
        decoratedShape.setStrokeWidth(originalStrokeWidth);
        decoratedShape.setStrokeType(originalStrokeType);
        decoratedShape.setFill(originalFill);
        decoratedShape.setOpacity(originalOpacity);
    }


    public void updateOriginalStrokeColor(Color newColor) {
        this.originalStrokeColor = newColor;
    }


}