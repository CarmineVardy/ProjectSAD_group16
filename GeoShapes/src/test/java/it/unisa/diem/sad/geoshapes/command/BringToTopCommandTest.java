package it.unisa.diem.sad.geoshapes.command;

import it.unisa.diem.sad.geoshapes.command.BringToFrontCommand;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.MyColor;
import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.*;

import static org.mockito.Mockito.*;

/**
 * JUnit test suite for the {@link BringToTopCommand} class.
 * This suite aims to verify the correct execution and undo behavior of the command,
 * ensuring it properly interacts with the {@link DrawingModel} to change the Z-order
 * of shapes.
 */
@ExtendWith(MockitoExtension.class)
public class BringToTopCommandTest {

    private BringToFrontCommand command;

    @Mock
    private DrawingModel mockDrawingModel;

    private MyShape shape1;
    private MyShape shape2;
    private MyShape shape3;

    @BeforeEach
    void setUp() {
        // Initialize concrete MyShape instances for testing.
        shape1 = new MyLine(0, 0, 1, 1, 0, new MyColor(0, 0, 0));
        shape2 = new MyRectangle(0, 0, 1, 1, 0, new MyColor(0, 0, 0), new MyColor(1, 1, 1));
        shape3 = new MyLine(0, 0, 1, 1, 0, new MyColor(0.5, 0.5, 0.5));

        // Create a real list for the DrawingModel to return.
        // This allows .indexOf() to work naturally on the returned list instance.
        List<MyShape> initialModelShapes = new ArrayList<>(Arrays.asList(shape1, shape2, shape3));
        when(mockDrawingModel.getShapes()).thenReturn(initialModelShapes);

        // Remove the problematic lines that tried to mock indexOf on the result of getShapes().
        // When getShapes() returns a real list, indexOf() on that list is handled by Java's List implementation,
        // not by Mockito.
        // when(mockDrawingModel.getShapes().indexOf(shape1)).thenReturn(0); // This line and similar are removed.
        // when(mockDrawingModel.getShapes().indexOf(shape2)).thenReturn(1);
        // when(mockDrawingModel.getShapes().indexOf(shape3)).thenReturn(2);
    }

    /**
     * Tests the {@code execute} method when bringing a single shape to the top.
     * Verifies that `bringToTop` is called on the DrawingModel for the specific shape.
     */
    @Test
    void executeCallsBringToTopForSingleShape() {
        // Arrange
        BringToFrontCommand command = new BringToFrontCommand(mockDrawingModel, Collections.singletonList(shape1));

        // Act
        command.execute();

        // Assert
        verify(mockDrawingModel, times(1)).bringToTop(shape1);
    }

    /**
     * Tests the {@code execute} method when bringing multiple shapes to the top.
     * Verifies that `bringToTop` is called on the DrawingModel for each specified shape.
     */
    @Test
    void executeCallsBringToTopForMultipleShapes() {
        // Arrange
        List<MyShape> shapesToBringToTop = Arrays.asList(shape1, shape3);
        BringToFrontCommand command = new BringToFrontCommand(mockDrawingModel, shapesToBringToTop);

        // Act
        command.execute();

        // Assert
        verify(mockDrawingModel, times(1)).bringToTop(shape1);
        verify(mockDrawingModel, times(1)).bringToTop(shape3);
    }

    /**
     * Tests the {@code execute} method with an empty list of shapes.
     * Verifies that no interactions occur with the DrawingModel.
     */
    @Test
    void executeWithEmptyShapesListDoesNothing() {
        // Arrange
        BringToFrontCommand command = new BringToFrontCommand(mockDrawingModel, Collections.emptyList());

        // Act
        command.execute();

        // Assert
        verify(mockDrawingModel, never()).bringToTop(any(MyShape.class));
    }

    /**
     * Tests the {@code undo} method after bringing a single shape to the top.
     * Verifies that `moveToPosition` is called on the DrawingModel to restore the shape
     * to its original index.
     */
    @Test
    void undoRestoresOriginalPositionForSingleShape() {
        // Arrange
        // The mockDrawingModel.getShapes() returns a real list initialized in setUp.
        // execute() will correctly capture the original index of shape2 as 1.
        BringToFrontCommand command = new BringToFrontCommand(mockDrawingModel, Collections.singletonList(shape2));
        command.execute(); // Perform execute to capture original index

        // Act
        command.undo();

        // Assert
        // The original index of shape2 from the initial list (shape1, shape2, shape3) is 1.
        verify(mockDrawingModel, times(1)).moveToPosition(eq(shape2), eq(1));
    }

    /**
     * Tests the {@code undo} method after bringing multiple shapes to the top.
     * Verifies that `moveToPosition` is called for each shape in the correct reverse order,
     * restoring them to their respective original indices.
     */
    @Test
    void undoRestoresOriginalPositionsForMultipleShapes() {
        // Arrange
        // The mockDrawingModel.getShapes() returns a real list initialized in setUp: (shape1, shape2, shape3)
        // Original indices: shape1=0, shape2=1, shape3=2
        List<MyShape> shapesToBringToTop = Arrays.asList(shape1, shape2); // These shapes are processed in this order
        BringToFrontCommand command = new BringToFrontCommand(mockDrawingModel, shapesToBringToTop);

        command.execute(); // Execute to store original indices

        // Act
        command.undo();

        // Assert
        // Undo should call moveToPosition in reverse order of how shapes were processed
        // for their original indices.
        // shape2 was processed after shape1 during execute, so it's undone first.
        InOrder inOrder = inOrder(mockDrawingModel);
        inOrder.verify(mockDrawingModel).moveToPosition(eq(shape2), eq(1)); // shape2's original index was 1
        inOrder.verify(mockDrawingModel).moveToPosition(eq(shape1), eq(0)); // shape1's original index was 0
    }

    /**
     * Tests that calling {@code undo} without a prior {@code execute} operation
     * does not cause any errors and performs no actions on the DrawingModel.
     */
    @Test
    void undoWithoutPriorExecutionDoesNothing() {
        // Arrange
        BringToFrontCommand command = new BringToFrontCommand(mockDrawingModel, Collections.singletonList(shape1));

        // Act
        command.undo(); // Calling undo before execute

        // Assert
        verify(mockDrawingModel, never()).moveToPosition(any(MyShape.class), anyInt());
    }



}