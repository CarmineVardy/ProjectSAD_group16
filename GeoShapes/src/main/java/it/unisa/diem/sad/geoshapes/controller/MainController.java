package it.unisa.diem.sad.geoshapes.controller;

import it.unisa.diem.sad.geoshapes.adapter.AdapterFactory;
import it.unisa.diem.sad.geoshapes.command.BringToFrontCommand;
import it.unisa.diem.sad.geoshapes.command.BringToTopCommand;
import it.unisa.diem.sad.geoshapes.command.Command;
import it.unisa.diem.sad.geoshapes.command.CommandInvoker;
import it.unisa.diem.sad.geoshapes.command.CreateShapeCommand;
import it.unisa.diem.sad.geoshapes.command.DeleteShapeCommand;
import it.unisa.diem.sad.geoshapes.command.FlipHShapeCommand;
import it.unisa.diem.sad.geoshapes.command.FlipVShapeCommand;
import it.unisa.diem.sad.geoshapes.command.ModifyShapeCommand;
import it.unisa.diem.sad.geoshapes.command.SendToBackCommand;
import it.unisa.diem.sad.geoshapes.command.SendToBottomCommand;
import it.unisa.diem.sad.geoshapes.controller.strategy.EllipseToolStrategy;
import it.unisa.diem.sad.geoshapes.controller.strategy.LineToolStrategy;
import it.unisa.diem.sad.geoshapes.controller.strategy.PolygonToolStrategy;
import it.unisa.diem.sad.geoshapes.controller.strategy.RectangleToolStrategy;
import it.unisa.diem.sad.geoshapes.controller.strategy.SelectionToolStrategy;
import it.unisa.diem.sad.geoshapes.controller.strategy.TextToolStrategy;
import it.unisa.diem.sad.geoshapes.controller.strategy.ToolStrategy;
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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main controller for the GeoShapes application.
 * This class handles all user interactions with the UI, manages the drawing model,
 * integrates various drawing tools (strategies), and provides functionalities
 * for file operations, undo/redo, and shape manipulation.
 */
public class MainController implements ShapeObserver, InteractionCallback {

    // FXML Injected UI Components

    // Top Bar Controls
    @FXML private Button undoButton;
    @FXML private Button hideButton;
    @FXML private Button maximizeButton;
    @FXML private Button closeButton;

    // File Menu Items
    @FXML private MenuItem menuItemLoad;
    @FXML private MenuItem menuItemSave;
    @FXML private MenuItem menuItemSaveAs;

    // View Menu Items
    @FXML private CheckMenuItem shapesManagerMenuItem;
    @FXML private VBox shapeManagerPanel;
    @FXML private Label shapeManagerTitle;
    @FXML private ListView<String> shapesListView;
    @FXML private CheckMenuItem menuItemToggleGrid;
    @FXML private RadioMenuItem cellSize10;
    @FXML private RadioMenuItem cellSize20;
    @FXML private RadioMenuItem cellSize50;
    @FXML private Slider zoomSlider;
    @FXML private RadioMenuItem zoom75Button;
    @FXML private RadioMenuItem zoom100Button;
    @FXML private RadioMenuItem zoom200Button;

    // Help Menu Items
    @FXML private MenuItem menuItemAbout;

    // Left Tools Panel
    @FXML private HBox leftControlsContainer;
    @FXML private VBox toolsVBox;
    @FXML private ToggleGroup toolToggleGroup;
    @FXML private ToggleGroup editToggleGroup;

    // Tool Buttons and their Previews/Edit Panels
    @FXML private ToggleButton selectionButton;
    @FXML private ToggleButton lineButton;
    @FXML private Line lineShapePreview;
    @FXML private ToggleButton lineEditButton;
    @FXML private HBox linePropertiesHBox;
    @FXML private ColorPicker lineBorderColorPicker;

    @FXML private ToggleButton rectangleButton;
    @FXML private Rectangle rectangleShapePreview;
    @FXML private ToggleButton rectangleEditButton;
    @FXML private HBox rectanglePropertiesHBox;
    @FXML private ColorPicker rectangleBorderColorPicker;
    @FXML private ColorPicker rectangleFillColorPicker;

    @FXML private ToggleButton ellipseButton;
    @FXML private Circle ellipseShapePreview;
    @FXML private ToggleButton ellipseEditButton;
    @FXML private HBox ellipsePropertiesHBox;
    @FXML private ColorPicker ellipseBorderColorPicker;
    @FXML private ColorPicker ellipseFillColorPicker;

    @FXML private ToggleButton polygonButton;
    @FXML private Polygon polygonShapePreview;
    @FXML private ToggleButton polygonEditButton;
    @FXML private HBox polygonPropertiesHBox;
    @FXML private ComboBox<Integer> polygonVertices;
    @FXML private ColorPicker polygonBorderColorPicker;
    @FXML private ColorPicker polygonFillColorPicker;
    @FXML private CheckBox regularPolygon;

    @FXML private ToggleButton textButton;
    @FXML private TextField textPreview;
    @FXML private ToggleButton textEditButton;
    @FXML private HBox textPropertiesHBox;
    @FXML private ComboBox<Integer> fontSize;
    @FXML private ColorPicker textBorderColorPicker;
    @FXML private ColorPicker textFillColorPicker;
    @FXML private ColorPicker textColorPicker;

    // Drawing Area
    @FXML private ScrollPane scrollPane;
    @FXML private Group zoomGroup;
    @FXML private Pane drawingArea;

