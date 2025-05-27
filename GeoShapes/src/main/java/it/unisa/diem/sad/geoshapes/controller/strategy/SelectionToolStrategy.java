package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.controller.ShapeMapping;
import it.unisa.diem.sad.geoshapes.decorator.SelectionDecorator;
import javafx.event.ActionEvent;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class SelectionToolStrategy implements ToolStrategy {

    private final Pane drawingPane;
    private final Group zoomGroup;
    private final ShapeMapping shapeMapping;
    private SelectionDecorator currentDecorator;
    private Shape selectedJavaFxShape;
    private InteractionCallback callback;

    private boolean isResizing = false;
    private boolean isMoving = false;
    private Point2D initialMousePress;
    private Bounds initialShapeBounds;
    private double initialTranslateX;
    private double initialTranslateY;
    private double initialLineStartX, initialLineStartY, initialLineEndX, initialLineEndY;

    private final List<MyShape> selectedModelShapes = new ArrayList<>();
    private final List<Shape> selectedJavaFxShapes = new ArrayList<>();

    private ResizeHandleType activeHandleType = ResizeHandleType.NONE;

    private enum ResizeHandleType {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        MIDDLE_LEFT, MIDDLE_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT, LINE_START, LINE_END,
        NONE
    }

    public SelectionToolStrategy(Pane drawingPane, Group zoomGroup, ShapeMapping shapeMapping, InteractionCallback callback) {
        this.drawingPane = drawingPane;
        this.zoomGroup = zoomGroup;
        this.shapeMapping = shapeMapping;
        this.callback = callback;
    }

    private void selectShape(Shape shapeToSelect) {
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
        }

        selectedJavaFxShapes.clear();
        selectedModelShapes.clear();

        if (shapeToSelect != null) {
            this.selectedJavaFxShape = shapeToSelect;
            selectedJavaFxShapes.add(shapeToSelect);
            MyShape model = shapeMapping.getModelShape(shapeToSelect);
            if (model != null) {
                selectedModelShapes.add(model);
            }

            currentDecorator = new SelectionDecorator(shapeToSelect);
            currentDecorator.applyDecoration();
            zoomGroup.setCursor(Cursor.MOVE);
            callback.onShapeSelected(shapeToSelect);
        } else {
            this.selectedJavaFxShape = null;
            currentDecorator = null;
            drawingPane.setCursor(Cursor.DEFAULT);
            callback.onShapeDeselected();
        }
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        Point2D localPoint = getTransformedCoordinates(event, zoomGroup);
        double x = localPoint.getX();
        double y = localPoint.getY();

        Circle handleAtPosition = findHandleAt(x, y);

        if (handleAtPosition != null && selectedJavaFxShape != null && event.getButton() == MouseButton.PRIMARY) {
            bakeTranslation(selectedJavaFxShape);
            isResizing = true;
            isMoving = false;
            initialMousePress = new Point2D(x, y);
            initialShapeBounds = selectedJavaFxShape.getBoundsInParent();
            activeHandleType = ResizeHandleType.valueOf((String) handleAtPosition.getUserData());
            event.consume();
            return;
        }

        Shape shapeAtPosition = findShapeAt(x, y);

        if (shapeAtPosition == null) {
            if (currentDecorator != null) currentDecorator.removeDecoration();
            selectedJavaFxShape = null;
            currentDecorator = null;
            isMoving = false;
            isResizing = false;
            drawingPane.setCursor(Cursor.DEFAULT);
            callback.onShapeDeselected();
            event.consume();
            return;
        }

        if (event.getButton() == MouseButton.SECONDARY) {
            if (!selectedJavaFxShapes.contains(shapeAtPosition)) {
                selectShape(shapeAtPosition);
            }
            callback.onSelectionMenuOpened(selectedJavaFxShape, event.getX(), event.getY());
            event.consume();
            return;
        }

        if (event.getButton() == MouseButton.PRIMARY) {
            if (shapeAtPosition != selectedJavaFxShape) {
                selectShape(shapeAtPosition);  // aggiorna selectedJavaFxShape
            }

            if (selectedJavaFxShape != null) {
                isMoving = true;
                isResizing = false;
                initialMousePress = new Point2D(x, y);
                initialTranslateX = selectedJavaFxShape.getTranslateX();
                initialTranslateY = selectedJavaFxShape.getTranslateY();

                if (selectedJavaFxShape instanceof Line line) {
                    initialLineStartX = line.getStartX();
                    initialLineStartY = line.getStartY();
                    initialLineEndX = line.getEndX();
                    initialLineEndY = line.getEndY();
                }

                drawingPane.setCursor(Cursor.MOVE);
                event.consume();
            } else {
                System.err.println("No shape selected on primary click.");
            }
        }
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        if (!isMoving && !isResizing || selectedJavaFxShape == null || initialMousePress == null) return;

        Point2D localPoint = getTransformedCoordinates(event, zoomGroup);
        double x = localPoint.getX();
        double y = localPoint.getY();

        double deltaX = x - initialMousePress.getX();
        double deltaY = y - initialMousePress.getY();

        if (isResizing) {
            updateJavaFxShapeDimensions(deltaX, deltaY, activeHandleType);
            event.consume();
        } else if (isMoving) {
            if (selectedJavaFxShape instanceof Line) {
                Line line = (Line) selectedJavaFxShape;
                line.setStartX(initialLineStartX + deltaX);
                line.setStartY(initialLineStartY + deltaY);
                line.setEndX(initialLineEndX + deltaX);
                line.setEndY(initialLineEndY + deltaY);
            } else {
                selectedJavaFxShape.setTranslateX(initialTranslateX + deltaX);
                selectedJavaFxShape.setTranslateY(initialTranslateY + deltaY);
            }

            if (currentDecorator != null) {
                currentDecorator.removeDecoration();
                currentDecorator.applyDecoration();
            }
            event.consume();
        }
    }

    private void bakeTranslation(Shape shape) {
        if (shape == null) return;
        double tx = shape.getTranslateX();
        double ty = shape.getTranslateY();
        if (tx == 0 && ty == 0) return;

        if (shape instanceof Rectangle r) {
            r.setX(r.getX() + tx);
            r.setY(r.getY() + ty);
        } else if (shape instanceof Ellipse e) {
            e.setCenterX(e.getCenterX() + tx);
            e.setCenterY(e.getCenterY() + ty);
        } else if (shape instanceof Line l) {
            l.setStartX(l.getStartX() + tx);
            l.setStartY(l.getStartY() + ty);
            l.setEndX(l.getEndX() + tx);
            l.setEndY(l.getEndY() + ty);
        }

        shape.setTranslateX(0);
        shape.setTranslateY(0);
    }

    @Override
    public void handleMouseReleased(MouseEvent event) {
        Point2D localPoint = getTransformedCoordinates(event, zoomGroup);
        double x = localPoint.getX();
        double y = localPoint.getY();

        boolean wasResizing = isResizing;
        boolean wasMoving = isMoving;

        isResizing = false;
        isMoving = false;
        activeHandleType = ResizeHandleType.NONE;

        boolean significantChange = false;
        if (initialMousePress != null && (wasResizing || wasMoving) && selectedJavaFxShape != null) {
            double dx = x - initialMousePress.getX();
            double dy = y - initialMousePress.getY();
            significantChange = (dx * dx + dy * dy) > 4;
        }

        if (significantChange) {
            if (selectedJavaFxShape != null) {
                bakeTranslation(selectedJavaFxShape);
                if (currentDecorator != null) {
                    currentDecorator.removeDecoration();
                }
                callback.onModifyShape(selectedJavaFxShape);
                event.consume();
            }
        } else if (wasMoving || wasResizing) {
            if (selectedJavaFxShape != null) {
                bakeTranslation(selectedJavaFxShape);
                if (currentDecorator != null) {
                    currentDecorator.removeDecoration();
                    currentDecorator.applyDecoration();
                }
            }
        }
        handleMouseMoved(event);
    }

    @Override
    public void handleMouseMoved(MouseEvent event) {
        if (isResizing || isMoving) {
            if (isMoving) zoomGroup.setCursor(Cursor.MOVE);
            return;
        }

        // Chiama il metodo default dell'interfaccia ToolStrategy
        Point2D localPoint = getTransformedCoordinates(event, zoomGroup);
        double x = localPoint.getX();
        double y = localPoint.getY();

        Circle handleAtPosition = findHandleAt(x, y);
        if (handleAtPosition != null) {
            zoomGroup.setCursor(Cursor.HAND);
        } else {
            Shape shapeAtPos = findShapeAt(x, y);
            if (shapeAtPos != null) {
                zoomGroup.setCursor(Cursor.MOVE);
            } else {
                zoomGroup.setCursor(Cursor.DEFAULT);
            }
        }
    }

    @Override
    public void handleBorderColorChange(Color color) {
        if (selectedJavaFxShape != null) {
            if (currentDecorator != null) currentDecorator.removeDecoration();
            this.selectedJavaFxShape.setStroke(color);
            this.callback.onModifyShape(selectedJavaFxShape);
            if (currentDecorator != null) currentDecorator.applyDecoration();
        }
    }

    @Override
    public void handleFillColorChange(Color color) {
        if (selectedJavaFxShape != null) {
            if (currentDecorator != null) currentDecorator.removeDecoration();
            this.selectedJavaFxShape.setFill(color);
            this.callback.onModifyShape(selectedJavaFxShape);
            if (currentDecorator != null) currentDecorator.applyDecoration();
        }
    }

    @Override
    public void handleBringToFront(ActionEvent actionEvent) {
        if (selectedJavaFxShape != null) {
            callback.onBringToFront(selectedJavaFxShape);
        }
    }

    @Override
    public void handleSendToBack(ActionEvent actionEvent) {
        if (selectedJavaFxShape != null) {
            callback.onSendToBack(selectedJavaFxShape);
        }
    }

    @Override
    public void reset() {
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            currentDecorator = null;
        }
        selectedJavaFxShape = null;
        isResizing = false;
        isMoving = false;
        activeHandleType = ResizeHandleType.NONE;
        drawingPane.setCursor(Cursor.DEFAULT);

        // Callback per deselezionare quando si resetta
        callback.onShapeDeselected();
    }

    @Override
    public void activate(Color borderColor, Color fillColor) {
        callback.onLineSelected(false);
    }

    private Shape findShapeAt(double x, double y) {
        List<javafx.scene.Node> children = drawingPane.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            javafx.scene.Node node = children.get(i);
            if (currentDecorator != null && node instanceof Circle && currentDecorator.getResizeHandles().contains(node)) {
                continue;
            }
            if (node instanceof Shape) {
                Shape shape = (Shape) node;
                if (shape.isVisible() && shape.contains(x, y)) {
                    return shape;
                }
            }
        }
        return null;
    }

    private Circle findHandleAt(double x, double y) {
        if (currentDecorator == null) return null;
        for (Circle handle : currentDecorator.getResizeHandles()) {
            if (handle.isVisible() && handle.getBoundsInParent().contains(x, y)) {
                return handle;
            }
        }
        return null;
    }

    private void updateJavaFxShapeDimensions(double deltaX, double deltaY, ResizeHandleType handleType) {
        if (selectedJavaFxShape == null || initialShapeBounds == null) return;

        Point2D pivot = calculatePivotPoint(handleType, initialShapeBounds);
        if (pivot == null && !(selectedJavaFxShape instanceof Line)) return;

        ResizeCalculations calculations = calculateResize(deltaX, deltaY, handleType, initialShapeBounds, initialMousePress, pivot);
        applyDimensionsToFxShape(selectedJavaFxShape, calculations);

        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            currentDecorator.applyDecoration();
        }
    }

    private record ResizeCalculations(double newX, double newY, double newWidth, double newHeight) { }

    private Point2D calculatePivotPoint(ResizeHandleType handleType, Bounds initialBounds) {
        double pivotX, pivotY;

        if (selectedJavaFxShape instanceof Line fxLine) {
            switch (handleType) {
                case LINE_START: return new Point2D(fxLine.getEndX(), fxLine.getEndY());
                case LINE_END: return new Point2D(fxLine.getStartX(), fxLine.getStartY());
                default: return null;
            }
        } else {
            switch (handleType) {
                case TOP_LEFT:      pivotX = initialBounds.getMaxX(); pivotY = initialBounds.getMaxY(); break;
                case TOP_RIGHT:     pivotX = initialBounds.getMinX(); pivotY = initialBounds.getMaxY(); break;
                case BOTTOM_LEFT:   pivotX = initialBounds.getMaxX(); pivotY = initialBounds.getMinY(); break;
                case BOTTOM_RIGHT:  pivotX = initialBounds.getMinX(); pivotY = initialBounds.getMinY(); break;
                case TOP_CENTER:    pivotX = initialBounds.getMinX() + initialBounds.getWidth() / 2; pivotY = initialBounds.getMaxY(); break;
                case BOTTOM_CENTER: pivotX = initialBounds.getMinX() + initialBounds.getWidth() / 2; pivotY = initialBounds.getMinY(); break;
                case MIDDLE_LEFT:   pivotX = initialBounds.getMaxX(); pivotY = initialBounds.getMinY() + initialBounds.getHeight() / 2; break;
                case MIDDLE_RIGHT:  pivotX = initialBounds.getMinX(); pivotY = initialBounds.getMinY() + initialBounds.getHeight() / 2; break;
                default:            return null;
            }
            return new Point2D(pivotX, pivotY);
        }
    }

    private ResizeCalculations calculateResize(double deltaX, double deltaY, ResizeHandleType handleType, Bounds initialBounds, Point2D initialMouse, Point2D pivot) {
        double currentMouseX = initialMouse.getX() + deltaX;
        double currentMouseY = initialMouse.getY() + deltaY;

        if (selectedJavaFxShape instanceof Line fxLine) {
            double newStartX, newStartY, newEndX, newEndY;
            if (handleType == ResizeHandleType.LINE_START) {
                newStartX = currentMouseX;
                newStartY = currentMouseY;
                newEndX = fxLine.getEndX();
                newEndY = fxLine.getEndY();
            } else if (handleType == ResizeHandleType.LINE_END) {
                newStartX = fxLine.getStartX();
                newStartY = fxLine.getStartY();
                newEndX = currentMouseX;
                newEndY = currentMouseY;
            } else {
                return new ResizeCalculations(fxLine.getStartX(), fxLine.getStartY(), fxLine.getEndX(), fxLine.getEndY());
            }
            return new ResizeCalculations(newStartX, newStartY, newEndX, newEndY);
        } else {
            double newX = initialBounds.getMinX();
            double newY = initialBounds.getMinY();
            double newWidth = initialBounds.getWidth();
            double newHeight = initialBounds.getHeight();
            final double MIN_SIZE = 5.0;

            switch (handleType) {
                case TOP_LEFT:
                case BOTTOM_RIGHT:
                case TOP_RIGHT:
                case BOTTOM_LEFT:
                    double tempWidth = Math.abs(currentMouseX - pivot.getX());
                    double tempHeight = Math.abs(currentMouseY - pivot.getY());

                    double originalRatio = (initialBounds.getHeight() == 0) ? 1 : initialBounds.getWidth() / initialBounds.getHeight();
                    if (initialBounds.getWidth() != 0 && initialBounds.getHeight() != 0) {
                        double ratioX = tempWidth / initialBounds.getWidth();
                        double ratioY = tempHeight / initialBounds.getHeight();
                        double scale = Math.max(ratioX, ratioY);
                        newWidth = initialBounds.getWidth() * scale;
                        newHeight = initialBounds.getHeight() * scale;
                    } else {
                        newWidth = tempWidth;
                        newHeight = tempHeight;
                    }

                    if (newWidth < MIN_SIZE) newWidth = MIN_SIZE;
                    if (newHeight < MIN_SIZE) newHeight = MIN_SIZE;

                    switch (handleType) {
                        case TOP_LEFT:      newX = pivot.getX() - newWidth; newY = pivot.getY() - newHeight; break;
                        case TOP_RIGHT:     newX = pivot.getX();            newY = pivot.getY() - newHeight; break;
                        case BOTTOM_LEFT:   newX = pivot.getX() - newWidth; newY = pivot.getY();            break;
                        case BOTTOM_RIGHT:  newX = pivot.getX();            newY = pivot.getY();            break;
                    }
                    break;

                case TOP_CENTER:
                    newY = currentMouseY;
                    newHeight = pivot.getY() - currentMouseY;
                    if (newHeight < MIN_SIZE) {
                        newHeight = MIN_SIZE;
                        newY = pivot.getY() - MIN_SIZE;
                    }
                    break;
                case BOTTOM_CENTER:
                    newHeight = currentMouseY - pivot.getY();
                    if (newHeight < MIN_SIZE) newHeight = MIN_SIZE;
                    break;
                case MIDDLE_LEFT:
                    newX = currentMouseX;
                    newWidth = pivot.getX() - currentMouseX;
                    if (newWidth < MIN_SIZE) {
                        newWidth = MIN_SIZE;
                        newX = pivot.getX() - MIN_SIZE;
                    }
                    break;
                case MIDDLE_RIGHT:
                    newWidth = currentMouseX - pivot.getX();
                    if (newWidth < MIN_SIZE) newWidth = MIN_SIZE;
                    break;

                default:
                    return new ResizeCalculations(newX, newY, newWidth, newHeight);
            }
            return new ResizeCalculations(newX, newY, newWidth, newHeight);
        }
    }


    private void applyDimensionsToFxShape(Shape fxShape, ResizeCalculations calc) {
        if (fxShape instanceof Rectangle fxRect) {
            applyDimensionsToRectangle(fxRect, calc);
        } else if (fxShape instanceof Ellipse fxEllipse) {
            applyDimensionsToEllipse(fxEllipse, calc);
        } else if (fxShape instanceof Line fxLine) {
            applyDimensionsToLine(fxLine, calc);
        }
    }

    private void applyDimensionsToRectangle(Rectangle fxRect, ResizeCalculations calc) {
        fxRect.setX(calc.newX);
        fxRect.setY(calc.newY);
        fxRect.setWidth(calc.newWidth);
        fxRect.setHeight(calc.newHeight);
    }

    private void applyDimensionsToEllipse(Ellipse fxEllipse, ResizeCalculations calc) {
        fxEllipse.setCenterX(calc.newX + calc.newWidth / 2);
        fxEllipse.setCenterY(calc.newY + calc.newHeight / 2);
        fxEllipse.setRadiusX(calc.newWidth / 2);
        fxEllipse.setRadiusY(calc.newHeight / 2);
    }

    private void applyDimensionsToLine(Line fxLine, ResizeCalculations calc) {
        fxLine.setStartX(calc.newX);
        fxLine.setStartY(calc.newY);
        fxLine.setEndX(calc.newWidth);
        fxLine.setEndY(calc.newHeight);
    }

    public List<MyShape> getSelectedShapes() {
        return new ArrayList<>(selectedModelShapes);
    }

    public void selectShapeByModel(MyShape shape) {
        Shape javafxShape = shapeMapping.getViewShape(shape);
        if (javafxShape != null) {
            selectShape(javafxShape);
        }
    }

    public void clearSelection() {
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            currentDecorator = null;
        }
        selectedJavaFxShapes.clear();
        selectedModelShapes.clear();
        drawingPane.setCursor(Cursor.DEFAULT);
    }
}