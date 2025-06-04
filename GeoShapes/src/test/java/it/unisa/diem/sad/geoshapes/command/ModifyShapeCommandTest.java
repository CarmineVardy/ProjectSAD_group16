package it.unisa.diem.sad.geoshapes.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

class ModifyShapeCommandTest {

    private DrawingModel model;
    private MyShape target1, target2;
    private MyShape old1, old2;
    private MyShape new1, new2;

    private ModifyShapeCommand command;

    @BeforeEach
    void setUp() {
        model = mock(DrawingModel.class);

        target1 = mock(MyShape.class);
        target2 = mock(MyShape.class);

        old1 = mock(MyShape.class);
        old2 = mock(MyShape.class);

        new1 = mock(MyShape.class);
        new2 = mock(MyShape.class);

        List<MyShape> targetShapes = List.of(target1, target2);
        List<MyShape> oldShapes = List.of(old1, old2);
        List<MyShape> newShapes = List.of(new1, new2);

        command = new ModifyShapeCommand(model, targetShapes, oldShapes, newShapes);
    }

    @Test
    void testExecuteShouldApplyNewShapes() {
        command.execute();

        verify(model).modifyShape(target1, new1);
        verify(model).modifyShape(target2, new2);
    }

    @Test
    void testUndoShouldRestoreOldShapes() {
        command.undo();

        verify(model).modifyShape(target1, old1);
        verify(model).modifyShape(target2, old2);
    }
}