package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.adapter.AdapterFactory;
import it.unisa.diem.sad.geoshapes.controller.command.*;
import it.unisa.diem.sad.geoshapes.controller.strategy.*;
import it.unisa.diem.sad.geoshapes.controller.util.GridRenderer;
import it.unisa.diem.sad.geoshapes.controller.util.ShapeClipboard;
import it.unisa.diem.sad.geoshapes.controller.util.ShapeClipboardImpl;
import it.unisa.diem.sad.geoshapes.controller.util.UIUtils;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyLine;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.model.util.GridSettings;
import it.unisa.diem.sad.geoshapes.observer.ShapeObserver;
import it.unisa.diem.sad.geoshapes.persistence.PersistenceService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.shape.Line;


public class MainController implements ShapeObserver, InteractionCallback {

    @FXML
    private Slider zoomSlider;
    @FXML
    private Label zoomPercentageLabel;
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
    private Button shapesManagerButton;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button sendToBackButton;
    @FXML
    private Button bringToFrontButton;
    @FXML
    private Group zoomGroup;
    @FXML
    private MenuItem menuItemPaste;
    @FXML
    private CheckMenuItem menuItemToggleGrid;
    @FXML
    private RadioMenuItem cellSize10;
    @FXML
    private RadioMenuItem cellSize20;
    @FXML
    private RadioMenuItem cellSize50;
    @FXML
    private ToggleGroup cellSizeToggleGroup;
    @FXML
    private Pane drawingArea;

    private Rectangle clipRect;

    @FXML
    private Pane contentPane;

    private DrawingModel model;
    private PersistenceService persistenceService;
    private UIUtils uiUtils;
    private ShapeMapping shapeMapping;
    private double currentZoomLevel = 1.0;

    private ToolStrategy currentStrategy;
    private Map<ToggleButton, ToolStrategy> toolStrategies;

    private AdapterFactory adapterFactory;

    private CommandInvoker commandInvoker;

    private BooleanProperty hasShapeSelected = new SimpleBooleanProperty(false);
    private BooleanProperty isLineSelected = new SimpleBooleanProperty(false);
    private double previousScale = 1.0;
    private double baseDrawingAreaWidth=1024;
    private double baseDrawingAreaHeight=768;

    private ShapeClipboard clipboard = new ShapeClipboardImpl();
    private Shape selectedShape;

    private GridSettings gridSettings;
    private GridRenderer gridRenderer;

    private RadioMenuItem lastSelectedSizeItem;

