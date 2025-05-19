package com.example.geoshapes.controller.strategy;

import com.example.geoshapes.controller.ShapeMapping;
import com.example.geoshapes.model.DrawingModel;
import com.example.geoshapes.model.shapes.MyShape;
import com.example.geoshapes.decorator.ShapeDecorator;
import com.example.geoshapes.decorator.SelectionDecorator;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class SelectionToolStrategy implements ToolStrategy {

    private final Pane drawingArea;
    private final ShapeMapping shapeMapping;
    private final DrawingModel model;

    private ShapeDecorator currentDecorator;
    private MyShape selectedModelShape;
    private Shape selectedJavaFxShape;

    public SelectionToolStrategy(Pane drawingArea, ShapeMapping shapeMapping, DrawingModel model) {
        this.drawingArea = drawingArea;
        this.shapeMapping = shapeMapping;
        this.model = model;
    }

    @Override
    public void handlePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        Shape clickedShapeView = findShapeAt(x, y); // Trova la JavaFX Shape cliccata

        if (event.getButton() == MouseButton.PRIMARY) { // Click sinistro
            if (currentDecorator != null) {
                currentDecorator.removeDecoration(); // Rimuovi decorazione precedente
                currentDecorator = null;
            }

            if (clickedShapeView != null) {
                selectedJavaFxShape = clickedShapeView;
                selectedModelShape = shapeMapping.getModelShape(selectedJavaFxShape);

                if (selectedModelShape != null) {
                    currentDecorator = new SelectionDecorator(selectedJavaFxShape);
                    currentDecorator.applyDecoration();
                } else {
                    // Cliccato su una JavaFX shape non mappata (non dovrebbe succedere)
                    resetSelectionState();
                }
            } else {
                // Click su area vuota, deseleziona tutto
                resetSelectionState();
            }
        } else if (event.getButton() == MouseButton.SECONDARY) { // Click destro
            // Se il click destro è su una forma
            if (clickedShapeView != null) {
                // Se la forma cliccata con il destro è diversa da quella attualmente selezionata,
                // o se nessuna forma è selezionata, allora seleziona la nuova forma.
                if (selectedJavaFxShape != clickedShapeView) {
                    if (currentDecorator != null) {
                        currentDecorator.removeDecoration();
                        currentDecorator = null;
                    }
                    selectedJavaFxShape = clickedShapeView;
                    selectedModelShape = shapeMapping.getModelShape(selectedJavaFxShape);

                    if (selectedModelShape != null) {
                        currentDecorator = new SelectionDecorator(selectedJavaFxShape);
                        currentDecorator.applyDecoration();
                    } else {
                        resetSelectionState(); // Forma vista non mappata
                    }
                }
                // Se è la stessa forma già selezionata, non fare nulla qui,
                // il controller gestirà la visualizzazione del menu.
            } else {
                // Click destro su area vuota: deseleziona la forma corrente (se ce n'è una)
                // Il controller non mostrerà il menu perché selectedModelShape sarà null.
                if (currentDecorator != null) {
                    currentDecorator.removeDecoration();
                    currentDecorator = null;
                }
                resetSelectionState();
            }
        }
    }


    @Override
    public void handleDragged(MouseEvent event) {
        // Drag functionality could be implemented here
    }

    @Override
    public void handleReleased(MouseEvent event) {
        // Release functionality could be implemented here
    }

    @Override
    public MyShape getFinalShape() {
        return selectedModelShape;
    }

    public MyShape getSelectedModelShape() {
        return selectedModelShape;
    }

    public Shape getSelectedJavaFxShape() {
        return selectedJavaFxShape;
    }

    public void resetSelection() {
        if (currentDecorator != null) {
            currentDecorator.removeDecoration();
            currentDecorator = null;
        }
        resetSelectionState();
    }

    private void resetSelectionState() {
        selectedModelShape = null;
        selectedJavaFxShape = null;
    }

    private Shape findShapeAt(double x, double y) {
        for (int i = drawingArea.getChildren().size() - 1; i >= 0; i--) {
            if (drawingArea.getChildren().get(i) instanceof Shape) {
                Shape shape = (Shape) drawingArea.getChildren().get(i);
                if (shape.contains(x, y)) {
                    return shape;
                }
            }
        }
        return null;
    }

    public void updateSelectedFxShape(Shape newFxShape) {
        // Questo metodo è cruciale se una forma viene modificata MENTRE è selezionata.
        // La vecchia istanza di selectedJavaFxShape potrebbe non essere più nella scena.
        if (selectedModelShape != null) { // Se c'era una MyShape selezionata
            if (currentDecorator != null) {
                // La vecchia decorazione era sulla vecchia selectedJavaFxShape.
                // Non è strettamente necessario chiamare removeDecoration() sulla vecchia shape
                // se è già stata rimossa dalla scena, ma è buona norma.
                // Tuttavia, la SelectionDecorator conserva riferimenti interni.
                // È più sicuro creare un nuovo decoratore per la newFxShape.
                currentDecorator.removeDecoration(); // Rimuove la decorazione dalla vecchia FxShape se ancora possibile
            }

            selectedJavaFxShape = newFxShape; // Aggiorna il riferimento alla JavaFX shape
            if (selectedJavaFxShape != null) { // Se la nuova FxShape esiste
                currentDecorator = new SelectionDecorator(selectedJavaFxShape); // Crea un nuovo decoratore
                currentDecorator.applyDecoration(); // Applica la decorazione alla nuova FxShape
            } else {
                // Se newFxShape è null (improbabile se MyShape esiste ancora), resetta
                currentDecorator = null;
                selectedModelShape = null; // MyShape non ha più una vista valida
            }
        }
    }

}