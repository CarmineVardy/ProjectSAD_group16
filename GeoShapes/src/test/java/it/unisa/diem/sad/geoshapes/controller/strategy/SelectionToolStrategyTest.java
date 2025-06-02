/*
package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.controller.ShapeMapping;
import it.unisa.diem.sad.geoshapes.decorator.SelectionDecorator;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SelectionToolStrategyTest {

    private DrawingModel model;
    private MyShape modelShape1;
    private MyShape modelShape2;
    private SelectionToolStrategy strategy;
    private Pane drawingPane;
    private Group zoomGroup;
    private ShapeMapping shapeMapping;
    private InteractionCallback callback;
    private Rectangle testRectangle;

    @BeforeEach
    void setUp() {
        model = mock(DrawingModel.class);
        modelShape1 = mock(MyShape.class);
        modelShape2 = mock(MyShape.class);
        drawingPane = new Pane();
        zoomGroup = new Group();
        shapeMapping = mock(ShapeMapping.class);
        callback = mock(InteractionCallback.class);

        strategy = new SelectionToolStrategy(drawingPane, zoomGroup, shapeMapping, callback);
        strategy.setModel(model);

        // Create a test rectangle and add it to the drawing pane
        testRectangle = new Rectangle(100, 100, 50, 50);
        testRectangle.setFill(Color.BLUE);
        testRectangle.setStroke(Color.BLACK);
        drawingPane.getChildren().add(testRectangle);

        // Setup shape mapping
        when(shapeMapping.getModelShape(testRectangle)).thenReturn(modelShape1);
        when(shapeMapping.getViewShape(modelShape1)).thenReturn(testRectangle);
    }

    @Test
    void testActivateCallsCallback() {
        strategy.activate(Color.BLACK, Color.WHITE);
        verify(callback).onLineSelected(false);
    }


    @Test
    void testSelectShapeOnPrimaryClick() {
        MouseEvent clickEvent = createMouseEvent(MouseEvent.MOUSE_PRESSED, 125, 125, MouseButton.PRIMARY);
        strategy.handleMousePressed(clickEvent);

        verify(callback).onShapeSelected(testRectangle);
        assertEquals(1, strategy.getSelectedShapes().size());
        assertEquals(modelShape1, strategy.getSelectedShapes().get(0));
    }

    @Test
    void testSecondaryClickOpensContextMenu() {
        MouseEvent rightClickEvent = createMouseEvent(MouseEvent.MOUSE_PRESSED, 125, 125, MouseButton.SECONDARY);
        rightClickEvent = spy(rightClickEvent);
        when(rightClickEvent.getScreenX()).thenReturn(200.0);
        when(rightClickEvent.getScreenY()).thenReturn(300.0);

        strategy.handleMousePressed(rightClickEvent);

        verify(callback).onSelectionMenuOpened(200.0, 300.0);
    }

    @Test
    void testMoveSelectedShape() {
        // Select the shape first
        MouseEvent selectEvent = createMouseEvent(MouseEvent.MOUSE_PRESSED, 125, 125, MouseButton.PRIMARY);
        strategy.handleMousePressed(selectEvent);

        // Start dragging
        MouseEvent dragEvent = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 135, 140, MouseButton.PRIMARY);
        strategy.handleMouseDragged(dragEvent);

        // Check that translation was applied
        assertEquals(10.0, testRectangle.getTranslateX());
        assertEquals(15.0, testRectangle.getTranslateY());
    }

    @Test
    void testReleaseAfterMoveCallsCallback() {
        double originalX = testRectangle.getX();
        double originalY = testRectangle.getY();

        // Select and move the shape
        MouseEvent selectEvent = createMouseEvent(MouseEvent.MOUSE_PRESSED, 125, 125, MouseButton.PRIMARY);
        strategy.handleMousePressed(selectEvent);

        MouseEvent dragEvent = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 135, 140, MouseButton.PRIMARY);
        strategy.handleMouseDragged(dragEvent);

        MouseEvent releaseEvent = createMouseEvent(MouseEvent.MOUSE_RELEASED, 135, 140, MouseButton.PRIMARY);
        strategy.handleMouseReleased(releaseEvent);

        // Check that translation was baked into position
        assertEquals(originalX + 10.0, testRectangle.getX());
        assertEquals(originalY + 15.0, testRectangle.getY());
        assertEquals(0.0, testRectangle.getTranslateX());
        assertEquals(0.0, testRectangle.getTranslateY());

        verify(callback).onModifyShape(testRectangle);
    }

    @Test
    void testCopySelectedShape() {
        // Select a shape first
        MouseEvent selectEvent = createMouseEvent(MouseEvent.MOUSE_PRESSED, 125, 125, MouseButton.PRIMARY);
        strategy.handleMousePressed(selectEvent);

        strategy.handleCopy(null);
        verify(callback).onCopyShape(testRectangle);
    }

    @Test
    void testCutSelectedShape() {
        // Select a shape first
        MouseEvent selectEvent = createMouseEvent(MouseEvent.MOUSE_PRESSED, 125, 125, MouseButton.PRIMARY);
        strategy.handleMousePressed(selectEvent);

        strategy.handleCut(null);
        verify(callback).onCutShape(testRectangle);
    }

    @Test
    void testDeleteSelectedShape() {
        // Select a shape first
        MouseEvent selectEvent = createMouseEvent(MouseEvent.MOUSE_PRESSED, 125, 125, MouseButton.PRIMARY);
        strategy.handleMousePressed(selectEvent);

        strategy.handleDelete(null);
        verify(callback).onDeleteShape(testRectangle);
    }

    @Test
    void testBringToFrontSelectedShape() {
        // Select a shape first
        MouseEvent selectEvent = createMouseEvent(MouseEvent.MOUSE_PRESSED, 125, 125, MouseButton.PRIMARY);
        strategy.handleMousePressed(selectEvent);

        strategy.handleBringToFront(null);
        verify(callback).onBringToFront(testRectangle);
    }


    @Test
    void testSelectShapeByModel() {
        strategy.selectShapeByModel(modelShape1);

        verify(callback).onShapeSelected(testRectangle);
        assertEquals(1, strategy.getSelectedShapes().size());
        assertEquals(modelShape1, strategy.getSelectedShapes().get(0));
    }

    @Test
    void testClearSelection() {
        // Select a shape first
        MouseEvent selectEvent = createMouseEvent(MouseEvent.MOUSE_PRESSED, 125, 125, MouseButton.PRIMARY);
        strategy.handleMousePressed(selectEvent);

        assertFalse(strategy.getSelectedShapes().isEmpty());

        strategy.clearSelection();

        assertTrue(strategy.getSelectedShapes().isEmpty());
    }

    @Test
    void testRotationHandling() {
        // Select a shape first
        MouseEvent selectEvent = createMouseEvent(MouseEvent.MOUSE_PRESSED, 125, 125, MouseButton.PRIMARY);
        strategy.handleMousePressed(selectEvent);

        // Test rotation callback
        double initialAngle = 0.0;
        double finalAngle = 45.0;

        testRectangle.setRotate(finalAngle);

        // Simulate rotation release
        MouseEvent releaseEvent = createMouseEvent(MouseEvent.MOUSE_RELEASED, 125, 125, MouseButton.PRIMARY);
        strategy.handleMouseReleased(releaseEvent);

        assertEquals(finalAngle, testRectangle.getRotate());
    }

    // Helper method to create MouseEvent objects for testing
    private MouseEvent createMouseEvent(javafx.event.EventType<MouseEvent> eventType, double x, double y, MouseButton button) {
        return new MouseEvent(
                drawingPane,             // source
                drawingPane,             // target
                eventType,               // eventType
                x, y,                    // x, y
                x, y,                    // screenX, screenY
                button,                  // button
                1,                       // clickCount
                false, false, false, false, // shift, control, alt, meta
                button == MouseButton.PRIMARY,   // primary
                button == MouseButton.MIDDLE,    // middle
                button == MouseButton.SECONDARY, // secondary
                false,                   // synthesized
                false,                   // popupTrigger
                false,                   // stillSincePress
                null                     // pickResult
        );
    }
}*/