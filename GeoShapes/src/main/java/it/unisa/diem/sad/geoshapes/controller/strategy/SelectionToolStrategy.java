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

/**
 * Implements the {@link ToolStrategy} for selecting, moving, resizing, and rotating shapes.
 * This strategy handles complex mouse and keyboard interactions to manipulate existing shapes
 * on the drawing area. It uses {@link SelectionShapeDecorator} to provide visual feedback
 * and interactive handles for selected shapes.
 */
public class SelectionToolStrategy implements ToolStrategy {

    // Private instance variables
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

    /**
     * Constructs a new {@code SelectionToolStrategy}.
     *
     * @param drawingArea The {@link Pane} where shapes are displayed and interactions occur.
     * @param shapeMapping The {@link ShapeMapping} instance for converting between JavaFX and model shapes.
     * @param callback The {@link InteractionCallback} to notify about selection changes and shape modifications.
     */
    public SelectionToolStrategy(Pane drawingArea, ShapeMapping shapeMapping, InteractionCallback callback) {
        this.drawingArea = drawingArea;
        this.shapeMapping = shapeMapping;
        this.callback = callback;
    }

    /**
     * Activates the selection tool.
     * This tool does not use the color or size parameters directly upon activation,
     * as its primary function is interaction with existing shapes.
     *
     * @param lineBorderColor The current border color for lines (ignored).
     * @param rectangleBorderColor The current border color for rectangles (ignored).
     * @param rectangleFillColor The current fill color for rectangles (ignored).
     * @param ellipseBorderColor The current border color for ellipses (ignored).
     * @param ellipseFillColor The current fill color for ellipses (ignored).
     * @param polygonBorderColor The current border color for polygons (ignored).
     * @param polygonFillColor The current fill color for polygons (ignored).
     * @param textBorderColor The current border color for text shapes (ignored).
     * @param textFillColor The current fill color for text shapes (ignored).
     * @param textColor The current text color for text shapes (ignored).
     * @param polygonVertices The number of vertices for polygons (ignored).
     * @param regularPolygon A boolean indicating if the polygon should be regular (ignored).
     * @param fontSize The font size for text shapes (ignored).
     */
    @Override
    public void activate(Color lineBorderColor, Color rectangleBorderColor, Color rectangleFillColor,
                         Color ellipseBorderColor, Color ellipseFillColor, Color polygonBorderColor,
                         Color polygonFillColor, Color textBorderColor, Color textFillColor,
                         Color textColor, int polygonVertices, boolean regularPolygon, int fontSize) {
        // No specific action needed upon activation as this tool operates on existing shapes.
    }

    /**
     * Handles a change in the border color from a UI control (e.g., properties panel or context menu).
     * Applies the new border color to all currently selected shapes and updates their decorators.
     *
     * @param color The new {@link Color} for the border.
     */
    @Override
    public void handleBorderColorChange(Color color) {
        if (selectedJavaFxShapes != null && !selectedJavaFxShapes.isEmpty()) {
            for (Shape shape : selectedJavaFxShapes) {
                shape.setStroke(color); // Apply the new color directly
            }
            this.callback.onModifyShapes(selectedJavaFxShapes); // Notify model about modification

            // Update the original color stored in decorators for correct restoration upon deselection
            for (Shape shape : selectedJavaFxShapes) {
                SelectionShapeDecorator decorator = decorators.get(shape);
                if (decorator != null) {
                    decorator.updateOriginalStrokeColor(color);
                }
            }
        }
    }

    /**
     * Handles a change in the fill color from a UI control.
     * Applies the new fill color to all currently selected shapes and updates their decorators.
     *
     * @param color The new {@link Color} for the fill.
     */
    @Override
    public void handleFillColorChange(Color color) {
        if (selectedJavaFxShapes != null && !selectedJavaFxShapes.isEmpty()) {
            // Temporarily remove decorations to avoid visual glitches during color change
            for (Shape shape : selectedJavaFxShapes) {
                SelectionShapeDecorator decorator = decorators.get(shape);
                if (decorator != null) {
                    decorator.removeDecoration();
                }
            }

            // Apply the new fill color
            for (Shape shape : selectedJavaFxShapes) {
                shape.setFill(color);
            }

            this.callback.onModifyShapes(selectedJavaFxShapes); // Notify model about modification

            // Reapply decorations with updated properties
            for (Shape shape : selectedJavaFxShapes) {
                SelectionShapeDecorator decorator = decorators.get(shape);
                if (decorator != null) {
                    decorator.applyDecoration();
                }
            }
        }
    }

