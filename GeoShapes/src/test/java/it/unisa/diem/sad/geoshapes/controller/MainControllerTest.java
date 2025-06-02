/*
package it.unisa.diem.sad.geoshapes.controller;

import static org.mockito.Mockito.*;

import java.util.List;

import it.unisa.diem.sad.geoshapes.controller.command.CommandInvoker;
import it.unisa.diem.sad.geoshapes.controller.strategy.SelectionToolStrategy;
import it.unisa.diem.sad.geoshapes.controller.util.Clipboard;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;

import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MainControllerTest {

    @InjectMocks
    private MainController controller;

    @Mock
    private Clipboard clipboard;

    @Mock
    private Pane drawingArea;

    @Mock
    private SelectionToolStrategy selectionStrategy;

    @Mock
    private MenuItem pasteMenuItem;

    @Mock
    private DrawingModel model;

    @BeforeEach
    void setup() {
        controller.setClipboard(clipboard);
        controller.setDrawingArea(drawingArea);
        controller.setPasteMenuItem(pasteMenuItem);
        controller.setModel(model);
    }

    @Test
    void testHandleCopyWithSelection() {
        MyShape shape = mock(MyShape.class);
        List<MyShape> shapes = List.of(shape);

        controller.setCurrentStrategy(selectionStrategy);

        when(selectionStrategy.getSelectedShapes()).thenReturn(shapes);

        controller.handleCopy(new ActionEvent());

        verify(clipboard).copy(shapes);
        verify(pasteMenuItem, atLeastOnce()).setDisable(anyBoolean());
    }

    @Test
    void testHandleCutWithSelection() {
        MyShape shape = mock(MyShape.class);
        List<MyShape> shapes = List.of(shape);

        controller.setCurrentStrategy(selectionStrategy);

        when(selectionStrategy.getSelectedShapes()).thenReturn(shapes);

        controller.handleCut(new ActionEvent());

        verify(clipboard).copy(shapes);
        verify(model).removeShape(shape);
        verify(selectionStrategy).clearSelection();
        verify(pasteMenuItem, atLeastOnce()).setDisable(anyBoolean());
    }

    @Test
    void testUpdateClipboardMenuItems() {
        when(clipboard.isEmpty()).thenReturn(true);
        controller.updateClipboardMenuItems();
        verify(pasteMenuItem).setDisable(true);

        when(clipboard.isEmpty()).thenReturn(false);
        controller.updateClipboardMenuItems();
        verify(pasteMenuItem).setDisable(false);
    }
}*/