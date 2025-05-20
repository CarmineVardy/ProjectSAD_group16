package com.example.geoshapes.controller.util;

import com.example.geoshapes.model.shapes.MyShape;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

public class UIUtils {

    private ContextMenu selectionShapeMenu;// private ContextMenu selectionShapeMenu;

    public void setupSelectionContextMenu() {
        selectionShapeMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        selectionShapeMenu.getItems().add(deleteItem);

        selectionShapeMenu.getItems().add(new javafx.scene.control.SeparatorMenuItem());
        selectionShapeMenu.getItems().add(createColorPickerMenuItem("Border Color:", new ColorPicker()));

        selectionShapeMenu.getItems().add(new javafx.scene.control.SeparatorMenuItem());
        selectionShapeMenu.getItems().add(createColorPickerMenuItem("Fill Color:", new ColorPicker()));
    }

    private CustomMenuItem createColorPickerMenuItem(String labelText, ColorPicker colorPicker) {
        Label label = new Label(labelText);
        HBox container = new HBox(5, label, colorPicker);
        container.setAlignment(Pos.CENTER_LEFT);
        CustomMenuItem menuItem = new CustomMenuItem(container);
        menuItem.setHideOnClick(false);
        return menuItem;
    }
    
    

    public void showSelectionShapeMenu(Node anchor, double screenX, double screenY, MyShape selectedShape, Consumer<MyShape> deleteActionHandler) {
        if (selectionShapeMenu == null || selectionShapeMenu.getItems().isEmpty()) {
            // Failsafe, though setupSelectionContextMenu should be called during initialization
            setupSelectionContextMenu();
        }

        MenuItem deleteItem = selectionShapeMenu.getItems().stream()
                .filter(item -> "Delete".equals(item.getText()))
                .findFirst().orElse(null);

        if (deleteItem != null) {
            deleteItem.setOnAction(event -> {
                if (selectedShape != null) {
                    deleteActionHandler.accept(selectedShape);
                }
                hideSelectionShapeMenu(); // Hide after action
            });
        }

        if (selectedShape != null) {
            selectionShapeMenu.show(anchor, screenX, screenY);
        }
    }

    public void hideSelectionShapeMenu() {
        if (selectionShapeMenu != null && selectionShapeMenu.isShowing()) {
            selectionShapeMenu.hide();
        }
    }

    public boolean isSelectionShapeMenuShowing() {
        return selectionShapeMenu != null && selectionShapeMenu.isShowing();
    }

    public FileChooser createFileChooser(String title, String suggestedFileName, File initialDirectory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (initialDirectory != null && initialDirectory.exists() && initialDirectory.isDirectory()) {
            fileChooser.setInitialDirectory(initialDirectory);
        } else {
            // Default to user's documents or downloads directory if initialDirectory is not suitable
            String userHome = System.getProperty("user.home");
            File defaultDir = new File(userHome, "Downloads"); // Or "Downloads"
            if (!defaultDir.exists() || !defaultDir.isDirectory()) {
                defaultDir = new File(userHome);
            }
            fileChooser.setInitialDirectory(defaultDir);
        }
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GeoShapes Drawing", "*.geoshapes"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        if (suggestedFileName != null && !suggestedFileName.isEmpty()) {
            fileChooser.setInitialFileName(suggestedFileName);
        }
        return fileChooser;
    }

    public boolean showConfirmDialog(String title, String header, String content) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle(title);
        confirmDialog.setHeaderText(header);
        confirmDialog.setContentText(content);
        Optional<ButtonType> result = confirmDialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void showSuccessDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }




}