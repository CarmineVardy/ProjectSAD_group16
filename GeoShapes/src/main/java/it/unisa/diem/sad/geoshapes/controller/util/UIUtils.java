package it.unisa.diem.sad.geoshapes.controller.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
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

    public void hidePropertiesHBox(HBox hboxToHide) {
        if (hboxToHide == null) return;
        hboxToHide.setVisible(false);
        hboxToHide.setManaged(false);
    }

    public void showPropertiesHBox(VBox toolsVBox, Group zoomGroup, ScrollPane scrollPane, HBox propertiesHBoxToShow, ToggleButton correspondingEditButton) {
        if (propertiesHBoxToShow == null || correspondingEditButton == null) {
            return;
        }
        if (toolsVBox == null || zoomGroup == null || scrollPane == null) {
            return;
        }

        // 1. Calcola la posizione X (a destra di toolsVBox)
        javafx.geometry.Bounds toolsVBoxBoundsOnScreen = toolsVBox.localToScreen(toolsVBox.getBoundsInLocal());
        if (toolsVBoxBoundsOnScreen == null) {
            propertiesHBoxToShow.setVisible(false); propertiesHBoxToShow.setManaged(false); return;
        }
        double toolsVBoxRightEdgeOnScreen = toolsVBoxBoundsOnScreen.getMaxX();
        javafx.geometry.Point2D xReferenceScreenPoint = new javafx.geometry.Point2D(toolsVBoxRightEdgeOnScreen, toolsVBoxBoundsOnScreen.getMinY());
        javafx.geometry.Point2D xReferenceInZoomGroup = zoomGroup.screenToLocal(xReferenceScreenPoint);

        if (xReferenceInZoomGroup == null) {
            propertiesHBoxToShow.setVisible(false); propertiesHBoxToShow.setManaged(false); return;
        }
        double targetX = xReferenceInZoomGroup.getX() + 10; // 10px di spazio

        // 2. Calcola il bordo superiore del bottone "Edit" in coordinate di zoomGroup
        javafx.geometry.Bounds editButtonBoundsOnScreen = correspondingEditButton.localToScreen(correspondingEditButton.getBoundsInLocal());
        if (editButtonBoundsOnScreen == null) {
            propertiesHBoxToShow.setVisible(false); propertiesHBoxToShow.setManaged(false); return;
        }
        javafx.geometry.Point2D buttonTopScreenPoint = new javafx.geometry.Point2D(editButtonBoundsOnScreen.getMinX(), editButtonBoundsOnScreen.getMinY());
        javafx.geometry.Point2D buttonTopInZoomGroup = zoomGroup.screenToLocal(buttonTopScreenPoint);

        if (buttonTopInZoomGroup == null) {
            propertiesHBoxToShow.setVisible(false); propertiesHBoxToShow.setManaged(false); return;
        }
        double baseFinalTargetY = buttonTopInZoomGroup.getY(); // Base: Allinea TOP HBox con TOP bottone Edit

        // 3. Determina l'altezza effettiva dell'HBox
        propertiesHBoxToShow.applyCss();
        propertiesHBoxToShow.layout();
        double hBoxActualHeight = propertiesHBoxToShow.prefHeight(-1);
        if (hBoxActualHeight <= 0) hBoxActualHeight = propertiesHBoxToShow.getHeight();

        if (hBoxActualHeight <= 0 && propertiesHBoxToShow.getChildren().size() > 0) {
            double maxChildHeight = 0;
            for (javafx.scene.Node child : propertiesHBoxToShow.getChildren()) {
                child.applyCss();
                double childPrefHeight = (child instanceof javafx.scene.layout.Region) ?
                        ((javafx.scene.layout.Region) child).prefHeight(-1) :
                        child.getBoundsInLocal().getHeight();
                if (childPrefHeight > maxChildHeight) maxChildHeight = childPrefHeight;
            }
            if (maxChildHeight > 0) {
                hBoxActualHeight = maxChildHeight + propertiesHBoxToShow.getPadding().getTop() + propertiesHBoxToShow.getPadding().getBottom();
            }
        }
        if (hBoxActualHeight <= 0) hBoxActualHeight = 50; // Fallback

        // 4. Applica un offset visivo specifico per tipo/altezza di pannello
        double visualCorrectionOffset;
        String hboxId = propertiesHBoxToShow.getId();
        double editButtonHeight = editButtonBoundsOnScreen.getHeight();

        if (hboxId.equals("linePropertiesHBox") || hboxId.equals("rectanglePropertiesHBox") || hboxId.equals("ellipsePropertiesHBox")) {
            // Per i pannelli corti: allinea il fondo dell'HBox vicino al centro del bottone, poi alza di 10px
            visualCorrectionOffset = (editButtonHeight / 2.0) - hBoxActualHeight - 10.0;
        } else if (hboxId.equals("polygonPropertiesHBox")) {
            // Per il poligono: Centra l'HBox con il bottone, poi alzalo un po' di più.
            // L'offset precedente era -67.6. Se deve andare "un po' più alto", rendiamo l'offset più negativo.
            // Proviamo ad alzarlo di altri 10-15 pixels rispetto a prima.
            // Prima: centerY_offset - 5.0.  centerY_offset = (editButtonHeight / 2.0) - (hBoxActualHeight / 2.0)
            // Quindi: (editButtonHeight / 2.0) - (hBoxActualHeight / 2.0) - 5.0
            // Per alzarlo di altri 10px, facciamo -15.0 invece di -5.0
            visualCorrectionOffset = (editButtonHeight / 2.0) - (hBoxActualHeight / 2.0) - 15.0; // Modificato da -5.0 a -15.0
        } else if (hboxId.equals("textPropertiesHBox")) {
            // Per il testo: Centra l'HBox con il bottone (il clamping gestirà il resto se necessario)
            visualCorrectionOffset = (editButtonHeight / 2.0) - (hBoxActualHeight / 2.0);
        } else {
            visualCorrectionOffset = -5.0; // Default per altri eventuali pannelli
        }

        double finalTargetY = baseFinalTargetY + visualCorrectionOffset;

        // 5. Clamp al Viewport
        javafx.geometry.Bounds viewportBounds = scrollPane.getViewportBounds();
        if (viewportBounds != null && viewportBounds.getHeight() > 0 && hBoxActualHeight > 0) {
            double scrollPaneViewportHeight = viewportBounds.getHeight();
            double scrollPaneViewportMinY = viewportBounds.getMinY();

            if (finalTargetY + hBoxActualHeight > scrollPaneViewportMinY + scrollPaneViewportHeight - 5) {
                finalTargetY = scrollPaneViewportMinY + scrollPaneViewportHeight - hBoxActualHeight - 5;
            }
            if (finalTargetY < scrollPaneViewportMinY + 5) {
                finalTargetY = scrollPaneViewportMinY + 5;
            }
        }

        // 6. Applica posizione e visibilità
        propertiesHBoxToShow.setLayoutX(targetX);
        propertiesHBoxToShow.setLayoutY(finalTargetY);
        propertiesHBoxToShow.setTranslateX(0);
        propertiesHBoxToShow.setTranslateY(0);

        propertiesHBoxToShow.setVisible(true);
        propertiesHBoxToShow.setManaged(true);

        if (!propertiesHBoxToShow.getStyleClass().contains("properties-panel-effect")) {
            propertiesHBoxToShow.getStyleClass().add("properties-panel-effect");
        }
    }

    public void updateTextPreviewStyle(TextField textPreview, ColorPicker textFillColorPicker, ColorPicker textColorPicker, ColorPicker textBorderColorPicker, ComboBox<Integer> fontSize) {
        if (textPreview == null) return;
        StringBuilder style = new StringBuilder();
        style.append("-fx-control-inner-background: ");
        if (textFillColorPicker.getValue() == Color.TRANSPARENT) {
            style.append("transparent");
        } else {
            style.append(String.format("rgb(%d,%d,%d)",
                    (int) (textFillColorPicker.getValue().getRed() * 255),
                    (int) (textFillColorPicker.getValue().getGreen() * 255),
                    (int) (textFillColorPicker.getValue().getBlue() * 255)));
        }
        style.append("; ");
        style.append("-fx-text-fill: ");
        style.append(String.format("rgb(%d,%d,%d)",
                (int) (textColorPicker.getValue().getRed() * 255),
                (int) (textColorPicker.getValue().getGreen() * 255),
                (int) (textColorPicker.getValue().getBlue() * 255)));
        style.append("; ");
        style.append("-fx-border-color: ");
        style.append(String.format("rgb(%d,%d,%d)",
                (int) (textBorderColorPicker.getValue().getRed() * 255),
                (int) (textBorderColorPicker.getValue().getGreen() * 255),
                (int) (textBorderColorPicker.getValue().getBlue() * 255)));
        style.append("; ");
        style.append("-fx-font-size: ").append(fontSize.getValue()).append("px; ");
        style.append("-fx-background-color: transparent;");
        textPreview.setStyle(style.toString());
    }

    public void initializePolygonPreview(Polygon polygonShapePreview) {
        if (polygonShapePreview != null) {
            double centerX = 25;
            double centerY = 25;
            double radius = 12;
            int sides = 3;
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


}