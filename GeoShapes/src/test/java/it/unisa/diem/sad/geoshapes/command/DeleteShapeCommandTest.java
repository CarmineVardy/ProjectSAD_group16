package it.unisa.diem.sad.geoshapes.command;

import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

class DeleteShapeCommandTest {

    private DrawingModel model;
    private MyShape shape1;
    private MyShape shape2;
    private List<MyShape> shapes;
    private DeleteShapeCommand command;

    @BeforeEach
    void setUp() {
        model = mock(DrawingModel.class);
        shape1 = mock(MyShape.class);
        shape2 = mock(MyShape.class);

        shapes = new ArrayList<>();
        shapes.add(shape1);
        shapes.add(shape2);

        command = new DeleteShapeCommand(model, shapes);
    }

    @Test
    void testExecuteShouldRemoveAllShapes() {
        command.execute();

        verify(model).removeShape(shape1);
        verify(model).removeShape(shape2);
    }

    @Test
    void testUndoShouldAddShapesBackInReverseOrder() {
        command.undo();

        verify(model).addShape(shape2);
        verify(model).addShape(shape1);
    }
}