    @FXML
    public void initialize() {
        initializeCoreComponents();
        configureDrawingArea();
        setupEventListeners();
        setDefaultUIState();
        zoomSlider.setMin(0.25); // Opzionale: imposta un valore minimo (es. 10%)
        zoomSlider.setMax(2.5);  // Imposta il massimo a 2.5 per il 250% di zoom
        zoomSlider.setValue(1.0); // Inizia con uno zoom del 100% (scala 1.0)

        // Aggiorna l'etichetta dello zoom per mostrare il valore iniziale
        zoomPercentageLabel.setText(String.format("%.0f%%", zoomSlider.getValue() * 100));



        menuItemPaste.setDisable(true);
        Platform.runLater(() -> {
            drawingArea.getScene().setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode() == KeyCode.DELETE) {
                    handleDelete();
                }
            });
        });
        cellSize20.setSelected(true); // default
        menuItemToggleGrid.setSelected(gridSettings.isGridEnabled());
        drawingArea.widthProperty().addListener((obs, oldVal, newVal) -> {
            gridRenderer.drawGrid();
        });

        drawingArea.heightProperty().addListener((obs, oldVal, newVal) -> {
            gridRenderer.drawGrid();
        });
    }


    private void applyZoom(double scale) {
            BigDecimal bd = new BigDecimal(Double.toString(scale));
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            final double newScale = bd.doubleValue();

            drawingArea.setScaleX(newScale);
            drawingArea.setScaleY(newScale);
            zoomPercentageLabel.setText(String.format("%.0f%%", newScale * 100));

        }

    private void initializeCoreComponents() {
        model = new DrawingModel();
        model.attach(this);

        persistenceService = new PersistenceService();
        uiUtils = new UIUtils();
        shapeMapping = new ShapeMapping();

        commandInvoker = new CommandInvoker();
        adapterFactory = new AdapterFactory();

        gridSettings = new GridSettings();

        initializeToolStrategies();
    }

    private void initializeToolStrategies() {
        toolStrategies = new HashMap<>();
        toolStrategies.put(selectionButton, new SelectionToolStrategy(drawingArea, zoomGroup, shapeMapping, this));
        toolStrategies.put(lineButton, new LineToolStrategy(zoomGroup, drawingArea, this));
        toolStrategies.put(rectangleButton, new RectangleToolStrategy(zoomGroup, drawingArea, this));
        toolStrategies.put(ellipseButton, new EllipseToolStrategy(zoomGroup, drawingArea, this));
    }

    private void configureDrawingArea() {
        clipRect = new Rectangle();
        drawingArea.setClip(clipRect);

        clipRect.widthProperty().bind(drawingArea.widthProperty());
        clipRect.heightProperty().bind(drawingArea.heightProperty());

        drawingArea.setPrefWidth(baseDrawingAreaWidth);
        drawingArea.setPrefHeight(baseDrawingAreaHeight);
        zoomGroup.setScaleX(1.0);
        zoomGroup.setScaleY(1.0);
        gridRenderer = new GridRenderer(drawingArea, gridSettings);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    }

    private void setupEventListeners() {
        setupToolToggleListener();
        setupColorPickerListeners();
        fillColorPicker.disableProperty().bind(isLineSelected);
        undoButton.setOnAction(event -> handleUndo());
        undoButton.disableProperty().bind(commandInvoker.canUndoProperty().not());
        sendToBackButton.disableProperty().bind(hasShapeSelected.not());
        bringToFrontButton.disableProperty().bind(hasShapeSelected.not());

        zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double scale = newValue.doubleValue();
            applyZoom(scale);
        });


        drawingArea.prefHeightProperty().addListener((observable, oldValue, newValue) -> {
           zoomGroup.prefHeight(newValue.doubleValue());}
           );

       drawingArea.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
            zoomGroup.prefWidth(newValue.doubleValue());}
        );










    }

    private void setupToolToggleListener() {
        toolToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            ToggleButton selectedToggle = (ToggleButton) newValue;
            if (selectedToggle == null) {
                toolToggleGroup.selectToggle(oldValue != null ? oldValue : selectionButton);
                return;
            }
            if (currentStrategy != null) {
                currentStrategy.reset();
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

    public BooleanProperty hasShapeSelectedProperty() {return hasShapeSelected;}

    public BooleanProperty isLineSelectedProperty() {return isLineSelected;}

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

    @FXML
    public void handleBringToFront(ActionEvent actionEvent) {
        if (currentStrategy != null) {
            currentStrategy.handleBringToFront(actionEvent);
        }
    }

    @FXML
    public void handleSendToBack(ActionEvent actionEvent) {
        if (currentStrategy != null) {
            currentStrategy.handleSendToBack(actionEvent);
        }
    }

    @FXML
    public void handleShowShapesManager(ActionEvent actionEvent) {
    }

    @Override
    public void onShapeSelected(Shape shape) {
        hasShapeSelected.set(true);
        isLineSelected.set(shape instanceof Line);
    }

    @Override
    public void onLineSelected(boolean selected) {
        isLineSelected.set(selected);
    }

    @Override
    public void onShapeDeselected() {
        hasShapeSelected.set(false);
        isLineSelected.set(false);
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
    public void onBringToFront(Shape shape) {
        Command bringToFrontCommand = new BringToFrontCommand(model, shapeMapping.getModelShape(shape));
        commandInvoker.executeCommand(bringToFrontCommand);
    }

    @Override
    public void onSendToBack(Shape shape) {
        Command sendToBackCommand = new SendToBackCommand(model, shapeMapping.getModelShape(shape));
        commandInvoker.executeCommand(sendToBackCommand);
    }


    @Override
    public void onSelectionMenuOpened(Shape viewShape, double x, double y) {
        MyShape modelShape = shapeMapping.getModelShape(viewShape);
        if (modelShape == null) return;

        ContextMenu menu = uiUtils.getSelectionShapeMenu();

        // Pulisci gli elementi esistenti per evitare duplicati
        menu.getItems().clear();

        // --- CUT ---
        MenuItem cutItem = new MenuItem("Cut");
        cutItem.setOnAction(e -> handleCut(new ActionEvent()));

        // --- COPY ---
        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(e -> handleCopy(new ActionEvent()));

        // --- PASTE ---
        MenuItem pasteItem = new MenuItem("Paste");
        pasteItem.setDisable(clipboard.isEmpty());
        pasteItem.setOnAction(e -> handlePaste(new ActionEvent()));

        // --- DELETE ---
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> handleDelete());

        // --- COLOR PICKERS ---
        boolean isLine = modelShape instanceof MyLine || viewShape instanceof javafx.scene.shape.Line;

        // Border Color Picker
        Label borderLabel = new Label("Border Color:");
        ColorPicker borderPicker = new ColorPicker((Color) adapterFactory.convertToJavaFxColor(modelShape.getBorderColor()));
        borderPicker.setOnAction(e -> currentStrategy.handleBorderColorChange(borderPicker.getValue()));
        HBox borderBox = new HBox(5, borderLabel, borderPicker);
        CustomMenuItem borderColorItem = new CustomMenuItem(borderBox, false);

        // Fill Color Picker
        Label fillLabel = new Label("Fill Color:");
        ColorPicker fillPicker = new ColorPicker((Color) adapterFactory.convertToJavaFxColor(modelShape.getFillColor()));
        fillPicker.setOnAction(e -> currentStrategy.handleFillColorChange(fillPicker.getValue()));
        HBox fillBox = new HBox(5, fillLabel, fillPicker);
        CustomMenuItem fillColorItem = new CustomMenuItem(fillBox, false);
        fillColorItem.setVisible(!isLine);

        // Aggiunta elementi al menu
        menu.getItems().addAll(cutItem, copyItem, pasteItem, new SeparatorMenuItem(), deleteItem, new SeparatorMenuItem(), borderColorItem, fillColorItem);

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
                if (oldViewShape != null) {
                    drawingArea.getChildren().remove(oldViewShape);
                }

                Shape newfxShape = adapterFactory.convertToJavaFx(shape, drawingArea.getWidth(), drawingArea.getHeight());
                shapeMapping.updateViewMapping(shape, newfxShape);

                if (position >= 0 && position <= drawingArea.getChildren().size()) {
                    drawingArea.getChildren().add(position, newfxShape);
                } else {
                    drawingArea.getChildren().add(newfxShape);
                }

                currentStrategy.reset();
                model.printAllShapes();
                break;
            case "BRINGTOFRONT":
                Shape viewShape = shapeMapping.getViewShape(shape);
                position = contentPane.getChildren().indexOf(viewShape);
                if (position >= 0 && position < contentPane.getChildren().size() - 1) {
                    contentPane.getChildren().remove(viewShape);
                    contentPane.getChildren().add(position + 1, viewShape);
                }
                shapeMapping.bringToFront(shape);
                model.printAllShapes();
                System.out.print(shapeMapping.getViewShapes().size() + " " + contentPane.getChildren().size() + "\n");
                break;
            case "SENDTOBACK":
                oldViewShape = shapeMapping.getViewShape(shape);
                position = contentPane.getChildren().indexOf(oldViewShape);
                if (position > 0) {
                    contentPane.getChildren().remove(oldViewShape);
                    contentPane.getChildren().add(position -1, oldViewShape);
                }
                shapeMapping.sendToBack(shape);
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
                    model.clearShapes();
                    if (currentStrategy != null)
                        currentStrategy.reset();
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

    @FXML
    public void handleCopy(ActionEvent event) {
        if (currentStrategy instanceof SelectionToolStrategy selectionStrategy) {
            List<MyShape> selectedShapes = selectionStrategy.getSelectedShapes();
            if (!selectedShapes.isEmpty()) {
                clipboard.copy(selectedShapes);
                updateClipboardMenuItems();
            }
        }
    }

    @FXML
    public void handleCut(ActionEvent event) {
        if (currentStrategy instanceof SelectionToolStrategy selectionStrategy) {
            List<MyShape> selectedShapes = selectionStrategy.getSelectedShapes();
            if (!selectedShapes.isEmpty()) {
                clipboard.copy(selectedShapes);
                for (MyShape shape : selectedShapes) {
                    model.removeShape(shape);
                }
                selectionStrategy.clearSelection();
                updateClipboardMenuItems();
            }
        }
    }

    @FXML
    public void handlePaste(ActionEvent event) {
        if (!clipboard.isEmpty()) {
            double offset = 20.0;

            // Ottieni dimensioni reali dell'area
            double width = drawingArea.getWidth();
            double height = drawingArea.getHeight();

            List<MyShape> clonedShapes = clipboard.paste();

            for (MyShape shape : clonedShapes) {
                double deltaX = offset / width;   // 10 pixel convertiti in [0,1]
                double deltaY = offset / height;

                shape.setStartX(shape.getStartX() + deltaX);
                shape.setStartY(shape.getStartY() + deltaY);
                shape.setEndX(shape.getEndX() + deltaX);
                shape.setEndY(shape.getEndY() + deltaY);

                model.addShape(shape);
            }

            updateClipboardMenuItems();
        }
    }

    private void updateClipboardMenuItems() {
        menuItemPaste.setDisable(clipboard.isEmpty());
    }

    private void handleDelete() {
        if (currentStrategy instanceof SelectionToolStrategy selectionStrategy) {
            List<MyShape> selectedShapes = selectionStrategy.getSelectedShapes();
            if (!selectedShapes.isEmpty()) {
                for (MyShape shape : selectedShapes) {
                    model.removeShape(shape);
                }
                selectionStrategy.clearSelection();
            }
        }
    }

    @FXML
    private void toggleGrid(ActionEvent event) {
        boolean enabled = menuItemToggleGrid.isSelected();
        gridSettings.setGridEnabled(enabled);

        if (!enabled) {
            // Deseleziona tutto
            if (lastSelectedSizeItem != null) {
                lastSelectedSizeItem.setSelected(false);
                lastSelectedSizeItem = null;
            }
        } else {
            // Se nulla è selezionato, seleziona default (20)
            if (lastSelectedSizeItem == null) {
                cellSize20.setSelected(true);
                lastSelectedSizeItem = cellSize20;
                gridSettings.setCellSize(20);
            }
        }

        gridRenderer.drawGrid();
    }

    @FXML
    private void setCellSize10(ActionEvent event) {
        gridSettings.setCellSize(10);
        gridRenderer.drawGrid();
    }

    @FXML
    private void setCellSize20(ActionEvent event) {
        gridSettings.setCellSize(20);
        gridRenderer.drawGrid();
    }

    @FXML
    private void setCellSize50(ActionEvent event) {
        gridSettings.setCellSize(50);
        gridRenderer.drawGrid();
    }

    @FXML
    private void handleCellSizeSelection(ActionEvent event) {
        RadioMenuItem selected = (RadioMenuItem) event.getSource();

        if (selected == lastSelectedSizeItem) {
            // Deseleziona se cliccato di nuovo
            selected.setSelected(false);
            lastSelectedSizeItem = null;
            gridSettings.setGridEnabled(false);
            menuItemToggleGrid.setSelected(false);
        } else {
            // Selezione nuova dimensione
            lastSelectedSizeItem = selected;
            gridSettings.setGridEnabled(true);
            menuItemToggleGrid.setSelected(true);

            if (selected == cellSize10) {
                gridSettings.setCellSize(10);
            } else if (selected == cellSize20) {
                gridSettings.setCellSize(20);
            } else if (selected == cellSize50) {
                gridSettings.setCellSize(50);
            }
        }

        gridRenderer.drawGrid();
    }
}