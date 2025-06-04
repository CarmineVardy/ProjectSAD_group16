package it.unisa.diem.sad.geoshapes.command;

import java.util.Collections;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class SendToBackCommandTest {

    private DrawingModel model;
    private MyShape shape;
    private SendToBackCommand command;

    @BeforeEach
    void setUp() {
        model = mock(DrawingModel.class);
        shape = mock(MyShape.class);
        command = new SendToBackCommand(model, Collections.singletonList(shape));
    }

    @Test
    void testExecute_CallsSendToBack() {
        command.execute();
        verify(model).sendToBack(shape);
    }

    @Test
    void testUndo_CallsBringToFront() {
        command.undo();
        verify(model).bringToFront(shape);
    }
}