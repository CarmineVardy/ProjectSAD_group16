package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.adapter.AdapterFactory;
import it.unisa.diem.sad.geoshapes.controller.command.*;
import it.unisa.diem.sad.geoshapes.controller.strategy.*;
import it.unisa.diem.sad.geoshapes.controller.util.UIUtils;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.observer.ShapeObserver;
import it.unisa.diem.sad.geoshapes.persistence.PersistenceService;
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
    private Button undoButton;
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
    private Rectangle clipRect; //Rectangle for clip the pane

    //Model, PersistenceService, UIUtils and ShapeMapping
    private DrawingModel model;
    private PersistenceService persistenceService;
    private UIUtils uiUtils;
    private ShapeMapping shapeMapping;

    //Pattern Strategy
    private ToolStrategy currentStrategy;
    private Map<ToggleButton, ToolStrategy> toolStrategies;

    //Pattern Adapter
    private AdapterFactory adapterFactory;

    //Pattern Command
    private CommandInvoker commandInvoker;


    @FXML
    public void initialize() {
        initializeCoreComponents();
        configureDrawingArea();
        setupEventListeners();
        setDefaultUIState();
    }

    private void initializeCoreComponents() {

        model = new DrawingModel();
        model.attach(this);

        persistenceService = new PersistenceService();
        uiUtils = new UIUtils();
        shapeMapping = new ShapeMapping();

        //Patterns
        commandInvoker = new CommandInvoker();
        adapterFactory = new AdapterFactory();
        initializeToolStrategies();
    }

    private void initializeToolStrategies() {
        toolStrategies = new HashMap<>();
        toolStrategies.put(selectionButton, new SelectionToolStrategy(drawingArea, shapeMapping, this));
        toolStrategies.put(lineButton, new LineToolStrategy(drawingArea, this));
        toolStrategies.put(rectangleButton, new RectangleToolStrategy(drawingArea, this));
        toolStrategies.put(ellipseButton, new EllipseToolStrategy(drawingArea, this));
    }

    private void configureDrawingArea() {
        clipRect = new Rectangle();
        drawingArea.setClip(clipRect);

        // Lega le dimensioni del clipRect alle dimensioni dell'area di disegno
        clipRect.widthProperty().bind(drawingArea.widthProperty());
        clipRect.heightProperty().bind(drawingArea.heightProperty());

        // Aggiungi ascoltatori per ridisegnare le forme quando l'area di disegno viene ridimensionata
        drawingArea.widthProperty().addListener((obs, oldVal, newVal) -> rebuildShapes());
        drawingArea.heightProperty().addListener((obs, oldVal, newVal) -> rebuildShapes());
    }

    private void setupEventListeners() {
        setupToolToggleListener();
        setupColorPickerListeners();
        undoButton.setOnAction(event -> handleUndo());
        undoButton.disableProperty().bind(commandInvoker.canUndoProperty().not());
    }

    private void setupToolToggleListener() {
        toolToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            ToggleButton selectedToggle = (ToggleButton) newValue;
            if (selectedToggle == null) {
                toolToggleGroup.selectToggle(oldValue != null ? oldValue : selectionButton);
                return;
            }
            if (currentStrategy != null) {
                currentStrategy.reset(); // Resetta la strategia precedente prima di cambiarla
            }
            currentStrategy = toolStrategies.get(selectedToggle);
            currentStrategy.activate(borderColorPicker.getValue(), fillColorPicker.getValue());
        });
    }

    private void setDefaultUIState() {
        toolToggleGroup.selectToggle(selectionButton);
        borderColorPicker.setValue(Color.BLACK);
        fillColorPicker.setValue(Color.TRANSPARENT);
    }

    private void rebuildShapes() {
        if (currentStrategy != null)
            currentStrategy.reset();
        drawingArea.getChildren().clear();
        for (MyShape modelShape : shapeMapping.getModelShapes()) {
            Shape fxShape = adapterFactory.convertToJavaFx(modelShape, drawingArea.getWidth(), drawingArea.getHeight());
            shapeMapping.updateViewMapping(modelShape, fxShape);
            drawingArea.getChildren().add(fxShape);
        }
    }

    private void setupColorPickerListeners() {
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

    private void handleUndo() {
        if (commandInvoker.canUndo()) {
            commandInvoker.undo();
        }
    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        if (currentStrategy != null) {
            currentStrategy.handleMousePressed(event);
        }
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        if (currentStrategy != null) {
            currentStrategy.handleMouseDragged(event);
        }
    }

    @FXML
    private void handleMouseReleased(MouseEvent event) {
        if (currentStrategy != null) {
            currentStrategy.handleMouseReleased(event);
        }
    }

    @FXML
    public void handleMouseMoved(MouseEvent event) {
        if (currentStrategy != null) {
            currentStrategy.handleMouseMoved(event);
        }
    }


    @Override
    public void onCreateShape(Shape shape) {
        Command createCommand = new CreateShapeCommand(model, adapterFactory.convertToModel(shape, drawingArea.getWidth(), drawingArea.getHeight()));
        commandInvoker.executeCommand(createCommand);
    }

    @Override
    public void onDeleteShape(Shape shape) {
        Command deleteCommand = new DeleteShapeCommand(model, shapeMapping.getModelShape(shape));
        commandInvoker.executeCommand(deleteCommand);
    }

    @Override
    public void onModifyShape(Shape shape) {
        Command modifyShapeCommand = new ModifyShapeCommand(model, shapeMapping.getModelShape(shape), shapeMapping.getModelShape(shape).clone(), adapterFactory.convertToModel(shape, drawingArea.getWidth(), drawingArea.getHeight()));
        commandInvoker.executeCommand(modifyShapeCommand);
    }


    @Override
    public void onSelectionMenuOpened(Shape viewShape, double x, double y) {
        MyShape modelShape = shapeMapping.getModelShape(viewShape);
        if (modelShape == null) return;

        ContextMenu menu = uiUtils.getSelectionShapeMenu();

        boolean isLine = modelShape instanceof MyLine || viewShape instanceof javafx.scene.shape.Line;
        uiUtils.setSelectMenuItemVisibleByLabel("Fill Color:", !isLine);

        for (MenuItem item : menu.getItems()) {
            if ("Delete".equals(item.getText())) {
                item.setOnAction(e -> {
                    onDeleteShape(viewShape);
                });
            } else if (item instanceof CustomMenuItem customItem &&
                    customItem.getContent() instanceof HBox hbox) {

                Label label = null;
                ColorPicker picker = null;

                for (Node node : hbox.getChildren()) {
                    if (node instanceof Label l) label = l;
                    else if (node instanceof ColorPicker cp) picker = cp;
                }

                if (label == null || picker == null) continue;

                if (label.getText().contains("Border")) {
                    final ColorPicker borderPicker = picker; // Crea una variabile finale
                    borderPicker.setValue((Color) adapterFactory.convertToJavaFxColor(modelShape.getBorderColor()));
                    borderPicker.setOnAction(e -> currentStrategy.handleBorderColorChange(borderPicker.getValue()));
                } else if (label.getText().contains("Fill")) {
                    final ColorPicker fillPicker = picker; // Crea una variabile finale
                    fillPicker.setValue((Color) adapterFactory.convertToJavaFxColor(modelShape.getFillColor()));
                    fillPicker.setOnAction(e -> currentStrategy.handleFillColorChange(fillPicker.getValue()));
                }
            }
        }

        Point2D screenPoint = viewShape.localToScreen(x, y);
        if (screenPoint != null) {
            menu.show(viewShape, screenPoint.getX(), screenPoint.getY());
        } else {
            menu.show(viewShape, x, y);
        }

        menu.setOnHidden(e -> onSelectionMenuClosed());
    }


    public void onSelectionMenuClosed() {
        ContextMenu menu = uiUtils.getSelectionShapeMenu();
        if (menu != null && menu.isShowing()) {
            menu.hide();
        }
    }

    @Override
    public void update(String eventType, MyShape shape) {
        switch (eventType) {
            case "CREATE":
                Shape javafxShape = adapterFactory.convertToJavaFx(shape, drawingArea.getWidth(), drawingArea.getHeight());
                drawingArea.getChildren().add(javafxShape);
                shapeMapping.register(shape, javafxShape);
                currentStrategy.reset();
                model.printAllShapes();
                System.out.print(shapeMapping.getViewShapes().size() + " " + drawingArea.getChildren().size() + "\n");
                break;
            case "DELETE":
                drawingArea.getChildren().remove(shapeMapping.getViewShape(shape));
                shapeMapping.unregister(shape);
                currentStrategy.reset();
                model.printAllShapes();
                System.out.print(shapeMapping.getViewShapes().size() + " " + drawingArea.getChildren().size() + "\n");
                break;
            case "MODIFY":
                Shape oldViewShape = shapeMapping.getViewShape(shape);
                int position = drawingArea.getChildren().indexOf(oldViewShape);
                drawingArea.getChildren().remove(oldViewShape);
                Shape newfxShape = adapterFactory.convertToJavaFx(shape, drawingArea.getWidth(), drawingArea.getHeight());
                shapeMapping.updateViewMapping(shape, newfxShape);
                if (position >= 0 && position <= drawingArea.getChildren().size()) {
                    drawingArea.getChildren().add(position, newfxShape);
                } else {
                    drawingArea.getChildren().add(newfxShape);
                }
                currentStrategy.reset();
                model.printAllShapes();
                System.out.print(shapeMapping.getViewShapes().size() + " " + drawingArea.getChildren().size() + "\n");
                break;
            case "CLEARALL":
                drawingArea.getChildren().clear();
                shapeMapping.clear();
                model.printAllShapes();
                System.out.print(shapeMapping.getViewShapes().size() + " " + drawingArea.getChildren().size() + "\n");
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
                    if (currentStrategy != null)
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