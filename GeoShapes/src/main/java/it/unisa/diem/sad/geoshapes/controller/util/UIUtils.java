package it.unisa.diem.sad.geoshapes.controller.util;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;

public class UIUtils {

    private ContextMenu selectionShapeMenu;// private ContextMenu selectionShapeMenu;

    public UIUtils () {
        setupSelectionContextMenu();
    }

    public ContextMenu getSelectionShapeMenu() {
        return selectionShapeMenu;
    }

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

    public boolean isSelectionShapeMenuShowing() {
        return selectionShapeMenu != null && selectionShapeMenu.isShowing();
    }

    public FileChooser createFileChooser(String title, String suggestedFileName, File initialDirectory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        File userHome = new File(System.getProperty("user.home"));
        File downloads = new File(userHome, "Downloads");
        File directory = initialDirectory;
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            directory = downloads.exists() && downloads.isDirectory() ? downloads : userHome;
        }
        fileChooser.setInitialDirectory(directory);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("GeoShapes Drawing", "*.geoshapes"),  new FileChooser.ExtensionFilter("All Files", "*.*") );
        if (suggestedFileName != null && !suggestedFileName.isEmpty()) {
            fileChooser.setInitialFileName(suggestedFileName);
        }
        return fileChooser;
    }

    public void setSelectMenuItemVisibleByLabel(String labelText, boolean visible) {

        for (int i = 0; i < selectionShapeMenu.getItems().size(); i++) {
            MenuItem item = selectionShapeMenu.getItems().get(i);
            if (item instanceof CustomMenuItem) {
                CustomMenuItem customItem = (CustomMenuItem) item;
                if (customItem.getContent() instanceof HBox) {
                    HBox hbox = (HBox) customItem.getContent();
                    for (Node nodeInHBox : hbox.getChildren()) {
                        if (nodeInHBox instanceof Label && ((Label) nodeInHBox).getText().contains(labelText)) {
                            item.setVisible(visible);

                            if (i > 0) {
                                MenuItem precedingItem = selectionShapeMenu.getItems().get(i - 1);
                                if (precedingItem instanceof SeparatorMenuItem) {
                                    precedingItem.setVisible(visible);
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    public void hideSelectMenuItemByLabel(String labelText) {
        setSelectMenuItemVisibleByLabel(labelText, false);
    }

    public void showSelectMenuItemByLabel(String labelText) {
        setSelectMenuItemVisibleByLabel(labelText, true);
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