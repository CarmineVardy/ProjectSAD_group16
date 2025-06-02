package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.adapter.AdapterFactory;
import it.unisa.diem.sad.geoshapes.controller.command.*;
import it.unisa.diem.sad.geoshapes.controller.strategy.*;
import it.unisa.diem.sad.geoshapes.controller.util.GridRenderer;
import it.unisa.diem.sad.geoshapes.controller.util.Clipboard;
import it.unisa.diem.sad.geoshapes.controller.util.UIUtils;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.controller.util.GridSettings;
import it.unisa.diem.sad.geoshapes.observer.ShapeObserver;
import it.unisa.diem.sad.geoshapes.persistence.PersistenceService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import javafx.scene.shape.Line;


public class MainController implements ShapeObserver, InteractionCallback {


    @FXML
    private MenuBar menubar;
    @FXML
    private MenuItem menuItemLoad;
    @FXML
    private MenuItem menuItemSave;
    @FXML
    private MenuItem menuItemSaveAs;
    @FXML
    private CheckMenuItem menuItemToggleGrid;
    @FXML
    private ToggleGroup cellSizeToggleGroup;
    @FXML
    private RadioMenuItem cellSize10;
    @FXML
    private RadioMenuItem cellSize20;
    @FXML
    private RadioMenuItem cellSize50;
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
    private ToggleButton polygonButton;
    @FXML
    private ComboBox<Integer> polygonVertices;
    @FXML
    private CheckBox regularPolygon;
    @FXML
    private ColorPicker borderColorPicker;
    @FXML
    private ColorPicker fillColorPicker;
    @FXML
    private SplitMenuButton bringToFrontSplitButton;
    @FXML
    private MenuItem bringToFrontMenuItem;
    @FXML
    private MenuItem bringToTopMenuItem;
    @FXML
    private SplitMenuButton sendToBackSplitButton;
    @FXML
    private MenuItem sendToBackMenuItem;
    @FXML
    private MenuItem sendToBottomMenuItem;
    @FXML
    private Button shapesManagerButton;
    @FXML
    private Slider zoomSlider;
    @FXML
    private Label zoomPercentageLabel;
    @FXML
    private Button zoom75Button;
    @FXML
    private Button zoom100Button;
    @FXML
    private Button zoom200Button;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private MenuItem cutMenuItem;
    @FXML
    private MenuItem copyMenuItem;
    @FXML
    private MenuItem pasteMenuItem;
    @FXML
    private MenuItem deleteMenuItem;
    @FXML
    private Group zoomGroup;
    @FXML
    private Pane drawingArea;
    @FXML
    private VBox shapeManagerPanel;
    @FXML
    private Label shapeManagerTitle;
    @FXML
    private ListView<String> shapesListView;

    private Rectangle clipRect;

    private DrawingModel model;
    private PersistenceService persistenceService;
    private UIUtils uiUtils;
    private ShapeMapping shapeMapping;
    private Clipboard clipboard;

    //Pattern Strategy
    private ToolStrategy currentStrategy;
    private Map<ToggleButton, ToolStrategy> toolStrategies;

    //Pattern Adapter
    private AdapterFactory adapterFactory;

    //Pattern Command
    private CommandInvoker commandInvoker;

    //GRID
    private GridSettings gridSettings;
    private GridRenderer gridRenderer;

    //DA VEDERE
    private RadioMenuItem lastSelectedSizeItem;

    //DA VEDERE
    private BooleanProperty hasShapeSelected = new SimpleBooleanProperty(false);
    private BooleanProperty isLineSelected = new SimpleBooleanProperty(false);

    //PER ZOOM
    private double currentZoomLevel = 1.0;
    private double previousScale = 1.0;
    private double baseDrawingAreaWidth = 1024;
    private double baseDrawingAreaHeight = 500;

    //MIRRORING
    private ToolStrategy toolStrategy;
    private DrawingModel drawingModel;
    @FXML
    private Button flipVButton;
    @FXML
    private Button flipHButton;


    @FXML
    public void initialize() {
        initializeCoreComponents();
        configureDrawingArea();
        setupEventListeners();
        setDefaultUIState();
        configureBind();
    }

    private void initializeCoreComponents() {
        //Initialize Model
        model = new DrawingModel();
        model.attach(this);

        //Initialize PersistenceService, uiUtils and shapeMapping
        persistenceService = new PersistenceService();
        uiUtils = new UIUtils();
        shapeMapping = new ShapeMapping();
        shapesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //Initialize Pattern Strategy, Adapter and Command
        initializeToolStrategies();
        adapterFactory = new AdapterFactory();
        commandInvoker = new CommandInvoker();
        clipboard = new Clipboard(adapterFactory);

        gridSettings = new GridSettings();
        gridRenderer = new GridRenderer(drawingArea, gridSettings);

    }

