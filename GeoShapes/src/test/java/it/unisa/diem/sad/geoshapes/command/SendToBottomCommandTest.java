package it.unisa.diem.sad.geoshapes.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

class SendToBottomCommandTest {

    private DrawingModel model;
    private MyShape shape1;
    private MyShape shape2;
    private List<MyShape> shapes;
    private SendToBottomCommand command;

    @BeforeEach
    void setUp() {
        model = mock(DrawingModel.class);
        shape1 = mock(MyShape.class);
        shape2 = mock(MyShape.class);

        shapes = new ArrayList<>();
        shapes.add(shape1);
        shapes.add(shape2);

        List<MyShape> currentShapes = new ArrayList<>();
        currentShapes.add(shape1);
        currentShapes.add(shape2);

        when(model.getShapes()).thenReturn(currentShapes);

        command = new SendToBottomCommand(model, shapes);
    }

    @Test
    void testExecuteShouldSendShapesToBottom() {
        command.execute();

        verify(model).sendToBottom(shape1);
        verify(model).sendToBottom(shape2);
    }

    @Test
    void testUndoShouldRestoreOriginalPositions() {
        command.execute();
        reset(model);

        command.undo();

        verify(model).moveToPosition(shape2, 1);
        verify(model).moveToPosition(shape1, 0);
    }
}