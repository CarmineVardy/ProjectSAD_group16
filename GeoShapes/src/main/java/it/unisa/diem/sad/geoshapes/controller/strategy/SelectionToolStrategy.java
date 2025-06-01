package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.controller.ShapeMapping;
import it.unisa.diem.sad.geoshapes.decorator.SelectionDecorator;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import javafx.event.ActionEvent;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.event.Event;
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
    private final ShapeMapping shapeMapping;
    private SelectionDecorator currentDecorator;
    private Shape selectedJavaFxShape;
    private InteractionCallback callback;
    private boolean isRotating;
    private double lastAngle;
    private Shape currentShape;

    private boolean isMoving = false;
    private Point2D initialMousePress;
    private double initialTranslateX;
    private double initialTranslateY;
    private double initialLineStartX, initialLineStartY, initialLineEndX, initialLineEndY;
    private double lastX;
    private double lastY;

    private final List<MyShape> selectedModelShapes = new ArrayList<>();
    private final List<Shape> selectedJavaFxShapes = new ArrayList<>();
    private final Group zoomGroup;
    private ResizeHandleType activeHandleType = ResizeHandleType.NONE;
    private DrawingModel model;
    private boolean isResizing;

    private enum ResizeHandleType {
        ROTATION,
        NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST,
        NORTH, SOUTH, EAST, WEST,
        NONE
    }

    public SelectionToolStrategy(Pane drawingPane, Group zoomGroup, ShapeMapping shapeMapping, InteractionCallback callback) {
        this.drawingPane = drawingPane;
        this.shapeMapping = shapeMapping;
        this.callback = callback;
        this.zoomGroup = zoomGroup;
    }

    @Override
    public void activate(Color borderColor, Color fillColor) {
        callback.onLineSelected(false);
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
    public void handleMousePressed(MouseEvent event) {
        Point2D localPoint = getTransformedCoordinates(event, drawingPane);
        double x = localPoint.getX();
        double y = localPoint.getY();

        if (event.getButton() == MouseButton.SECONDARY) {
            Shape shapeAtPosition = findShapeAt(x, y);
            if (shapeAtPosition != null && shapeAtPosition != selectedJavaFxShape) {
                selectShape(shapeAtPosition);
            }
            callback.onSelectionMenuOpened(event.getScreenX(), event.getScreenY());
            event.consume();
            return;
        }

        if (event.getButton() == MouseButton.PRIMARY) {
            Circle handleAtPosition = findHandleAt(x, y);
            if (handleAtPosition != null) {
                String handleTypeStr = (String) handleAtPosition.getUserData();
                try {
                    activeHandleType = ResizeHandleType.valueOf(handleTypeStr);
                } catch (IllegalArgumentException e) {
                    activeHandleType = ResizeHandleType.NONE;
                }

                if (activeHandleType == ResizeHandleType.ROTATION) {
                    isRotating = true;
                    this.currentShape = selectedJavaFxShape;
                    lastAngle = calculateAngle(x, y, selectedJavaFxShape);
                    isMoving = false;
                    drawingPane.setCursor(Cursor.CROSSHAIR);
                    event.consume();
                    return;
                } else if (activeHandleType != ResizeHandleType.NONE) {
                    isMoving = false;
                    isRotating = false;
                    isResizing = true;
                    lastX = x;
                    lastY = y;
                    event.consume();
                    return;
                }
            }

            Shape shapeAtPosition = findShapeAt(x, y);
            if (shapeAtPosition != null) {
                if (shapeAtPosition != selectedJavaFxShape) {
                    selectShape(shapeAtPosition);
                }
                isMoving = true;
                isRotating = false;
                activeHandleType = ResizeHandleType.NONE;
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
                return;
            }

            reset();
            event.consume();
        }
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        Point2D localPoint = getTransformedCoordinates(event, drawingPane);
        double x = localPoint.getX();
        double y = localPoint.getY();

        if (selectedJavaFxShape == null) return;

        if (isRotating && currentShape != null) {
            double currentAngle = calculateAngle(x, y, currentShape);
            double deltaAngle = currentAngle - lastAngle;
            if (currentDecorator != null) currentDecorator.removeDecoration();
            currentShape.setRotate(currentShape.getRotate() + deltaAngle);
            lastAngle = currentAngle;
            if (currentDecorator != null) currentDecorator.applyDecoration();
            event.consume();
            return;
        }

        if (isResizing && activeHandleType != ResizeHandleType.NONE && activeHandleType != ResizeHandleType.ROTATION) {
            double deltaX = x - lastX;
            double deltaY = y - lastY;

            if (currentDecorator != null) currentDecorator.deactivateDecoration();

            double minSize = 5;

            if (selectedJavaFxShape instanceof Rectangle rect) {
                double currentX = rect.getX();
                double currentY = rect.getY();
                double currentWidth = rect.getWidth();
                double currentHeight = rect.getHeight();
                double newX = currentX, newY = currentY, newWidth = currentWidth, newHeight = currentHeight;

                switch (activeHandleType) {
                    case NORTH_WEST: newX += deltaX; newY += deltaY; newWidth -= deltaX; newHeight -= deltaY; break;
                    case NORTH_EAST: newY += deltaY; newWidth += deltaX; newHeight -= deltaY; break;
                    case SOUTH_WEST: newX += deltaX; newWidth -= deltaX; newHeight += deltaY; break;
                    case SOUTH_EAST: newWidth += deltaX; newHeight += deltaY; break;
                    case NORTH: newY += deltaY; newHeight -= deltaY; break;
                    case SOUTH: newHeight += deltaY; break;
                    case EAST: newWidth += deltaX; break;
                    case WEST: newX += deltaX; newWidth -= deltaX; break;
                    default: break;
                }

                if (newWidth < minSize) {
                    if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.SOUTH_WEST || activeHandleType == ResizeHandleType.WEST) newX += (newWidth - minSize);
                    newWidth = minSize;
                }
                if (newHeight < minSize) {
                    if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.NORTH_EAST || activeHandleType == ResizeHandleType.NORTH) newY += (newHeight - minSize);
                    newHeight = minSize;
                }
                rect.setX(newX); rect.setY(newY); rect.setWidth(newWidth); rect.setHeight(newHeight);

            } else if (selectedJavaFxShape instanceof Ellipse ellipse) {
                double currentCenterX = ellipse.getCenterX();
                double currentCenterY = ellipse.getCenterY();
                double currentRadiusX = ellipse.getRadiusX();
                double currentRadiusY = ellipse.getRadiusY();
                double newX = currentCenterX - currentRadiusX;
                double newY = currentCenterY - currentRadiusY;
                double newWidth = currentRadiusX * 2;
                double newHeight = currentRadiusY * 2;

                switch (activeHandleType) {
                    case NORTH_WEST: newX += deltaX; newY += deltaY; newWidth -= deltaX; newHeight -= deltaY; break;
                    case NORTH_EAST: newY += deltaY; newWidth += deltaX; newHeight -= deltaY; break;
                    case SOUTH_WEST: newX += deltaX; newWidth -= deltaX; newHeight += deltaY; break;
                    case SOUTH_EAST: newWidth += deltaX; newHeight += deltaY; break;
                    case NORTH: newY += deltaY; newHeight -= deltaY; break;
                    case SOUTH: newHeight += deltaY; break;
                    case EAST: newWidth += deltaX; break;
                    case WEST: newX += deltaX; newWidth -= deltaX; break;
                    default: break;
                }

                if (newWidth < minSize) {
                    if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.SOUTH_WEST || activeHandleType == ResizeHandleType.WEST) newX += (newWidth - minSize);
                    newWidth = minSize;
                }
                if (newHeight < minSize) {
                    if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.NORTH_EAST || activeHandleType == ResizeHandleType.NORTH) newY += (newHeight - minSize);
                    newHeight = minSize;
                }
                ellipse.setCenterX(newX + newWidth / 2); ellipse.setCenterY(newY + newHeight / 2);
                ellipse.setRadiusX(newWidth / 2); ellipse.setRadiusY(newHeight / 2);

            } else if (selectedJavaFxShape instanceof Line line) {
                switch (activeHandleType) {
                    case NORTH_WEST: line.setStartX(line.getStartX() + deltaX); line.setStartY(line.getStartY() + deltaY); break;
                    case NORTH_EAST: line.setEndX(line.getEndX() + deltaX); line.setStartY(line.getStartY() + deltaY); break;
                    case SOUTH_WEST: line.setStartX(line.getStartX() + deltaX); line.setEndY(line.getEndY() + deltaY); break;
                    case SOUTH_EAST: line.setEndX(line.getEndX() + deltaX); line.setEndY(line.getEndY() + deltaY); break;
                    case NORTH: line.setStartY(line.getStartY() + deltaY); break;
                    case SOUTH: line.setEndY(line.getEndY() + deltaY); break;
                    case EAST: line.setEndX(line.getEndX() + deltaX); break;
                    case WEST: line.setStartX(line.getStartX() + deltaX); break;
                    default: break;
                }
            }

            lastX = x; lastY = y;
            if (currentDecorator != null) currentDecorator.applyDecoration();
            event.consume();
            return;
        }

        if (isMoving) {
            double deltaX = x - initialMousePress.getX();
            double deltaY = y - initialMousePress.getY();

            if (selectedJavaFxShape instanceof Line line) {
                line.setStartX(initialLineStartX + deltaX);
                line.setStartY(initialLineStartY + deltaY);
                line.setEndX(initialLineEndX + deltaX);
                line.setEndY(initialLineEndY + deltaY);
            } else {
                selectedJavaFxShape.setTranslateX(initialTranslateX + deltaX);
                selectedJavaFxShape.setTranslateY(initialTranslateY + deltaY);
            }

            if (currentDecorator != null) {
                currentDecorator.deactivateDecoration();
                currentDecorator.activateDecoration();
            }
            event.consume();
        }
    }

    @Override
    public void handleMouseReleased(MouseEvent event) {
        boolean wasMoving = isMoving;
        boolean wasRotating = isRotating;
        boolean wasResizing = isResizing;

        isMoving = false;
        isRotating = false;
        isResizing = false;
        ResizeHandleType previousActiveHandleType = activeHandleType; // Store before resetting
        activeHandleType = ResizeHandleType.NONE;


        if (selectedJavaFxShape == null) {
            handleMouseMoved(event);
            return;
        }

        if (wasRotating || (wasResizing && previousActiveHandleType != ResizeHandleType.NONE && previousActiveHandleType != ResizeHandleType.ROTATION)) {
            if (currentDecorator != null) currentDecorator.removeDecoration();
            callback.onModifyShape(selectedJavaFxShape);
            if (currentDecorator != null) currentDecorator.applyDecoration();
        } else if (wasMoving) {
            double dx = event.getX() - initialMousePress.getX(); // Use event.getX() relative to scene/source for consistency if localPoint transformation not needed here
            double dy = event.getY() - initialMousePress.getY(); // Same as above
            boolean significantChange = (dx * dx + dy * dy) > 4;

            if (significantChange) {
                bakeTranslation(selectedJavaFxShape);
                if (currentDecorator != null) currentDecorator.removeDecoration();
                callback.onModifyShape(selectedJavaFxShape);
                // Re-apply decoration after baking and model update, which might involve re-creating decorator
                if (this.selectedJavaFxShape != null) { // Ensure shape is still selected
                    selectShape(this.selectedJavaFxShape); // This will re-apply decorator correctly
                }
            } else {
                if (currentDecorator != null) {
                    currentDecorator.deactivateDecoration();
                    currentDecorator.activateDecoration();
                }
            }
        }
        handleMouseMoved(event);
        event.consume();
    }

    @Override
    public void handleMouseMoved(MouseEvent event) {
        if (isMoving) { drawingPane.setCursor(Cursor.MOVE); return; }
        if (isRotating) { drawingPane.setCursor(Cursor.CROSSHAIR); return; }
        if (activeHandleType != ResizeHandleType.NONE) return;

        Point2D localPoint = getTransformedCoordinates(event, drawingPane);
        double x = localPoint.getX();
        double y = localPoint.getY();

        Circle handleAtPosition = findHandleAt(x, y);
        if (handleAtPosition != null) {
            String handleType = (String) handleAtPosition.getUserData();
            switch (handleType) {
                case "ROTATION": drawingPane.setCursor(Cursor.CROSSHAIR); break;
                case "NORTH_WEST": case "SOUTH_EAST": drawingPane.setCursor(Cursor.NW_RESIZE); break;
                case "NORTH_EAST": case "SOUTH_WEST": drawingPane.setCursor(Cursor.NE_RESIZE); break;
                case "NORTH": case "SOUTH": drawingPane.setCursor(Cursor.V_RESIZE); break;
                case "EAST": case "WEST": drawingPane.setCursor(Cursor.H_RESIZE); break;
                default: drawingPane.setCursor(Cursor.HAND); break;
            }
        } else {
            Shape shapeAtPos = findShapeAt(x, y);
            drawingPane.setCursor(shapeAtPos != null ? Cursor.MOVE : Cursor.DEFAULT);
        }
    }

    @Override
    public void handleCopy(Event event) {
        if (selectedJavaFxShape != null) {
            if (currentDecorator != null) {
                currentDecorator.removeDecoration();
                currentDecorator = null;
            }
            callback.onCopyShape(selectedJavaFxShape);
        }
    }

    @Override
    public void handleCut(Event event) {
        if (selectedJavaFxShape != null) {
            if (currentDecorator != null) {
                currentDecorator.removeDecoration();
                currentDecorator = null;
            }
            callback.onCutShape(selectedJavaFxShape);
        }
    }

    @Override
    public void handleDelete(Event event) {
        if (selectedJavaFxShape != null) callback.onDeleteShape(selectedJavaFxShape);
    }

    @Override
    public void handleBringToFront(ActionEvent actionEvent) {
        if (selectedJavaFxShape != null) callback.onBringToFront(selectedJavaFxShape);
    }

    @Override
    public void handleBringToTop(ActionEvent actionEvent) {
        if (selectedJavaFxShape != null) callback.onBringToTop(selectedJavaFxShape);
    }

    @Override
    public void handleSendToBack(ActionEvent actionEvent) {
        if (selectedJavaFxShape != null) callback.onSendToBack(selectedJavaFxShape);
    }

    @Override
    public void handleSendToBottom(ActionEvent actionEvent) {
        if (selectedJavaFxShape != null) callback.onSendToBottom(selectedJavaFxShape);
    }

    @Override
    public void reset() {
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            currentDecorator = null;
        }
        selectedJavaFxShape = null;
        isMoving = false;
        isRotating = false;
        isResizing = false;
        activeHandleType = ResizeHandleType.NONE;
        drawingPane.setCursor(Cursor.DEFAULT);
        callback.onShapeDeselected();
    }

    private void bakeTranslation(Shape shape) {
        if (shape == null) return;
        double tx = shape.getTranslateX();
        double ty = shape.getTranslateY();
        if (tx == 0 && ty == 0) return;

        if (shape instanceof Rectangle r) {
            r.setX(r.getX() + tx); r.setY(r.getY() + ty);
        } else if (shape instanceof Ellipse e) {
            e.setCenterX(e.getCenterX() + tx); e.setCenterY(e.getCenterY() + ty);
        } else if (shape instanceof Line l) {
            l.setStartX(l.getStartX() + tx); l.setStartY(l.getStartY() + ty);
            l.setEndX(l.getEndX() + tx); l.setEndY(l.getEndY() + ty);
        }
        shape.setTranslateX(0); shape.setTranslateY(0);
    }

    private void selectShape(Shape shapeToSelect) {
        if (currentDecorator != null) currentDecorator.removeDecoration();

        selectedJavaFxShapes.clear();
        selectedModelShapes.clear();

        if (shapeToSelect != null) {
            bakeTranslation(shapeToSelect);
            this.selectedJavaFxShape = shapeToSelect;
            selectedJavaFxShapes.add(shapeToSelect);
            MyShape modelShape = shapeMapping.getModelShape(shapeToSelect);
            if (modelShape != null) selectedModelShapes.add(modelShape);

            currentDecorator = new SelectionDecorator(shapeToSelect);
            currentDecorator.applyDecoration();
            drawingPane.setCursor(Cursor.MOVE);
            callback.onShapeSelected(shapeToSelect);
        } else {
            reset();
        }
    }

    private Shape findShapeAt(double x, double y) {
        List<javafx.scene.Node> children = drawingPane.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            javafx.scene.Node node = children.get(i);
            if (currentDecorator != null && node instanceof Circle && currentDecorator.getResizeHandles().contains(node)) {
                continue;
            }
            if (node instanceof Shape shape) {
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
public void selectShapeByModel(MyShape shape) {
        Shape javafxShape = shapeMapping.getViewShape(shape);
        if (javafxShape != null) selectShape(javafxShape);
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

    @Override
    public List<MyShape> getSelectedShapes() {
        return this.selectedModelShapes;
    }

    public void onMousePressed(double x, double y) {
        lastX = x;
        lastY = y;
    }

    public void onMouseDragged(double x, double y) {
        double dx = x - lastX;
        double dy = y - lastY;
        if (model != null && model.getSelectedShapes() != null) {
            for (MyShape shape : model.getSelectedShapes()) {
                shape.moveBy(dx, dy);
            }
        }
        lastX = x;
        lastY = y;
    }

    public void setModel(DrawingModel model) {
        this.model = model;
    }

    private double calculateAngle(double x, double y, Shape shape) {
        Bounds bounds = shape.getBoundsInParent();
        double centerX = bounds.getMinX() + bounds.getWidth() / 2;
        double centerY = bounds.getMinY() + bounds.getHeight() / 2;
        return Math.toDegrees(Math.atan2(y - centerY, x - centerX));
    }
}