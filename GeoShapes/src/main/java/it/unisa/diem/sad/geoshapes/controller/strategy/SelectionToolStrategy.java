package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.controller.ShapeMapping;
import it.unisa.diem.sad.geoshapes.decorator.SelectionDecorator;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import javafx.event.ActionEvent;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectionToolStrategy implements ToolStrategy {

    private final Pane drawingPane;
    private final ShapeMapping shapeMapping;

    private InteractionCallback callback;
    private boolean isRotating;
    private double lastAngle;

    //VARIABILI VECCHIE CHE NON USO
    //private SelectionDecorator currentDecorator;
    // private Shape selectedJavaFxShape;
    //private Shape currentShape;
    //private double initialTranslateX;
    //private double initialTranslateY;
    // private double initialLineStartX, initialLineStartY, initialLineEndX, initialLineEndY;
    //private double lastX;
    //private double lastY;
    // private boolean isResizing;
    // #################################################################


    //VARIABILI NUOVE
    private final Map<Shape, SelectionDecorator> activeDecorators = new HashMap<>();
    // Per le operazioni di gruppo
    private Bounds initialGroupBounds; // Bounding box iniziale del gruppo per ridimensionamento/rotazione
    private Point2D initialMousePressForGroup; // Punto iniziale del click per trascinamento/ridimensionamento/rotazione del gruppo

    // Mappe per memorizzare le proprietà iniziali di OGNI forma selezionata
    private final Map<Shape, Double> initialShapeX = new HashMap<>();
    private final Map<Shape, Double> initialShapeY = new HashMap<>();
    private final Map<Shape, Double> initialShapeWidth = new HashMap<>(); // Per Rect/Ellipse
    private final Map<Shape, Double> initialShapeHeight = new HashMap<>(); // Per Rect/Ellipse
    private final Map<Shape, Double> initialShapeRotate = new HashMap<>(); // Per la rotazione

    // Per le linee, memorizza start/end
    private final Map<Line, Point2D> initialLineStart = new HashMap<>();
    private final Map<Line, Point2D> initialLineEnd = new HashMap<>();

    // Flag di stato per le operazioni di gruppo
    private boolean isGroupMoving = false;
    private boolean isGroupResizing = false;
    private boolean isGroupRotating = false;

    // Handle per il gruppo (se visibili, in questo caso solo per interazione, non per highlighting)
    private Circle groupRotationHandle;
    private final List<Circle> groupResizeHandles = new ArrayList<>(); // Inizializza come lista vuota

    // Variabili per il ridimensionamento/rotazione individuale se si clicca sull'handle di una singola forma
    private Shape currentShapeBeingTransformed; // La singola forma attualmente in trasformazione (ridimensionamento/rotazione)
    private boolean isIndividualResizing = false;
    private double lastXForIndividualResize; // Solo per il delta X/Y per resize individuale
    private double lastYForIndividualResize; // Solo per il delta X/Y per resize individuale

    private boolean isMoving = false;
    private Point2D initialMousePress;


    private final List<MyShape> selectedModelShapes = new ArrayList<>();
    private final List<Shape> selectedJavaFxShapes = new ArrayList<>();
    private final Group zoomGroup;
    private ResizeHandleType activeHandleType = ResizeHandleType.NONE;
    private DrawingModel model;


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

   /* @Override
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


    /*
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
                    case NORTH_WEST:
                        newX += deltaX;
                        newY += deltaY;
                        newWidth -= deltaX;
                        newHeight -= deltaY;
                        break;
                    case NORTH_EAST:
                        newY += deltaY;
                        newWidth += deltaX;
                        newHeight -= deltaY;
                        break;
                    case SOUTH_WEST:
                        newX += deltaX;
                        newWidth -= deltaX;
                        newHeight += deltaY;
                        break;
                    case SOUTH_EAST:
                        newWidth += deltaX;
                        newHeight += deltaY;
                        break;
                    case NORTH:
                        newY += deltaY;
                        newHeight -= deltaY;
                        break;
                    case SOUTH:
                        newHeight += deltaY;
                        break;
                    case EAST:
                        newWidth += deltaX;
                        break;
                    case WEST:
                        newX += deltaX;
                        newWidth -= deltaX;
                        break;
                    default:
                        break;
                }

                if (newWidth < minSize) {
                    if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.SOUTH_WEST || activeHandleType == ResizeHandleType.WEST)
                        newX += (newWidth - minSize);
                    newWidth = minSize;
                }
                if (newHeight < minSize) {
                    if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.NORTH_EAST || activeHandleType == ResizeHandleType.NORTH)
                        newY += (newHeight - minSize);
                    newHeight = minSize;
                }
                rect.setX(newX);
                rect.setY(newY);
                rect.setWidth(newWidth);
                rect.setHeight(newHeight);

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
                    case NORTH_WEST:
                        newX += deltaX;
                        newY += deltaY;
                        newWidth -= deltaX;
                        newHeight -= deltaY;
                        break;
                    case NORTH_EAST:
                        newY += deltaY;
                        newWidth += deltaX;
                        newHeight -= deltaY;
                        break;
                    case SOUTH_WEST:
                        newX += deltaX;
                        newWidth -= deltaX;
                        newHeight += deltaY;
                        break;
                    case SOUTH_EAST:
                        newWidth += deltaX;
                        newHeight += deltaY;
                        break;
                    case NORTH:
                        newY += deltaY;
                        newHeight -= deltaY;
                        break;
                    case SOUTH:
                        newHeight += deltaY;
                        break;
                    case EAST:
                        newWidth += deltaX;
                        break;
                    case WEST:
                        newX += deltaX;
                        newWidth -= deltaX;
                        break;
                    default:
                        break;
                }

                if (newWidth < minSize) {
                    if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.SOUTH_WEST || activeHandleType == ResizeHandleType.WEST)
                        newX += (newWidth - minSize);
                    newWidth = minSize;
                }
                if (newHeight < minSize) {
                    if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.NORTH_EAST || activeHandleType == ResizeHandleType.NORTH)
                        newY += (newHeight - minSize);
                    newHeight = minSize;
                }
                ellipse.setCenterX(newX + newWidth / 2);
                ellipse.setCenterY(newY + newHeight / 2);
                ellipse.setRadiusX(newWidth / 2);
                ellipse.setRadiusY(newHeight / 2);

            } else if (selectedJavaFxShape instanceof Line line) {
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
                    default:
                        break;
                }
            }

            lastX = x;
            lastY = y;
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
        if (isMoving) {
            drawingPane.setCursor(Cursor.MOVE);
            return;
        }
        if (isRotating) {
            drawingPane.setCursor(Cursor.CROSSHAIR);
            return;
        }
        if (activeHandleType != ResizeHandleType.NONE) return;

        Point2D localPoint = getTransformedCoordinates(event, drawingPane);
        double x = localPoint.getX();
        double y = localPoint.getY();

        Circle handleAtPosition = findHandleAt(x, y);
        if (handleAtPosition != null) {
            String handleType = (String) handleAtPosition.getUserData();
            switch (handleType) {
                case "ROTATION":
                    drawingPane.setCursor(Cursor.CROSSHAIR);
                    break;
                case "NORTH_WEST":
                case "SOUTH_EAST":
                    drawingPane.setCursor(Cursor.NW_RESIZE);
                    break;
                case "NORTH_EAST":
                case "SOUTH_WEST":
                    drawingPane.setCursor(Cursor.NE_RESIZE);
                    break;
                case "NORTH":
                case "SOUTH":
                    drawingPane.setCursor(Cursor.V_RESIZE);
                    break;
                case "EAST":
                case "WEST":
                    drawingPane.setCursor(Cursor.H_RESIZE);
                    break;
                default:
                    drawingPane.setCursor(Cursor.HAND);
                    break;
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
*/
    @Override
    public void reset() {
        clearSelection(); // Usa il nuovo metodo clearSelection()
        drawingPane.setCursor(Cursor.DEFAULT);
        callback.onShapeDeselected(); // Notifica al controller che non c'è selezione
        currentShapeBeingTransformed = null; // Resetta la forma attualmente in trasformazione
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
/*
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






    @Override
    public List<MyShape> getSelectedShapes() {
        return this.selectedModelShapes;
    }



    public void setModel(DrawingModel model) {
        this.model = model;
    }


*/
















































































































 //METODI HELPER
// Rimuove tutte le decorazioni e resetta lo stato di selezione
    public void clearSelection() {
        for (SelectionDecorator decorator : activeDecorators.values()) {
            decorator.removeDecoration();
        }
        activeDecorators.clear();
        selectedJavaFxShapes.clear();
        selectedModelShapes.clear();
        // Rimuovi eventuali handle di gruppo se sono stati disegnati
        removeGroupDecorations();
        drawingPane.setCursor(Cursor.DEFAULT);
        callback.onShapesSelected(selectedJavaFxShapes); // Notifica che la selezione è vuota
    }

    // Aggiunge una singola forma alla selezione
    private void addShapeToSelection(Shape shapeToAdd) {
        if (!selectedJavaFxShapes.contains(shapeToAdd)) {
            // Applica trasformazioni pendenti PRIMA di aggiungere
            bakeTransformations(shapeToAdd);
            selectedJavaFxShapes.add(shapeToAdd);
            MyShape modelShape = shapeMapping.getModelShape(shapeToAdd);
            if (modelShape != null) {
                selectedModelShapes.add(modelShape);
            }

            // Applica decorazione individuale
            SelectionDecorator decorator = new SelectionDecorator(shapeToAdd);
            decorator.applyDecoration();
            activeDecorators.put(shapeToAdd, decorator);
        }
        // Dopo aver modificato la selezione, aggiorna le decorazioni del gruppo (handles)
        updateGroupDecorations();
    }

    // Rimuove una singola forma dalla selezione
    private void deselectShape(Shape shapeToRemove) {
        if (selectedJavaFxShapes.remove(shapeToRemove)) {
            selectedModelShapes.remove(shapeMapping.getModelShape(shapeToRemove));
            SelectionDecorator decorator = activeDecorators.remove(shapeToRemove);
            if (decorator != null) {
                decorator.removeDecoration();
            }
        }
        // Dopo aver modificato la selezione, aggiorna le decorazioni del gruppo (handles)
        updateGroupDecorations();
    }

    // Gestisce la logica di selezione (singolo click vs Shift-click)
    private void manageSelection(Shape shapeToSelect, MouseEvent event) {
        boolean isShiftDown = event.isShiftDown();
        boolean isShapeAlreadySelected = selectedJavaFxShapes.contains(shapeToSelect);

        if (isShiftDown) {
            if (isShapeAlreadySelected) {
                deselectShape(shapeToSelect); // Shift-click su forma già selezionata: deseleziona
            } else {
                addShapeToSelection(shapeToSelect); // Shift-click su forma non selezionata: aggiungi
            }
        } else {
            // Click senza Shift:
            // Se la forma cliccata non è già selezionata, o se è selezionata ma ci sono altre forme nel gruppo,
            // deseleziona tutto e seleziona solo questa.
            if (!isShapeAlreadySelected || selectedJavaFxShapes.size() > 1) {
                clearSelection(); // Deseleziona tutte le forme attuali
                addShapeToSelection(shapeToSelect); // Aggiungi solo questa forma
            }
            // Se la forma è già l'unica selezionata, non fare nulla sulla selezione (prepara per drag)
        }
        // Notifica il callback con l'intera lista di forme selezionate
        callback.onShapesSelected(selectedJavaFxShapes);
    }

    // Calcola il bounding box complessivo di tutte le forme selezionate
    private Bounds calculateGroupBoundsInParent() {
        if (selectedJavaFxShapes.isEmpty()) {
            return null;
        }
        Bounds bounds = null;
        for (Shape shape : selectedJavaFxShapes) {
            // Ottieni i bounds in parent, che include traslazioni e rotazioni locali
            Bounds shapeBounds = shape.getBoundsInParent();
            if (bounds == null) {
                bounds = shapeBounds;
            } else {
                // Unisci i bounds (custom method or use Bounds.union, but be careful with rotation)
                bounds = unionBounds(bounds, shapeBounds);
            }
        }
        return bounds;
    }

    // Funzione helper per unire due Bounds
    private Bounds unionBounds(Bounds b1, Bounds b2) {
        double minX = Math.min(b1.getMinX(), b2.getMinX());
        double minY = Math.min(b1.getMinY(), b2.getMinY());
        double maxX = Math.max(b1.getMaxX(), b2.getMaxX());
        double maxY = Math.max(b1.getMaxY(), b2.getMaxY());
        return new Bounds(minX, minY, maxX - minX, maxY - minY,) {
            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Point2D p) {
                return false;
            }

            @Override
            public boolean contains(Point3D p) {
                return false;
            }

            @Override
            public boolean contains(double x, double y) {
                return false;
            }

            @Override
            public boolean contains(double x, double y, double z) {
                return false;
            }

            @Override
            public boolean contains(Bounds b) {
                return false;
            }

            @Override
            public boolean contains(double x, double y, double w, double h) {
                return false;
            }

            @Override
            public boolean contains(double x, double y, double z, double w, double h, double d) {
                return false;
            }

            @Override
            public boolean intersects(Bounds b) {
                return false;
            }

            @Override
            public boolean intersects(double x, double y, double w, double h) {
                return false;
            }

            @Override
            public boolean intersects(double x, double y, double z, double w, double h, double d) {
                return false;
            }
        };
    }

    // Memorizza le proprietà iniziali di tutte le forme selezionate per il ridimensionamento e lo spostamento
    private void storeInitialShapeProperties() {
        initialShapeX.clear();
        initialShapeY.clear();
        initialShapeWidth.clear();
        initialShapeHeight.clear();
        initialShapeRotate.clear();
        initialLineStart.clear();
        initialLineEnd.clear();

        for (Shape shape : selectedJavaFxShapes) {
            // Assicurati che le traslazioni siano "cotte" nella posizione base prima di memorizzare
            bakeTransformations(shape);
            initialShapeRotate.put(shape, shape.getRotate());

            if (shape instanceof Rectangle rect) {
                initialShapeX.put(shape, rect.getX());
                initialShapeY.put(shape, rect.getY());
                initialShapeWidth.put(shape, rect.getWidth());
                initialShapeHeight.put(shape, rect.getHeight());
            } else if (shape instanceof Ellipse ellipse) {
                // Memorizza i bounds della forma per coerenza con Rect
                Bounds b = ellipse.getLayoutBounds(); // LayoutBounds non include rotation/translate
                // Ottieni bounds in parent per la posizione reale se non hai bakeTranslation
                // Oppure gestisci centerX/Y e radiusX/Y in modo specifico
                initialShapeX.put(shape, ellipse.getCenterX());
                initialShapeY.put(shape, ellipse.getCenterY());
                initialShapeWidth.put(shape, ellipse.getRadiusX() * 2);
                initialShapeHeight.put(shape, ellipse.getRadiusY() * 2);
            } else if (shape instanceof Line line) {
                initialLineStart.put(line, new Point2D(line.getStartX(), line.getStartY()));
                initialLineEnd.put(line, new Point2D(line.getEndX(), line.getEndY()));
            }
        }
    }

    // Trova l'handle di ridimensionamento/rotazione del gruppo sotto il mouse
    private Circle findGroupHandleAt(double x, double y) {
        // Controlla gli handle di ridimensionamento
        for (Circle handle : groupResizeHandles) {
            if (handle.isVisible() && handle.getBoundsInParent().contains(x, y)) {
                return handle;
            }
        }
        // Controlla l'handle di rotazione
        if (groupRotationHandle != null && groupRotationHandle.isVisible() && groupRotationHandle.getBoundsInParent().contains(x, y)) {
            return groupRotationHandle;
        }
        return null;
    }

    // Trova l'handle di ridimensionamento/rotazione di una SINGOLA forma sotto il mouse
    private Circle findIndividualHandleAt(double x, double y) {
        if (selectedJavaFxShapes.size() != 1) return null; // Solo se c'è una singola selezione
        SelectionDecorator decorator = activeDecorators.get(selectedJavaFxShapes.get(0));
        if (decorator == null) return null;

        for (Circle handle : decorator.getResizeHandles()) {
            if (handle.isVisible() && handle.getBoundsInParent().contains(x, y)) {
                return handle;
            }
        }
        return null;
    }


    // Calcola l'angolo per la rotazione del gruppo o della singola forma
    private double calculateAngle(double x, double y, Shape shape) {
        Bounds bounds = shape.getBoundsInParent(); // Usa bounds in parent per la posizione con rotazione
        double centerX = bounds.getMinX() + bounds.getWidth() / 2;
        double centerY = bounds.getMinY() + bounds.getHeight() / 2;
        return Math.toDegrees(Math.atan2(y - centerY, x - centerX));
    }

    // Calcola l'angolo per la rotazione del gruppo
    private double calculateAngleForGroup(double x, double y) {
        Bounds groupBounds = calculateGroupBoundsInParent();
        if (groupBounds == null) return 0;
        double centerX = groupBounds.getMinX() + groupBounds.getWidth() / 2;
        double centerY = groupBounds.getMinY() + groupBounds.getHeight() / 2;
        return Math.toDegrees(Math.atan2(y - centerY, x - centerX));
    }

    // Questo metodo "cuoce" le traslazioni e rotazioni nella posizione base della forma
    // È fondamentale prima di convertire in MyShape o di salvare le proprietà iniziali
    private void bakeTransformations(Shape shape) {
        if (shape == null) return;

        // "Bake" la traslazione
        double tx = shape.getTranslateX();
        double ty = shape.getTranslateY();
        if (tx != 0 || ty != 0) {
            if (shape instanceof Rectangle r) {
                r.setX(r.getX() + tx); r.setY(r.getY() + ty);
            } else if (shape instanceof Ellipse e) {
                e.setCenterX(e.getCenterX() + tx); e.setCenterY(e.getCenterY() + ty);
            } else if (shape instanceof Line l) {
                l.setStartX(l.getStartX() + tx); l.setStartY(l.getStartY() + ty);
                l.setEndX(l.getEndX() + tx); l.setEndY(l.getEndY() + ty);
            }
            shape.setTranslateX(0);
            shape.setTranslateY(0);
        }

        // Se la forma ha una rotazione, anche questa deve essere "cotta"
        // Questo è più complesso e di solito richiede di creare un nuovo nodo senza rotazione e riposizionarlo
        // O di modificare i punti della forma. Per ora, supponiamo che la rotazione sia sempre gestita
        // sulla proprietà `rotate` e convertita/riconvertita dagli adapter.
        // Se la rotazione deve influenzare X/Y/W/H della forma stessa, è un'altra complessità.
        // Per il momento, manteniamo la rotazione sulla proprietà `rotate` per le forme JavaFX.
    }

    // Aggiorna i decoratori (individuali o di gruppo)
    private void updateGroupDecorations() {
        // Rimuovi eventuali handle di gruppo precedenti
        removeGroupDecorations();

        // Rimuovi i decoratori individuali prima di decidere cosa mostrare
        for (SelectionDecorator decorator : activeDecorators.values()) {
            decorator.removeDecoration();
        }
        activeDecorators.clear(); // Clear the map as we'll re-populate or use group handles

        if (selectedJavaFxShapes.isEmpty()) {
            // Nessuna selezione, niente decorazioni
            return;
        }

        if (selectedJavaFxShapes.size() == 1) {
            // Selezione singola: usa il decoratore individuale
            Shape s = selectedJavaFxShapes.get(0);
            SelectionDecorator decorator = new SelectionDecorator(s);
            decorator.applyDecoration();
            activeDecorators.put(s, decorator); // Aggiungi al map per gestione futura
            currentShapeBeingTransformed = s; // Imposta per le operazioni individuali
            return;
        }

        // Selezione multipla (gruppo logico): Disegna gli handle di gruppo
        Bounds groupBounds = calculateGroupBoundsInParent();
        if (groupBounds == null) return;

        // Handle di rotazione per il gruppo
        double rotationHandleOffsetX = groupBounds.getMinX() + groupBounds.getWidth() / 2;
        double rotationHandleOffsetY = groupBounds.getMinY() - 15; // Offset sopra il centro superiore

        groupRotationHandle = new Circle(rotationHandleOffsetX, rotationHandleOffsetY, 4, Color.DARKORANGE); // Raggio 4
        groupRotationHandle.setStroke(Color.WHITE);
        groupRotationHandle.setStrokeWidth(1);
        groupRotationHandle.setUserData("ROTATION");
        drawingPane.getChildren().add(groupRotationHandle);
        groupRotationHandle.toFront();

        // Handle di ridimensionamento del gruppo
        groupResizeHandles.clear(); // Assicurati che sia vuoto prima di aggiungere
        String[] handleTypes = {
                "NORTH_WEST", "NORTH", "NORTH_EAST",
                "WEST", "EAST",
                "SOUTH_WEST", "SOUTH", "SOUTH_EAST"
        };

        Point2D[] localHandlePositions = {
                new Point2D(groupBounds.getMinX(), groupBounds.getMinY()),
                new Point2D(groupBounds.getMinX() + groupBounds.getWidth() / 2, groupBounds.getMinY()),
                new Point2D(groupBounds.getMinX() + groupBounds.getWidth(), groupBounds.getMinY()),

                new Point2D(groupBounds.getMinX(), groupBounds.getMinY() + groupBounds.getHeight() / 2),
                new Point2D(groupBounds.getMinX() + groupBounds.getWidth(), groupBounds.getMinY() + groupBounds.getHeight() / 2),

                new Point2D(groupBounds.getMinX(), groupBounds.getMinY() + groupBounds.getHeight()),
                new Point2D(groupBounds.getMinX() + groupBounds.getWidth() / 2, groupBounds.getMinY() + groupBounds.getHeight()),
                new Point2D(groupBounds.getMinX() + groupBounds.getWidth(), groupBounds.getMinY() + groupBounds.getHeight())
        };

        for (int i = 0; i < handleTypes.length; i++) {
            Point2D localPos = localHandlePositions[i];
            Circle handle = new Circle(localPos.getX(), localPos.getY(), 4, Color.BLUE); // Raggio 4
            handle.setStroke(Color.WHITE);
            handle.setStrokeWidth(1);
            handle.setUserData(handleTypes[i]);
            groupResizeHandles.add(handle);
            drawingPane.getChildren().add(handle);
            handle.toFront();
        }
    }

    // Rimuove gli handle di gruppo dalla pane
    private void removeGroupDecorations() {
        if (groupRotationHandle != null) {
            drawingPane.getChildren().remove(groupRotationHandle);
            groupRotationHandle = null;
        }
        if (!groupResizeHandles.isEmpty()) {
            drawingPane.getChildren().removeAll(groupResizeHandles);
            groupResizeHandles.clear();
        }
    }



















    //NUOVA LOGICA
    @Override
    public void handleMousePressed(MouseEvent event) {
        Point2D localPoint = getTransformedCoordinates(event, drawingPane);
        double x = localPoint.getX();
        double y = localPoint.getY();

        // Reset dei flag di stato
        isGroupMoving = false;
        isGroupResizing = false;
        isGroupRotating = false;
        isIndividualResizing = false;
        activeHandleType = ResizeHandleType.NONE; // Reset handle type

        // --- Gestione click destro (menu contestuale) ---
        if (event.getButton() == MouseButton.SECONDARY) {
            Shape shapeAtPosition = findShapeAt(x, y);
            if (shapeAtPosition != null) {
                // Se clicco con il destro su una forma NON selezionata, la seleziono SOLO quella
                if (!selectedJavaFxShapes.contains(shapeAtPosition)) {
                    manageSelection(shapeAtPosition, event); // Seleziona solo questa
                }
                // Se la forma è già selezionata (anche se parte di una selezione multipla),
                // mantieni la selezione esistente e apri il menu contestuale.
            } else {
                // Click destro sullo sfondo: deseleziona tutto
                clearSelection();
            }
            callback.onSelectionMenuOpened(event.getScreenX(), event.getScreenY());
            event.consume();
            return;
        }

        // --- Gestione click sinistro (selezione e trasformazioni) ---
        if (event.getButton() == MouseButton.PRIMARY) {

            // 1. Cerca handle di ridimensionamento/rotazione del GRUPPO
            Circle handleAtPosition = findGroupHandleAt(x, y);
            if (handleAtPosition != null) {
                String handleTypeStr = (String) handleAtPosition.getUserData();
                try {
                    activeHandleType = ResizeHandleType.valueOf(handleTypeStr);
                } catch (IllegalArgumentException e) {
                    activeHandleType = ResizeHandleType.NONE;
                }

                if (activeHandleType == ResizeHandleType.ROTATION) {
                    isGroupRotating = true;
                    lastAngle = calculateAngleForGroup(x, y);
                } else { // Ridimensionamento del gruppo
                    isGroupResizing = true;
                    initialGroupBounds = calculateGroupBoundsInParent(); // Calcola bounds del gruppo
                }
                initialMousePressForGroup = new Point2D(x, y); // Punto iniziale per operazioni di gruppo
                storeInitialShapeProperties(); // Memorizza le proprietà iniziali di tutte le forme selezionate
                drawingPane.setCursor(getCursorForHandle(activeHandleType)); // Metodo per ottenere il cursore corretto
                event.consume();
                return;
            }

            // 2. Cerca handle di ridimensionamento/rotazione INDIVIDUALE (solo se c'è UNA sola forma selezionata)
            handleAtPosition = findIndividualHandleAt(x, y);
            if (handleAtPosition != null) {
                String handleTypeStr = (String) handleAtPosition.getUserData();
                try {
                    activeHandleType = ResizeHandleType.valueOf(handleTypeStr);
                } catch (IllegalArgumentException e) {
                    activeHandleType = ResizeHandleType.NONE;
                }

                if (activeHandleType == ResizeHandleType.ROTATION) {
                    isRotating = true; // flag per rotazione individuale
                    lastAngle = calculateAngle(x, y, currentShapeBeingTransformed);
                } else { // Ridimensionamento individuale
                    isIndividualResizing = true;
                }
                lastXForIndividualResize = x; // Per delta X/Y nel resize individuale
                lastYForIndividualResize = y; // Per delta X/Y nel resize individuale
                drawingPane.setCursor(getCursorForHandle(activeHandleType));
                event.consume();
                return;
            }

            // 3. Cerca forma sotto il mouse (per selezione o spostamento)
            Shape shapeAtPosition = findShapeAt(x, y);
            if (shapeAtPosition != null) {
                manageSelection(shapeAtPosition, event); // Gestisce la selezione singola/multipla

                // Se ci sono forme selezionate dopo la gestione della selezione, prepara lo spostamento
                if (!selectedJavaFxShapes.isEmpty()) {
                    isGroupMoving = true; // Ora lo spostamento è sempre di gruppo (anche per selezione singola)
                    initialMousePressForGroup = new Point2D(x, y); // Punto iniziale del click
                    storeInitialShapeProperties(); // Memorizza le posizioni iniziali di tutte le forme selezionate
                    drawingPane.setCursor(Cursor.MOVE);
                }
                event.consume();
                return;
            }

            // 4. Click sullo sfondo: deseleziona tutto
            clearSelection();
            event.consume();
        }
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        Point2D localPoint = getTransformedCoordinates(event, drawingPane);
        double x = localPoint.getX();
        double y = localPoint.getY();

        if (selectedJavaFxShapes.isEmpty()) return; // Niente da fare se non c'è selezione

        // 1. Rotazione INDIVIDUALE (se una singola forma è selezionata e si ruota l'handle)
        if (isRotating && currentShapeBeingTransformed != null) { // isRotating è per la singola forma
            double currentAngle = calculateAngle(x, y, currentShapeBeingTransformed);
            double deltaAngle = currentAngle - lastAngle;
            currentShapeBeingTransformed.setRotate(currentShapeBeingTransformed.getRotate() + deltaAngle);
            lastAngle = currentAngle;
            // Aggiorna la decorazione della singola forma (o ricrea l'handle se necessario)
            updateGroupDecorations(); // Questo aggiornerà anche gli handle individuali
            event.consume();
            return;
        }

        // 2. Ridimensionamento INDIVIDUALE (se una singola forma è selezionata e si ridimensiona l'handle)
        if (isIndividualResizing && currentShapeBeingTransformed != null && activeHandleType != ResizeHandleType.NONE && activeHandleType != ResizeHandleType.ROTATION) {
            double deltaX = x - lastXForIndividualResize;
            double deltaY = y - lastYForIndividualResize;

            double minSize = 5;

            if (currentShapeBeingTransformed instanceof Rectangle rect) {
                double currentX = rect.getX(); double currentY = rect.getY();
                double currentWidth = rect.getWidth(); double currentHeight = rect.getHeight();
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

                if (newWidth < minSize) { if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.SOUTH_WEST || activeHandleType == ResizeHandleType.WEST) newX += (newWidth - minSize); newWidth = minSize; }
                if (newHeight < minSize) { if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.NORTH_EAST || activeHandleType == ResizeHandleType.NORTH) newY += (newHeight - minSize); newHeight = minSize; }
                rect.setX(newX); rect.setY(newY); rect.setWidth(newWidth); rect.setHeight(newHeight);

            } else if (currentShapeBeingTransformed instanceof Ellipse ellipse) {
                double currentCenterX = ellipse.getCenterX(); double currentCenterY = ellipse.getCenterY();
                double currentRadiusX = ellipse.getRadiusX(); double currentRadiusY = ellipse.getRadiusY();
                double newX = currentCenterX - currentRadiusX; double newY = currentCenterY - currentRadiusY;
                double newWidth = currentRadiusX * 2; double newHeight = currentRadiusY * 2;

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

                if (newWidth < minSize) { if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.SOUTH_WEST || activeHandleType == ResizeHandleType.WEST) newX += (newWidth - minSize); newWidth = minSize; }
                if (newHeight < minSize) { if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.NORTH_EAST || activeHandleType == ResizeHandleType.NORTH) newY += (newHeight - minSize); newHeight = minSize; }
                ellipse.setCenterX(newX + newWidth / 2); ellipse.setCenterY(newY + newHeight / 2);
                ellipse.setRadiusX(newWidth / 2); ellipse.setRadiusY(newHeight / 2);

            } else if (currentShapeBeingTransformed instanceof Line line) {
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
            lastXForIndividualResize = x; lastYForIndividualResize = y;
            updateGroupDecorations(); // Questo aggiornerà anche gli handle individuali
            event.consume();
            return;
        }

        // 3. Rotazione di GRUPPO
        if (isGroupRotating && !selectedJavaFxShapes.isEmpty()) {
            double currentAngle = calculateAngleForGroup(x, y);
            double deltaAngle = currentAngle - lastAngle;

            // Il centro di rotazione è il centro del bounding box del gruppo
            Bounds groupBounds = calculateGroupBoundsInParent(); // Ricalcola per sicurezza
            double centerX = groupBounds.getMinX() + groupBounds.getWidth() / 2;
            double centerY = groupBounds.getMinY() + groupBounds.getHeight() / 2;

            for (Shape shape : selectedJavaFxShapes) {
                // Applica la rotazione rispetto al centro del gruppo
                double currentShapeRotate = initialShapeRotate.getOrDefault(shape, 0.0);
                shape.setRotate(currentShapeRotate + deltaAngle);
            }
            lastAngle = currentAngle;
            updateGroupDecorations(); // Aggiorna handle e bordo del gruppo
            event.consume();
            return;
        }

        // 4. Ridimensionamento di GRUPPO
        if (isGroupResizing && activeHandleType != ResizeHandleType.NONE && !selectedJavaFxShapes.isEmpty()) {
            double originalX = initialGroupBounds.getMinX();
            double originalY = initialGroupBounds.getMinY();
            double originalWidth = initialGroupBounds.getWidth();
            double originalHeight = initialGroupBounds.getHeight();

            double newX = originalX, newY = originalY, newWidth = originalWidth, newHeight = originalHeight;
            double minGroupSize = 5; // Dimensione minima per il gruppo

            double deltaX = x - initialMousePressForGroup.getX();
            double deltaY = y - initialMousePressForGroup.getY();

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

            // Limita la dimensione minima del gruppo
            if (newWidth < minGroupSize) {
                if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.SOUTH_WEST || activeHandleType == ResizeHandleType.WEST) newX += (newWidth - minGroupSize);
                newWidth = minGroupSize;
            }
            if (newHeight < minGroupSize) {
                if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.NORTH_EAST || activeHandleType == ResizeHandleType.NORTH) newY += (newHeight - minGroupSize);
                newHeight = minGroupSize;
            }

            // Calcola i fattori di scala per il gruppo
            double scaleX = (originalWidth > 0) ? newWidth / originalWidth : 1;
            double scaleY = (originalHeight > 0) ? newHeight / originalHeight : 1;

            for (Shape shape : selectedJavaFxShapes) {
                double initialShapeLocalX, initialShapeLocalY, initialShapeLocalWidth, initialShapeLocalHeight;

                if (shape instanceof Rectangle rect) {
                    initialShapeLocalX = initialShapeX.get(rect);
                    initialShapeLocalY = initialShapeY.get(rect);
                    initialShapeLocalWidth = initialShapeWidth.get(rect);
                    initialShapeLocalHeight = initialShapeHeight.get(rect);

                    rect.setX(newX + (initialShapeLocalX - originalX) * scaleX);
                    rect.setY(newY + (initialShapeLocalY - originalY) * scaleY);
                    rect.setWidth(initialShapeLocalWidth * scaleX);
                    rect.setHeight(initialShapeLocalHeight * scaleY);
                } else if (shape instanceof Ellipse ellipse) {
                    // Per le ellissi, le coordinate iniziali memorizzate erano centerX/Y e 2*RadiusX/Y
                    double initialEllipseCenterX = initialShapeX.get(ellipse);
                    double initialEllipseCenterY = initialShapeY.get(ellipse);
                    double initialEllipseRadiusX = initialShapeWidth.get(ellipse) / 2;
                    double initialEllipseRadiusY = initialShapeHeight.get(ellipse) / 2;

                    // Calcola la posizione del centro rispetto all'origine del bounding box del gruppo
                    double relativeCenterX = initialEllipseCenterX - originalX;
                    double relativeCenterY = initialEllipseCenterY - originalY;

                    // Applica la scala relativa e la nuova posizione del gruppo
                    ellipse.setCenterX(newX + relativeCenterX * scaleX);
                    ellipse.setCenterY(newY + relativeCenterY * scaleY);
                    ellipse.setRadiusX(initialEllipseRadiusX * scaleX);
                    ellipse.setRadiusY(initialEllipseRadiusY * scaleY);
                } else if (shape instanceof Line line) {
                    Point2D initialStart = initialLineStart.get(line);
                    Point2D initialEnd = initialLineEnd.get(line);

                    if (initialStart != null && initialEnd != null) {
                        double relStartX = initialStart.getX() - originalX;
                        double relStartY = initialStart.getY() - originalY;
                        double relEndX = initialEnd.getX() - originalX;
                        double relEndY = initialEnd.getY() - originalY;

                        line.setStartX(newX + relStartX * scaleX);
                        line.setStartY(newY + relStartY * scaleY);
                        line.setEndX(newX + relEndX * scaleX);
                        line.setEndY(newY + relEndY * scaleY);
                    }
                }
            }
            updateGroupDecorations(); // Aggiorna handle del gruppo
            event.consume();
            return;
        }

        // 5. Spostamento di GRUPPO (anche se selezione singola)
        if (isGroupMoving && !selectedJavaFxShapes.isEmpty()) {
            double deltaX = x - initialMousePressForGroup.getX();
            double deltaY = y - initialMousePressForGroup.getY();

            for (Shape shape : selectedJavaFxShapes) {
                // Recupera le posizioni iniziali per ogni forma
                if (shape instanceof Line line) {
                    line.setStartX(initialLineStart.get(line).getX() + deltaX);
                    line.setStartY(initialLineStart.get(line).getY() + deltaY);
                    line.setEndX(initialLineEnd.get(line).getX() + deltaX);
                    line.setEndY(initialLineEnd.get(line).getY() + deltaY);
                } else if (shape instanceof Rectangle rect) {
                    rect.setX(initialShapeX.get(rect) + deltaX);
                    rect.setY(initialShapeY.get(rect) + deltaY);
                } else if (shape instanceof Ellipse ellipse) {
                    ellipse.setCenterX(initialShapeX.get(ellipse) + deltaX);
                    ellipse.setCenterY(initialShapeY.get(ellipse) + deltaY);
                }
            }
            updateGroupDecorations(); // Aggiorna i decoratori dopo lo spostamento
            event.consume();
        }
    }


    @Override
    public void handleMouseMoved(MouseEvent event) {
        // Se c'è un'operazione attiva, mantiene il cursore specifico
        if (isGroupMoving) { drawingPane.setCursor(Cursor.MOVE); return; }
        if (isGroupRotating) { drawingPane.setCursor(Cursor.CROSSHAIR); return; }
        if (isGroupResizing) { drawingPane.setCursor(getCursorForHandle(activeHandleType)); return; }

        if (isIndividualResizing) { drawingPane.setCursor(getCursorForHandle(activeHandleType)); return; }
        if (isRotating) { drawingPane.setCursor(Cursor.CROSSHAIR); return; }


        Point2D localPoint = getTransformedCoordinates(event, drawingPane);
        double x = localPoint.getX();
        double y = localPoint.getY();

        // 1. Cerca handle di gruppo
        Circle handleAtPosition = findGroupHandleAt(x, y);
        if (handleAtPosition != null) {
            String handleType = (String) handleAtPosition.getUserData();
            drawingPane.setCursor(getCursorForHandle(ResizeHandleType.valueOf(handleType)));
            return;
        }

        // 2. Cerca handle individuali (solo se c'è una singola forma selezionata)
        handleAtPosition = findIndividualHandleAt(x, y);
        if (handleAtPosition != null) {
            String handleType = (String) handleAtPosition.getUserData();
            drawingPane.setCursor(getCursorForHandle(ResizeHandleType.valueOf(handleType)));
            return;
        }

        // 3. Cerca forma sotto il mouse (per spostamento)
        Shape shapeAtPos = findShapeAt(x, y);
        drawingPane.setCursor(shapeAtPos != null ? Cursor.MOVE : Cursor.DEFAULT);
    }

    @Override
    public void handleBorderColorChange(Color color) {
        if (!selectedJavaFxShapes.isEmpty()) {
            List<Shape> oldStates = new ArrayList<>();
            List<Shape> newStates = new ArrayList<>();

            for (Shape shape : selectedJavaFxShapes) {
                    oldStates.add(shape);


                shape.setStroke(color);
                // "Cuocere" le trasformazioni prima della conversione nel modello
                bakeTransformations(shape);
            }

            callback.onModifyGroup(oldStates, newStates);
        }
    }

    @Override
    public void handleFillColorChange(Color color) {
        if (!selectedJavaFxShapes.isEmpty()) {
            List<Shape> oldStates = new ArrayList<>();
            List<Shape> newStates = new ArrayList<>();

            for (Shape shape : selectedJavaFxShapes) {
                oldStates.add(shape);


                if (!(shape instanceof Line)) { // Le linee non hanno riempimento
                    shape.setFill(color);
                }
                // "Cuocere" le trasformazioni prima della conversione nel modello
                bakeTransformations(shape);
                newStates.add(shape);
            }
            callback.onModifyGroup(oldStates, newStates);

        }
    }


    public void handleCopy(Event event) {
        if (!selectedJavaFxShapes.isEmpty()) {
            List<Shape> shapesToCopy = new ArrayList<>();
            for (Shape fxShape : selectedJavaFxShapes) {
                bakeTransformations(fxShape); // Cuocere le trasformazioni prima della copia
            }
            callback.onCopyShapes(shapesToCopy); // Nuovo callback
        }
    }

    @Override
    public void handleCut(Event event) {
        if (!selectedJavaFxShapes.isEmpty()) {

            callback.onCutShapes(selectedJavaFxShapes); // Nuovo callback
        }
    }

    @Override
    public void handleDelete(Event event) {
            callback.onDeleteShapes(selectedJavaFxShapes); // Nuovo callback
        }


    // Le operazioni Z-order (BringToFront, ecc.) devono agire su TUTTE le forme selezionate.
    // L'ordine in cui le si applica è importante per l'undo/redo.
    // Suggerimento: un comando per ogni forma, o un comando di gruppo che le ordina in base alla posizione originale
    // Forse il tuo MainController già gestisce questo tramite onBringToFront etc. per singola forma.
    // Se è così, puoi iterare. Se ha bisogno di un comando di gruppo Z-order, allora modifichiamo il callback.
    // Per ora, iteriamo e chiamiamo il callback individuale.
    @Override
    public void handleBringToFront(ActionEvent actionEvent) {
        if (!selectedJavaFxShapes.isEmpty()) {
            for (Shape shape : selectedJavaFxShapes) {
                callback.onBringToFront(shape);
            }
        }
    }

    @Override
    public void handleBringToTop(ActionEvent actionEvent) {
        if (!selectedJavaFxShapes.isEmpty()) {
            for (Shape shape : selectedJavaFxShapes) {
                callback.onBringToTop(shape);
            }
        }
    }

    @Override
    public void handleSendToBack(ActionEvent actionEvent) {
        if (!selectedJavaFxShapes.isEmpty()) {
            for (Shape shape : selectedJavaFxShapes) {
                callback.onSendToBack(shape);
            }
        }
    }

    @Override
    public void handleSendToBottom(ActionEvent actionEvent) {
        if (!selectedJavaFxShapes.isEmpty()) {
            for (Shape shape : selectedJavaFxShapes) {
                callback.onSendToBottom(shape);
            }
        }
    }





    // Metodo helper per ottenere il cursore corretto per l'handle
    private Cursor getCursorForHandle(SelectionToolStrategy.ResizeHandleType type) {
        return switch (type) {
            case ROTATION -> Cursor.CROSSHAIR;
            case NORTH_WEST, SOUTH_EAST -> Cursor.NW_RESIZE;
            case NORTH_EAST, SOUTH_WEST -> Cursor.NE_RESIZE;
            case NORTH, SOUTH -> Cursor.V_RESIZE;
            case EAST, WEST -> Cursor.H_RESIZE;
            default -> Cursor.HAND;
        };
    }


    private Shape findShapeAt(double x, double y) {
        List<javafx.scene.Node> children = drawingPane.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            javafx.scene.Node node = children.get(i);
            // Ignora gli handle individuali
            if (activeDecorators.values().stream().anyMatch(d -> d.getResizeHandles().contains(node))) {
                continue;
            }
            // Ignora gli handle di gruppo
            if (groupResizeHandles.contains(node) || node == groupRotationHandle) {
                continue;
            }
            if (node instanceof Shape shape) {
                // Non considerare le forme che sono già selezionate se stiamo cercando una nuova forma
                // Questa è una logica per la selezione MARQUEE, non per il click.
                // Per il click: se è una forma selezionata, il clic su di essa dovrebbe preparare al drag o deselezionare.
                // Quindi, non ignorare le forme selezionate qui.
                if (shape.isVisible() && shape.contains(x, y)) {
                    return shape;
                }
            }
        }
        return null;
    }


    public void selectShapeByModel(MyShape shape) {
        Shape javafxShape = shapeMapping.getViewShape(shape);
        if (javafxShape != null) {
            clearSelection(); // Deseleziona tutto prima
            addShapeToSelection(javafxShape); // Aggiungi la singola forma
            callback.onShapesSelected(selectedJavaFxShapes); // Notifica la selezione
        }
    }

}