    // Context Menu
    @FXML private ContextMenu contextMenu;
    @FXML private MenuItem borderColorModifyMenuItem;
    @FXML private ColorPicker borderColorModifyPicker;
    @FXML private MenuItem fillColorModifyMenuItem;
    @FXML private ColorPicker fillColorModifyPicker;
    @FXML private MenuItem textColorModifyMenuItem;
    @FXML private ColorPicker textColorModifyPicker;
    @FXML private MenuItem fontSizeModifyMenuItem;
    @FXML private ComboBox<Integer> fontSizeModifyComboBox;

    // Context Menu Actions
    @FXML private MenuItem cutMenuItem;
    @FXML private MenuItem copyMenuItem;
    @FXML private MenuItem pasteMenuItem;
    @FXML private MenuItem deleteMenuItem;
    @FXML private Menu bringToFrontMenu;
    @FXML private MenuItem bringToFrontMenuItem;
    @FXML private MenuItem bringToTopMenuItem;
    @FXML private Menu sendToBackMenu;
    @FXML private MenuItem sendToBackMenuItem;
    @FXML private MenuItem sendToBottomMenuItem;
    @FXML private MenuItem groupMenuItem;
    @FXML private MenuItem ungroupMenuItem;
    @FXML private MenuItem flipHMenuItem;
    @FXML private MenuItem flipVButton; // Renamed to flipVMenuItem for consistency

    // Status Bar Labels
    @FXML private Label cursorPosition;
    @FXML private Label paneDimension;
    @FXML private Label numberOfShapes;
    @FXML private Label zoomPercentageLabel;

    // Private Instance Variables (non-FXML)
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
    private ToggleButton currentlySelectedEditToggle = null; // Track the currently selected edit toggle button

    private AdapterFactory adapterFactory;
    private CommandInvoker commandInvoker;
    private Grid grid;

    // Properties for UI binding
    private final BooleanProperty hasSelectedShapes = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyLinesSelected = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyTextsSelected = new SimpleBooleanProperty(false);
    private final BooleanProperty canGroup = new SimpleBooleanProperty(false); // Can group if at least 2 shapes are selected

    // Zoom and Canvas Dimensions
    private double currentZoomLevel = 1.0;
    private double previousScale = 1.0; // Not currently used, could be for future features.
    private double baseDrawingAreaWidth = 1024;
    private double baseDrawingAreaHeight = 500;

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method sets up core components, configures the drawing area,
     * registers event listeners, sets default UI states, and binds properties.
     */
    @FXML
    public void initialize() {
        initializeCoreComponents();
        configureDrawingArea();
        setupEventListeners();
        setDefaultUIState();
        configureBindings(); // Renamed from configureBind for consistency
    }

    /**
     * Initializes core application components such as the drawing model,
     * persistence service, UI utilities, shape mapping, tool strategies,
     * adapter factory, command invoker, and clipboard.
     */
    private void initializeCoreComponents() {
        model = new DrawingModel();
        model.attach(this); // Attach this controller as an observer to the model

        persistenceService = new PersistenceService();
        uiUtils = new UIUtils();
        shapeMapping = new ShapeMapping();
        shapesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // Allow multiple selections in the list view

        initializeToolStrategies();
        initializeEditButtonMappings();
        adapterFactory = new AdapterFactory();
        commandInvoker = new CommandInvoker();
        clipboard = new Clipboard(adapterFactory);

        grid = new Grid(drawingArea); // Initialize the grid for the drawing area
    }

    /**
     * Initializes the map of {@link ToggleButton}s to their corresponding {@link ToolStrategy} implementations.
     * This allows dynamic switching of drawing behaviors based on user tool selection.
     */
    private void initializeToolStrategies() {
        toolStrategies = new HashMap<>();
        toolStrategies.put(selectionButton, new SelectionToolStrategy(drawingArea, shapeMapping, this));
        toolStrategies.put(lineButton, new LineToolStrategy(drawingArea, this));
        toolStrategies.put(rectangleButton, new RectangleToolStrategy(drawingArea, this));
        toolStrategies.put(ellipseButton, new EllipseToolStrategy(drawingArea, this));
        toolStrategies.put(polygonButton, new PolygonToolStrategy(drawingArea, this));
        toolStrategies.put(textButton, new TextToolStrategy(drawingArea, this));
    }

    /**
     * Initializes the mappings between edit toggle buttons and their respective properties panels.
     * Also sets up the mapping between edit buttons and their associated tool buttons.
     * Hides all properties HBoxes initially.
     */
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

