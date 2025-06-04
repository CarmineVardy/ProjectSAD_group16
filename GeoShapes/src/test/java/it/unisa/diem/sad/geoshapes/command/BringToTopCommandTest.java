package it.unisa.diem.sad.geoshapes.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

class BringToTopCommandTest {

    private DrawingModel model;
    private MyShape shape1;
    private MyShape shape2;
    private List<MyShape> shapes;
    private BringToTopCommand command;

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
        command = new BringToTopCommand(model, shapes);
    }

    @Test
    void testExecuteShouldBringShapesToTop() {
        command.execute();

        verify(model).bringToTop(shape1);
        verify(model).bringToTop(shape2);
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