package it.unisa.diem.sad.geoshapes.controller.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.beans.property.BooleanProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandInvokerTest {

    private CommandInvoker invoker;
    private DrawingModel model;
    private MyShape shape;

    @BeforeEach
    void setUp() {
        invoker = new CommandInvoker();
        model = mock(DrawingModel.class);
        shape = mock(MyShape.class);
    }

    // verificare che dopo l'esecuzione di un comando, la possibilità di fare undo (canUndo) sia true
    @Test
    void testExecuteCommand_AddsToHistoryAndUpdatesCanUndo() {
        Command command = new CreateShapeCommand(model, shape);
        invoker.executeCommand(command);

        assertTrue(invoker.canUndo(), "Dopo l'esecuzione, canUndo deve essere true");
        BooleanProperty canUndoProp = invoker.canUndoProperty();
        assertTrue(canUndoProp.get());
    }

    // verifica che il comando venga eseguito, l'undo funzioni, lo stato di canUndo sia aggiornato
    @Test
    void testUndo_RemovesCommandFromHistoryAndUpdatesCanUndo() {
        Command command = new CreateShapeCommand(model, shape);
        invoker.executeCommand(command);
        invoker.undo();

        verify(model).addShape(shape);
        verify(model).removeShape(shape);

        assertFalse(invoker.canUndo(), "Dopo l'undo, canUndo deve essere false");
        assertFalse(invoker.canUndoProperty().get());
    }

    // verifica il comportamento di undo se non ci sono comandi nella history
    @Test
    void testUndo_WithoutCommand_DoesNothing() {
        invoker.undo();  // Non ci sono comandi
        assertFalse(invoker.canUndo(), "Senza comandi, canUndo deve essere false");
    }

    @Test
    void testClearHistory() {
        invoker.executeCommand(new CreateShapeCommand(model, shape));
        invoker.clearHistory();
        assertFalse(invoker.canUndo(), "Dopo clearHistory, canUndo deve essere false");
    }
}