    private void initializeToolStrategies() {
        toolStrategies = new HashMap<>();
        toolStrategies.put(selectionButton, new SelectionToolStrategy(drawingArea,zoomGroup,shapeMapping, this));
        toolStrategies.put(lineButton, new LineToolStrategy(drawingArea, this,zoomGroup));
        toolStrategies.put(rectangleButton, new RectangleToolStrategy(drawingArea, this,zoomGroup));
        toolStrategies.put(ellipseButton, new EllipseToolStrategy(drawingArea, this,zoomGroup));
        toolStrategies.put(polygonButton, new PolygonToolStrategy(drawingArea, this,zoomGroup));
    }

    private void configureDrawingArea() {
        clipRect = new Rectangle();
        drawingArea.setClip(clipRect);
        drawingArea.setPrefWidth(baseDrawingAreaWidth);
        drawingArea.setPrefHeight(baseDrawingAreaHeight);
        zoomGroup.setScaleX(1.0);
        zoomGroup.setScaleY(1.0);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    }

    private void setupEventListeners() {
        setupToolToggleListener();
        setupColorPickerListeners();
        setupZoomListerners();
        setupGridListeners();
        scrollPane.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);

        polygonVertices.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentStrategy.handleChangePolygonVertices(newVal.byteValue());
        });

        regularPolygon.selectedProperty().addListener((obs, oldVal, newVal) -> {
            currentStrategy.handleRegularPolygon(newVal);
        });
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
            currentStrategy.activate(borderColorPicker.getValue(), fillColorPicker.getValue(), polygonVertices.getValue().intValue(), regularPolygon.isSelected());
        });
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

    private void setupZoomListerners() {
        zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double scale = newValue.doubleValue();
            applyZoom(scale);
        });
        drawingArea.prefHeightProperty().addListener((observable, oldValue, newValue) -> {
                    zoomGroup.prefHeight(newValue.doubleValue());
                }
        );
        drawingArea.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
            zoomGroup.prefWidth(newValue.doubleValue());
        });
    }

    private void setupGridListeners() {
        drawingArea.widthProperty().addListener((obs, oldVal, newVal) -> {
            gridRenderer.drawGrid();
        });
        drawingArea.heightProperty().addListener((obs, oldVal, newVal) -> {
            gridRenderer.drawGrid();
        });
    }


    private void setDefaultUIState() {
        toolToggleGroup.selectToggle(selectionButton);
        borderColorPicker.setValue(Color.BLACK);
        fillColorPicker.setValue(Color.TRANSPARENT);

        //GRIGLIA DESELEZIONATA
        //CELLSIZE20 COME DEFAULT4
        menuItemToggleGrid.setSelected(false);
        cellSize20.setSelected(true);

        zoomSlider.setMin(0.25); // Opzionale: imposta un valore minimo (es. 10%)
        zoomSlider.setMax(2.5);  // Imposta il massimo a 2.5 per il 250% di zoom
        zoomSlider.setValue(1.0); // Inizia con uno zoom del 100% (scala 1.0)
        // Aggiorna l'etichetta dello zoom per mostrare il valore iniziale
        zoomPercentageLabel.setText(String.format("%.0f%%", zoomSlider.getValue() * 100));

        if (polygonVertices.getItems().isEmpty()) {
            polygonVertices.getItems().addAll(3, 4, 5, 6, 7, 8);
        }
        polygonVertices.setValue(3); // Default
        regularPolygon.setSelected(false);
    }

    private void configureBind() {
        clipRect.widthProperty().bind(drawingArea.widthProperty());
        clipRect.heightProperty().bind(drawingArea.heightProperty());
        fillColorPicker.disableProperty().bind(isLineSelected);
        undoButton.disableProperty().bind(commandInvoker.canUndoProperty().not());
        bringToFrontSplitButton.disableProperty().bind(hasShapeSelected.not());
        sendToBackSplitButton.disableProperty().bind(hasShapeSelected.not());
        cutMenuItem.disableProperty().bind(hasShapeSelected.not());
        copyMenuItem.disableProperty().bind(hasShapeSelected.not());
        deleteMenuItem.disableProperty().bind(hasShapeSelected.not());
        pasteMenuItem.disableProperty().bind(clipboard.emptyProperty());
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

    //DA VEDERE
    @FXML
    private void toggleGrid(ActionEvent event) {
        gridSettings.setGridEnabled(menuItemToggleGrid.isSelected());
        gridRenderer.drawGrid();
    }

    //DA VEDERE
    @FXML
    private void handleCellSizeSelection(ActionEvent event) {
        RadioMenuItem selected = (RadioMenuItem) event.getSource();

        if (selected == cellSize10) {
            gridSettings.setCellSize(10);
        } else if (selected == cellSize20) {
            gridSettings.setCellSize(20);
        } else if (selected == cellSize50) {
            gridSettings.setCellSize(50);
        }

        gridRenderer.drawGrid();
    }

    @FXML
    private void handleUndo() {
        if (commandInvoker.canUndo()) {
            commandInvoker.undo();
        }
    }

    @FXML
    public void handleBringToFront(ActionEvent actionEvent) {
        if (currentStrategy != null) {
            currentStrategy.handleBringToFront(actionEvent);
        }
    }

    @FXML
    public void handleBringToTop(ActionEvent actionEvent) {
        if (currentStrategy != null) {
            currentStrategy.handleBringToTop(actionEvent);
        }
    }

    @FXML
    public void handleSendToBack(ActionEvent actionEvent) {
        if (currentStrategy != null) {
            currentStrategy.handleSendToBack(actionEvent);
        }
    }

    @FXML
    public void handleSendToBottom(ActionEvent actionEvent) {
        if (currentStrategy != null) {
            currentStrategy.handleSendToBottom(actionEvent);
        }
    }

    @FXML
    private void handleShowShapesManager(ActionEvent actionEvent) {
        boolean currentlyVisible = shapeManagerPanel.isVisible();
        shapeManagerPanel.setVisible(!currentlyVisible);
        shapeManagerPanel.setManaged(!currentlyVisible);

        if (!currentlyVisible) {
            // Stiamo mostrando il pannello → evidenzia il bottone
            shapesManagerButton.setStyle("-fx-background-color: lightgray; -fx-border-color: darkgray;");
        } else {
            // Stiamo nascondendo il pannello → resetta lo stile
            shapesManagerButton.setStyle("");
        }
    }

    private void applyZoom(double scale) {
        BigDecimal bd = new BigDecimal(Double.toString(scale));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        final double newScale = bd.doubleValue();


        drawingArea.setScaleX(newScale);
        drawingArea.setScaleY(newScale);
        zoomPercentageLabel.setText(String.format("%.0f%%", newScale * 100));


    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        if (currentStrategy != null) {
            if (!(currentStrategy instanceof SelectionToolStrategy))
                scrollPane.setPannable(false);
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

    public void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            handleDelete(event);
            System.out.println("Tasto Canc premuto");
        } else if (event.isControlDown()) {
            switch (event.getCode()) {
                case C:
                    handleCopy(event);
                    System.out.println("Ctrl+C premuto");
                    break;
                case V:
                    handlePaste(event);
                    System.out.println("Ctrl+V premuto");
                    break;
                case X:
                    handleCut(event);
                    System.out.println("Ctrl+X premuto");
                    break;
                case Z:
                    handleUndo();
                    System.out.println("Ctrl+Z premuto");
                    break;
                default:
                    break;
            }
        }
    }

    @FXML
    public void handleCopy(Event event) {
        if (currentStrategy != null) {
            currentStrategy.handleCopy(event);
        }
    }

    @FXML
    public void handleCut(Event event) {
        if (currentStrategy != null) {
            currentStrategy.handleCut(event);
        }
    }

    @FXML
    public void handlePaste(Event event) {
        if (clipboard.isEmpty()) {
            return;
        }

        List<MyShape> pastedShapes = clipboard.paste();

        for (MyShape shape : pastedShapes) {
            Command createCommand = new CreateShapeCommand(model, shape);
            commandInvoker.executeCommand(createCommand);
        }
    }

    @FXML
    private void handleDelete(Event event) {
        if (currentStrategy != null) {
            currentStrategy.handleDelete(event);
        }
    }

    @Override
    public void update() {

        drawingArea.getChildren().clear();

        drawingArea.getChildren().add(0, gridRenderer.getGridCanvas());

        List<MyShape> modelShapes = model.getShapes();
        List<Shape> newViewShapes = new ArrayList<>();

        for (MyShape shape : modelShapes) {
            Shape javafxShape = adapterFactory.convertToJavaFx(shape, drawingArea.getWidth(), drawingArea.getHeight());
            drawingArea.getChildren().add(javafxShape);
            newViewShapes.add(javafxShape);
        }

        shapeMapping.rebuildMapping(modelShapes, newViewShapes);

        updateShapesListView();

        currentStrategy.reset();

        System.out.println("Forme nel modello: " + modelShapes.size());
        System.out.println("Forme nel mapping: " + shapeMapping.size());
        System.out.println("Forme nella view: " + drawingArea.getChildren().size());
        model.printAllShapes();
    }

    private void updateShapesListView() {
        ObservableList<String> shapeNames = FXCollections.observableArrayList();
        model.getShapesReversed().forEach(shape -> shapeNames.add(shape.getName()));
        shapesListView.setItems(shapeNames);
    }

    @Override
    public void onSelectionMenuOpened(double x, double y) {
        contextMenu.show(drawingArea, x, y);

        // Aggiungi un listener temporaneo per chiudere il menu se clicchi fuori
        drawingArea.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Se il click è fuori dal ContextMenu, chiudilo
                if (!contextMenu.isShowing()) return;

                contextMenu.hide();

                // Rimuovi il listener subito dopo
                drawingArea.getScene().removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
            }
        });
    }

    @Override
    public void onShapeSelected(Shape shape) {
        hasShapeSelected.set(true);
        isLineSelected.set(shape instanceof Line);

        shapesListView.getSelectionModel().select(model.getShapesReversed().indexOf(shapeMapping.getModelShape(shape)));
    }

    @Override
    public void onLineSelected(boolean selected) {
        isLineSelected.set(selected);

    }

    @Override
    public void onShapeDeselected() {
        hasShapeSelected.set(false);
        isLineSelected.set(false);
        shapesListView.getSelectionModel().clearSelection();
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
    public void onRotateShape(Shape shape, double oldAngle, double newAngle) {
        RotateShapeCommand command = new RotateShapeCommand(model, shapeMapping.getModelShape(shape), oldAngle, newAngle);
        commandInvoker.executeCommand(command);

    }


    @Override
    public void onBringToFront(Shape shape) {
        Command bringToFrontCommand = new BringToFrontCommand(model, shapeMapping.getModelShape(shape));
        commandInvoker.executeCommand(bringToFrontCommand);
    }

    @Override
    public void onBringToTop(Shape shape) {
        Command bringToTopCommand = new BringToTopCommand(model, shapeMapping.getModelShape(shape));
        commandInvoker.executeCommand(bringToTopCommand);
    }

    @Override
    public void onSendToBack(Shape shape) {
        Command sendToBackCommand = new SendToBackCommand(model, shapeMapping.getModelShape(shape));
        commandInvoker.executeCommand(sendToBackCommand);
    }

    @Override
    public void onSendToBottom(Shape shape) {
        Command sendToBottomCommand = new SendToBottomCommand(model, shapeMapping.getModelShape(shape));
        commandInvoker.executeCommand(sendToBottomCommand);
    }

    @Override
    public void onCopyShape(Shape shape) {
        if (shape == null) {
            return;
        }
        List<MyShape> shapesToCopy = new ArrayList<>();
        shapesToCopy.add(shapeMapping.getModelShape(shape));
        clipboard.copy(shapesToCopy);
    }

    @Override
    public void onCutShape(Shape shape) {
        if (shape == null) {
            return;
        }
        onCopyShape(shape);
        onDeleteShape(shape);
    }

    @FXML
    public void handleZoom75(ActionEvent actionEvent) {
        zoomSlider.setValue(0.75);
    }

    @FXML
    public void handleZoom100(ActionEvent actionEvent) {
        zoomSlider.setValue(1.0);
    }

    @FXML
    public void handleZoom200(ActionEvent actionEvent) {
        zoomSlider.setValue(2.0);
    }

    public void setClipboard(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    public void setDrawingArea(Pane drawingArea) {
        this.drawingArea = drawingArea;
    }

    public void setPasteMenuItem(MenuItem pasteMenuItem) {
        this.pasteMenuItem = pasteMenuItem;
    }

    public void setModel(DrawingModel model) {
        this.model = model;
    }

    public void setCurrentStrategy(ToolStrategy currentStrategy) {
        this.currentStrategy = currentStrategy;
    }

    public void updateClipboardMenuItems() {
        pasteMenuItem.setDisable(clipboard.isEmpty());
    }

    //MIRRORING
    private List<MyShape> getSelectedShapes() {
        System.out.println("Tool attivo: " + toolStrategy.getClass().getSimpleName());
        if (toolStrategy instanceof SelectionToolStrategy selectionStrategy) {
            return selectionStrategy.getSelectedShapes();
        }
        return Collections.emptyList();
    }

    @FXML
    private void onFlipHorizontal() {
        List<MyShape> selectedShapes = getSelectedShapes();
        System.out.println("Figure selezionate: " + selectedShapes.size());

        for (MyShape shape : selectedShapes) {
            shape.flipHorizontal(); // Metodo da implementare
        }

        model.notifyObservers();
    }

    @FXML
    private void onFlipVertical() {
        List<MyShape> selectedShapes = getSelectedShapes();
        for (MyShape shape : selectedShapes) {
            shape.flipVertical();
        }
        model.notifyObservers();
    }

    public void setDrawingModel(DrawingModel drawingModel) {
        this.drawingModel = drawingModel;
    }

}