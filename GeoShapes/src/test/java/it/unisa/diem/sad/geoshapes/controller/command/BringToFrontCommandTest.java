package it.unisa.diem.sad.geoshapes.controller.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class BringToFrontCommandTest {

    private DrawingModel model;
    private MyShape shape;
    private BringToFrontCommand command;

    @BeforeEach
    void setUp() {
        model = mock(DrawingModel.class);
        shape = mock(MyShape.class);
        command = new BringToFrontCommand(model, shape);
    }

    @Test
    void testExecute_CallsBringToFront() {
        command.execute();
        verify(model).bringToFront(shape);
    }

    @Test
    void testUndo_CallsSendToBack() {
        command.undo();
        verify(model).sendToBack(shape);
    }
}