        // Hide all properties HBoxes initially
        for (HBox hbox : editButtonToPropertiesMap.values()) {
            hbox.setVisible(false);
            hbox.setManaged(false);
            hbox.setTranslateX(0); // Ensure no residual translation
        }
    }

    /**
     * Configures the main drawing area.
     * Sets up clipping, initial dimensions, and scroll pane policies.
     */
    private void configureDrawingArea() {
        clipRect = new Rectangle(); // Create a clipping rectangle for the drawing area
        drawingArea.setClip(clipRect); // Apply the clipping rectangle
        drawingArea.setPrefWidth(baseDrawingAreaWidth); // Set initial preferred width
        drawingArea.setPrefHeight(baseDrawingAreaHeight); // Set initial preferred height
        zoomGroup.setScaleX(1.0); // Set initial zoom scale X
        zoomGroup.setScaleY(1.0); // Set initial zoom scale Y
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Always show horizontal scroll bar
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Always show vertical scroll bar
    }

    /**
     * Sets up various event listeners for UI interactions,
     * including tool selection, property changes, zoom, and grid.
     */
    private void setupEventListeners() {
        setupToolToggleListener();
        setupEditToggleListener();
        setupPropertiesListeners();
        setupChangeListeners(); // Renamed from setupChangesListeners for consistency
        setupZoomListeners(); // Renamed from setupZoomListerners for consistency
        setupGridListeners();

        // Listener for drawing area dimension changes to update status bar
        drawingArea.widthProperty().addListener((obs, oldVal, newVal) -> updatePaneDimensions());
        drawingArea.heightProperty().addListener((obs, oldVal, newVal) -> updatePaneDimensions());

        // Consume context menu event on scroll pane to prevent default behavior
        scrollPane.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
    }

    /**
     * Sets up the listener for the tool toggle group.
     * When a new tool is selected, it resets the previous tool's state
     * and activates the new tool with current property values.
     */
    private void setupToolToggleListener() {
        toolToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            ToggleButton selectedToggle = (ToggleButton) newValue;
            if (selectedToggle == null) {
                // If no toggle is selected (e.g., due to code deselecting it), re-select the previous or selection tool.
                toolToggleGroup.selectToggle(oldValue != null ? oldValue : selectionButton);
                return;
            }
            if (currentStrategy != null) {
                currentStrategy.reset(); // Reset the state of the previously active tool
            }
            currentStrategy = toolStrategies.get(selectedToggle); // Get the strategy for the newly selected tool
            if (currentStrategy != null) {
                // Activate the new tool with current property values from UI controls
                currentStrategy.activate(lineBorderColorPicker.getValue(), rectangleBorderColorPicker.getValue(), rectangleFillColorPicker.getValue(), ellipseBorderColorPicker.getValue(), ellipseFillColorPicker.getValue(), polygonBorderColorPicker.getValue(), polygonFillColorPicker.getValue(), textBorderColorPicker.getValue(), textFillColorPicker.getValue(), textColorPicker.getValue(), polygonVertices.getValue(), regularPolygon.isSelected(), fontSize.getValue());
            }
        });
    }

    /**
     * Sets up the listener for the edit toggle group.
     * Manages the visibility and positioning of properties panels based on
     * which edit button is selected.
     */
    private void setupEditToggleListener() {
        editToggleGroup.selectedToggleProperty().addListener((observable, oldToggleRaw, newToggleRaw) -> {
            ToggleButton oldToggle = (ToggleButton) oldToggleRaw;
            ToggleButton newToggle = (ToggleButton) newToggleRaw;

            // Handle deselection if the same button is clicked again
            if (newToggle != null && newToggle == oldToggle) {
                newToggle.setSelected(false);
                return;
            }

            currentlySelectedEditToggle = newToggle;
            HBox hboxToShow = null;
            if (newToggle != null) {
                hboxToShow = editButtonToPropertiesMap.get(newToggle);
            }

            // Hide all other properties HBoxes
            for (Map.Entry<ToggleButton, HBox> entry : editButtonToPropertiesMap.entrySet()) {
                HBox currentHBox = entry.getValue();
                if (currentHBox != hboxToShow) {
                    if (currentHBox.isVisible()) {
                        uiUtils.hidePropertiesHBox(currentHBox);
                    }
                }
            }

            // Show the selected properties HBox if it's not null
            if (hboxToShow != null) {
                if (!hboxToShow.isVisible()) {
                    uiUtils.showPropertiesHBox(toolsVBox, zoomGroup, scrollPane, hboxToShow, newToggle);
                }
            }
        });
    }

    /**
     * Sets up listeners for property change events on various color pickers and combo boxes.
     * Notifies the current tool strategy about these changes and updates preview shapes.
     */
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
                // Update the style of the text preview field
                uiUtils.updateTextPreviewStyle(textPreview, textFillColorPicker, textColorPicker, textBorderColorPicker, fontSize);
            }
        });
        textFillColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleTextFillColorChange(newColor);
            }
            if (textPreview != null) {
                // Update the style of the text preview field
                uiUtils.updateTextPreviewStyle(textPreview, textFillColorPicker, textColorPicker, textBorderColorPicker, fontSize);
            }
        });
        textColorPicker.valueProperty().addListener((obs, oldColor, newColor) -> {
            if (currentStrategy != null) {
                currentStrategy.handleTextColorChange(newColor);
            }
            if (textPreview != null) {
                // Update the style of the text preview field
                uiUtils.updateTextPreviewStyle(textPreview, textFillColorPicker, textColorPicker, textBorderColorPicker, fontSize);
            }
        });
        fontSize.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (currentStrategy != null && newVal != null) {
                currentStrategy.handleFontSizeChange(newVal.intValue());
            }
            if (textPreview != null && newVal != null) {
                // Update the style of the text preview field
                uiUtils.updateTextPreviewStyle(textPreview, textFillColorPicker, textColorPicker, textBorderColorPicker, fontSize);
            }
        });
        polygonVertices.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (currentStrategy != null && newVal != null) {
                currentStrategy.handlePolygonVerticesChange(newVal.intValue());
            }
            if (polygonShapePreview != null && newVal != null) {
                // Update polygon preview based on new number of vertices
                int sides = newVal.intValue();
                if (sides >= 3 && sides <= 8) { // Ensure valid range for polygon vertices
                    double centerX = 25;
                    double centerY = 25;
                    double radius = 12;
                    double angleStep = 2 * Math.PI / sides;
                    ObservableList<Double> points = FXCollections.observableArrayList();

                    for (int i = 0; i < sides; i++) {
                        double angle = i * angleStep - Math.PI / 2; // Start from top
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

    /**
     * Sets up listeners for property changes that originate from context menus
     * or global modification panels (e.g., color pickers for selected shapes).
     */
    private void setupChangeListeners() {
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
            if (currentStrategy != null && newVal != null) {
                currentStrategy.handleFontSizeMenuChange(newVal.intValue());
            }
        });
    }

    /**
     * Sets up listeners for zoom-related UI controls.
     * Binds the zoom slider value to the drawing area's scale.
     */
    private void setupZoomListeners() {
        zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double scale = newValue.doubleValue();
            applyZoom(scale);
        });
        // Listeners to ensure zoomGroup's preferred size matches drawing area's size
        drawingArea.prefHeightProperty().addListener((observable, oldValue, newValue) ->
                zoomGroup.prefHeight(newValue.doubleValue())
        );
        drawingArea.prefWidthProperty().addListener((observable, oldValue, newVal) ->
                zoomGroup.prefWidth(newVal.doubleValue())
        );
    }

    /**
     * Applies the specified zoom scale to the drawing area and updates the zoom percentage label.
     * Also triggers an update of the displayed pane dimensions.
     *
     * @param scale The desired zoom scale (e.g., 1.0 for 100%, 0.5 for 50%).
     */
    private void applyZoom(double scale) {
        // Round scale to two decimal places for cleaner display
        BigDecimal bd = new BigDecimal(Double.toString(scale));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        final double newScale = bd.doubleValue();

        drawingArea.setScaleX(newScale); // Apply scale to X-axis
        drawingArea.setScaleY(newScale); // Apply scale to Y-axis
        zoomPercentageLabel.setText(String.format("%.0f%%", newScale * 100)); // Update percentage label
        currentZoomLevel = newScale; // Store the current zoom level
        updatePaneDimensions(); // Update the displayed pane dimensions after zoom
    }

    /**
     * Sets up listeners for grid-related UI controls and property changes.
     * Triggers grid redrawing when drawing area dimensions change.
     */
    private void setupGridListeners() {
        drawingArea.widthProperty().addListener((obs, oldVal, newVal) -> {
            grid.drawGrid(); // Redraw grid when width changes
        });
        drawingArea.heightProperty().addListener((obs, oldVal, newVal) -> {
            grid.drawGrid(); // Redraw grid when height changes
        });
    }

    /**
     * Sets the default initial state of the UI controls and previews.
     * This includes selecting the default tool, setting default colors and sizes,
     * and initializing preview shapes.
     */
    private void setDefaultUIState() {
        toolToggleGroup.selectToggle(selectionButton); // Select the selection tool by default
        lineBorderColorPicker.setValue(Color.BLACK);
        rectangleBorderColorPicker.setValue(Color.BLACK);
        rectangleFillColorPicker.setValue(Color.TRANSPARENT);
        ellipseBorderColorPicker.setValue(Color.BLACK);
        ellipseFillColorPicker.setValue(Color.TRANSPARENT);
        polygonBorderColorPicker.setValue(Color.BLACK);
        polygonFillColorPicker.setValue(Color.TRANSPARENT);

        // Initialize polygon vertices combo box
        if (polygonVertices.getItems().isEmpty()) {
            polygonVertices.getItems().addAll(3, 4, 5, 6, 7, 8);
        }
        polygonVertices.setValue(3); // Default to triangle
        regularPolygon.setSelected(false); // Default to irregular polygon

        textBorderColorPicker.setValue(Color.BLACK);
        textFillColorPicker.setValue(Color.TRANSPARENT);
        textColorPicker.setValue(Color.BLACK);

        // Initialize font size combo box
        if (fontSize.getItems().isEmpty()) {
            fontSize.getItems().addAll(14, 16, 18, 20, 22, 24);
        }
        fontSize.setValue(14); // Default font size

        // Initial style for text preview
        if (textPreview != null) {
            textPreview.setStyle("-fx-control-inner-background: transparent; -fx-background-color: transparent;");
        }
        uiUtils.initializePolygonPreview(polygonShapePreview); // Initialize the polygon shape preview

        menuItemToggleGrid.setSelected(false); // Grid off by default
        cellSize20.setSelected(true); // Default cell size to 20

        // Configure zoom slider properties
        zoomSlider.setMin(0.25);
        zoomSlider.setMax(2.5);
        zoomSlider.setValue(1.0); // Default zoom level to 100%
        zoomPercentageLabel.setText(String.format("%.0f%%", zoomSlider.getValue() * 100)); // Update label
    }

    /**
     * Configures property bindings for UI elements, enabling/disabling controls
     * based on application state (e.g., selection status, undo availability).
     */
    private void configureBindings() { // Renamed from configureBind
        clipRect.widthProperty().bind(drawingArea.widthProperty());
        clipRect.heightProperty().bind(drawingArea.heightProperty());

        undoButton.disableProperty().bind(commandInvoker.canUndoProperty().not()); // Undo button enabled when undo is possible

        borderColorModifyMenuItem.disableProperty().bind(hasSelectedShapes.not()); // Border color modify enabled only if shapes are selected
        fillColorModifyMenuItem.disableProperty().bind(
                hasSelectedShapes.not().or(onlyLinesSelected) // Fill color modify enabled if shapes selected AND not only lines
        );

        // Text-specific modification options enabled only if only text shapes are selected
        textColorModifyMenuItem.disableProperty().bind(onlyTextsSelected.not());
        fontSizeModifyMenuItem.disableProperty().bind(onlyTextsSelected.not());

        // Clipboard actions
        cutMenuItem.disableProperty().bind(hasSelectedShapes.not()); // Cut enabled only if shapes are selected
        copyMenuItem.disableProperty().bind(hasSelectedShapes.not()); // Copy enabled only if shapes are selected
        deleteMenuItem.disableProperty().bind(hasSelectedShapes.not()); // Delete enabled only if shapes are selected
        pasteMenuItem.disableProperty().bind(clipboard.emptyProperty()); // Paste enabled only if clipboard is not empty

        // Z-order manipulation
        bringToFrontMenu.disableProperty().bind(hasSelectedShapes.not()); // Bring to front menu enabled only if shapes are selected
        sendToBackMenu.disableProperty().bind(hasSelectedShapes.not()); // Send to back menu enabled only if shapes are selected

        // Grouping/Ungrouping (currently not fully implemented in commands)
        // groupMenuItem.disableProperty().bind(canGroup.not());
        // ungroupMenuItem.disableProperty().bind(canGroup.not());

        // Flipping actions
        flipHMenuItem.disableProperty().bind(hasSelectedShapes.not()); // Flip Horizontal enabled only if shapes are selected
        flipVButton.disableProperty().bind(hasSelectedShapes.not()); // Flip Vertical enabled only if shapes are selected (renamed in FXML for consistency)
    }

    /**
     * Updates the displayed dimensions of the drawing area in the status bar.
     */
    private void updatePaneDimensions() {
        // Get the bounds of the drawingArea within the zoomed group
        Bounds zoomedBounds = drawingArea.getBoundsInParent();
        paneDimension.setText(String.format("%.0f x %.0f", zoomedBounds.getWidth(), zoomedBounds.getHeight()));
    }

    // FXML Event Handlers

    /**
     * Handles the undo button click event.
     * Executes the undo operation via the {@link CommandInvoker}.
     *
     * @param actionEvent The {@link ActionEvent} generated by the button click.
     */
    @FXML
    public void handleUndo(Event actionEvent) {
        if (commandInvoker.canUndo()) {
            commandInvoker.undo();
        }
    }

    /**
     * Handles the hide (minimize) button click event.
     * Minimizes the application window.
     *
     * @param actionEvent The {@link ActionEvent} generated by the button click.
     */
    @FXML
    public void handleHide(ActionEvent actionEvent) {
        Stage stage = (Stage) hideButton.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Handles the maximize/restore button click event.
     * Toggles the maximization state of the application window and updates the button icon.
     *
     * @param actionEvent The {@link ActionEvent} generated by the button click.
     */
    @FXML
    public void handleMaximize(ActionEvent actionEvent) {
        Stage stage = (Stage) maximizeButton.getScene().getWindow();
        if (stage.isMaximized()) {
            stage.setMaximized(false);
            FontIcon icon = (FontIcon) maximizeButton.getGraphic();
            icon.setIconLiteral("mdi2c-crop-square"); // Icon for restore
        } else {
            stage.setMaximized(true);
            FontIcon icon = (FontIcon) maximizeButton.getGraphic();
            icon.setIconLiteral("mdi2w-window-restore"); // Icon for maximize
        }
    }

    /**
     * Handles the close button click event.
     * Closes the application window.
     *
     * @param actionEvent The {@link ActionEvent} generated by the button click.
     */
    @FXML
    public void handleClose(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the "Load" menu item click event.
     * Opens a file chooser to select a drawing file and loads its content into the model.
     * Prompts for confirmation if unsaved changes might be lost.
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
    @FXML
    public void handleLoad(ActionEvent actionEvent) {
        FileChooser fileChooser = uiUtils.createFileChooser("Open Drawing", null, persistenceService.getDirectoryName());
        File file = fileChooser.showOpenDialog(drawingArea.getScene().getWindow());
        if (file != null) {
            boolean proceed = uiUtils.showConfirmDialog("Confirm Load", "Load New Drawing", "Loading a new drawing will discard any unsaved changes. Continue?");
            if (proceed) {
                try {
                    List<MyShape> loadedShapes = persistenceService.loadDrawing(file);
                    model.clearShapes(); // Clear current shapes in model
                    if (currentStrategy != null) {
                        currentStrategy.reset(); // Reset current tool strategy
                    }
                    for (MyShape shape : loadedShapes) {
                        model.addShape(shape); // Add loaded shapes to model
                    }
                    uiUtils.showSuccessDialog("Load Successful", "Drawing loaded from " + file.getName());
                } catch (Exception e) {
                    uiUtils.showErrorDialog("Load Error", "Could not load drawing: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handles the "Save" menu item click event.
     * Saves the current drawing to the last used file. If no file has been used,
     * it behaves like "Save As".
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
    @FXML
    public void handleSave(ActionEvent actionEvent) {
        if (persistenceService.getCurrentFile() == null) {
            handleSaveAs(actionEvent); // If no current file, prompt for "Save As"
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

    /**
     * Handles the "Save As" menu item click event.
     * Opens a file chooser to allow the user to select a new file location and name
     * for saving the current drawing. Appends ".geoshapes" extension if missing.
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
    @FXML
    public void handleSaveAs(ActionEvent actionEvent) {
        FileChooser fileChooser = uiUtils.createFileChooser("Save Drawing As...", persistenceService.getFileName() != null ? persistenceService.getFileName() : "myDrawing.geoshapes", persistenceService.getDirectoryName());
        File file = fileChooser.showSaveDialog(drawingArea.getScene().getWindow());
        if (file != null) {
            String filePath = file.getAbsolutePath();
            // Ensure .geoshapes extension
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

    /**
     * Handles the "Show Shapes Manager" menu item (CheckMenuItem) click event.
     * Toggles the visibility of the shapes manager panel.
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
    @FXML
    private void handleShowShapesManager(ActionEvent actionEvent) {
        shapeManagerPanel.setVisible(shapesManagerMenuItem.isSelected());
        shapeManagerPanel.setManaged(shapesManagerMenuItem.isSelected());
    }

    /**
     * Handles the "Toggle Grid" menu item (CheckMenuItem) click event.
     * Enables or disables the drawing grid and redraws it.
     *
     * @param event The {@link ActionEvent} generated by the menu item click.
     */
    @FXML
    private void toggleGrid(ActionEvent event) {
        grid.setGridEnabled(menuItemToggleGrid.isSelected());
        grid.drawGrid();
    }

    /**
     * Handles the grid cell size radio menu item selection.
     * Sets the grid cell size based on the selected radio button and redraws the grid.
     *
     * @param event The {@link ActionEvent} generated by the radio menu item click.
     */
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

    /**
     * Handles the 75% zoom radio menu item click event.
     * Sets the zoom slider value to 0.75 (75%).
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
    @FXML
    public void handleZoom75(ActionEvent actionEvent) {
        zoomSlider.setValue(0.75);
    }

    /**
     * Handles the 100% zoom radio menu item click event.
     * Sets the zoom slider value to 1.0 (100%).
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
    @FXML
    public void handleZoom100(ActionEvent actionEvent) {
        zoomSlider.setValue(1.0);
    }

    /**
     * Handles the 200% zoom radio menu item click event.
     * Sets the zoom slider value to 2.0 (200%).
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
    @FXML
    public void handleZoom200(ActionEvent actionEvent) {
        zoomSlider.setValue(2.0);
    }

    /**
     * Handles the "About" menu item click event.
     * This method is currently a placeholder for displaying application information.
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
    @FXML
    public void handleAbout(ActionEvent actionEvent) {
        // Placeholder for "About" dialog logic
    }

    /**
     * Handles the mouse pressed event on the drawing area.
     * Passes the event to the current tool strategy for processing.
     * Disables scroll pane pannable if the current strategy is not for selection.
     *
     * @param event The {@link MouseEvent} generated by the mouse press.
     */
    @FXML
    public void handleMousePressed(MouseEvent event) {
        if (currentStrategy != null) {
            // If not in selection mode, disable scrolling to allow tool drawing.
            if (!(currentStrategy instanceof SelectionToolStrategy)) {
                scrollPane.setPannable(false);
            }
            currentStrategy.handleMousePressed(event);
        }
    }

    /**
     * Handles the mouse dragged event on the drawing area.
     * Passes the event to the current tool strategy for processing.
     *
     * @param event The {@link MouseEvent} generated by the mouse drag.
     */
    @FXML
    public void handleMouseDragged(MouseEvent event) {
        if (currentStrategy != null) {
            currentStrategy.handleMouseDragged(event);
        }
    }

    /**
     * Handles the mouse released event on the drawing area.
     * Passes the event to the current tool strategy for processing.
     *
     * @param event The {@link MouseEvent} generated by the mouse release.
     */
    @FXML
    public void handleMouseReleased(MouseEvent event) {
        if (currentStrategy != null) {
            currentStrategy.handleMouseReleased(event);
        }
    }

    /**
     * Handles the mouse moved event on the drawing area.
     * Passes the event to the current tool strategy for processing and updates
     * the cursor position label in the status bar.
     *
     * @param event The {@link MouseEvent} generated by the mouse movement.
     */
    @FXML
    public void handleMouseMoved(MouseEvent event) {
        if (currentStrategy != null) {
            currentStrategy.handleMouseMoved(event);
        }
        Point2D mousePoint = drawingArea.sceneToLocal(event.getSceneX(), event.getSceneY());
        cursorPosition.setText(String.format("%.0f, %.0f", mousePoint.getX(), mousePoint.getY()));
    }

    /**
     * Handles global key pressed events on the scene.
     * Processes shortcuts for delete, copy, paste, cut, and undo.
     * Passes other key events to the current tool strategy.
     *
     * @param event The {@link KeyEvent} generated by the key press.
     */
    public void handleKeyPressed(KeyEvent event) {
        // Global command shortcuts
        if (event.getCode() == KeyCode.DELETE) {
            handleDelete(event);
            event.consume();
            return;
        } else if (event.isControlDown()) {
            switch (event.getCode()) {
                case C:
                    handleCopy(event);
                    event.consume();
                    return;
                case V:
                    handlePaste(event);
                    event.consume();
                    return;
                case X:
                    handleCut(event);
                    event.consume();
                    return;
                case Z:
                    handleUndo(event);
                    event.consume();
                    return;
                default:
                    break; // Do nothing for other Ctrl combinations
            }
        }

        // Other global keys
        switch (event.getCode()) {
            case ESCAPE:
                // Deselect everything or cancel current tool operation
                if (currentStrategy != null) {
                    currentStrategy.reset();
                }
                event.consume();
                return;
            default:
                break; // Do nothing for other keys
        }

        // Pass event to current tool strategy if not consumed by global commands
        if (currentStrategy != null) {
            currentStrategy.handleKeyPressed(event);
        }
    }

    /**
     * Handles global key typed events on the scene.
     * Passes the event to the current tool strategy.
     *
     * @param event The {@link KeyEvent} generated by the key typed action.
     */
    public void handleKeyTyped(KeyEvent event) {
        if (currentStrategy != null) {
            currentStrategy.handleKeyTyped(event);
        }
    }

    /**
     * Handles the "Cut" menu item or shortcut.
     * Copies selected shapes to the clipboard and then deletes them from the model.
     *
     * @param actionEvent The {@link Event} that triggered this action.
     */
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
            clipboard.copy(shapesToCopy); // Copy to clipboard
            Command deleteCommand = new DeleteShapeCommand(model, shapesToCopy); // Create delete command
            commandInvoker.executeCommand(deleteCommand); // Execute delete command
        }
    }

    /**
     * Handles the "Copy" menu item or shortcut.
     * Copies selected shapes to the clipboard.
     *
     * @param actionEvent The {@link Event} that triggered this action.
     */
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
                clipboard.copy(shapesToCopy); // Copy to clipboard
            }
        }
    }

    /**
     * Handles the "Paste" menu item or shortcut.
     * Pastes shapes from the clipboard to the drawing area, applying an offset.
     *
     * @param actionEvent The {@link Event} that triggered this action.
     */
    @FXML
    public void handlePaste(Event actionEvent) {
        if (clipboard.isEmpty()) {
            return; // Nothing to paste
        }
        List<MyShape> pastedShapes = clipboard.paste(); // Paste shapes with offset
        Command createCommand = new CreateShapeCommand(model, pastedShapes); // Create command to add new shapes
        commandInvoker.executeCommand(createCommand); // Execute command
    }

    /**
     * Handles the "Delete" menu item or shortcut.
     * Deletes currently selected shapes from the drawing model.
     *
     * @param actionEvent The {@link Event} that triggered this action.
     */
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
            Command deleteCommand = new DeleteShapeCommand(model, shapesToDelete); // Create delete command
            commandInvoker.executeCommand(deleteCommand); // Execute delete command
        }
    }

    /**
     * Handles the "Bring To Front" menu item.
     * Moves selected shapes one level forward in the Z-order.
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
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
            Command bringToFrontCommand = new BringToFrontCommand(model, myShapes); // Create command
            commandInvoker.executeCommand(bringToFrontCommand); // Execute command
        }
    }

    /**
     * Handles the "Bring To Top" menu item.
     * Moves selected shapes to the very top of the Z-order.
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
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
            Command bringToTopCommand = new BringToTopCommand(model, myShapes); // Create command
            commandInvoker.executeCommand(bringToTopCommand); // Execute command
        }
    }

    /**
     * Handles the "Send To Back" menu item.
     * Moves selected shapes one level backward in the Z-order.
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
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
            Command sendToBackCommand = new SendToBackCommand(model, myShapes); // Create command
            commandInvoker.executeCommand(sendToBackCommand); // Execute command
        }
    }

    /**
     * Handles the "Send To Bottom" menu item.
     * Moves selected shapes to the very bottom of the Z-order.
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
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
            Command sendToBottomCommand = new SendToBottomCommand(model, myShapes); // Create command
            commandInvoker.executeCommand(sendToBottomCommand); // Execute command
        }
    }

    /**
     * Handles the "Group" menu item.
     * This method is currently a placeholder for grouping shapes.
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
    @FXML
    public void handleGroup(ActionEvent actionEvent) {
        // Placeholder for grouping logic
    }

    /**
     * Handles the "Ungroup" menu item.
     * This method is currently a placeholder for ungrouping shapes.
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
    @FXML
    public void handleUngroup(ActionEvent actionEvent) {
        // Placeholder for ungrouping logic
    }

    /**
     * Handles the "Flip Horizontal" menu item.
     * Flips selected shapes horizontally.
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
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
            FlipHShapeCommand command = new FlipHShapeCommand(model, myShapes); // Create command
            commandInvoker.executeCommand(command); // Execute command
        }
    }

    /**
     * Handles the "Flip Vertical" menu item.
     * Flips selected shapes vertically.
     *
     * @param actionEvent The {@link ActionEvent} generated by the menu item click.
     */
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
            FlipVShapeCommand command = new FlipVShapeCommand(model, myShapes); // Create command
            commandInvoker.executeCommand(command); // Execute command
        }
    }

    // Public methods implementing interfaces

    /**
     * Implements the {@code update()} method from {@link ShapeObserver}.
     * This method is called when the {@link DrawingModel} changes.
     * It redraws all shapes on the drawing area, rebuilds the shape mapping,
     * updates the shapes list view, and refreshes the shape count label.
     */
    @Override
    public void update() {
        currentStrategy.reset(); // Reset the current tool strategy to clear previews/selections
        drawingArea.getChildren().clear(); // Clear all existing JavaFX shapes from the drawing area

        drawingArea.getChildren().add(0, grid.getGridCanvas()); // Re-add the grid as the first child

        List<MyShape> modelShapes = model.getShapes(); // Get updated shapes from the model
        List<Shape> newViewShapes = new ArrayList<>();

        // Convert model shapes to JavaFX shapes and add to drawing area
        for (MyShape shape : modelShapes) {
            Shape javafxShape = adapterFactory.convertToJavaFx(shape, drawingArea.getWidth(), drawingArea.getHeight());
            drawingArea.getChildren().add(javafxShape);
            newViewShapes.add(javafxShape);
        }

        shapeMapping.rebuildMapping(modelShapes, newViewShapes); // Rebuild the mapping

        // Update the shapes list view
        ObservableList<String> shapeNames = FXCollections.observableArrayList();
        model.getShapesReversed().forEach(shape -> shapeNames.add(shape.getName()));
        shapesListView.setItems(shapeNames);

        // Update the number of shapes label
        numberOfShapes.setText(String.valueOf(modelShapes.size()));

        // Debugging output (translated to English)
        System.out.println("Shapes in model: " + modelShapes.size());
        System.out.println("Shapes in mapping: " + shapeMapping.size());
        System.out.println("Shapes in view: " + (drawingArea.getChildren().size() - 1)); // Subtract grid from children count
        model.printAllShapes();
    }

    /**
     * Implements the {@code onCreateShape()} method from {@link InteractionCallback}.
     * This method is called by tool strategies when a new JavaFX shape has been drawn.
     * It converts the JavaFX shape to a model shape and executes a {@link CreateShapeCommand}.
     *
     * @param shape The newly created JavaFX {@link Shape} object.
     */
    @Override
    public void onCreateShape(Shape shape) {
        // Convert JavaFX shape to model shape and create a command
        Command createCommand = new CreateShapeCommand(model, adapterFactory.convertToModel(shape, drawingArea.getWidth(), drawingArea.getHeight()));
        commandInvoker.executeCommand(createCommand); // Execute the command
    }

    /**
     * Implements the {@code onModifyShapes()} method from {@link InteractionCallback}.
     * This method is called by tool strategies when selected JavaFX shapes have been modified.
     * It creates a {@link ModifyShapeCommand} to update the model.
     *
     * @param shapes A {@code List} of JavaFX {@link Shape} objects whose properties have changed.
     */
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

            // Create deep clones of the old model shapes to preserve their state for undo
            List<MyShape> clonedOldModelShapes = new ArrayList<>();
            for (MyShape modelShape : oldModelShapes) {
                clonedOldModelShapes.add(modelShape.clone());
            }

            List<MyShape> newModelShapes = new ArrayList<>();
            for (Shape viewShape : shapes) {
                MyShape newModelShape = adapterFactory.convertToModel(viewShape, drawingArea.getWidth(), drawingArea.getHeight());
                if (newModelShape != null) {
                    newModelShapes.add(newModelShape);
                }
            }

            Command modifyShapeCommand = new ModifyShapeCommand(model, oldModelShapes, clonedOldModelShapes, newModelShapes);
            commandInvoker.executeCommand(modifyShapeCommand); // Execute the command
        }
    }

    /**
     * Implements the {@code onSelectionMenuOpened()} method from {@link InteractionCallback}.
     * Displays the context menu at the specified screen coordinates.
     * Adds an event filter to hide the context menu when the mouse is pressed outside it.
     *
     * @param x The screen X-coordinate where the context menu should appear.
     * @param y The screen Y-coordinate where the context menu should appear.
     */
    @Override
    public void onSelectionMenuOpened(double x, double y) {
        contextMenu.show(drawingArea, x, y);

        // Add a temporary event filter to hide the context menu on any mouse press outside it
        drawingArea.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!contextMenu.isShowing()) {
                    return; // Menu is already hidden, no need to do anything
                }
                contextMenu.hide(); // Hide the menu
                // Remove this event filter after it has served its purpose
                drawingArea.getScene().removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
            }
        });
    }

    /**
     * Implements the {@code onChangeShapeSelected()} method from {@link InteractionCallback}.
     * Updates the UI state based on the current selection of shapes.
     * This includes enabling/disabling menu items and updating the shapes list view.
     */
    @Override
    public void onChangeShapeSelected() {
        List<Shape> currentSelection = currentStrategy.getSelectedShapes(); // Get selected shapes

        int size = currentSelection.size();

        hasSelectedShapes.set(size > 0); // Update property for any selection
        canGroup.set(size >= 2); // Update property for grouping (requires at least 2 shapes)

        // Determine if only lines are selected
        boolean areOnlyLinesSelected = size > 0 && currentSelection.stream()
                .allMatch(shape -> shape instanceof Line);
        onlyLinesSelected.set(areOnlyLinesSelected);

        // Determine if only text shapes are selected
        // NOTE: The original code had 'boolean onlyTexts = false;' followed by 'onlyLinesSelected.set(onlyTexts);'
        // This implies that text selection logic was not fully implemented or intentionally disabled.
        // As per instructions, not altering logic, but commenting on the observed behavior.
        boolean areOnlyTextsSelected = false; // Logic for identifying text shapes is not fully implemented in provided code.
        onlyTextsSelected.set(areOnlyTextsSelected); // Assign the (currently always false) value

        updateListViewSelection(currentSelection); // Update the shapes list view
    }

    // Private methods

    /**
     * Updates the selection state in the {@link ListView} of shapes
     * to reflect the currently selected JavaFX shapes on the canvas.
     *
     * @param currentSelection A {@code List} of currently selected JavaFX {@link Shape} objects.
     */
    private void updateListViewSelection(List<Shape> currentSelection) {
        // Always clear previous selection in the ListView first
        shapesListView.getSelectionModel().clearSelection();

        if (currentSelection == null || currentSelection.isEmpty()) {
            return;
        }

        try {
            for (Shape viewShape : currentSelection) {
                MyShape modelShape = shapeMapping.getModelShape(viewShape);
                if (modelShape != null) {
                    // Find the index of the model shape in the reversed list used by ListView
                    int indexInReversedList = model.getShapesReversed().indexOf(modelShape);
                    if (indexInReversedList >= 0) {
                        // Select the corresponding item in the ListView
                        shapesListView.getSelectionModel().select(indexInReversedList);
                    }
                }
            }
        } catch (Exception e) {
            // In case of any unexpected error during list view update, clear selection to avoid inconsistent state.
            System.err.println("Error updating list view selection: " + e.getMessage());
            shapesListView.getSelectionModel().clearSelection();
        }
    }
}