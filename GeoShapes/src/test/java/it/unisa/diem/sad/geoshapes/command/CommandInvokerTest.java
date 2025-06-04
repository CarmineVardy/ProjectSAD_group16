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
}