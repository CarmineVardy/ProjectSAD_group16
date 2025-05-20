package com.example.geoshapes.controller;

import com.example.geoshapes.adapter.EllipseAdapter;
import com.example.geoshapes.adapter.LineAdapter;
import com.example.geoshapes.adapter.RectangleAdapter;
import com.example.geoshapes.adapter.ShapeAdapter;
import com.example.geoshapes.controller.command.Command;
import com.example.geoshapes.controller.command.CreateShapeCommand;
import com.example.geoshapes.controller.command.DeleteShapeCommand;
import com.example.geoshapes.controller.strategy.*;
import com.example.geoshapes.controller.util.UIUtils;
import com.example.geoshapes.model.DrawingModel;
import com.example.geoshapes.model.shapes.MyEllipse;
import com.example.geoshapes.model.shapes.MyLine;
import com.example.geoshapes.model.shapes.MyRectangle;
import com.example.geoshapes.model.shapes.MyShape;
import com.example.geoshapes.observer.ShapeObserver;
import com.example.geoshapes.perstistence.PersistenceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainController implements ShapeObserver {

    @FXML
    private MenuItem menuItemLoad;
    @FXML
    private MenuItem menuItemSave;
    @FXML
    private MenuItem menuItemSaveAs;
    @FXML
    private ToggleGroup toolToggleGroup;
    @FXML
    private ToggleButton selectionButton;
    @FXML
    private ToggleButton lineButton;
    @FXML
    private ToggleButton rectangleButton;
    @FXML
    private ToggleButton ellipseButton;
    @FXML
    private ColorPicker borderColorPicker;
    @FXML
    private ColorPicker fillColorPicker;
    @FXML
    private Pane drawingArea;

    //Rectangle for clip the pane
    private Rectangle clipRect;

    //Model
    private DrawingModel model;

    //Mapping between JavaFX shapes and model shapes
    private ShapeMapping shapeMapping;

    //Pattern Strategy
    private ToolStrategy currentStrategy;
    private Map<ToggleButton, ToolStrategy> toolStrategies;

    //Pattern Adapter
    private ShapeAdapter adapter;

    //Service for Load and Save
    private PersistenceService persistenceService;

    //Utility Class for UI elements
    private UIUtils uiUtils;

    @FXML
    public void initialize() {

        //Initialize Model and Observer to him
        model = new DrawingModel();
        model.attach(this);

        //Initialize Mapping, PersistenceService and UIUtils
        shapeMapping = new ShapeMapping();
        persistenceService = new PersistenceService();
        uiUtils = new UIUtils(); // Instantiate UIUtils

        setupDefaultUIState();
        setupPanel();
        initializeToolStrategies();
        setupToolListeners();
    }

    private void setupDefaultUIState() {
        selectionButton.setSelected(true); // Set selection tool as default
        borderColorPicker.setValue(Color.BLACK);
        fillColorPicker.setValue(Color.TRANSPARENT);
        uiUtils.setupSelectionContextMenu(); // Setup context menu for selection tool
    }

    private void setupPanel() {
        clipRect = new Rectangle();
        drawingArea.setClip(clipRect);
        clipRect.widthProperty().bind(drawingArea.widthProperty());
        clipRect.heightProperty().bind(drawingArea.heightProperty());
        drawingArea.widthProperty().addListener((obs, oldVal, newVal) -> rebuildShapes());
        drawingArea.heightProperty().addListener((obs, oldVal, newVal) -> rebuildShapes());
    }

    private void initializeToolStrategies() {
        toolStrategies = new HashMap<>();
        toolStrategies.put(selectionButton, new SelectionToolStrategy(drawingArea, shapeMapping));
        toolStrategies.put(lineButton, new LineToolStrategy(drawingArea, borderColorPicker, fillColorPicker));
        toolStrategies.put(rectangleButton, new RectangleToolStrategy(drawingArea, borderColorPicker, fillColorPicker));
        toolStrategies.put(ellipseButton, new EllipseToolStrategy(drawingArea, borderColorPicker, fillColorPicker));
    }

    private void setupToolListeners() {
        toolToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            uiUtils.hideSelectionShapeMenu();
            currentStrategy = toolStrategies.get((ToggleButton) newValue);
        });
    }

    private void rebuildShapes() {
        MyShape previouslySelectedModel = null;
        SelectionToolStrategy selectionStrategy = null;

        if (currentStrategy instanceof SelectionToolStrategy) {
            selectionStrategy = (SelectionToolStrategy) currentStrategy;
            previouslySelectedModel = selectionStrategy.getSelectedModelShape();
            // The strategy's own fxShape reference will become stale.
            // We will call updateSelectedFxShape after new shapes are created.
            // For now, don't remove decoration, as it might flicker.
            // The updateSelectedFxShape will handle new decoration.
        }

        drawingArea.getChildren().clear();
        shapeMapping.clear(); // Clear old mappings

        for (MyShape shape : model.getShapes()) {
            setAdapter(shape);
            if (adapter != null) {
                Shape fxShape = adapter.getFxShape(shape, drawingArea.getWidth(), drawingArea.getHeight());
                drawingArea.getChildren().add(fxShape);
                shapeMapping.register(shape, fxShape); // Register new mapping

                if (selectionStrategy != null && shape == previouslySelectedModel) {
                    // If this shape was the one selected, tell the strategy to update its fxShape and re-apply decorator
                    selectionStrategy.updateSelectedFxShape(fxShape);
                }
            }
        }

        // If the previously selected model shape no longer exists or has no view, ensure selection is fully reset
        if (selectionStrategy != null && previouslySelectedModel != null && shapeMapping.getViewShape(previouslySelectedModel) == null) {
            selectionStrategy.resetSelection();
        } else if (selectionStrategy != null && previouslySelectedModel == null && selectionStrategy.getSelectedModelShape() != null) {
            // If nothing was supposed to be selected but strategy still thinks something is
            selectionStrategy.resetSelection();
        }
    }






    // This method will be called by the context menu's delete action
    private void deleteSelectedShape(MyShape shapeToDelete) {
        if (shapeToDelete != null) {
            Command deleteCommand = new DeleteShapeCommand(model, shapeToDelete);
            deleteCommand.execute();
            // The update method (observer) will handle UI changes and deselecting if necessary
        }
    }


    @FXML
    private void handleMousePressed(MouseEvent event) {
        if (uiUtils.isSelectionShapeMenuShowing()) {
            uiUtils.hideSelectionShapeMenu(); // Hide if already visible and user clicks elsewhere
        }

        if (currentStrategy != null) {
            currentStrategy.handlePressed(event); // Strategy updates its internal selection state
        }


        // Controller decides if a context menu should be shown based on strategy's state
        if (event.getButton() == MouseButton.SECONDARY && currentStrategy instanceof SelectionToolStrategy) {
            SelectionToolStrategy selectionStrategy = (SelectionToolStrategy) currentStrategy;
            MyShape currentSelectedModel = selectionStrategy.getSelectedModelShape();
            Shape currentSelectedFx = selectionStrategy.getSelectedJavaFxShape();

            // Show menu if a shape is selected AND the right-click was on that selected shape
            if (currentSelectedModel != null && currentSelectedFx != null && currentSelectedFx.contains(event.getX(), event.getY())) {
                uiUtils.showSelectionShapeMenu(drawingArea, event.getScreenX(), event.getScreenY(), currentSelectedModel, this::deleteSelectedShape);
            }
            // If right-click was on empty space or a *different* shape, the strategy handles selection changes.
            // The controller doesn't need to deselect here; the strategy did it.
        }
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        // Do not allow dragging if context menu is active
        if (currentStrategy != null && !uiUtils.isSelectionShapeMenuShowing()) {
            currentStrategy.handleDragged(event);
        }
    }

    @FXML
    private void handleMouseReleased(MouseEvent event) {
        if (currentStrategy != null && !uiUtils.isSelectionShapeMenuShowing()) {
            currentStrategy.handleReleased(event);

            // In MainController.java
// ... within handleMouseReleased method ...
            if (!(currentStrategy instanceof SelectionToolStrategy)) {
                MyShape newShape = currentStrategy.getFinalShape(); // This now consumes the shape from strategy
                if (newShape != null) {
                    Command command = new CreateShapeCommand(model, newShape);
                    command.execute();
                    // The reset is now called regardless of whether a shape was created,
                    // as getFinalShape() returning null implies the strategy should reset.
                    // The strategies themselves handle their internal state for getFinalShape().
                }
                // Always reset the drawing strategy after a draw attempt (release)
                // to clear its state (preview, coordinates, etc.)
                if (currentStrategy instanceof LineToolStrategy) {
                    ((LineToolStrategy) currentStrategy).reset();
                } else if (currentStrategy instanceof RectangleToolStrategy) {
                    ((RectangleToolStrategy) currentStrategy).reset();
                } else if (currentStrategy instanceof EllipseToolStrategy) {
                    ((EllipseToolStrategy) currentStrategy).reset();
                }
            }

        }
    }

    private void setAdapter(MyShape shape) {
        if (shape instanceof MyLine) {
            adapter = new LineAdapter();
        } else if (shape instanceof MyRectangle) {
            adapter = new RectangleAdapter();
        } else if (shape instanceof MyEllipse) {
            adapter = new EllipseAdapter();
        } else {
            adapter = null;
        }
    }

    @Override
    public void update(String eventType, MyShape shape) {
        switch (eventType) {
            case "CREATE":
                setAdapter(shape);
                if (adapter != null) {
                    Shape fxShape = adapter.getFxShape(shape, drawingArea.getWidth(), drawingArea.getHeight());
                    drawingArea.getChildren().add(fxShape);
                    shapeMapping.register(shape, fxShape);
                }
                break;
            case "DELETE":
                Shape fxShapeToRemove = shapeMapping.getViewShape(shape);
                if (fxShapeToRemove != null) {
                    drawingArea.getChildren().remove(fxShapeToRemove);
                    shapeMapping.unregister(shape);
                }
                // If the deleted shape was selected, tell the selection strategy to reset
                if (currentStrategy instanceof SelectionToolStrategy) {
                    SelectionToolStrategy selectionStrategy = (SelectionToolStrategy) currentStrategy;
                    if (selectionStrategy.getSelectedModelShape() == shape) {
                        selectionStrategy.resetSelection();
                    }
                }
                uiUtils.hideSelectionShapeMenu(); // Hide menu if it was for the deleted shape
                break;
            case "MODIFY":
                // Find the old JavaFX shape, remove it
                Shape oldFxShape = shapeMapping.getViewShape(shape);
                if (oldFxShape != null) {
                    drawingArea.getChildren().remove(oldFxShape);
                }
                // Create new JavaFX shape with modified properties
                setAdapter(shape);
                if (adapter != null) {
                    Shape newFxShape = adapter.getFxShape(shape, drawingArea.getWidth(), drawingArea.getHeight());
                    drawingArea.getChildren().add(newFxShape);
                    shapeMapping.register(shape, newFxShape); // Re-register with new FxShape

                    // If the modified shape was selected, update the selection strategy's FxShape reference
                    if (currentStrategy instanceof SelectionToolStrategy) {
                        SelectionToolStrategy selectionStrategy = (SelectionToolStrategy) currentStrategy;
                        if (selectionStrategy.getSelectedModelShape() == shape) {
                            selectionStrategy.updateSelectedFxShape(newFxShape);
                        }
                    }
                }
                break;
            // Potentially other events like "CLEAR_ALL"
        }
    }


    @FXML
    public void handleLoad(ActionEvent actionEvent) {
        File initialDir = persistenceService.getCurrentFile() != null ? persistenceService.getCurrentFile().getParentFile() : null;
        FileChooser fileChooser = uiUtils.createFileChooser("Open Drawing", null, initialDir);
        File file = fileChooser.showOpenDialog(drawingArea.getScene().getWindow());

        if (file != null) {
            boolean proceed = uiUtils.showConfirmDialog("Confirm Load", "Load New Drawing", "Loading a new drawing will discard any unsaved changes. Continue?");
            if (proceed) {
                try {
                    List<MyShape> loadedShapes = persistenceService.loadDrawing(file);
                    model.clearShapes(); // This should trigger observer updates or be handled carefully

                    // Before clearing UI, ensure selection is reset if selection tool is active
                    if (currentStrategy instanceof SelectionToolStrategy) {
                        ((SelectionToolStrategy) currentStrategy).resetSelection();
                    }
                    drawingArea.getChildren().clear(); // Clear UI
                    shapeMapping.clear();          // Clear mappings

                    for (MyShape shape : loadedShapes) {
                        // Use model to add shapes, which will trigger 'CREATE' in observer
                        model.addShape(shape); // Add a method to model for this to avoid command overhead for loading
                        // or just call update("CREATE", shape) if model.addShape triggers it.
                        // For simplicity, assuming model.addShape (or similar) will notify observers.
                    }
                    model.notifyObservers("RELOAD_COMPLETE", null); // Or let individual adds notify
                    rebuildShapes(); // Or rely on individual "CREATE" notifications from model

                    persistenceService.setCurrentFile(file);
                    uiUtils.showSuccessDialog("Load Successful", "Drawing loaded from " + file.getName());
                } catch (IOException | ClassNotFoundException e) {
                    uiUtils.showErrorDialog("Load Error", "Could not load drawing: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }


    @FXML
    public void handleSave(ActionEvent actionEvent) {
        if (persistenceService.getCurrentFile() == null) {
            handleSaveAs(actionEvent);
        } else {
            try {
                persistenceService.saveDrawing(model.getShapes(), persistenceService.getCurrentFile());
                uiUtils.showSuccessDialog("Save Successful", "Drawing updated in " + persistenceService.getCurrentFile().getName());
            } catch (IOException e) {
                uiUtils.showErrorDialog("Save Error", "Could not save drawing: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleSaveAs(ActionEvent actionEvent) {
        String suggestedFileName = "myDrawing.geoshapes";
        File initialDirectory = null;
        if (persistenceService.getCurrentFile() != null) {
            suggestedFileName = persistenceService.getCurrentFile().getName();
            initialDirectory = persistenceService.getCurrentFile().getParentFile();
        }

        FileChooser fileChooser = uiUtils.createFileChooser("Save Drawing As...", suggestedFileName, initialDirectory);
        File file = fileChooser.showSaveDialog(drawingArea.getScene().getWindow());

        if (file != null) {
            String filePath = file.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".geoshapes")) {
                file = new File(filePath + ".geoshapes");
            }
            try {
                persistenceService.saveDrawing(model.getShapes(), file);
                persistenceService.setCurrentFile(file);
                uiUtils.showSuccessDialog("Save Successful", "Drawing saved to " + file.getName());
            } catch (IOException e) {
                uiUtils.showErrorDialog("Save Error", "Could not save drawing: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}