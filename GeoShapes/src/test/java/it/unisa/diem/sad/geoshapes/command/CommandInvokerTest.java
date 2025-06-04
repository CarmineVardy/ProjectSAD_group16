package it.unisa.diem.sad.geoshapes.command;

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

    @Test
    void testExecuteCommand_AddsToHistoryAndUpdatesCanUndo() {
        Command command = new CreateShapeCommand(model, shape);
        invoker.executeCommand(command);

        assertTrue(invoker.canUndo(), "afyer execution, canUndo must be true");
        BooleanProperty canUndoProp = invoker.canUndoProperty();
        assertTrue(canUndoProp.get());
    }

    @Test
    void testUndo_RemovesCommandFromHistoryAndUpdatesCanUndo() {
        Command command = new CreateShapeCommand(model, shape);
        invoker.executeCommand(command);
        invoker.undo();

        verify(model).addShape(shape);
        verify(model).removeShape(shape);

        assertFalse(invoker.canUndo(), "after undo, canUndo must be false");
        assertFalse(invoker.canUndoProperty().get());
    }

    @Test
    void testUndo_WithoutCommand_DoesNothing() {
        invoker.undo();
        assertFalse(invoker.canUndo(), "without command, canUndo must be false");
    }

    @Test
    void testClearHistory() {
        invoker.executeCommand(new CreateShapeCommand(model, shape));
        invoker.clearHistory();
        assertFalse(invoker.canUndo(), "Dopo clearHistory, canUndo deve essere false");
    }
}