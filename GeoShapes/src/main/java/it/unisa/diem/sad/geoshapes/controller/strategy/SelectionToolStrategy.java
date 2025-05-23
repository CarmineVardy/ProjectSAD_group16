package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.controller.ShapeMapping;
import it.unisa.diem.sad.geoshapes.decorator.SelectionDecorator;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.geometry.Point2D;

import java.util.List;

public class SelectionToolStrategy implements ToolStrategy {

    private final Pane drawingArea;
    private final ShapeMapping shapeMapping;
    private SelectionDecorator currentDecorator;
    private MyShape selectedModelShape;
    private Shape selectedJavaFxShape;
    private InteractionCallback callback;

    private boolean isResizing = false;
    private boolean isMoving = false;
    private Point2D initialMousePress;
    private Bounds initialShapeBounds;
    private double initialTranslateX;
    private double initialTranslateY;

    private ResizeHandleType activeHandleType = ResizeHandleType.NONE;

    private enum ResizeHandleType {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        MIDDLE_LEFT, MIDDLE_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT, LINE_START, LINE_END,
        NONE
    }

    public SelectionToolStrategy(Pane drawingArea, ShapeMapping shapeMapping, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.shapeMapping = shapeMapping;
        this.callback = callback;
    }

    private void selectShape(Shape shapeToSelect) {
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
        }

        selectedJavaFxShape = shapeToSelect;

