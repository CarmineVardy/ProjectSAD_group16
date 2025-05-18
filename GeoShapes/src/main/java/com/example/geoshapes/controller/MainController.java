package com.example.geoshapes.controller;

import com.example.geoshapes.model.DrawingModel;
import com.example.geoshapes.model.shapes.Shape;
import com.example.geoshapes.controller.strategy.ToolStrategy;
import com.example.geoshapes.controller.strategy.LineToolStrategy;
import com.example.geoshapes.controller.strategy.RectangleToolStrategy;
import com.example.geoshapes.controller.strategy.EllipseToolStrategy;
import com.example.geoshapes.controller.command.Command;
import com.example.geoshapes.controller.command.CreateShapeCommand;
import com.example.geoshapes.observer.ShapeObserver;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

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

    //@FXML
    //private ColorPicker borderColorPicker;

    //@FXML
    //private ColorPicker fillColorPicker;

    private Rectangle clipRect;

    private DrawingModel model;
    private ToolStrategy currentStrategy;
    private Map<ToggleButton, ToolStrategy> toolStrategies;



    @FXML
    public void initialize() {

        setupClipping();

        model = new DrawingModel();
        model.attach(this);

        toolStrategies = new HashMap<>();
        //toolStrategies.put(selectionButton, new SelectionToolStrategy(drawingArea));
        toolStrategies.put(lineButton, new LineToolStrategy(drawingArea));
        toolStrategies.put(rectangleButton, new RectangleToolStrategy(drawingArea));
        toolStrategies.put(ellipseButton, new EllipseToolStrategy(drawingArea));

        // Selezione come default
        // Da implementare
        // lineButton.setSelected(true);

        setupToolListeners();

    }

    private void setupClipping() {
        clipRect = new Rectangle();
        drawingArea.setClip(clipRect);

        clipRect.widthProperty().bind(drawingArea.widthProperty());
        clipRect.heightProperty().bind(drawingArea.heightProperty());
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

            Command command = new CreateShapeCommand(model, currentStrategy.getFinalShape());
            command.execute();

        }
    }

    @Override
    public void update(Shape shape) {
        drawingArea.getChildren().add(shape.getJavaFXShape());
    }

}