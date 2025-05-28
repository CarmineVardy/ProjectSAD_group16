package it.unisa.diem.sad.geoshapes.controller.util;

import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.Optional;

public class UIUtils {

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