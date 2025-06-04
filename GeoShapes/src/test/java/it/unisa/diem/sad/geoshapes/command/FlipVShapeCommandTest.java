package it.unisa.diem.sad.geoshapes.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

class FlipVShapeCommandTest {

    private DrawingModel model;
    private MyShape shape1;
    private MyShape shape2;
    private List<MyShape> shapes;
    private FlipVShapeCommand command;

    @BeforeEach
    void setUp() {
        model = mock(DrawingModel.class);
        shape1 = mock(MyShape.class);
        shape2 = mock(MyShape.class);

        shapes = new ArrayList<>();
        shapes.add(shape1);
        shapes.add(shape2);

        command = new FlipVShapeCommand(model, shapes);
    }

    @Test
    void testExecuteShouldFlipAllShapesVertically() {
        command.execute();

        verify(model).flipVertical(shape1);
        verify(model).flipVertical(shape2);
    }

    @Test
    void testUndoShouldFlipAllShapesAgain() {
        command.undo();

        verify(model).flipVertical(shape1);
        verify(model).flipVertical(shape2);
    }
}