        if (selectedJavaFxShape == null) {
            selectedModelShape = null;
            currentDecorator = null;
            drawingArea.setCursor(Cursor.DEFAULT);
        } else {
            selectedModelShape = shapeMapping.getModelShape(selectedJavaFxShape);
            currentDecorator = new SelectionDecorator(selectedJavaFxShape);
            currentDecorator.applyDecoration();
            drawingArea.setCursor(Cursor.HAND);
        }
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
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
            selectedModelShape = null;
            currentDecorator = null;
            isMoving = false;
            isResizing = false;
            drawingArea.setCursor(Cursor.DEFAULT);
            event.consume();
            return;
        }

        if (event.getButton() == MouseButton.SECONDARY) {
            if (shapeAtPosition != selectedJavaFxShape) selectShape(shapeAtPosition);
            callback.onSelectionMenuOpened(selectedJavaFxShape, event.getX(), event.getY());
            event.consume();
            return;
        }

        if (event.getButton() == MouseButton.PRIMARY) {
            if (shapeAtPosition != selectedJavaFxShape) selectShape(shapeAtPosition);
            isMoving = true;
            isResizing = false;
            initialMousePress = new Point2D(x, y);
            initialTranslateX = selectedJavaFxShape.getTranslateX();
            initialTranslateY = selectedJavaFxShape.getTranslateY();
            drawingArea.setCursor(Cursor.MOVE);
            event.consume();
        }
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        if (!isMoving && !isResizing || selectedJavaFxShape == null || initialMousePress == null) return;

        double deltaX = event.getX() - initialMousePress.getX();
        double deltaY = event.getY() - initialMousePress.getY();

        if (isResizing) {
            updateJavaFxShapeDimensions(deltaX, deltaY, activeHandleType);
            event.consume();
        } else if (isMoving) {
            selectedJavaFxShape.setTranslateX(initialTranslateX + deltaX);
            selectedJavaFxShape.setTranslateY(initialTranslateY + deltaY);
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
        boolean wasResizing = isResizing;
        boolean wasMoving = isMoving;

        isResizing = false;
        isMoving = false;
        activeHandleType = ResizeHandleType.NONE;

        boolean significantChange = false;
        if (initialMousePress != null && (wasResizing || wasMoving) && selectedJavaFxShape != null) {
            double dx = event.getX() - initialMousePress.getX();
            double dy = event.getY() - initialMousePress.getY();
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
            if (isMoving) drawingArea.setCursor(Cursor.MOVE);
            return;
        }

        Circle handleAtPosition = findHandleAt(event.getX(), event.getY());
        if (handleAtPosition != null) {
            drawingArea.setCursor(Cursor.CROSSHAIR);
        } else {
            Shape shapeAtPos = findShapeAt(event.getX(), event.getY());
            if (shapeAtPos != null) {
                drawingArea.setCursor(Cursor.HAND);
            } else {
                drawingArea.setCursor(Cursor.DEFAULT);
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
    public void reset() {
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            currentDecorator = null;
        }
        if (selectedJavaFxShape != null) {
            if (drawingArea.getChildren().contains(selectedJavaFxShape)) {
                drawingArea.getChildren().remove(selectedJavaFxShape);
            }
            selectedJavaFxShape = null;
        }
        selectedModelShape = null;
        isResizing = false;
        isMoving = false;
        activeHandleType = ResizeHandleType.NONE;
        drawingArea.setCursor(Cursor.DEFAULT);
    }

    @Override
    public void activate(Color borderColor, Color fillColor) {

    }

    private Shape findShapeAt(double x, double y) {
        List<javafx.scene.Node> children = drawingArea.getChildren();
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

        if (selectedJavaFxShape instanceof Line) {
            Line fxLine = (Line) selectedJavaFxShape;
            switch (handleType) {
                case LINE_START: return new Point2D(fxLine.getEndX(), fxLine.getEndY());
                case LINE_END: return new Point2D(fxLine.getStartX(), fxLine.getStartY());
                default: return null;
            }
        } else {
            // Assicurati che questi siano corretti: il pivot è il lato/angolo OPPOSTO.
            switch (handleType) {
                case TOP_LEFT:      pivotX = initialBounds.getMaxX(); pivotY = initialBounds.getMaxY(); break; // Bottom-Right
                case TOP_RIGHT:     pivotX = initialBounds.getMinX(); pivotY = initialBounds.getMaxY(); break; // Bottom-Left
                case BOTTOM_LEFT:   pivotX = initialBounds.getMaxX(); pivotY = initialBounds.getMinY(); break; // Top-Right
                case BOTTOM_RIGHT:  pivotX = initialBounds.getMinX(); pivotY = initialBounds.getMinY(); break; // Top-Left
                case TOP_CENTER:    pivotX = initialBounds.getMinX() + initialBounds.getWidth() / 2; pivotY = initialBounds.getMaxY(); break; // Bottom-Center
                case BOTTOM_CENTER: pivotX = initialBounds.getMinX() + initialBounds.getWidth() / 2; pivotY = initialBounds.getMinY(); break; // Top-Center
                case MIDDLE_LEFT:   pivotX = initialBounds.getMaxX(); pivotY = initialBounds.getMinY() + initialBounds.getHeight() / 2; break; // Middle-Right
                case MIDDLE_RIGHT:  pivotX = initialBounds.getMinX(); pivotY = initialBounds.getMinY() + initialBounds.getHeight() / 2; break; // Middle-Left
                default:            return null;
            }
            return new Point2D(pivotX, pivotY);
        }
    }

    /**
     * Calcola il ridimensionamento (proporzionale per angoli, non per lati).
     */
    private ResizeCalculations calculateResize(double deltaX, double deltaY, ResizeHandleType handleType, Bounds initialBounds, Point2D initialMouse, Point2D pivot) {
        double currentMouseX = initialMouse.getX() + deltaX;
        double currentMouseY = initialMouse.getY() + deltaY;

        if (selectedJavaFxShape instanceof Line fxLine) {
            // Logica Line invariata
            double newStartX, newStartY, newEndX, newEndY;
            if (handleType == ResizeHandleType.LINE_START) {
                newStartX = currentMouseX; newStartY = currentMouseY;
                newEndX = fxLine.getEndX(); newEndY = fxLine.getEndY();
            } else if (handleType == ResizeHandleType.LINE_END) {
                newStartX = fxLine.getStartX(); newStartY = fxLine.getStartY();
                newEndX = currentMouseX; newEndY = currentMouseY;
            } else {
                return new ResizeCalculations(fxLine.getStartX(), fxLine.getStartY(), fxLine.getEndX(), fxLine.getEndY());
            }
            return new ResizeCalculations(newStartX, newStartY, newEndX, newEndY);
        } else {
            // Logica per Rettangoli/Ellissi
            double newX = initialBounds.getMinX();
            double newY = initialBounds.getMinY();
            double newWidth = initialBounds.getWidth();
            double newHeight = initialBounds.getHeight();
            final double MIN_SIZE = 5.0;

            switch (handleType) {
                // --- Angoli (Proporzionale) ---
                case TOP_LEFT:
                case BOTTOM_RIGHT:
                case TOP_RIGHT:
                case BOTTOM_LEFT:
                    double dx = Math.abs(currentMouseX - pivot.getX());
                    double dy = Math.abs(currentMouseY - pivot.getY());
                    double originalRatio = (newHeight == 0) ? 1 : newWidth / newHeight;

                    double scaleX = (newWidth == 0) ? 0 : dx / newWidth;
                    double scaleY = (newHeight == 0) ? 0 : dy / newHeight;
                    double scale = Math.max(scaleX, scaleY);

                    newWidth = initialBounds.getWidth() * scale;
                    newHeight = initialBounds.getHeight() * scale;

                    // Gestione MIN_SIZE proporzionale
                    if (newWidth < MIN_SIZE || newHeight < MIN_SIZE) {
                        double scaleW = (initialBounds.getWidth() == 0) ? Double.MAX_VALUE : MIN_SIZE / initialBounds.getWidth();
                        double scaleH = (initialBounds.getHeight() == 0) ? Double.MAX_VALUE : MIN_SIZE / initialBounds.getHeight();
                        scale = Math.max(scale, Math.max(scaleW, scaleH));
                        newWidth = initialBounds.getWidth() * scale;
                        newHeight = initialBounds.getHeight() * scale;
                    }

                    // Calcola X/Y basandoti sul pivot
                    if (handleType == ResizeHandleType.TOP_LEFT || handleType == ResizeHandleType.BOTTOM_LEFT) newX = pivot.getX() - newWidth; else newX = pivot.getX();
                    if (handleType == ResizeHandleType.TOP_LEFT || handleType == ResizeHandleType.TOP_RIGHT) newY = pivot.getY() - newHeight; else newY = pivot.getY();
                    break;

                // --- Lati (Non Proporzionale) ---
                case TOP_CENTER:
                    newY = currentMouseY; // Il nuovo Y è la posizione del mouse
                    newHeight = pivot.getY() - currentMouseY; // L'altezza è la distanza dal pivot (fondo)
                    if (newHeight < MIN_SIZE) { // Se l'altezza è troppo piccola...
                        newHeight = MIN_SIZE;   // ...impostala al minimo...
                        newY = pivot.getY() - MIN_SIZE; // ...e ricalcola Y per mantenere il fondo fisso.
                    }
                    break;
                case BOTTOM_CENTER:
                    newHeight = currentMouseY - pivot.getY(); // L'altezza è la distanza dal pivot (sopra)
                    if (newHeight < MIN_SIZE) newHeight = MIN_SIZE;
                    // newY rimane quello iniziale (pivot.getY())
                    break;
                case MIDDLE_LEFT:
                    newX = currentMouseX; // Il nuovo X è la posizione del mouse
                    newWidth = pivot.getX() - currentMouseX; // La larghezza è la distanza dal pivot (destra)
                    if (newWidth < MIN_SIZE) { // Se la larghezza è troppo piccola...
                        newWidth = MIN_SIZE;    // ...impostala al minimo...
                        newX = pivot.getX() - MIN_SIZE; // ...e ricalcola X per mantenere la destra fissa.
                    }
                    break;
                case MIDDLE_RIGHT:
                    newWidth = currentMouseX - pivot.getX(); // La larghezza è la distanza dal pivot (sinistra)
                    if (newWidth < MIN_SIZE) newWidth = MIN_SIZE;
                    // newX rimane quello iniziale (pivot.getX())
                    break;

                default: // Caso non previsto o LINE
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
}