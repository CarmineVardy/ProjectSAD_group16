package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.adapter.AdapterFactory;
import it.unisa.diem.sad.geoshapes.command.*;
import it.unisa.diem.sad.geoshapes.controller.strategy.*;
import it.unisa.diem.sad.geoshapes.controller.util.Clipboard;
import it.unisa.diem.sad.geoshapes.controller.util.Grid;
import it.unisa.diem.sad.geoshapes.controller.util.UIUtils;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import it.unisa.diem.sad.geoshapes.model.shapes.MyShape;
import it.unisa.diem.sad.geoshapes.observer.ShapeObserver;
import it.unisa.diem.sad.geoshapes.persistence.PersistenceService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.animation.TranslateTransition;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Node;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController implements ShapeObserver, InteractionCallback {

    @FXML
    private Button undoButton;
    @FXML
    private Button hideButton;
    @FXML
    private Button maximizeButton;
    @FXML
    private Button closeButton;
    @FXML
    private MenuItem menuItemLoad;
    @FXML
    private MenuItem menuItemSave;
    @FXML
    private MenuItem menuItemSaveAs;
    @FXML
    private CheckMenuItem shapesManagerMenuItem;
    @FXML
    private VBox shapeManagerPanel;
    @FXML
    private Label shapeManagerTitle;
    @FXML
    private ListView<String> shapesListView;
    @FXML
    private CheckMenuItem menuItemToggleGrid;
    @FXML
    private RadioMenuItem cellSize10;
    @FXML
    private RadioMenuItem cellSize20;
    @FXML
    private RadioMenuItem cellSize50;
    @FXML
    private Slider zoomSlider;
    @FXML
    private RadioMenuItem zoom75Button;
    @FXML
    private RadioMenuItem zoom100Button;
    @FXML
    private RadioMenuItem zoom200Button;
    @FXML
    private MenuItem menuItemAbout;
    @FXML
    private HBox leftControlsContainer;
    @FXML
    private VBox toolsVBox;
    @FXML
    private ToggleGroup toolToggleGroup;
    @FXML
    private ToggleGroup editToggleGroup;
    @FXML
    private ToggleButton selectionButton;
    @FXML
    private ToggleButton lineButton;
    @FXML
    private Line lineShapePreview;
    @FXML
    private ToggleButton lineEditButton;
    @FXML
    private HBox linePropertiesHBox;
    @FXML
    private ColorPicker lineBorderColorPicker;
    @FXML
    private ToggleButton rectangleButton;
    @FXML
    private Rectangle rectangleShapePreview;
    @FXML
    private ToggleButton rectangleEditButton;
    @FXML
    private HBox rectanglePropertiesHBox;
    @FXML
    private ColorPicker rectangleBorderColorPicker;
    @FXML
    private ColorPicker rectangleFillColorPicker;
    @FXML
    private ToggleButton ellipseButton;
    @FXML
    private Circle ellipseShapePreview;
    @FXML
    private ToggleButton ellipseEditButton;
    @FXML
    private HBox ellipsePropertiesHBox;
    @FXML
    private ColorPicker ellipseBorderColorPicker;
    @FXML
    private ColorPicker ellipseFillColorPicker;
    @FXML
    private ToggleButton polygonButton;
    @FXML
    private Polygon polygonShapePreview;
    @FXML
    private ToggleButton polygonEditButton;
    @FXML
    private HBox polygonPropertiesHBox;
    @FXML
    private ComboBox<Integer> polygonVertices;
    @FXML
    private ColorPicker polygonBorderColorPicker;
    @FXML
    private ColorPicker polygonFillColorPicker;
    @FXML
    private CheckBox regularPolygon;
    @FXML
    private ToggleButton textButton;
    @FXML
    private TextField textPreview;
    @FXML
    private ToggleButton textEditButton;
    @FXML
    private HBox textPropertiesHBox;
    @FXML
    private ComboBox<Integer> fontSize;
    @FXML
    private ColorPicker textBorderColorPicker;
    @FXML
    private ColorPicker textFillColorPicker;
    @FXML
    private ColorPicker textColorPicker;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Group zoomGroup;
    @FXML
    private Pane drawingArea;
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private MenuItem borderColorModifyMenuItem;
    @FXML
    private ColorPicker borderColorModifyPicker;
    @FXML
    private MenuItem fillColorModifyMenuItem;
    @FXML
    private ColorPicker fillColorModifyPicker;
    @FXML
    private MenuItem textColorModifyMenuItem;
    @FXML
    private ColorPicker textColorModifyPicker;
    @FXML
    private MenuItem fontSizeModifyMenuItem;
    @FXML
    private ComboBox<Integer> fontSizeModifyComboBox;
    @FXML
    private MenuItem cutMenuItem;
    @FXML
    private MenuItem copyMenuItem;
    @FXML
    private MenuItem pasteMenuItem;
    @FXML
    private MenuItem deleteMenuItem;
    @FXML
    private Menu bringToFrontMenu;
    @FXML
    private MenuItem bringToFrontMenuItem;
    @FXML
    private MenuItem bringToTopMenuItem;
    @FXML
    private Menu sendToBackMenu;
    @FXML
    private MenuItem sendToBackMenuItem;
    @FXML
    private MenuItem sendToBottomMenuItem;
    @FXML
    private MenuItem groupMenuItem;
    @FXML
    private MenuItem ungroupMenuItem;
    @FXML
    private MenuItem flipHMenuItem;
    @FXML
    private MenuItem flipVButton;
    @FXML
    private Label cursorPosition;
    @FXML
    private Label paneDimension;
    @FXML
    private Label numberOfShapes;
    @FXML
    private Label zoomPercentageLabel;


    private Rectangle clipRect;

    private DrawingModel model;
    private PersistenceService persistenceService;
    private UIUtils uiUtils;
    private ShapeMapping shapeMapping;
    private Clipboard clipboard;

    private ToolStrategy currentStrategy;
    private Map<ToggleButton, ToolStrategy> toolStrategies;

    private Map<ToggleButton, HBox> editButtonToPropertiesMap;
    private Map<ToggleButton, ToggleButton> editButtonToToolButtonMap;
    private ToggleButton currentlySelectedEditToggle = null;

    private AdapterFactory adapterFactory;

    private CommandInvoker commandInvoker;

    private Grid grid;

    private final BooleanProperty hasSelectedShapes = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyLinesSelected = new SimpleBooleanProperty(false);
    private final BooleanProperty canGroup = new SimpleBooleanProperty(false); // almeno 2 forme

    private double currentZoomLevel = 1.0;
    private double previousScale = 1.0;
    private double baseDrawingAreaWidth = 1024;
    private double baseDrawingAreaHeight = 500;

    @FXML
    public void initialize() {
        initializeCoreComponents();
        configureDrawingArea();
        setupEventListeners();
        setDefaultUIState();
        configureBind();
    }

    private void initializeCoreComponents() {

        model = new DrawingModel();
        model.attach(this);

        persistenceService = new PersistenceService();
        uiUtils = new UIUtils();
        shapeMapping = new ShapeMapping();
        shapesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        initializeToolStrategies();
        initializeEditButtonMappings();
        adapterFactory = new AdapterFactory();
        commandInvoker = new CommandInvoker();
        clipboard = new Clipboard(adapterFactory);

        grid = new Grid(drawingArea);

    }

    private void initializeToolStrategies() {
        toolStrategies = new HashMap<>();
        toolStrategies.put(selectionButton, new SelectionToolStrategy(drawingArea, shapeMapping, this));
        toolStrategies.put(lineButton, new LineToolStrategy(drawingArea, this));
        toolStrategies.put(rectangleButton, new RectangleToolStrategy(drawingArea, this));
        toolStrategies.put(ellipseButton, new EllipseToolStrategy(drawingArea, this));
        toolStrategies.put(polygonButton, new PolygonToolStrategy(drawingArea, this));
        toolStrategies.put(textButton, new TextToolStrategy(drawingArea, this));
    }

    private void initializeEditButtonMappings() {
        editButtonToPropertiesMap = new HashMap<>();
        editButtonToPropertiesMap.put(lineEditButton, linePropertiesHBox);
        editButtonToPropertiesMap.put(rectangleEditButton, rectanglePropertiesHBox);
        editButtonToPropertiesMap.put(ellipseEditButton, ellipsePropertiesHBox);
        editButtonToPropertiesMap.put(polygonEditButton, polygonPropertiesHBox);
        editButtonToPropertiesMap.put(textEditButton, textPropertiesHBox);
        editButtonToToolButtonMap = new HashMap<>();
        editButtonToToolButtonMap.put(lineEditButton, lineButton);
        editButtonToToolButtonMap.put(rectangleEditButton, rectangleButton);
        editButtonToToolButtonMap.put(ellipseEditButton, ellipseButton);
        editButtonToToolButtonMap.put(polygonEditButton, polygonButton);
        editButtonToToolButtonMap.put(textEditButton, textButton);
        for (HBox hbox : editButtonToPropertiesMap.values()) {
            hbox.setVisible(false);
            hbox.setManaged(false);
            hbox.setTranslateX(0);
        }
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
        setupEditToggleListener();
        setupPropertiesListeners();
        setupChangesListeners();
        setupZoomListerners();
        setupGridListeners();
        scrollPane.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
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
            currentStrategy.activate(lineBorderColorPicker.getValue(), rectangleBorderColorPicker.getValue(), rectangleFillColorPicker.getValue(), ellipseBorderColorPicker.getValue(), ellipseFillColorPicker.getValue(), polygonBorderColorPicker.getValue(), polygonFillColorPicker.getValue(), textBorderColorPicker.getValue(), textFillColorPicker.getValue(), textColorPicker.getValue(), polygonVertices.getValue(), regularPolygon.isSelected(), fontSize.getValue());
        });
    }

    private void setupEditToggleListener() {
        editToggleGroup.selectedToggleProperty().addListener((observable, oldToggleRaw, newToggleRaw) -> {
            ToggleButton oldToggle = (ToggleButton) oldToggleRaw;
            ToggleButton newToggle = (ToggleButton) newToggleRaw;
            if (newToggle != null && newToggle == oldToggle) {
                newToggle.setSelected(false);
                return;
            }
            currentlySelectedEditToggle = newToggle;
            HBox hboxToShow = null;
            if (newToggle != null) {
                hboxToShow = editButtonToPropertiesMap.get(newToggle);
            }
            for (Map.Entry<ToggleButton, HBox> entry : editButtonToPropertiesMap.entrySet()) {
                HBox currentHBox = entry.getValue();
                if (currentHBox != hboxToShow) {
                    if (currentHBox.isVisible()) {
                        uiUtils.hidePropertiesHBox(currentHBox);
                    }
                }
            }
            if (hboxToShow != null) {
                if (!hboxToShow.isVisible()) {
                    uiUtils.showPropertiesHBox(toolsVBox, zoomGroup, scrollPane, hboxToShow, newToggle);
                }
            }
        });
    }

    private void setupPropertiesListeners() {
        lineBorderColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleLineBorderColorChange(newColor);
            }
            if (lineShapePreview != null) {
                lineShapePreview.setStroke(newColor);
            }
        });
        rectangleBorderColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleRectangleBorderColorChange(newColor);
            }
            if (rectangleShapePreview != null) {
                rectangleShapePreview.setStroke(newColor);
            }
        });
        rectangleFillColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleRectangleFillColorChange(newColor);
            }
            if (rectangleShapePreview != null) {
                rectangleShapePreview.setFill(newColor);
            }
        });
        ellipseBorderColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleEllipseBorderColorChange(newColor);
            }
            if (ellipseShapePreview != null) {
                ellipseShapePreview.setStroke(newColor);
            }
        });
        ellipseFillColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleEllipseFillColorChange(newColor);
            }
            if (ellipseShapePreview != null) {
                ellipseShapePreview.setFill(newColor);
            }
        });
        polygonBorderColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handlePolygonBorderColorChange(newColor);
            }
            if (polygonShapePreview != null) {
                polygonShapePreview.setStroke(newColor);
            }
        });
        polygonFillColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handlePolygonFillColorChange(newColor);
            }
            if (polygonShapePreview != null) {
                polygonShapePreview.setFill(newColor);
            }
        });
        textBorderColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleTextBorderColorChange(newColor);
            }
            if (textPreview != null) {
                String borderColor = String.format("rgb(%d,%d,%d)",
                        (int) (newColor.getRed() * 255),
                        (int) (newColor.getGreen() * 255),
                        (int) (newColor.getBlue() * 255));

                uiUtils.updateTextPreviewStyle(textPreview, textFillColorPicker, textColorPicker, textBorderColorPicker, fontSize);
            }
        });
        textFillColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleTextFillColorChange(newColor);
            }
            if (textPreview != null) {
                uiUtils.updateTextPreviewStyle(textPreview, textFillColorPicker, textColorPicker, textBorderColorPicker, fontSize);
            }
        });
        textColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleTextColorChange(newColor);
            }
            if (textPreview != null) {
                uiUtils.updateTextPreviewStyle(textPreview, textFillColorPicker, textColorPicker, textBorderColorPicker, fontSize);
            }
        });
        fontSize.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (currentStrategy != null) {
                currentStrategy.handleFontSizeChange(newVal.byteValue());
            }
            if (textPreview != null) {
                uiUtils.updateTextPreviewStyle(textPreview, textFillColorPicker, textColorPicker, textBorderColorPicker, fontSize);
            }
        });
        polygonVertices.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (currentStrategy != null) {
                currentStrategy.handlePolygonVerticesChange(newVal.byteValue());
            }
            if (polygonShapePreview != null && newVal != null) {
                int sides = newVal.intValue();
                if (sides >= 3 && sides <= 8) {
                    double centerX = 25;
                    double centerY = 25;
                    double radius = 12;
                    double angleStep = 2 * Math.PI / sides;
                    ObservableList<Double> points = FXCollections.observableArrayList();

                    for (int i = 0; i < sides; i++) {
                        double angle = i * angleStep - Math.PI / 2;
                        double x = centerX + radius * Math.cos(angle);
                        double y = centerY + radius * Math.sin(angle);
                        points.add(x);
                        points.add(y);
                    }
                    polygonShapePreview.getPoints().setAll(points);
                }
            }
        });
        regularPolygon.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (currentStrategy != null) {
                currentStrategy.handleRegularPolygon(newVal);
            }
        });
    }

    private void setupChangesListeners() {
        borderColorModifyPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleBorderColorChange(newColor);
            }
        });
        fillColorModifyPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleFillColorChange(newColor);
            }
        });
        textColorModifyPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleTextColorMenuChange(newColor);
            }
        });
        fontSizeModifyComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (currentStrategy != null) {
                currentStrategy.handleFontSizeMenuChange(newVal.byteValue());
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

    private void applyZoom(double scale) {
        BigDecimal bd = new BigDecimal(Double.toString(scale));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        final double newScale = bd.doubleValue();
        drawingArea.setScaleX(newScale);
        drawingArea.setScaleY(newScale);
        zoomPercentageLabel.setText(String.format("%.0f%%", newScale * 100));
    }

    private void setupGridListeners() {
        drawingArea.widthProperty().addListener((obs, oldVal, newVal) -> {
            grid.drawGrid();
        });
        drawingArea.heightProperty().addListener((obs, oldVal, newVal) -> {
            grid.drawGrid();
        });
    }


    private void setDefaultUIState() {
        toolToggleGroup.selectToggle(selectionButton);
        lineBorderColorPicker.setValue(Color.BLACK);
        rectangleBorderColorPicker.setValue(Color.BLACK);
        rectangleFillColorPicker.setValue(Color.TRANSPARENT);
        ellipseBorderColorPicker.setValue(Color.BLACK);
        ellipseFillColorPicker.setValue(Color.TRANSPARENT);
        polygonBorderColorPicker.setValue(Color.BLACK);
        polygonFillColorPicker.setValue(Color.TRANSPARENT);
        if (polygonVertices.getItems().isEmpty()) {
            polygonVertices.getItems().addAll(3, 4, 5, 6, 7, 8);
        }
        polygonVertices.setValue(3);
        regularPolygon.setSelected(false);
        textBorderColorPicker.setValue(Color.BLACK);
        textFillColorPicker.setValue(Color.TRANSPARENT);
        textColorPicker.setValue(Color.BLACK);
        if (fontSize.getItems().isEmpty()) {
            fontSize.getItems().addAll(14, 16, 18, 20, 22, 24);
        }
        fontSize.setValue(14);
        if (textPreview != null) {
            textPreview.setStyle("-fx-control-inner-background: transparent; -fx-background-color: transparent;");
        }
        uiUtils.initializePolygonPreview(polygonShapePreview);

        menuItemToggleGrid.setSelected(false);
        cellSize20.setSelected(true);
        zoomSlider.setMin(0.25);
        zoomSlider.setMax(2.5);
        zoomSlider.setValue(1.0);
        zoomPercentageLabel.setText(String.format("%.0f%%", zoomSlider.getValue() * 100));
    }

    private void configureBind() {
        clipRect.widthProperty().bind(drawingArea.widthProperty());
        clipRect.heightProperty().bind(drawingArea.heightProperty());
        undoButton.disableProperty().bind(commandInvoker.canUndoProperty().not());
        borderColorModifyMenuItem.disableProperty().bind(hasSelectedShapes.not());
        fillColorModifyMenuItem.disableProperty().bind(onlyLinesSelected);
        fillColorModifyMenuItem.disableProperty().bind(hasSelectedShapes.not());
        //ABILITATI SOLO SE HO SELEZIOANTO UN TESTO
        //textColorModifyMenuItem
        //fontSizeModifyMenuItem
        cutMenuItem.disableProperty().bind(hasSelectedShapes.not());
        copyMenuItem.disableProperty().bind(hasSelectedShapes.not());
        deleteMenuItem.disableProperty().bind(hasSelectedShapes.not());
        pasteMenuItem.disableProperty().bind(clipboard.emptyProperty());
        bringToFrontMenu.disableProperty().bind(hasSelectedShapes.not());
        sendToBackMenu.disableProperty().bind(hasSelectedShapes.not());
        groupMenuItem.disableProperty().bind(canGroup.not());
        //ABILITATO SOLO SE HO SELEZIONATO UN GROUP
        ungroupMenuItem.disableProperty().bind(canGroup.not());
        //ungroupMenuItem
        flipHMenuItem.disableProperty().bind(hasSelectedShapes.not());
        flipVButton.disableProperty().bind(hasSelectedShapes.not());
    }

    @FXML
    public void handleUndo(Event actionEvent) {
        if (commandInvoker.canUndo()) {
            commandInvoker.undo();
        }
    }

    @FXML
    public void handleHide(ActionEvent actionEvent) {
        Stage stage = (Stage) hideButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void handleMaximize(ActionEvent actionEvent) {
        Stage stage = (Stage) maximizeButton.getScene().getWindow();
        if (stage.isMaximized()) {
            stage.setMaximized(false);
            FontIcon icon = (FontIcon) maximizeButton.getGraphic();
            icon.setIconLiteral("mdi2c-crop-square");
        } else {
            stage.setMaximized(true);
            FontIcon icon = (FontIcon) maximizeButton.getGraphic();
            icon.setIconLiteral("mdi2w-window-restore");
        }
    }

    @FXML
    public void handleClose(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
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
    private void handleShowShapesManager(ActionEvent actionEvent) {
        shapeManagerPanel.setVisible(shapesManagerMenuItem.isSelected());
        shapeManagerPanel.setManaged(shapesManagerMenuItem.isSelected());
    }

    @FXML
    private void toggleGrid(ActionEvent event) {
        grid.setGridEnabled(menuItemToggleGrid.isSelected());
        grid.drawGrid();
    }

    @FXML
    private void handleCellSizeSelection(ActionEvent event) {
        RadioMenuItem selected = (RadioMenuItem) event.getSource();

        if (selected == cellSize10) {
            grid.setCellSize(10);
        } else if (selected == cellSize20) {
            grid.setCellSize(20);
        } else if (selected == cellSize50) {
            grid.setCellSize(50);
        }

        grid.drawGrid();
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

    @FXML
    public void handleAbout(ActionEvent actionEvent) {
    }

    @FXML
    public void handleMousePressed(MouseEvent event) {
        if (currentStrategy != null) {
            if (!(currentStrategy instanceof SelectionToolStrategy))
                scrollPane.setPannable(false);
            currentStrategy.handleMousePressed(event);
        }
    }

    @FXML
    public void handleMouseDragged(MouseEvent event) {
        if (currentStrategy != null) {
            currentStrategy.handleMouseDragged(event);
        }
    }

    @FXML
    public void handleMouseReleased(MouseEvent event) {
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
        if (currentStrategy != null) {
            currentStrategy.handleKeyPressed(event);
        } else {
            if (event.getCode() == KeyCode.DELETE) {
                handleDelete(event);
            } else if (event.isControlDown()) {
                switch (event.getCode()) {
                    case C:
                        handleCopy(event);
                        break;
                    case V:
                        handlePaste(event);
                        break;
                    case X:
                        handleCut(event);
                        break;
                    case Z:
                        handleUndo(event);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void handleKeyTyped(KeyEvent event) {
        if (currentStrategy != null) {
            currentStrategy.handleKeyTyped(event);
        }
    }

    @FXML
    public void handleCut(Event actionEvent) {
        List<Shape> selectedShapes = currentStrategy.getSelectedShapes();
        if (selectedShapes != null && !selectedShapes.isEmpty()) {
            List<MyShape> shapesToCopy = new ArrayList<>();

            for (Shape viewShape : selectedShapes) {
                MyShape modelShape = shapeMapping.getModelShape(viewShape);
                if (modelShape != null) {
                    shapesToCopy.add(modelShape);
                }
            }
            clipboard.copy(shapesToCopy);
            Command deleteCommand = new DeleteShapeCommand(model, shapesToCopy);
            commandInvoker.executeCommand(deleteCommand);
        }
    }

    @FXML
    public void handleCopy(Event actionEvent) {
        List<Shape> selectedShapes = currentStrategy.getSelectedShapes();
        if (selectedShapes != null && !selectedShapes.isEmpty()) {
            List<MyShape> shapesToCopy = new ArrayList<>();
            for (Shape viewShape : selectedShapes) {
                MyShape modelShape = shapeMapping.getModelShape(viewShape);
                if (modelShape != null) {
                    shapesToCopy.add(modelShape);
                }
            }
            if (!shapesToCopy.isEmpty()) {
                clipboard.copy(shapesToCopy);
            }
        }
    }

    @FXML
    public void handlePaste(Event actionEvent) {
        if (clipboard.isEmpty()) {
            return;
        }
        List<MyShape> pastedShapes = clipboard.paste();
        Command createCommand = new CreateShapeCommand(model, pastedShapes);
        commandInvoker.executeCommand(createCommand);

    }

    @FXML
    public void handleDelete(Event actionEvent) {
        List<Shape> selectedShapes = currentStrategy.getSelectedShapes();
        if (selectedShapes != null && !selectedShapes.isEmpty()) {
            List<MyShape> shapesToDelete = new ArrayList<>();
            for (Shape viewShape : selectedShapes) {
                MyShape modelShape = shapeMapping.getModelShape(viewShape);
                if (modelShape != null) {
                    shapesToDelete.add(modelShape);
                }
            }
            Command deleteCommand = new DeleteShapeCommand(model, shapesToDelete);
            commandInvoker.executeCommand(deleteCommand);
        }
    }

    @FXML
    public void handleBringToFront(ActionEvent actionEvent) {
        List<Shape> selectedShapes = currentStrategy.getSelectedShapes();
        if (selectedShapes != null && !selectedShapes.isEmpty()) {
            List<MyShape> myShapes = new ArrayList<>();
            for (Shape viewShape : selectedShapes) {
                MyShape modelShape = shapeMapping.getModelShape(viewShape);
                if (modelShape != null) {
                    myShapes.add(modelShape);
                }
            }
            Command bringToFrontCommand = new BringToFrontCommand(model, myShapes);
            commandInvoker.executeCommand(bringToFrontCommand);
        }
    }

    @FXML
    public void handleBringToTop(ActionEvent actionEvent) {
        List<Shape> selectedShapes = currentStrategy.getSelectedShapes();
        if (selectedShapes != null && !selectedShapes.isEmpty()) {
            List<MyShape> myShapes = new ArrayList<>();
            for (Shape viewShape : selectedShapes) {
                MyShape modelShape = shapeMapping.getModelShape(viewShape);
                if (modelShape != null) {
                    myShapes.add(modelShape);
                }
            }
            Command bringToTopCommand = new BringToTopCommand(model, myShapes);
            commandInvoker.executeCommand(bringToTopCommand);
        }
    }

    @FXML
    public void handleSendToBack(ActionEvent actionEvent) {
        List<Shape> selectedShapes = currentStrategy.getSelectedShapes();
        if (selectedShapes != null && !selectedShapes.isEmpty()) {
            List<MyShape> myShapes = new ArrayList<>();
            for (Shape viewShape : selectedShapes) {
                MyShape modelShape = shapeMapping.getModelShape(viewShape);
                if (modelShape != null) {
                    myShapes.add(modelShape);
                }
            }
            Command sendToBackCommand = new SendToBackCommand(model, myShapes);
            commandInvoker.executeCommand(sendToBackCommand);
        }
    }

    @FXML
    public void handleSendToBottom(ActionEvent actionEvent) {
        List<Shape> selectedShapes = currentStrategy.getSelectedShapes();
        if (selectedShapes != null && !selectedShapes.isEmpty()) {
            List<MyShape> myShapes = new ArrayList<>();
            for (Shape viewShape : selectedShapes) {
                MyShape modelShape = shapeMapping.getModelShape(viewShape);
                if (modelShape != null) {
                    myShapes.add(modelShape);
                }
            }
            Command sendToBottomCommand = new SendToBottomCommand(model, myShapes);
            commandInvoker.executeCommand(sendToBottomCommand);
        }
    }

    @FXML
    public void handleGroup(ActionEvent actionEvent) {
    }

    @FXML
    public void handleUngroup(ActionEvent actionEvent) {
    }

    @FXML
    public void onFlipHorizontal(ActionEvent actionEvent) {
        List<Shape> selectedShapes = currentStrategy.getSelectedShapes();
        if (selectedShapes != null && !selectedShapes.isEmpty()) {
            List<MyShape> myShapes = new ArrayList<>();
            for (Shape viewShape : selectedShapes) {
                MyShape modelShape = shapeMapping.getModelShape(viewShape);
                if (modelShape != null) {
                    myShapes.add(modelShape);
                }
            }
            FlipHShapeCommand command = new FlipHShapeCommand(model, myShapes);
            commandInvoker.executeCommand(command);
        }

    }

    @FXML
    public void onFlipVertical(ActionEvent actionEvent) {
        List<Shape> selectedShapes = currentStrategy.getSelectedShapes();
        if (selectedShapes != null && !selectedShapes.isEmpty()) {
            List<MyShape> myShapes = new ArrayList<>();
            for (Shape viewShape : selectedShapes) {
                MyShape modelShape = shapeMapping.getModelShape(viewShape);
                if (modelShape != null) {
                    myShapes.add(modelShape);
                }
            }
            FlipVShapeCommand command = new FlipVShapeCommand(model, myShapes);
            commandInvoker.executeCommand(command);
        }
    }

    @Override
    public void update() {

        currentStrategy.reset();
        drawingArea.getChildren().clear();

        drawingArea.getChildren().add(0, grid.getGridCanvas());

        List<MyShape> modelShapes = model.getShapes();
        List<Shape> newViewShapes = new ArrayList<>();

        for (MyShape shape : modelShapes) {
            Shape javafxShape = adapterFactory.convertToJavaFx(shape, drawingArea.getWidth(), drawingArea.getHeight());
            drawingArea.getChildren().add(javafxShape);
            newViewShapes.add(javafxShape);
        }

        shapeMapping.rebuildMapping(modelShapes, newViewShapes);

        ObservableList<String> shapeNames = FXCollections.observableArrayList();
        model.getShapesReversed().forEach(shape -> shapeNames.add(shape.getName()));
        shapesListView.setItems(shapeNames);

        System.out.println("Forme nel modello: " + modelShapes.size());
        System.out.println("Forme nel mapping: " + shapeMapping.size());
        System.out.println("Forme nella view: " + (drawingArea.getChildren().size() - 1)); //Sottriamo la griglia dai figli.
        model.printAllShapes();

    }

    @Override
    public void onCreateShape(Shape shape) {
        Command createCommand = new CreateShapeCommand(model, adapterFactory.convertToModel(shape, drawingArea.getWidth(), drawingArea.getHeight()));
        commandInvoker.executeCommand(createCommand);
    }

    @Override
    public void onModifyShapes(List<Shape> shapes) {
        if (shapes != null && !shapes.isEmpty()) {
            List<MyShape> oldModelShapes = new ArrayList<>();
            for (Shape viewShape : shapes) {
                MyShape modelShape = shapeMapping.getModelShape(viewShape);
                if (modelShape != null) {
                    oldModelShapes.add(modelShape);
                }
            }
            List<MyShape> cloneModelShapes = new ArrayList<>();
            for (MyShape modelShape : oldModelShapes) {
                cloneModelShapes.add(modelShape.clone());
            }

            List<MyShape> newModelShapes = new ArrayList<>();
            for (Shape viewShape : shapes) {
                MyShape newModelShape = adapterFactory.convertToModel(viewShape, drawingArea.getWidth(), drawingArea.getHeight());
                if (newModelShape != null) {
                    newModelShapes.add(newModelShape);
                }
            }

            Command modifyShapeCommand = new ModifyShapeCommand(model, oldModelShapes, cloneModelShapes, newModelShapes);
            commandInvoker.executeCommand(modifyShapeCommand);
        }
    }

    @Override
    public void onSelectionMenuOpened(double x, double y) {
        contextMenu.show(drawingArea, x, y);

        drawingArea.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!contextMenu.isShowing()) return;
                contextMenu.hide();
                drawingArea.getScene().removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
            }
        });
    }

    public void onChangeShapeSelected() {

        List<Shape> selectedShapes = currentStrategy.getSelectedShapes();

        int size = selectedShapes.size();

        hasSelectedShapes.set(size > 0);
        canGroup.set(size >= 2);
        boolean onlyLines = size > 0 && selectedShapes.stream()
                .allMatch(shape -> shape instanceof Line);
        onlyLinesSelected.set(onlyLines);

        updateListViewSelection(selectedShapes);
    }

    private void updateListViewSelection(List<Shape> currentSelection) {
        shapesListView.getSelectionModel().clearSelection();

        if (currentSelection == null || currentSelection.isEmpty()) {
            return;
        }

        try {
            for (Shape viewShape : currentSelection) {
                MyShape modelShape = shapeMapping.getModelShape(viewShape);

                if (modelShape != null) {
                    int index = model.getShapesReversed().indexOf(modelShape);
                    if (index >= 0) {
                        shapesListView.getSelectionModel().select(index);
                    }
                }
            }
        } catch (Exception e) {
            shapesListView.getSelectionModel().clearSelection();
        }
    }
}