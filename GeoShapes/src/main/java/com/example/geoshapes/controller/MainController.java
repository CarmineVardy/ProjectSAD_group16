package com.example.geoshapes.controller;

import com.example.geoshapes.adapter.EllipseAdapter;
import com.example.geoshapes.adapter.LineAdapter;
import com.example.geoshapes.adapter.RectangleAdapter;
import com.example.geoshapes.adapter.ShapeAdapter;
import com.example.geoshapes.model.DrawingModel;
import com.example.geoshapes.model.shapes.MyEllipse;
import com.example.geoshapes.model.shapes.MyLine;
import com.example.geoshapes.model.shapes.MyRectangle;
import com.example.geoshapes.model.shapes.MyShape;
import com.example.geoshapes.controller.strategy.ToolStrategy;
import com.example.geoshapes.controller.strategy.SelectionToolStrategy;
import com.example.geoshapes.controller.strategy.LineToolStrategy;
import com.example.geoshapes.controller.strategy.RectangleToolStrategy;
import com.example.geoshapes.controller.strategy.EllipseToolStrategy;
import com.example.geoshapes.controller.command.Command;
import com.example.geoshapes.controller.command.CreateShapeCommand;
import com.example.geoshapes.observer.ShapeObserver;
import com.example.geoshapes.service.PersistenceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


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

    private Rectangle clipRect;

    private DrawingModel model;

    private ToolStrategy currentStrategy;
    private Map<ToggleButton, ToolStrategy> toolStrategies;

    private ShapeAdapter adapter;

    private PersistenceService persistenceService;
    private File currentFile = null;

    @FXML
    public void initialize() {

        setupDefaultUIState();
        initializeToolStrategies();

        // Da gestire meglio nel servizio dei dati
        this.persistenceService = new PersistenceService();
        currentFile = null;

        model = new DrawingModel();
        model.attach(this);

        setupPanel();
        setupToolListeners();
    }

    private void setupPanel() {

        clipRect = new Rectangle();
        drawingArea.setClip(clipRect);
        clipRect.widthProperty().bind(drawingArea.widthProperty());
        clipRect.heightProperty().bind(drawingArea.heightProperty());

        drawingArea.widthProperty().addListener((obs, oldVal, newVal) -> rebuildShapes());
        drawingArea.heightProperty().addListener((obs, oldVal, newVal) -> rebuildShapes());
    }

    private void rebuildShapes() {
        drawingArea.getChildren().clear();

        for (MyShape shape : model.getShapes()) {
            addShapeToView(shape);
        }
    }

    private void setupDefaultUIState() {

        selectionButton.setSelected(true);

        borderColorPicker.setValue(Color.BLACK);
        fillColorPicker.setValue(Color.TRANSPARENT);
    }

    private void initializeToolStrategies() {
        toolStrategies = new HashMap<>();
        toolStrategies.put(selectionButton, new SelectionToolStrategy(drawingArea));
        toolStrategies.put(lineButton, new LineToolStrategy(drawingArea, borderColorPicker, fillColorPicker));
        toolStrategies.put(rectangleButton, new RectangleToolStrategy(drawingArea, borderColorPicker, fillColorPicker));
        toolStrategies.put(ellipseButton, new EllipseToolStrategy(drawingArea, borderColorPicker, fillColorPicker));
    }


    private void setupToolListeners() {
        toolToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                currentStrategy = toolStrategies.get(selectionButton);
            } else {
                currentStrategy = toolStrategies.get((ToggleButton) newValue);
            }
        });
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

            if (!(currentStrategy instanceof SelectionToolStrategy)) {
                MyShape newShape = currentStrategy.getFinalShape();
                if (newShape != null) {
                    Command command = new CreateShapeCommand(model, newShape);
                    command.execute();
                }
            }
        }
    }

    private void addShapeToView(MyShape shape) {
        if (shape instanceof MyLine) {
            adapter = new LineAdapter((MyLine) shape, drawingArea);
        } else if (shape instanceof MyRectangle) {
            adapter = new RectangleAdapter((MyRectangle) shape, drawingArea);
        } else if (shape instanceof MyEllipse) {
            adapter = new EllipseAdapter((MyEllipse) shape, drawingArea);
        }
        if (adapter != null) {
            drawingArea.getChildren().add(adapter.getFxShape());
        }
    }


    @Override
    public void update(MyShape shape) {
        addShapeToView(shape);
    }

    @FXML
    public void handleLoad(ActionEvent actionEvent) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirm Load");
        confirmationDialog.setHeaderText("Load New Drawing");
        confirmationDialog.setContentText("Loading a new drawing will discard any unsaved changes. Continue?");

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Drawing");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("GeoShapes Drawing", "*.ser"), // NUOVA ESTENSIONE
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File file = fileChooser.showOpenDialog(drawingArea.getScene().getWindow());

            if (file != null) {
                try {
                    List<MyShape> loadedMyShapes = persistenceService.loadDrawing(file);

                    // Pulisci modello e vista
                    // Se hai il sistema onModelCleared():
                    // model.clearAndLoadShapes(loadedMyShapes);
                    // Altrimenti, approccio diretto:
                    model.clearShapes();
                    drawingArea.getChildren().clear();
                    for (MyShape myShape : loadedMyShapes) {
                        model.addShape(myShape); // Questo chiamerà MainController.update()
                    }

                    currentFile = file;
                    showSuccessAlert("Load Successful", "Drawing loaded from " + file.getName());
                } catch (IOException | ClassNotFoundException e) { // Aggiungi ClassNotFoundException
                    showErrorAlert("Load Error", "Could not load drawing: " + e.getMessage());
                    e.printStackTrace();
                    currentFile = null;
                }
            }
        }
    }

    @FXML
    public void handleSave(ActionEvent actionEvent) {
        if (currentFile == null) {
            handleSaveAs(actionEvent);
        } else {
            try {
                persistenceService.saveDrawing(currentFile, model.getShapes());
                showSuccessAlert("Save Successful", "Drawing updated in " + currentFile.getName());
            } catch (IOException e) {
                showErrorAlert("Save Error", "Could not save drawing: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    @FXML
    public void handleSaveAs(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Drawing As...");
        // Aggiorna estensione file
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GeoShapes Drawing", "*.ser"), // NUOVA ESTENSIONE
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        // Stage owner (per centrare il dialogo)
        File file = fileChooser.showSaveDialog(drawingArea.getScene().getWindow());


        if (file != null) {
            // Assicura che il nome del file finisca con .ser se l'utente non l'ha specificato
            String filePath = file.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".ser")) {
                file = new File(filePath + ".ser");
            }

            try {
                persistenceService.saveDrawing(file, model.getShapes());
                currentFile = file;
                showSuccessAlert("Save Successful", "Drawing saved to " + file.getName());
            } catch (IOException e) {
                showErrorAlert("Save Error", "Could not save drawing: " + e.getMessage());
                e.printStackTrace(); // Utile per debug
            }
        }
    }


    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}