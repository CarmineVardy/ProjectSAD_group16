package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.controller.ShapeMapping;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SelectionToolStrategyTest {

    static {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Già inizializzato, ignora
        }
    }

    private Pane drawingArea;
    private SelectionToolStrategy strategy;
    private Rectangle fxRect;
    private MyShape modelShape;

    // Implementazione fittizia di ShapeMapping
    private class TestShapeMapping extends ShapeMapping {
        private final Shape shape;
        private final MyShape myShape;

        public TestShapeMapping(Shape shape, MyShape myShape) {
            this.shape = shape;
            this.myShape = myShape;
        }

        @Override
        public MyShape getModelShape(Shape s) {
            return s == shape ? myShape : null;
        }
    }

    @BeforeEach
    public void setUp() {
        drawingArea = new Pane();
        drawingArea.setMinSize(100, 100);
        drawingArea.setMaxSize(100, 100);
        drawingArea.setPrefSize(100, 100);
        drawingArea.resize(100, 100);

        fxRect = new Rectangle(10, 10, 50, 50);
        fxRect.setVisible(true);

        // MyShape fittizio: implementa tutti i metodi astratti (anche vuoti)
        modelShape = new MyShape() {
            @Override
            public void setStartPoint(double x, double y) {}
            @Override
            public void setEndPoint(double x, double y) {}
            @Override
            public MyShape clone() { return this; }
            // Se MyShape ha altri metodi astratti, aggiungili qui nello stesso modo
        };

        drawingArea.getChildren().add(fxRect);

        ShapeMapping shapeMapping = new TestShapeMapping(fxRect, modelShape);

        strategy = new SelectionToolStrategy(drawingArea, shapeMapping);
    }

    @Test
    public void testSelectShapeOnPrimaryClick() {
        MouseEvent click = new MouseEvent(MouseEvent.MOUSE_PRESSED, 15, 15, 15, 15, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);

        strategy.handlePressed(click);

        assertEquals(modelShape, strategy.getSelectedModelShape(), "La shape dovrebbe essere selezionata");
        assertEquals(fxRect, strategy.getSelectedJavaFxShape(), "La shape JavaFX dovrebbe essere selezionata");
    }

    @Test
    public void testDeselectOnClickOutside() {
        MouseEvent click = new MouseEvent(MouseEvent.MOUSE_PRESSED, 15, 15, 15, 15, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);
        strategy.handlePressed(click);

        MouseEvent clickOutside = new MouseEvent(MouseEvent.MOUSE_PRESSED, 90, 90, 90, 90, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);
        strategy.handlePressed(clickOutside);

        assertNull(strategy.getSelectedModelShape(), "La selezione dovrebbe essere rimossa dopo click fuori");
        assertNull(strategy.getSelectedJavaFxShape(), "La selezione JavaFX dovrebbe essere rimossa dopo click fuori");
    }

    @Test
    public void testRightClickOnSelectedShapeDoesNotDeselect() {
        MouseEvent click = new MouseEvent(MouseEvent.MOUSE_PRESSED, 15, 15, 15, 15, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);
        strategy.handlePressed(click);

        MouseEvent rightClick = new MouseEvent(MouseEvent.MOUSE_PRESSED, 15, 15, 15, 15, MouseButton.SECONDARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);
        strategy.handlePressed(rightClick);

        assertEquals(modelShape, strategy.getSelectedModelShape(), "La shape dovrebbe restare selezionata dopo click destro");
    }

    @Test
    public void testResetSelection() {
        MouseEvent click = new MouseEvent(MouseEvent.MOUSE_PRESSED, 15, 15, 15, 15, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);
        strategy.handlePressed(click);

        strategy.resetSelection();

        assertNull(strategy.getSelectedModelShape(), "La selezione dovrebbe essere nulla dopo reset");
        assertNull(strategy.getSelectedJavaFxShape(), "La selezione JavaFX dovrebbe essere nulla dopo reset");
    }
}