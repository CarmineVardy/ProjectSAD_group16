package com.example.geoshapes.controller;

import com.example.geoshapes.adapter.EllipseAdapter;
import com.example.geoshapes.adapter.LineAdapter;
import com.example.geoshapes.adapter.RectangleAdapter;
import com.example.geoshapes.adapter.ShapeAdapter;
import com.example.geoshapes.controller.command.DeleteShapeCommand;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import java.util.*;

import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;


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

    private ContextMenu shapeContextMenu;

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

    private ShapeMapping shapeMapping;

    private ToolStrategy currentStrategy;
    private Map<ToggleButton, ToolStrategy> toolStrategies;

    private ShapeAdapter adapter;


    private PersistenceService persistenceService;
    private File currentFile = null;

    @FXML
    public void initialize() {

        model = new DrawingModel();
        model.attach(this);

        shapeMapping = new ShapeMapping();

        /*
        this.persistenceService = new PersistenceService();
        currentFile = null;
         */

        setupDefaultUIState();
        setupPanel();
        initializeToolStrategies();
        setupToolListeners();
        setupContextMenu();
    }

    private void setupDefaultUIState() {
        selectionButton.setSelected(true);
        borderColorPicker.setValue(Color.BLACK);
        fillColorPicker.setValue(Color.TRANSPARENT);
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
        shapeMapping.clear();

        for (MyShape shape : model.getShapes()) {
            setAdapter(shape);
            if (adapter != null) {
                Shape fxShape = adapter.getFxShape(shape, drawingArea.getWidth(), drawingArea.getHeight());
                drawingArea.getChildren().add(fxShape);
                shapeMapping.register(shape, fxShape);
            }
        }

        if (currentStrategy instanceof SelectionToolStrategy) {
            ((SelectionToolStrategy) currentStrategy).resetSelection();
        }

    }

    private void initializeToolStrategies() {
        toolStrategies = new HashMap<>();
        toolStrategies.put(selectionButton, new SelectionToolStrategy(drawingArea, shapeMapping, model));
        toolStrategies.put(lineButton, new LineToolStrategy(drawingArea, borderColorPicker, fillColorPicker));
        toolStrategies.put(rectangleButton, new RectangleToolStrategy(drawingArea, borderColorPicker, fillColorPicker));
        toolStrategies.put(ellipseButton, new EllipseToolStrategy(drawingArea, borderColorPicker, fillColorPicker));

        currentStrategy = toolStrategies.get(selectionButton);

    }

    private void setupToolListeners() {
        toolToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            if (shapeContextMenu != null && shapeContextMenu.isShowing()) {
                shapeContextMenu.hide(); // Nascondi il menu contestuale se si cambia strumento
            }
            if (currentStrategy instanceof SelectionToolStrategy) {
                ((SelectionToolStrategy) currentStrategy).resetSelection(); // Rimuovi la decorazione dalla forma precedentemente selezionata quando si cambia strumento
            }

            if (newValue == null) {
                toolToggleGroup.selectToggle(selectionButton);
            } else {
                currentStrategy = toolStrategies.get((ToggleButton) newValue);
            }
        });
    }

    private void setupContextMenu() {
        shapeContextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> {
            if (currentStrategy instanceof SelectionToolStrategy) {
                MyShape selectedShape = ((SelectionToolStrategy) currentStrategy).getSelectedModelShape();
                if (selectedShape != null) {
                    Command deleteCommand = new DeleteShapeCommand(model, selectedShape);
                    deleteCommand.execute();
                }
            }
            shapeContextMenu.hide(); // Nascondi sempre il menu dopo un'azione
        });
        shapeContextMenu.getItems().add(deleteItem);

    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        if (shapeContextMenu.isShowing()) {
            shapeContextMenu.hide(); // Nascondi se già visibile e l'utente clicca altrove
        }

        if (currentStrategy != null) {
            currentStrategy.handlePressed(event);
        }

        // Ora, il Controller decide se mostrare il menu contestuale
        if (event.getButton() == MouseButton.SECONDARY && currentStrategy instanceof SelectionToolStrategy) {
            SelectionToolStrategy selectionStrategy = (SelectionToolStrategy) currentStrategy;
            MyShape selectedModelShape = selectionStrategy.getSelectedModelShape();
            Shape selectedFxShape = selectionStrategy.getSelectedJavaFxShape(); // JavaFX shape selezionato dalla strategy

            // Mostra il menu solo se il click destro è avvenuto sulla forma attualmente selezionata
            if (selectedModelShape != null && selectedFxShape != null && selectedFxShape.contains(event.getX(), event.getY())) {
                shapeContextMenu.show(drawingArea, event.getScreenX(), event.getScreenY());
            } else if (selectedModelShape != null) {
                // Clic destro fuori dalla forma attualmente selezionata, ma una forma è selezionata: deseleziona.
                selectionStrategy.resetSelection();
            }
        }
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        if (currentStrategy != null && (shapeContextMenu == null || !shapeContextMenu.isShowing())) {
            currentStrategy.handleDragged(event);
        }
    }

    @FXML
    private void handleMouseReleased(MouseEvent event) {
        if (currentStrategy != null && (shapeContextMenu == null || !shapeContextMenu.isShowing())) {
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
    public void update(String event, MyShape shape) {

        if (Objects.equals(event, "CREATE")) {
            setAdapter(shape);
            if (adapter != null) {
                Shape fxShape = adapter.getFxShape(shape, drawingArea.getWidth(), drawingArea.getHeight());
                drawingArea.getChildren().add(fxShape);
                shapeMapping.register(shape, fxShape);
            }
        } else if (Objects.equals(event, "DELETE")) {
            Shape fxShape = shapeMapping.getViewShape(shape);
            if (fxShape != null) {
                drawingArea.getChildren().remove(fxShape);
                shapeMapping.unregister(shape);
            }
            if (currentStrategy instanceof SelectionToolStrategy) {
                SelectionToolStrategy selectionStrategy = (SelectionToolStrategy) currentStrategy;
                if (selectionStrategy.getSelectedModelShape() == shape) {
                    selectionStrategy.resetSelection(); // Resetta lo stato della strategia di selezione
                }
            }
            if (shapeContextMenu != null && shapeContextMenu.isShowing()) {
                shapeContextMenu.hide(); // Nascondi il menu se la forma target viene eliminata
            }
        } else if (Objects.equals(event, "MODIFY")) {
            // Da implementare

        }

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