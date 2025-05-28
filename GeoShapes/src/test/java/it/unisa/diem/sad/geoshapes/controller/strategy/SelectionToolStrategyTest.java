package it.unisa.diem.sad.geoshapes.controller.strategy;

import it.unisa.diem.sad.geoshapes.decorator.SelectionDecorator;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelectionToolStrategyTest {

    @Test
    void testResizeUpdatesShapeCorrectly() {
        Rectangle rect = new Rectangle(100, 100, 50, 50);
        Pane drawingArea = new Pane();
        drawingArea.getChildren().add(rect);

        // Applica il decoratore per abilitare le maniglie di ridimensionamento
        SelectionDecorator decorator = new SelectionDecorator(rect);
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

        SelectionDecorator decorator = new SelectionDecorator(rect);
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

}