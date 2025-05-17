package com.example.geoshapes.controller;

import com.example.geoshapes.model.DrawingModel;
import com.example.geoshapes.model.shapes.Shape;
import com.example.geoshapes.strategy.ToolStrategy;
import com.example.geoshapes.strategy.LineToolStrategy;
import com.example.geoshapes.strategy.RectangleToolStrategy;
import com.example.geoshapes.strategy.EllipseToolStrategy;
import com.example.geoshapes.command.Command;
import com.example.geoshapes.command.CreateShapeCommand;
import com.example.geoshapes.observer.ShapeObserver;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import java.util.HashMap;
import java.util.Map;

public class MainController implements ShapeObserver {
    @FXML
    private Pane drawingArea;

    @FXML
    private ToggleGroup toolToggleGroup;

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

    private DrawingModel model;
    private ToolStrategy currentStrategy;
    private Map<ToggleButton, ToolStrategy> toolStrategies;

    @FXML
    public void initialize() {
        model = new DrawingModel();
        model.attach(this);

        toolStrategies = new HashMap<>();
        //toolStrategies.put(selectionButton, new SelectionToolStrategy());
        toolStrategies.put(lineButton, new LineToolStrategy());
        toolStrategies.put(rectangleButton, new RectangleToolStrategy());
        toolStrategies.put(ellipseButton, new EllipseToolStrategy());

        setupToolListeners();
    }

    private void setupToolListeners() {
        toolToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                // Se nessuno strumento è selezionato, imposta la selezione come default
                // Ancora da implementare
            } else {
                currentStrategy = toolStrategies.get((ToggleButton) newValue);
            }
        });
    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        currentStrategy.handlePressed(event);


    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        currentStrategy.handleDragged(event);
    }

    @FXML
    private void handleMouseReleased(MouseEvent event) {
        currentStrategy.handleReleased(event);

        Command command = new CreateShapeCommand(model, currentStrategy);
        command.execute();
    }

    @Override
    public void update(Shape shape) {
        drawingArea.getChildren().add(shape.getJavaFXShape());
    }

}