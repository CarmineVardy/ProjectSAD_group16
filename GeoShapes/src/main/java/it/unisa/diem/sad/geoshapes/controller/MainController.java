package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.adapter.EllipseAdapter;
import it.unisa.diem.sad.geoshapes.adapter.LineAdapter;
import it.unisa.diem.sad.geoshapes.adapter.RectangleAdapter;
import it.unisa.diem.sad.geoshapes.adapter.ShapeAdapter;
import it.unisa.diem.sad.geoshapes.controller.command.*;
import it.unisa.diem.sad.geoshapes.controller.strategy.*;
import it.unisa.diem.sad.geoshapes.controller.util.UIUtils;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import it.unisa.diem.sad.geoshapes.model.shapes.MyRectangle;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.MyColor;
import it.unisa.diem.sad.geoshapes.observer.ShapeObserver;
import it.unisa.diem.sad.geoshapes.perstistence.PersistenceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainController implements ShapeObserver, InteractionCallback {

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
    private ShapeAdapter currentAdapter;
    private Map<Class<? extends MyShape>, ShapeAdapter> shapeAdapters;

    //Pattern Command
    private CommandInvoker commandInvoker;

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
        commandInvoker = new CommandInvoker();
        uiUtils = new UIUtils(); // Instantiate UIUtils

        setupPanel();
        setupDefaultUIState();

        initializeShapeAdapters();

        initializeToolStrategies();
        setupToolListeners();
        setupColoPickerListeners();
    }

    private void setupDefaultUIState() {
        selectionButton.setSelected(true); // Set selection tool as default
        borderColorPicker.setValue(Color.BLACK);
        fillColorPicker.setValue(Color.TRANSPARENT);
        uiUtils.setupSelectionContextMenu(); // Initialize context menu for selection tool
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
        toolStrategies.put(selectionButton, new SelectionToolStrategy(drawingArea, shapeMapping, this));
        toolStrategies.put(lineButton, new LineToolStrategy(drawingArea, borderColorPicker, fillColorPicker, this));
        toolStrategies.put(rectangleButton, new RectangleToolStrategy(drawingArea, borderColorPicker, fillColorPicker, this));
        toolStrategies.put(ellipseButton, new EllipseToolStrategy(drawingArea, borderColorPicker, fillColorPicker, this));
    }

    private void setupToolListeners() {
        toolToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (currentStrategy != null)
                currentStrategy.reset();
            currentStrategy = toolStrategies.get((ToggleButton) newValue);
        });
    }

    private void setupColoPickerListeners() {

        borderColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {

            if (currentStrategy != null) {
                currentStrategy.handleBorderColorChange(newColor);
            }
        });

        fillColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleFillColorChange(newColor);
            }
        });


    }

    private void initializeShapeAdapters() {
        shapeAdapters = new HashMap<>();
        shapeAdapters.put(MyLine.class, new LineAdapter());
        shapeAdapters.put(MyRectangle.class, new RectangleAdapter());
        shapeAdapters.put(MyEllipse.class, new EllipseAdapter());
    }

    private void setAdapter(MyShape shape) {
        if (shape == null) {
            currentAdapter = null;
            return;
        }
        currentAdapter = shapeAdapters.get(shape.getClass());
    }

    private void rebuildShapes() {
        if (currentStrategy != null)
            currentStrategy.reset();
        drawingArea.getChildren().clear();
        for (MyShape modelShape : shapeMapping.getAllModelShapes()) {
            setAdapter(modelShape);
            if (currentAdapter != null) {
                Shape fxShape = currentAdapter.getFxShape(modelShape, drawingArea.getWidth(), drawingArea.getHeight());
                shapeMapping.updateViewMapping(modelShape, fxShape);
                drawingArea.getChildren().add(fxShape);
            }
        }
    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        if (currentStrategy != null) {
            currentStrategy.handlePressed(event);
        }
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        if (currentStrategy != null) {
            currentStrategy.handleDragged(event);
        }
    }

    @FXML
    private void handleMouseReleased(MouseEvent event) {
        if (currentStrategy != null) {
            currentStrategy.handleReleased(event);
        }
    }


    @Override
    public void onCreateShape(MyShape shape) {
        Command createCommand = new CreateShapeCommand(model, shape);
        commandInvoker.setCommand(createCommand);
        commandInvoker.executeCommand();
        currentStrategy.reset();
    }

    @Override
    public void onDeleteShape(MyShape shape) {
        Command deleteCommand = new DeleteShapeCommand(model, shape);
        commandInvoker.setCommand(deleteCommand);
        commandInvoker.executeCommand();
        currentStrategy.reset();
    }

    @Override
    public void onChangeBorderColor(MyShape shape, Color color) {
        MyColor myColor = new MyColor(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity());
        Command changeBorderColorCommand = new ChangeBorderColorCommand(model, shape, myColor);
        commandInvoker.setCommand(changeBorderColorCommand);
        commandInvoker.executeCommand();
    }

    @Override
    public void onChangeFillColor(MyShape shape, Color color) {
        MyColor myColor = new MyColor(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity());
        Command changeFillColorCommand = new ChangeFillColorCommand(model, shape, myColor);
        commandInvoker.setCommand(changeFillColorCommand);
        commandInvoker.executeCommand();
    }


    @Override
    public void onSelectionMenuOpened(Shape viewShape, MyShape modelShape, double x, double y) {
        if (modelShape == null) {
            return;
        }
        if (modelShape instanceof MyLine || viewShape instanceof javafx.scene.shape.Line) {
            uiUtils.setSelectMenuItemVisibleByLabel("Fill Color:", false);
        } else
            uiUtils.setSelectMenuItemVisibleByLabel("Fill Color:", true);
        ContextMenu selectionShapeMenu = uiUtils.getSelectionShapeMenu();
        initializeMenuItems(selectionShapeMenu, modelShape);
        initializeColorPickers(selectionShapeMenu, viewShape, modelShape);
        showMenuAtPosition(selectionShapeMenu, viewShape, x, y);
    }

    private void initializeMenuItems(ContextMenu menu, MyShape modelShape) {
        for (MenuItem item : menu.getItems()) {
            if ("Delete".equals(item.getText())) {
                item.setOnAction(event -> {
                    onDeleteShape(modelShape);
                    currentStrategy.reset();
                });
            }
        }
    }

    private void initializeColorPickers(ContextMenu menu, Shape viewShape, MyShape modelShape) {
        for (MenuItem item : menu.getItems()) {
            if (!(item instanceof CustomMenuItem)) {
                continue;
            }
            CustomMenuItem customItem = (CustomMenuItem) item;
            if (!(customItem.getContent() instanceof HBox)) {
                continue;
            }
            HBox hbox = (HBox) customItem.getContent();
            Label label = null;
            ColorPicker colorPicker = null;
            for (Node node : hbox.getChildren()) {
                if (node instanceof Label) {
                    label = (Label) node;
                } else if (node instanceof ColorPicker) {
                    colorPicker = (ColorPicker) node;
                }
            }
            if (label == null || colorPicker == null) {
                continue;
            }
            final ColorPicker finalColorPicker = colorPicker;
            if (label.getText().contains("Border Color")) {
                finalColorPicker.setValue((Color) currentAdapter.convertToJavaFxColor(modelShape.getBorderColor()));

                finalColorPicker.setOnAction(event ->
                        onChangeBorderColor(modelShape, finalColorPicker.getValue())
                );
            } else if (label.getText().contains("Fill Color")) {
                finalColorPicker.setValue((Color) currentAdapter.convertToJavaFxColor(modelShape.getFillColor()));

                finalColorPicker.setOnAction(event ->
                        onChangeFillColor(modelShape, finalColorPicker.getValue())
                );
            }
        }
    }

    private void showMenuAtPosition(ContextMenu menu, Shape viewShape, double x, double y) {
        Point2D screenPoint = viewShape.localToScreen(x, y);
        if (screenPoint != null) {
            menu.show(viewShape, screenPoint.getX(), screenPoint.getY());
        } else {
            menu.show(viewShape, x, y);
        }
    }


    @Override
    public void onSelectionMenuClosed() {
        if (uiUtils.getSelectionShapeMenu() != null && uiUtils.isSelectionShapeMenuShowing()) {
            uiUtils.getSelectionShapeMenu().hide();
        }
    }


    @Override
    public void update(String eventType, MyShape shape) {
        switch (eventType) {
            case "CREATE":
                setAdapter(shape);
                if (currentAdapter != null) {
                    Shape fxShape = currentAdapter.getFxShape(shape, drawingArea.getWidth(), drawingArea.getHeight());
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
                break;
            case "MODIFYBORDERCOLOR":
                currentStrategy.reset();
                shapeMapping.getViewShape(shape).setStroke(currentAdapter.convertToJavaFxColor(shape.getBorderColor()));
                break;
            case "MODIFYFILLCOLOR":
                currentStrategy.reset();
                shapeMapping.getViewShape(shape).setFill(currentAdapter.convertToJavaFxColor(shape.getFillColor()));
                break;
            case "CLEARALL":
                drawingArea.getChildren().clear(); // Clear UI
                shapeMapping.clear(); // Clear Mapping
                break;
        }
    }


    @FXML
    public void handleLoad(ActionEvent actionEvent) {
        FileChooser fileChooser = uiUtils.createFileChooser("Open Drawing", null, persistenceService.getDirectoryName());
        File file = fileChooser.showOpenDialog(drawingArea.getScene().getWindow());
        if (file != null) {
            boolean proceed = uiUtils.showConfirmDialog("Confirm Load", "Load New Drawing", "Loading a new drawing will discard any unsaved changes. Continue?");
            if (proceed) {
                try {
                    List<MyShape> loadedShapes = persistenceService.loadDrawing(file);
                    model.clearShapes(); //Clear Model
                    currentStrategy.reset(); //Reset Strategy
                    for (MyShape shape : loadedShapes) {
                        model.addShape(shape);
                    }
                    uiUtils.showSuccessDialog("Load Successful", "Drawing loaded from " + file.getName());
                } catch (Exception e) {
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
            } catch (Exception e) {
                uiUtils.showErrorDialog("Save Error", "Could not save drawing: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleSaveAs(ActionEvent actionEvent) {
        FileChooser fileChooser = uiUtils.createFileChooser("Save Drawing As...", persistenceService.getFileName() != null ? persistenceService.getFileName() : "myDrawing.geoshapes", persistenceService.getDirectoryName());
        File file = fileChooser.showSaveDialog(drawingArea.getScene().getWindow());
        if (file != null) {
            String filePath = file.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".geoshapes")) {
                file = new File(filePath + ".geoshapes");
            }
            try {
                persistenceService.saveDrawing(model.getShapes(), file);
                uiUtils.showSuccessDialog("Save Successful", "Drawing saved to " + file.getName());
            } catch (Exception e) {
                uiUtils.showErrorDialog("Save Error", "Could not save drawing: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


}