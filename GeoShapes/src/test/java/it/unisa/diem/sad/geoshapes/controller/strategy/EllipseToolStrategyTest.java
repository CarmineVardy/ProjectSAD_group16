package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.application.Platform;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EllipseToolStrategyTest {

    static {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Già inizializzato, ignora
        }
    }

    private Pane drawingArea;
    private ColorPicker borderColorPicker;
    private ColorPicker fillColorPicker;
    private EllipseToolStrategy strategy;

    @BeforeEach
    public void setUp() {
        drawingArea = new Pane();
        drawingArea.setMinSize(100, 100);
        drawingArea.setMaxSize(100, 100);
        drawingArea.setPrefSize(100, 100);
        drawingArea.resize(100, 100); // Fondamentale per le coordinate normalizzate
        borderColorPicker = new ColorPicker(Color.BLACK);
        fillColorPicker = new ColorPicker(Color.BLUE);
        strategy = new EllipseToolStrategy(drawingArea, borderColorPicker, fillColorPicker);
    }

    @Test
    public void testHandlePressedAndDraggedAndReleased_CreatesValidEllipse() {
        // Simula pressione mouse all'inizio
        MouseEvent press = new MouseEvent(MouseEvent.MOUSE_PRESSED, 10, 20, 10, 20, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);
        strategy.handlePressed(press);

        // Simula trascinamento mouse
        MouseEvent drag = new MouseEvent(MouseEvent.MOUSE_DRAGGED, 60, 80, 60, 80, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);
        strategy.handleDragged(drag);

        // Simula rilascio mouse
        MouseEvent release = new MouseEvent(MouseEvent.MOUSE_RELEASED, 60, 80, 60, 80, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);
        strategy.handleReleased(release);

        // Ottieni la shape finale
        MyShape shape = strategy.getFinalShape();

        assertNotNull(shape, "L'ellisse dovrebbe essere creata se le dimensioni sono sufficienti");
        // Verifica che le coordinate siano normalizzate (tra 0 e 1)
        assertEquals(0.10, getX1(shape), 0.0001);
        assertEquals(0.20, getY1(shape), 0.0001);
        assertEquals(0.60, getX2(shape), 0.0001);
        assertEquals(0.80, getY2(shape), 0.0001);
    }

    @Test
    public void testHandleReleased_TooSmallEllipse_NoShapeCreated() {
        // Simula pressione e rilascio quasi nello stesso punto
        MouseEvent press = new MouseEvent(MouseEvent.MOUSE_PRESSED, 10, 10, 10, 10, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);
        strategy.handlePressed(press);

        MouseEvent release = new MouseEvent(MouseEvent.MOUSE_RELEASED, 11, 11, 11, 11, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);
        strategy.handleReleased(release);

        MyShape shape = strategy.getFinalShape();
        assertNull(shape, "L'ellisse non dovrebbe essere creata se troppo piccola");
    }

    @Test
    public void testReset_RemovesPreviewAndState() {
        // Simula pressione mouse per creare preview
        MouseEvent press = new MouseEvent(MouseEvent.MOUSE_PRESSED, 10, 20, 10, 20, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);
        strategy.handlePressed(press);

        assertFalse(drawingArea.getChildren().isEmpty(), "La preview dovrebbe essere aggiunta al Pane");

        strategy.reset();

        assertTrue(drawingArea.getChildren().isEmpty(), "La preview dovrebbe essere rimossa dal Pane dopo reset");
        assertNull(strategy.getFinalShape(), "Dopo reset non deve esserci shape finale");
    }

    // --- Utility per accedere alle coordinate normalizzate ---
    private double getX1(MyShape shape) {
        try {
            return (double) shape.getClass().getMethod("getStartX").invoke(shape);
        } catch (Exception e) {
            throw new RuntimeException("Metodo getStartX non trovato su MyShape", e);
        }
    }
    private double getY1(MyShape shape) {
        try {
            return (double) shape.getClass().getMethod("getStartY").invoke(shape);
        } catch (Exception e) {
            throw new RuntimeException("Metodo getStartY non trovato su MyShape", e);
        }
    }
    private double getX2(MyShape shape) {
        try {
            return (double) shape.getClass().getMethod("getEndX").invoke(shape);
        } catch (Exception e) {
            throw new RuntimeException("Metodo getEndX non trovato su MyShape", e);
        }
    }
    private double getY2(MyShape shape) {
        try {
            return (double) shape.getClass().getMethod("getEndY").invoke(shape);
        } catch (Exception e) {
            throw new RuntimeException("Metodo getEndY non trovato su MyShape", e);
        }
    }
}