    /**
     * Handles a text color change from a context menu or global property panel.
     * This implementation does nothing, as text editing is handled by a separate tool.
     *
     * @param color The new {@link Color} for the text.
     */
    @Override
    public void handleTextColorMenuChange(Color color) {
        // Not applicable for selection tool
    }

    /**
     * Handles a font size change from a context menu or global property panel.
     * This implementation does nothing, as text editing is handled by a separate tool.
     *
     * @param fontSize The new font size.
     */
    @Override
    public void handleFontSizeMenuChange(int fontSize) {
        // Not applicable for selection tool
    }

    /**
     * Returns a list of shapes currently selected by this tool.
     *
     * @return A {@code List} of selected JavaFX {@link Shape} objects.
     */
    @Override
    public List<Shape> getSelectedShapes() {
        return new ArrayList<>(selectedJavaFxShapes);
    }

    /**
     * Handles the mouse pressed event.
     * Determines if a shape or a resize/rotation handle was clicked.
     * Manages selection (single, multiple with Ctrl), initiates moving, resizing, or rotating.
     * Also handles right-click to open a context menu.
     *
     * @param event The {@link MouseEvent} generated by the mouse press.
     */
    @Override
    public void handleMousePressed(MouseEvent event) {
        Point2D localPoint = drawingArea.sceneToLocal(event.getSceneX(), event.getSceneY());
        double x = localPoint.getX();
        double y = localPoint.getY();

        if (event.getButton() == MouseButton.SECONDARY) { // Right-click for context menu
            Shape shapeAtPosition = findShapeAt(x, y);
            if (shapeAtPosition != null && !selectedJavaFxShapes.contains(shapeAtPosition)) {
                if (!event.isControlDown()) {
                    clearSelection(); // Clear selection if Ctrl is not pressed
                }
                addShapeToSelection(shapeAtPosition); // Add shape to selection if not already selected
            }
            callback.onSelectionMenuOpened(event.getScreenX(), event.getScreenY()); // Open context menu
            event.consume(); // Consume event to prevent further processing
            return;
        }

        if (event.getButton() == MouseButton.PRIMARY) { // Left-click for selection/manipulation
            Circle handleAtPosition = findHandleAt(x, y);
            if (handleAtPosition != null && !selectedJavaFxShapes.isEmpty()) {
                String handleTypeStr = (String) handleAtPosition.getUserData();
                try {
                    activeHandleType = ResizeHandleType.valueOf(handleTypeStr); // Determine handle type
                } catch (IllegalArgumentException e) {
                    activeHandleType = ResizeHandleType.NONE;
                }

                if (activeHandleType == ResizeHandleType.ROTATION) {
                    isRotating = true;
                    // Use primary selected shape for rotation if available, otherwise the first selected
                    currentShapeBeingRotated = primarySelectedShape != null ? primarySelectedShape : selectedJavaFxShapes.get(0);
                    lastAngle = calculateAngle(x, y, currentShapeBeingRotated); // Calculate initial angle
                    drawingArea.setCursor(Cursor.CROSSHAIR); // Change cursor for rotation
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
                if (event.isControlDown()) { // Ctrl-click for multi-selection
                    if (selectedJavaFxShapes.contains(shapeAtPosition)) {
                        removeShapeFromSelection(shapeAtPosition);
                    } else {
                        addShapeToSelection(shapeAtPosition);
                    }
                } else { // Single selection
                    if (!selectedJavaFxShapes.contains(shapeAtPosition)) {
                        clearSelection(); // Clear previous selection
                        // This line below removes from decorator map, but decorator should be removed by clearSelection or removeShapeFromSelection already.
                        // Keeping it for now as per "Do not change any logic or functionality"
                        decorators.remove(shapeAtPosition);
                        addShapeToSelection(shapeAtPosition);
                    }
                    setPrimarySelectedShape(shapeAtPosition); // Set the clicked shape as primary
                }

                if (!selectedJavaFxShapes.isEmpty()) {
                    isMoving = true; // Initiate moving
                    initialMousePress = new Point2D(x, y);
                    initialTranslations.clear();
                    initialLinePositions.clear();

                    // Store initial positions for all selected shapes for smooth dragging
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
                    drawingArea.setCursor(Cursor.MOVE); // Change cursor for moving
                }
                event.consume();
                return;
            }

            if (!event.isControlDown()) {
                clearSelection(); // Deselect all if clicking on empty area without Ctrl
            }
            event.consume();
        }
    }

    /**
     * Handles the mouse dragged event.
     * Performs rotation, resizing, or moving of selected shapes based on the active mode.
     *
     * @param event The {@link MouseEvent} generated by the mouse drag.
     */
    @Override
    public void handleMouseDragged(MouseEvent event) {
        Point2D localPoint = drawingArea.sceneToLocal(event.getSceneX(), event.getSceneY());
        double x = localPoint.getX();
        double y = localPoint.getY();

        if (selectedJavaFxShapes.isEmpty()) {
            return; // No shapes selected to drag
        }

        if (isRotating && currentShapeBeingRotated != null) {
            double currentAngle = calculateAngle(x, y, currentShapeBeingRotated);
            double deltaAngle = currentAngle - lastAngle;

            for (Shape shape : selectedJavaFxShapes) {
                SelectionShapeDecorator decorator = decorators.get(shape);
                if (decorator != null) {
                    decorator.removeDecoration(); // Temporarily remove decoration
                }
                shape.setRotate(shape.getRotate() + deltaAngle); // Apply rotation
                if (decorator != null) {
                    decorator.applyDecoration(); // Reapply decoration
                }
            }
            lastAngle = currentAngle; // Update last angle
            event.consume();
            return;
        }

        if (isResizing && activeHandleType != ResizeHandleType.NONE && activeHandleType != ResizeHandleType.ROTATION) {
            double deltaX = x - lastX;
            double deltaY = y - lastY;

            for (Shape shape : selectedJavaFxShapes) {
                SelectionShapeDecorator decorator = decorators.get(shape);
                if (decorator != null) {
                    decorator.removeDecoration(); // Temporarily remove decoration
                }
                performResize(shape, deltaX, deltaY); // Perform resize
                if (decorator != null) {
                    decorator.applyDecoration(); // Reapply decoration
                }
            }
            lastX = x; // Update last coordinates
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

            // Update decorators after moving all shapes
            for (SelectionShapeDecorator decorator : decorators.values()) {
                if (decorator != null) {
                    decorator.removeDecoration(); // Temporarily remove decoration
                    decorator.applyDecoration(); // Reapply decoration
                }
            }
            event.consume();
        }
    }

    /**
     * Handles the mouse released event.
     * Finalizes moving, resizing, or rotating operations and notifies the model about changes.
     * Clears selection after manipulation is complete.
     *
     * @param event The {@link MouseEvent} generated by the mouse release.
     */
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
                // Remove decorators before notifying model, then clear selection
                if (selectedJavaFxShapes != null && !selectedJavaFxShapes.isEmpty()) {
                    for (Shape shape : selectedJavaFxShapes) {
                        SelectionShapeDecorator decorator = decorators.get(shape);
                        if (decorator != null) {
                            decorator.removeDecoration();
                        }
                    }
                    callback.onModifyShapes(selectedJavaFxShapes); // Notify model
                    clearSelection(); // Clear selection after modification
                }
            } else if (wasMoving) {
                Point2D localPoint = drawingArea.sceneToLocal(event.getSceneX(), event.getSceneY());
                double deltaX = localPoint.getX() - initialMousePress.getX();
                double deltaY = localPoint.getY() - initialMousePress.getY();
                boolean significantChange = (deltaX * deltaX + deltaY * deltaY) > 4; // Check for significant movement

                if (significantChange) {
                    for (Shape shape : selectedJavaFxShapes) {
                        bakeTranslation(shape); // Apply pending translations
                    }

                    // Remove decorators before notifying model, then clear selection
                    if (selectedJavaFxShapes != null && !selectedJavaFxShapes.isEmpty()) {
                        for (Shape shape : selectedJavaFxShapes) {
                            SelectionShapeDecorator decorator = decorators.get(shape);
                            if (decorator != null) {
                                decorator.removeDecoration();
                            }
                        }
                        callback.onModifyShapes(selectedJavaFxShapes); // Notify model
                        clearSelection(); // Clear selection after modification
                    }
                }
            }
        }

        handleMouseMoved(event); // Update cursor to default if no interaction active

        event.consume();
    }

    /**
     * Handles the mouse moved event.
     * Updates the cursor based on whether the mouse is over a selected shape
     * or a resize/rotation handle.
     *
     * @param event The {@link MouseEvent} generated by the mouse movement.
     */
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
        if (activeHandleType != ResizeHandleType.NONE) {
            return; // Maintain current resize cursor if active
        }

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

    /**
     * Handles a change in the line border color property (ignored by this strategy).
     *
     * @param color The new {@link Color} for the line border.
     */
    @Override
    public void handleLineBorderColorChange(Color color) {
        // Not applicable for selection tool
    }

    /**
     * Handles a change in the rectangle border color property (ignored by this strategy).
     *
     * @param color The new {@link Color} for the rectangle border.
     */
    @Override
    public void handleRectangleBorderColorChange(Color color) {
        // Not applicable for selection tool
    }

    /**
     * Handles a change in the rectangle fill color property (ignored by this strategy).
     *
     * @param color The new {@link Color} for the rectangle fill.
     */
    @Override
    public void handleRectangleFillColorChange(Color color) {
        // Not applicable for selection tool
    }

    /**
     * Handles a change in the ellipse border color property (ignored by this strategy).
     *
     * @param color The new {@link Color} for the ellipse border.
     */
    @Override
    public void handleEllipseBorderColorChange(Color color) {
        // Not applicable for selection tool
    }

    /**
     * Handles a change in the ellipse fill color property (ignored by this strategy).
     *
     * @param color The new {@link Color} for the ellipse fill.
     */
    @Override
    public void handleEllipseFillColorChange(Color color) {
        // Not applicable for selection tool
    }

    /**
     * Handles a change in the polygon border color property (ignored by this strategy).
     *
     * @param color The new {@link Color} for the polygon border.
     */
    @Override
    public void handlePolygonBorderColorChange(Color color) {
        // Not applicable for selection tool
    }

    /**
     * Handles a change in the polygon fill color property (ignored by this strategy).
     *
     * @param color The new {@link Color} for the polygon fill.
     */
    @Override
    public void handlePolygonFillColorChange(Color color) {
        // Not applicable for selection tool
    }

    /**
     * Handles a change in the text shape's border color property (ignored by this strategy).
     *
     * @param color The new {@link Color} for the text shape's border.
     */
    @Override
    public void handleTextBorderColorChange(Color color) {
        // Not applicable for selection tool
    }

    /**
     * Handles a change in the text shape's fill color property (ignored by this strategy).
     *
     * @param color The new {@link Color} for the text shape's fill.
     */
    @Override
    public void handleTextFillColorChange(Color color) {
        // Not applicable for selection tool
    }

    /**
     * Handles a change in the text color property (ignored by this strategy).
     *
     * @param color The new {@link Color} for the text.
     */
    @Override
    public void handleTextColorChange(Color color) {
        // Not applicable for selection tool
    }

    /**
     * Handles a change in the number of vertices for polygons (ignored by this strategy).
     *
     * @param polygonVertices The new number of vertices.
     */
    @Override
    public void handlePolygonVerticesChange(int polygonVertices) {
        // Not applicable for selection tool
    }

    /**
     * Handles a change in the regular polygon property (ignored by this strategy).
     *
     * @param regularPolygon A boolean indicating if the polygon should be regular.
     */
    @Override
    public void handleRegularPolygon(boolean regularPolygon) {
        // Not applicable for selection tool
    }

    /**
     * Handles a change in the font size property for text shapes (ignored by this strategy).
     *
     * @param fontSize The new font size.
     */
    @Override
    public void handleFontSizeChange(int fontSize) {
        // Not applicable for selection tool
    }

    /**
     * Handles a key pressed event (ignored by this strategy).
     *
     * @param event The {@link KeyEvent} generated by the key press.
     */
    @Override
    public void handleKeyPressed(KeyEvent event) {
        // No specific action on key pressed for selection tool
    }

    /**
     * Handles a key typed event (ignored by this strategy).
     *
     * @param event The {@link KeyEvent} generated by the key typed action.
     */
    @Override
    public void handleKeyTyped(KeyEvent event) {
        // No specific action on key typed for selection tool
    }

    /**
     * Resets the tool's internal state, clearing any active selections,
     * movement, rotation, or resizing operations, and restores the default cursor.
     */
    @Override
    public void reset() {
        clearSelection();
        isMoving = false;
        isRotating = false;
        isResizing = false;
        activeHandleType = ResizeHandleType.NONE;
        drawingArea.setCursor(Cursor.DEFAULT);
    }

    // Private methods

    /**
     * Applies any pending translations ({@code translateX}, {@code translateY})
     * directly to the shape's coordinates (e.g., X, Y, CenterX, CenterY, StartX, StartY, etc.)
     * and then resets the translation properties to zero.
     * This "bakes" the translation into the shape's actual position.
     *
     * @param shape The JavaFX {@link Shape} whose translation should be baked.
     */
    private void bakeTranslation(Shape shape) {
        if (shape == null) {
            return;
        }
        double translateX = shape.getTranslateX();
        double translateY = shape.getTranslateY();
        if (translateX == 0 && translateY == 0) {
            return; // No translation to bake
        }

        if (shape instanceof Rectangle rectangle) {
            rectangle.setX(rectangle.getX() + translateX);
            rectangle.setY(rectangle.getY() + translateY);
        } else if (shape instanceof Ellipse ellipse) {
            ellipse.setCenterX(ellipse.getCenterX() + translateX);
            ellipse.setCenterY(ellipse.getCenterY() + translateY);
        } else if (shape instanceof Line line) {
            line.setStartX(line.getStartX() + translateX);
            line.setStartY(line.getStartY() + translateY);
            line.setEndX(line.getEndX() + translateX);
            line.setEndY(line.getEndY() + translateY);
        } else if (shape instanceof Polygon polygon) {
            for (int i = 0; i < polygon.getPoints().size(); i += 2) {
                polygon.getPoints().set(i, polygon.getPoints().get(i) + translateX);
                polygon.getPoints().set(i + 1, polygon.getPoints().get(i + 1) + translateY);
            }
        }
        shape.setTranslateX(0); // Reset translation after baking
        shape.setTranslateY(0);
    }

    /**
     * Finds a JavaFX {@link Shape} at the given local coordinates within the drawing area.
     * It iterates through the children of the drawing area in reverse order (top-most first)
     * and excludes decorator elements.
     *
     * @param x The local X-coordinate.
     * @param y The local Y-coordinate.
     * @return The {@link Shape} found at the given coordinates, or {@code null} if no shape is found.
     */
    private Shape findShapeAt(double x, double y) {
        List<Node> children = drawingArea.getChildren();

        // Iterate in reverse to prioritize shapes on top
        for (int i = children.size() - 1; i >= 0; i--) {
            Node node = children.get(i);

            // Skip decorator elements (handles and selection boxes)
            boolean isDecoratorElement = false;
            for (SelectionShapeDecorator decorator : decorators.values()) {
                if (decorator != null) {
                    if ((node instanceof Circle && decorator.getResizeHandles().contains(node)) ||
                            (node instanceof Shape && decorator.getSelectionBoundingBoxes().contains(node))) {
                        isDecoratorElement = true;
                        break;
                    }
                }
            }

            if (isDecoratorElement) {
                continue;
            }

            // Consider only specific shape types that are selectable/manipulable
            if (node instanceof Shape shape) {
                if (!(shape instanceof Rectangle || shape instanceof Ellipse ||
                        shape instanceof Line || shape instanceof Polygon)) {
                    // Exclude other types of shapes or non-shape nodes if they somehow get here.
                    // Text shapes might be handled separately or wrapped in a Rectangle.
                    continue;
                }

                if (shape.isVisible() && shape.contains(x, y)) {
                    return shape;
                }
            }
        }
        return null; // No shape found at position
    }

    /**
     * Finds a resize or rotation handle at the given local coordinates within the drawing area.
     *
     * @param x The local X-coordinate.
     * @param y The local Y-coordinate.
     * @return The {@link Circle} representing the handle found at the given coordinates, or {@code null} if no handle is found.
     */
    private Circle findHandleAt(double x, double y) {
        for (SelectionShapeDecorator decorator : decorators.values()) {
            if (decorator != null) {
                for (Circle handle : decorator.getResizeHandles()) {
                    if (handle.isVisible() && handle.getBoundsInParent().contains(x, y)) {
                        return handle; // Return the handle found
                    }
                }
            }
        }
        return null; // No handle found at position
    }

    /**
     * Selects a shape by its model representation.
     * This method clears the current selection and then adds the specified shape
     * to the selection.
     *
     * @param shape The {@link MyShape} model object to select.
     */
    public void selectShapeByModel(MyShape shape) {
        Shape javafxShape = shapeMapping.getViewShape(shape);
        if (javafxShape != null) {
            clearSelection(); // Clear existing selection
            addShapeToSelection(javafxShape); // Add the specific shape
        }
    }

    /**
     * Clears all currently selected shapes, removes their decorations, and resets
     * the tool's internal state related to selection.
     */
    public void clearSelection() {
        boolean hadSelection = !selectedJavaFxShapes.isEmpty();

        // Remove decorations from all currently selected shapes
        for (SelectionShapeDecorator decorator : decorators.values()) {
            if (decorator != null) {
                decorator.removeDecoration();
            }
        }

        decorators.clear(); // Clear the map of decorators
        selectedJavaFxShapes.clear(); // Clear the list of JavaFX selected shapes
        selectedModelShapes.clear(); // Clear the list of model selected shapes
        primarySelectedShape = null; // Reset primary selected shape
        drawingArea.setCursor(Cursor.DEFAULT); // Restore default cursor

        if (hadSelection) {
            callback.onChangeShapeSelected(); // Notify about selection change
        }
    }

    /**
     * Adds a shape to the current selection.
     * If the shape is already selected or null, no action is taken.
     * Applies a {@link SelectionShapeDecorator} to the added shape.
     *
     * @param shape The JavaFX {@link Shape} to add to the selection.
     */
    private void addShapeToSelection(Shape shape) {
        if (shape == null || selectedJavaFxShapes.contains(shape)) {
            return;
        }

        bakeTranslation(shape); // Apply any pending translation before adding to selection
        selectedJavaFxShapes.add(shape); // Add to JavaFX list

        MyShape modelShape = shapeMapping.getModelShape(shape);
        if (modelShape != null) {
            selectedModelShapes.add(modelShape); // Add to model list
        }

        SelectionShapeDecorator decorator = new SelectionShapeDecorator(shape);
        decorators.put(shape, decorator); // Store decorator
        decorator.applyDecoration(); // Apply visual decoration

        if (primarySelectedShape == null) {
            setPrimarySelectedShape(shape); // Set as primary if none yet
        }

        callback.onChangeShapeSelected(); // Notify about selection change
    }

    /**
     * Removes a shape from the current selection.
     * If the shape is not selected or null, no action is taken.
     * Removes its {@link SelectionShapeDecorator} and updates the primary selected shape if necessary.
     *
     * @param shape The JavaFX {@link Shape} to remove from the selection.
     */
    private void removeShapeFromSelection(Shape shape) {
        if (shape == null || !selectedJavaFxShapes.contains(shape)) {
            return;
        }

        selectedJavaFxShapes.remove(shape); // Remove from JavaFX list

        MyShape modelShape = shapeMapping.getModelShape(shape);
        if (modelShape != null) {
            selectedModelShapes.remove(modelShape); // Remove from model list
        }

        SelectionShapeDecorator decorator = decorators.get(shape);
        if (decorator != null) {
            decorator.removeDecoration(); // Remove visual decoration
            decorators.remove(shape); // Remove decorator from map
        }

        // Update primary selected shape if the removed shape was primary
        if (primarySelectedShape == shape) {
            primarySelectedShape = selectedJavaFxShapes.isEmpty() ? null : selectedJavaFxShapes.get(0);
        }

        callback.onChangeShapeSelected(); // Notify about selection change
    }

    /**
     * Sets the specified shape as the primary selected shape.
     * This shape will be used as the reference for operations like rotation if multiple
     * shapes are selected.
     *
     * @param shape The JavaFX {@link Shape} to set as primary.
     */
    private void setPrimarySelectedShape(Shape shape) {
        if (selectedJavaFxShapes.contains(shape)) {
            primarySelectedShape = shape;
        }
    }

    /**
     * Performs a resize operation on the given shape based on the active handle type
     * and mouse delta.
     *
     * @param shape The JavaFX {@link Shape} to resize.
     * @param deltaX The change in X-coordinate from the last mouse position.
     * @param deltaY The change in Y-coordinate from the last mouse position.
     */
    private void performResize(Shape shape, double deltaX, double deltaY) {
        double minSize = 5; // Minimum size for shapes

        if (shape instanceof Rectangle rectangle) {
            resizeRectangle(rectangle, deltaX, deltaY, minSize);
        } else if (shape instanceof Ellipse ellipse) {
            resizeEllipse(ellipse, deltaX, deltaY, minSize);
        } else if (shape instanceof Line line) {
            resizeLine(line, deltaX, deltaY);
        } else if (shape instanceof Polygon polygon) {
            resizePolygon(polygon, deltaX, deltaY, minSize);
        }
    }

    /**
     * Resizes a {@link Rectangle} based on the active handle and mouse movement.
     *
     * @param rectangle The {@link Rectangle} to resize.
     * @param deltaX The change in X-coordinate.
     * @param deltaY The change in Y-coordinate.
     * @param minSize The minimum allowed dimension for the rectangle.
     */
    private void resizeRectangle(Rectangle rectangle, double deltaX, double deltaY, double minSize) {
        double currentX = rectangle.getX();
        double currentY = rectangle.getY();
        double currentWidth = rectangle.getWidth();
        double currentHeight = rectangle.getHeight();
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
        }

        // Enforce minimum size and adjust position to keep corner fixed if necessary
        if (newWidth < minSize) {
            if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.SOUTH_WEST || activeHandleType == ResizeHandleType.WEST) {
                newX += (newWidth - minSize); // Shift X to compensate for shrinking below minSize
            }
            newWidth = minSize;
        }
        if (newHeight < minSize) {
            if (activeHandleType == ResizeHandleType.NORTH_WEST || activeHandleType == ResizeHandleType.NORTH_EAST || activeHandleType == ResizeHandleType.NORTH) {
                newY += (newHeight - minSize); // Shift Y to compensate for shrinking below minSize
            }
            newHeight = minSize;
        }

        rectangle.setX(newX);
        rectangle.setY(newY);
        rectangle.setWidth(newWidth);
        rectangle.setHeight(newHeight);
    }

    /**
     * Resizes an {@link Ellipse} based on the active handle and mouse movement.
     * It first converts ellipse properties to a bounding box, resizes the box,
     * and then converts back to ellipse properties.
     *
     * @param ellipse The {@link Ellipse} to resize.
     * @param deltaX The change in X-coordinate.
     * @param deltaY The change in Y-coordinate.
     * @param minSize The minimum allowed dimension for the ellipse's radii.
     */
    private void resizeEllipse(Ellipse ellipse, double deltaX, double deltaY, double minSize) {
        double currentCenterX = ellipse.getCenterX();
        double currentCenterY = ellipse.getCenterY();
        double currentRadiusX = ellipse.getRadiusX();
        double currentRadiusY = ellipse.getRadiusY();

        // Convert ellipse to its bounding box for easier resize calculation
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
        }

        // Enforce minimum size similar to rectangle
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

        // Convert bounding box back to ellipse properties
        ellipse.setCenterX(newX + newWidth / 2);
        ellipse.setCenterY(newY + newHeight / 2);
        ellipse.setRadiusX(newWidth / 2);
        ellipse.setRadiusY(newHeight / 2);
    }

    /**
     * Resizes a {@link Line} by moving its start or end points based on the active handle
     * and mouse movement.
     *
     * @param line The {@link Line} to resize.
     * @param deltaX The change in X-coordinate.
     * @param deltaY The change in Y-coordinate.
     */
    private void resizeLine(Line line, double deltaX, double deltaY) {
        switch (activeHandleType) {
            case NORTH_WEST: // Corresponds to start point
                line.setStartX(line.getStartX() + deltaX);
                line.setStartY(line.getStartY() + deltaY);
                break;
            case NORTH_EAST: // Top-right handle might move end X and start Y
                line.setEndX(line.getEndX() + deltaX);
                line.setStartY(line.getStartY() + deltaY); // This logic might need review depending on desired line behavior
                break;
            case SOUTH_WEST: // Bottom-left handle might move start X and end Y
                line.setStartX(line.getStartX() + deltaX);
                line.setEndY(line.getEndY() + deltaY); // This logic might need review
                break;
            case SOUTH_EAST: // Corresponds to end point
                line.setEndX(line.getEndX() + deltaX);
                line.setEndY(line.getEndY() + deltaY);
                break;
            case NORTH: // Move start Y
                line.setStartY(line.getStartY() + deltaY);
                break;
            case SOUTH: // Move end Y
                line.setEndY(line.getEndY() + deltaY);
                break;
            case EAST: // Move end X
                line.setEndX(line.getEndX() + deltaX);
                break;
            case WEST: // Move start X
                line.setStartX(line.getStartX() + deltaX);
                break;
        }
    }

    /**
     * Resizes a {@link Polygon} by scaling its vertices relative to its bounding box
     * based on the active handle and mouse movement.
     *
     * @param polygon The {@link Polygon} to resize.
     * @param deltaX The change in X-coordinate.
     * @param deltaY The change in Y-coordinate.
     * @param minSize The minimum allowed dimension for the polygon's bounding box.
     */
    private void resizePolygon(Polygon polygon, double deltaX, double deltaY, double minSize) {
        Bounds bounds = polygon.getBoundsInLocal();
        double currentX = bounds.getMinX();
        double currentY = bounds.getMinY();
        double currentWidth = bounds.getWidth();
        double currentHeight = bounds.getHeight();

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
        }

        // Do not resize if new dimensions are too small
        if (newWidth < minSize || newHeight < minSize) {
            return;
        }

        double scaleX = newWidth / currentWidth;
        double scaleY = newHeight / currentHeight;
        double offsetX = newX - currentX;
        double offsetY = newY - currentY;

        // Apply scaling and offset to each point
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

    /**
     * Calculates the angle (in degrees) between the center of a shape and a given point.
     * This is used for rotation operations.
     *
     * @param x The X-coordinate of the point.
     * @param y The Y-coordinate of the point.
     * @param shape The JavaFX {@link Shape} for which to calculate the center.
     * @return The angle in degrees.
     */
    private double calculateAngle(double x, double y, Shape shape) {
        Bounds bounds = shape.getBoundsInParent();
        double centerX = bounds.getMinX() + bounds.getWidth() / 2;
        double centerY = bounds.getMinY() + bounds.getHeight() / 2;
        return Math.toDegrees(Math.atan2(y - centerY, x - centerX));
    }

    /**
     * An enumeration representing the types of resize/rotation handles.
     * Each type corresponds to a specific corner, edge, or rotation point on a shape's bounding box.
     */
    private enum ResizeHandleType {
        ROTATION, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST,
        NORTH, SOUTH, EAST, WEST, NONE
    }
}