package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.controller.ShapeMapping;
import it.unisa.diem.sad.geoshapes.controller.decorator.SelectionShapeDecorator;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectionToolStrategy implements ToolStrategy {

    private final Pane drawingArea;
    private final ShapeMapping shapeMapping;
    private final InteractionCallback callback;
    private final Map<Shape, SelectionShapeDecorator> decorators = new HashMap<>();
    private final List<Shape> selectedJavaFxShapes = new ArrayList<>();
    private final List<MyShape> selectedModelShapes = new ArrayList<>();

    private boolean isRotating = false;
    private boolean isMoving = false;
    private boolean isResizing = false;
    private double lastAngle;
    private Shape currentShapeBeingRotated;
    private Shape primarySelectedShape;
    private Point2D initialMousePress;
    private final Map<Shape, Point2D> initialTranslations = new HashMap<>();
    private final Map<Line, Point2D[]> initialLinePositions = new HashMap<>();
    private double lastX, lastY;
    private ResizeHandleType activeHandleType = ResizeHandleType.NONE;

    private enum ResizeHandleType {
        ROTATION, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST,
        NORTH, SOUTH, EAST, WEST, NONE
    }

    public SelectionToolStrategy(Pane drawingArea, ShapeMapping shapeMapping, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.shapeMapping = shapeMapping;
        this.callback = callback;
    }

    @Override
    public void activate(Color lineBorderColor, Color rectangleBorderColor, Color rectangleFillColor,
                         Color ellipseBorderColor, Color ellipseFillColor, Color polygonBorderColor,
                         Color polygonFillColor, Color textBorderColor, Color textFillColor,
                         Color textColor, int polygonVertices, boolean regularPolygon, int fontSize) {
    }

    @Override
    public void handleBorderColorChange(Color color) {
        if (selectedJavaFxShapes != null && !selectedJavaFxShapes.isEmpty()) {
            // Cambia direttamente il colore senza rimuovere le decorazioni
            for (Shape shape : selectedJavaFxShapes) {
                shape.setStroke(color);
            }
            this.callback.onModifyShapes(selectedJavaFxShapes);

            // Aggiorna il colore originale memorizzato nei decoratori
            for (Shape shape : selectedJavaFxShapes) {
                SelectionShapeDecorator decorator = decorators.get(shape);
                if (decorator != null) {
                    decorator.updateOriginalStrokeColor(color);
                }
            }
        }
    }


    @Override
    public void handleFillColorChange(Color color) {
        if (selectedJavaFxShapes != null && !selectedJavaFxShapes.isEmpty()) {
            for (Shape shape : selectedJavaFxShapes) {
                SelectionShapeDecorator decorator = decorators.get(shape);
                if (decorator != null) {
                    decorator.removeDecoration();
                }
            }

            for (Shape shape : selectedJavaFxShapes) {
                shape.setFill(color);
            }
            if (selectedJavaFxShapes != null && !selectedJavaFxShapes.isEmpty()) {
                for (Shape shape : selectedJavaFxShapes) {
                    SelectionShapeDecorator decorator = decorators.get(shape);
                    if (decorator != null) {
                        decorator.removeDecoration();
                    }
                }

                this.callback.onModifyShapes(selectedJavaFxShapes);

                for (Shape shape : selectedJavaFxShapes) {
                    SelectionShapeDecorator decorator = decorators.get(shape);
                    if (decorator != null) {
                        decorator.applyDecoration();
                    }
                }
            }

        }
    }
    
    @Override
    public void handleTextColorMenuChange(Color color) {}

    @Override
    public void handleFontSizeMenuChange(int fontSize) {}

    @Override
    public List<Shape> getSelectedShapes() {
        return new ArrayList<>(selectedJavaFxShapes);
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        Point2D localPoint = drawingArea.sceneToLocal(event.getSceneX(), event.getSceneY());
        double x = localPoint.getX();
        double y = localPoint.getY();

        if (event.getButton() == MouseButton.SECONDARY) {
            Shape shapeAtPosition = findShapeAt(x, y);
            if (shapeAtPosition != null && !selectedJavaFxShapes.contains(shapeAtPosition)) {
                if (!event.isControlDown()) clearSelection();
                addShapeToSelection(shapeAtPosition);
            }
            callback.onSelectionMenuOpened(event.getScreenX(), event.getScreenY());
            event.consume();
            return;
        }

        if (event.getButton() == MouseButton.PRIMARY) {
            Circle handleAtPosition = findHandleAt(x, y);
            if (handleAtPosition != null && !selectedJavaFxShapes.isEmpty()) {
                String handleTypeStr = (String) handleAtPosition.getUserData();
                try {
                    activeHandleType = ResizeHandleType.valueOf(handleTypeStr);
                } catch (IllegalArgumentException e) {
                    activeHandleType = ResizeHandleType.NONE;
                }

                if (activeHandleType == ResizeHandleType.ROTATION) {
                    isRotating = true;
                    currentShapeBeingRotated = primarySelectedShape != null ? primarySelectedShape : selectedJavaFxShapes.get(0);
                    lastAngle = calculateAngle(x, y, currentShapeBeingRotated);
                    drawingArea.setCursor(Cursor.CROSSHAIR);
                } else if (activeHandleType != ResizeHandleType.NONE) {
                    isResizing = true;
                    lastX = x;
                    lastY = y;
                }
                event.consume();
                return;
            }

            Shape shapeAtPosition = findShapeAt(x, y);
            if (shapeAtPosition != null) {
                if (event.isControlDown()) {
                    if (selectedJavaFxShapes.contains(shapeAtPosition)) {
                        removeShapeFromSelection(shapeAtPosition);
                    } else {
                        addShapeToSelection(shapeAtPosition);
                    }
                } else {
                    if (!selectedJavaFxShapes.contains(shapeAtPosition)) {
                        clearSelection();
                        decorators.remove(shapeAtPosition);
                        addShapeToSelection(shapeAtPosition);
                    }
                    setPrimarySelectedShape(shapeAtPosition);
                }

                if (!selectedJavaFxShapes.isEmpty()) {
                    isMoving = true;
                    initialMousePress = new Point2D(x, y);
                    initialTranslations.clear();
                    initialLinePositions.clear();

                    for (Shape shape : selectedJavaFxShapes) {
                        initialTranslations.put(shape, new Point2D(shape.getTranslateX(), shape.getTranslateY()));
                        if (shape instanceof Line line) {
                            Point2D[] linePos = {
                                    new Point2D(line.getStartX(), line.getStartY()),
                                    new Point2D(line.getEndX(), line.getEndY())
                            };
                            initialLinePositions.put(line, linePos);
                        }
                    }
                    drawingArea.setCursor(Cursor.MOVE);
                }
                event.consume();
                return;
            }

            if (!event.isControlDown()) clearSelection();
            event.consume();
        }
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        Point2D localPoint = drawingArea.sceneToLocal(event.getSceneX(), event.getSceneY());
        double x = localPoint.getX();
        double y = localPoint.getY();

        if (selectedJavaFxShapes.isEmpty()) return;

        if (isRotating && currentShapeBeingRotated != null) {
            double currentAngle = calculateAngle(x, y, currentShapeBeingRotated);
            double deltaAngle = currentAngle - lastAngle;

            for (Shape shape : selectedJavaFxShapes) {
                SelectionShapeDecorator decorator = decorators.get(shape);
                if (decorator != null) decorator.removeDecoration();
                shape.setRotate(shape.getRotate() + deltaAngle);
                if (decorator != null) decorator.applyDecoration();
            }
            lastAngle = currentAngle;
            event.consume();
            return;
        }

        if (isResizing && activeHandleType != ResizeHandleType.NONE && activeHandleType != ResizeHandleType.ROTATION) {
            double deltaX = x - lastX;
            double deltaY = y - lastY;

            for (Shape shape : selectedJavaFxShapes) {
                SelectionShapeDecorator decorator = decorators.get(shape);
                if (decorator != null) decorator.removeDecoration();
                performResize(shape, deltaX, deltaY);
                if (decorator != null) decorator.applyDecoration();
            }
            lastX = x;
            lastY = y;
            event.consume();
            return;
        }

        if (isMoving) {
            double deltaX = x - initialMousePress.getX();
            double deltaY = y - initialMousePress.getY();

            for (Shape shape : selectedJavaFxShapes) {
                Point2D initialTranslation = initialTranslations.get(shape);
                if (shape instanceof Line line) {
                    Point2D[] initialPos = initialLinePositions.get(line);
                    if (initialPos != null) {
                        line.setStartX(initialPos[0].getX() + deltaX);
                        line.setStartY(initialPos[0].getY() + deltaY);
                        line.setEndX(initialPos[1].getX() + deltaX);
                        line.setEndY(initialPos[1].getY() + deltaY);
                    }
                } else if (initialTranslation != null) {
                    shape.setTranslateX(initialTranslation.getX() + deltaX);
                    shape.setTranslateY(initialTranslation.getY() + deltaY);
                }
            }

            for (SelectionShapeDecorator decorator : decorators.values()) {
                if (decorator != null) {
                    decorator.removeDecoration();
                    decorator.applyDecoration();
                }
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
        activeHandleType = ResizeHandleType.NONE;
        if (!selectedJavaFxShapes.isEmpty()) {
            if (wasRotating || wasResizing) {

                if (selectedJavaFxShapes != null && !selectedJavaFxShapes.isEmpty()) {
                    for (Shape shape : selectedJavaFxShapes) {
                        SelectionShapeDecorator decorator = decorators.get(shape);
                        if (decorator != null) {
                            decorator.removeDecoration();
                        }
                    }
                    callback.onModifyShapes(selectedJavaFxShapes);
                    clearSelection();
                }





            } else if (wasMoving) {
                Point2D localPoint = drawingArea.sceneToLocal(event.getSceneX(), event.getSceneY());
                double dx = localPoint.getX() - initialMousePress.getX();
                double dy = localPoint.getY() - initialMousePress.getY();
                boolean significantChange = (dx * dx + dy * dy) > 4;

                if (significantChange) {
                    for (Shape shape : selectedJavaFxShapes) {
                        bakeTranslation(shape);
                    }

                    if (selectedJavaFxShapes != null && !selectedJavaFxShapes.isEmpty()) {
                        for (Shape shape : selectedJavaFxShapes) {
                            SelectionShapeDecorator decorator = decorators.get(shape);
                            if (decorator != null) {
                                decorator.removeDecoration();
                            }
                        }
                        callback.onModifyShapes(selectedJavaFxShapes);
                        clearSelection();
                    }
                }
            }
        }


        handleMouseMoved(event);

        event.consume();
    }

    @Override
    public void handleMouseMoved(MouseEvent event) {
        if (isMoving) {
            drawingArea.setCursor(Cursor.MOVE);
            return;
        }
        if (isRotating) {
            drawingArea.setCursor(Cursor.CROSSHAIR);
            return;
        }
        if (activeHandleType != ResizeHandleType.NONE) return;

        Point2D localPoint = drawingArea.sceneToLocal(event.getSceneX(), event.getSceneY());
        double x = localPoint.getX();
        double y = localPoint.getY();

        Circle handleAtPosition = findHandleAt(x, y);
        if (handleAtPosition != null) {
            String handleType = (String) handleAtPosition.getUserData();
            switch (handleType) {
                case "ROTATION":
                    drawingArea.setCursor(Cursor.CROSSHAIR);
                    break;
                case "NORTH_WEST":
                case "SOUTH_EAST":
                    drawingArea.setCursor(Cursor.NW_RESIZE);
                    break;
                case "NORTH_EAST":
                case "SOUTH_WEST":
                    drawingArea.setCursor(Cursor.NE_RESIZE);
                    break;
                case "NORTH":
                case "SOUTH":
                    drawingArea.setCursor(Cursor.V_RESIZE);
                    break;
                case "EAST":
                case "WEST":
                    drawingArea.setCursor(Cursor.H_RESIZE);
                    break;
                default:
                    drawingArea.setCursor(Cursor.HAND);
                    break;
            }
        } else {
            Shape shapeAtPos = findShapeAt(x, y);
            drawingArea.setCursor(shapeAtPos != null ? Cursor.MOVE : Cursor.DEFAULT);
        }
    }

    @Override
    public void handleLineBorderColorChange(Color color) {}

    @Override
    public void handleRectangleBorderColorChange(Color color) {}

    @Override
    public void handleRectangleFillColorChange(Color color) {}

    @Override
    public void handleEllipseBorderColorChange(Color color) {}

    @Override
    public void handleEllipseFillColorChange(Color color) {}

    @Override
    public void handlePolygonBorderColorChange(Color color) {}

    @Override
    public void handlePolygonFillColorChange(Color color) {}

    @Override
    public void handleTextBorderColorChange(Color color) {}

    @Override
    public void handleTextFillColorChange(Color color) {}

    @Override
    public void handleTextColorChange(Color color) {}

    @Override
    public void handlePolygonVerticesChange(int polygonVertices) {}

    @Override
    public void handleRegularPolygon(boolean regularPolygon) {}

    @Override
    public void handleFontSizeChange(int fontSize) {}

    @Override
    public void handleKeyPressed(KeyEvent event) {}

    @Override
    public void handleKeyTyped(KeyEvent event) {}

    public void reset() {
        clearSelection();
        isMoving = false;
        isRotating = false;
        isResizing = false;
        activeHandleType = ResizeHandleType.NONE;
        drawingArea.setCursor(Cursor.DEFAULT);
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
        } else if (shape instanceof Polygon p) {
            for (int i = 0; i < p.getPoints().size(); i += 2) {
                p.getPoints().set(i, p.getPoints().get(i) + tx);
                p.getPoints().set(i + 1, p.getPoints().get(i + 1) + ty);
            }
        }
        shape.setTranslateX(0);
        shape.setTranslateY(0);
    }

    private Shape findShapeAt(double x, double y) {
        List<Node> children = drawingArea.getChildren();

        for (int i = children.size() - 1; i >= 0; i--) {
            Node node = children.get(i);

            boolean isDecoratorElement = false;
            for (SelectionShapeDecorator decorator : decorators.values()) {
                if (decorator != null) {
                    if ((node instanceof Circle && decorator.getResizeHandles().contains(node)) ||
                            (node instanceof Shape && decorator.getSelectionBorders().contains(node))) {
                        isDecoratorElement = true;
                        break;
                    }
                }
            }

            if (isDecoratorElement) continue;

            if (node instanceof Shape shape) {
                if (!(shape instanceof Rectangle || shape instanceof Ellipse ||
                        shape instanceof Line || shape instanceof Polygon)) {
                    continue;
                }

                if (shape.isVisible() && shape.contains(x, y)) {
                    return shape;
                }
            }
        }
        return null;
    }

    private Circle findHandleAt(double x, double y) {
        for (SelectionShapeDecorator decorator : decorators.values()) {
            if (decorator != null) {
                for (Circle handle : decorator.getResizeHandles()) {
                    if (handle.isVisible() && handle.getBoundsInParent().contains(x, y)) {
                        return handle;
                    }
                }
            }
        }
        return null;
    }

    public void selectShapeByModel(MyShape shape) {
        Shape javafxShape = shapeMapping.getViewShape(shape);
        if (javafxShape != null) {
            clearSelection();
            addShapeToSelection(javafxShape);
        }
    }

    public void clearSelection() {
        boolean hadSelection = !selectedJavaFxShapes.isEmpty();

        for (SelectionShapeDecorator decorator : decorators.values()) {
            if (decorator != null) decorator.removeDecoration();
        }
        decorators.clear();
        selectedJavaFxShapes.clear();
        selectedModelShapes.clear();
        primarySelectedShape = null;
        drawingArea.setCursor(Cursor.DEFAULT);

        if (hadSelection) {
            callback.onChangeShapeSelected();
        }
    }

    private void addShapeToSelection(Shape shape) {
        if (shape == null || selectedJavaFxShapes.contains(shape)) return;

        bakeTranslation(shape);
        selectedJavaFxShapes.add(shape);

        MyShape modelShape = shapeMapping.getModelShape(shape);
        if (modelShape != null) {
            selectedModelShapes.add(modelShape);
        }

        SelectionShapeDecorator decorator = new SelectionShapeDecorator(shape);
        decorators.put(shape, decorator);
        decorator.applyDecoration();

        if (primarySelectedShape == null) {
            setPrimarySelectedShape(shape);
        }

        callback.onChangeShapeSelected();
    }

    private void removeShapeFromSelection(Shape shape) {
        if (shape == null || !selectedJavaFxShapes.contains(shape)) return;

        selectedJavaFxShapes.remove(shape);

        MyShape modelShape = shapeMapping.getModelShape(shape);
        if (modelShape != null) {
            selectedModelShapes.remove(modelShape);
        }

        SelectionShapeDecorator decorator = decorators.get(shape);
        if (decorator != null) {
            decorator.removeDecoration();
            decorators.remove(shape);
        }

        if (primarySelectedShape == shape) {
            primarySelectedShape = selectedJavaFxShapes.isEmpty() ? null : selectedJavaFxShapes.get(0);
        }

        callback.onChangeShapeSelected();
    }

    private void setPrimarySelectedShape(Shape shape) {
        if (selectedJavaFxShapes.contains(shape)) {
            primarySelectedShape = shape;
        }
    }

    private void performResize(Shape shape, double deltaX, double deltaY) {
        double minSize = 5;

        if (shape instanceof Rectangle rect) {
            resizeRectangle(rect, deltaX, deltaY, minSize);
        } else if (shape instanceof Ellipse ellipse) {
            resizeEllipse(ellipse, deltaX, deltaY, minSize);
        } else if (shape instanceof Line line) {
            resizeLine(line, deltaX, deltaY);
        } else if (shape instanceof Polygon polygon) {
            resizePolygon(polygon, deltaX, deltaY, minSize);
        }
    }

    private void resizeRectangle(Rectangle rect, double deltaX, double deltaY, double minSize) {
        double currentX = rect.getX();
        double currentY = rect.getY();
        double currentWidth = rect.getWidth();
        double currentHeight = rect.getHeight();
        double newX = currentX, newY = currentY, newWidth = currentWidth, newHeight = currentHeight;

        switch (activeHandleType) {
            case NORTH_WEST:
                newX += deltaX; newY += deltaY; newWidth -= deltaX; newHeight -= deltaY;
                break;
            case NORTH_EAST:
                newY += deltaY; newWidth += deltaX; newHeight -= deltaY;
                break;
            case SOUTH_WEST:
                newX += deltaX; newWidth -= deltaX; newHeight += deltaY;
                break;
            case SOUTH_EAST:
                newWidth += deltaX; newHeight += deltaY;
                break;
            case NORTH:
                newY += deltaY; newHeight -= deltaY;
                break;
            case SOUTH:
                newHeight += deltaY;
                break;
            case EAST:
                newWidth += deltaX;
                break;
            case WEST:
                newX += deltaX; newWidth -= deltaX;
                break;
        }

        if (newWidth < minSize) {
            if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.SOUTH_WEST || activeHandleType == ResizeHandleType.WEST) {
                newX += (newWidth - minSize);
            }
            newWidth = minSize;
        }
        if (newHeight < minSize) {
            if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.NORTH_EAST || activeHandleType == ResizeHandleType.NORTH) {
                newY += (newHeight - minSize);
            }
            newHeight = minSize;
        }

        rect.setX(newX);
        rect.setY(newY);
        rect.setWidth(newWidth);
        rect.setHeight(newHeight);
    }

    private void resizeEllipse(Ellipse ellipse, double deltaX, double deltaY, double minSize) {
        double currentCenterX = ellipse.getCenterX();
        double currentCenterY = ellipse.getCenterY();
        double currentRadiusX = ellipse.getRadiusX();
        double currentRadiusY = ellipse.getRadiusY();

        double newX = currentCenterX - currentRadiusX;
        double newY = currentCenterY - currentRadiusY;
        double newWidth = currentRadiusX * 2;
        double newHeight = currentRadiusY * 2;

        switch (activeHandleType) {
            case NORTH_WEST:
                newX += deltaX; newY += deltaY; newWidth -= deltaX; newHeight -= deltaY;
                break;
            case NORTH_EAST:
                newY += deltaY; newWidth += deltaX; newHeight -= deltaY;
                break;
            case SOUTH_WEST:
                newX += deltaX; newWidth -= deltaX; newHeight += deltaY;
                break;
            case SOUTH_EAST:
                newWidth += deltaX; newHeight += deltaY;
                break;
            case NORTH:
                newY += deltaY; newHeight -= deltaY;
                break;
            case SOUTH:
                newHeight += deltaY;
                break;
            case EAST:
                newWidth += deltaX;
                break;
            case WEST:
                newX += deltaX; newWidth -= deltaX;
                break;
        }

        if (newWidth < minSize) {
            if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.SOUTH_WEST || activeHandleType == ResizeHandleType.WEST) {
                newX += (newWidth - minSize);
            }
            newWidth = minSize;
        }
        if (newHeight < minSize) {
            if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.NORTH_EAST || activeHandleType == ResizeHandleType.NORTH) {
                newY += (newHeight - minSize);
            }
            newHeight = minSize;
        }

        ellipse.setCenterX(newX + newWidth / 2);
        ellipse.setCenterY(newY + newHeight / 2);
        ellipse.setRadiusX(newWidth / 2);
        ellipse.setRadiusY(newHeight / 2);
    }

    private void resizeLine(Line line, double deltaX, double deltaY) {
        switch (activeHandleType) {
            case NORTH_WEST:
                line.setStartX(line.getStartX() + deltaX);
                line.setStartY(line.getStartY() + deltaY);
                break;
            case NORTH_EAST:
                line.setEndX(line.getEndX() + deltaX);
                line.setStartY(line.getStartY() + deltaY);
                break;
            case SOUTH_WEST:
                line.setStartX(line.getStartX() + deltaX);
                line.setEndY(line.getEndY() + deltaY);
                break;
            case SOUTH_EAST:
                line.setEndX(line.getEndX() + deltaX);
                line.setEndY(line.getEndY() + deltaY);
                break;
            case NORTH:
                line.setStartY(line.getStartY() + deltaY);
                break;
            case SOUTH:
                line.setEndY(line.getEndY() + deltaY);
                break;
            case EAST:
                line.setEndX(line.getEndX() + deltaX);
                break;
            case WEST:
                line.setStartX(line.getStartX() + deltaX);
                break;
        }
    }

    private void resizePolygon(Polygon polygon, double deltaX, double deltaY, double minSize) {
        Bounds bounds = polygon.getBoundsInLocal();
        double currentX = bounds.getMinX();
        double currentY = bounds.getMinY();
        double currentWidth = bounds.getWidth();
        double currentHeight = bounds.getHeight();

        double newX = currentX, newY = currentY, newWidth = currentWidth, newHeight = currentHeight;

        switch (activeHandleType) {
            case NORTH_WEST:
                newX += deltaX; newY += deltaY; newWidth -= deltaX; newHeight -= deltaY;
                break;
            case NORTH_EAST:
                newY += deltaY; newWidth += deltaX; newHeight -= deltaY;
                break;
            case SOUTH_WEST:
                newX += deltaX; newWidth -= deltaX; newHeight += deltaY;
                break;
            case SOUTH_EAST:
                newWidth += deltaX; newHeight += deltaY;
                break;
            case NORTH:
                newY += deltaY; newHeight -= deltaY;
                break;
            case SOUTH:
                newHeight += deltaY;
                break;
            case EAST:
                newWidth += deltaX;
                break;
            case WEST:
                newX += deltaX; newWidth -= deltaX;
                break;
        }

        if (newWidth < minSize || newHeight < minSize) return;

        double scaleX = newWidth / currentWidth;
        double scaleY = newHeight / currentHeight;
        double offsetX = newX - currentX;
        double offsetY = newY - currentY;

        for (int i = 0; i < polygon.getPoints().size(); i += 2) {
            double pointX = polygon.getPoints().get(i);
            double pointY = polygon.getPoints().get(i + 1);

            double relativeX = pointX - currentX;
            double relativeY = pointY - currentY;

            double newPointX = currentX + relativeX * scaleX + offsetX;
            double newPointY = currentY + relativeY * scaleY + offsetY;

            polygon.getPoints().set(i, newPointX);
            polygon.getPoints().set(i + 1, newPointY);
        }
    }

    private double calculateAngle(double x, double y, Shape shape) {
        Bounds bounds = shape.getBoundsInParent();
        double centerX = bounds.getMinX() + bounds.getWidth() / 2;
        double centerY = bounds.getMinY() + bounds.getHeight() / 2;
        return Math.toDegrees(Math.atan2(y - centerY, x - centerX));
    }
}