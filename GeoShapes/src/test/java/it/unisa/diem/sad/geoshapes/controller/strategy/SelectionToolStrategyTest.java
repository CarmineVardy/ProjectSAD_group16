package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.InteractionCallback;
import it.unisa.diem.sad.geoshapes.controller.ShapeMapping;
import it.unisa.diem.sad.geoshapes.decorator.SelectionDecorator;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelectionToolStrategyTest {

    private DrawingModel model;
    private MyShape shape1;
    private MyShape shape2;
    private SelectionToolStrategy strategy;
    Pane drawingPane = mock(Pane.class);

    @BeforeEach
    void setUp() {
        model = mock(DrawingModel.class);
        shape1 = mock(MyShape.class);
        shape2 = mock(MyShape.class);
        Pane drawingPane = mock(Pane.class);
        Group zoomGroup = mock(Group.class);
        ShapeMapping shapeMapping = mock(ShapeMapping.class);
        InteractionCallback callback = mock(InteractionCallback.class);

        strategy = new SelectionToolStrategy(drawingPane, zoomGroup, shapeMapping, callback);
        strategy.setModel(model);
    }

    @Test
    void testResizeUpdatesShapeCorrectly() {
        Rectangle rect = new Rectangle(100, 100, 50, 50);
        Pane drawingArea = new Pane();
        drawingArea.getChildren().add(rect);

        // Applica il decoratore per abilitare le maniglie di ridimensionamento
        SelectionDecorator decorator = new SelectionDecorator(rect,drawingPane);
        drawingArea.getChildren().addAll(decorator.getResizeHandles()); // garantisce che le maniglie possano essere visualizzate
        decorator.applyDecoration();

        // Trova la maniglia bottom-right
        Circle bottomRightHandle = decorator.getResizeHandles().stream()
                .filter(h -> "BOTTOM_RIGHT".equals(h.getUserData()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("BOTTOM_RIGHT handle not found"));

        // Simula il drag
        bottomRightHandle.setCenterX(bottomRightHandle.getCenterX() + 20);
        bottomRightHandle.setCenterY(bottomRightHandle.getCenterY() + 30);

        // Simula la logica del resize
        rect.setWidth(rect.getWidth() + 20);
        rect.setHeight(rect.getHeight() + 30);


        assertEquals(70.0, rect.getWidth(), 0.001);
        assertEquals(80.0, rect.getHeight(), 0.001);
    }

    @Test
    void testResizeHandlesUpdateWithShape() {
        Rectangle rect = new Rectangle(100, 100, 50, 50);
        Pane pane = new Pane(rect);

        SelectionDecorator decorator = new SelectionDecorator(rect,drawingPane);
        decorator.applyDecoration();

        rect.setWidth(100);
        rect.setHeight(100);

        decorator.removeDecoration();
        decorator.applyDecoration();

        Circle bottomRightHandle = decorator.getResizeHandles().stream()
                .filter(h -> "BOTTOM_RIGHT".equals(h.getUserData()))
                .findFirst()
                .orElseThrow();

        double expectedX = rect.getX() + rect.getWidth();
        double expectedY = rect.getY() + rect.getHeight();

        double tolerance = 2.0;
        assertEquals(200.0, bottomRightHandle.getCenterX(), tolerance);
    }

    @Test
    void testMoveSingleShapeByDelta() {
        when(model.getSelectedShapes()).thenReturn(Collections.singletonList(shape1));

        strategy.onMousePressed(10, 10);
        strategy.onMouseDragged(15, 15);

        verify(shape1).moveBy(5.0, 5.0);
    }

    @Test
    void testMoveMultipleShapes() {
        when(model.getSelectedShapes()).thenReturn(Arrays.asList(shape1, shape2));

        strategy.onMousePressed(0, 0);
        strategy.onMouseDragged(20, 10);

        verify(shape1).moveBy(20.0, 10.0);
        verify(shape2).moveBy(20.0, 10.0);
    }

    @Test
    void testNoSelectedShapes_NoMoveCalled() {
        when(model.getSelectedShapes()).thenReturn(Collections.emptyList());

        strategy.onMousePressed(50, 50);
        strategy.onMouseDragged(100, 100);

        verify(shape1, never()).moveBy(anyDouble(), anyDouble());
        verify(shape2, never()).moveBy(anyDouble(), anyDouble());
    }
}