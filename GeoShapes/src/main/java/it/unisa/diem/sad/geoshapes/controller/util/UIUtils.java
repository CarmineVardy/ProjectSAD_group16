package it.unisa.diem.sad.geoshapes.controller.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region; // Explicitly import Region
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.Optional;

/**
 * Provides a collection of utility methods for common User Interface (UI) operations
 * in the GeoShapes application. This includes file chooser creation, alert dialogs,
 * and dynamic UI adjustments for property panels.
 */
public class UIUtils {

    /**
     * Creates and configures a {@link FileChooser} for opening or saving files.
     * Sets the title, initial directory (prioritizing Downloads, then user home),
     * and file extension filters.
     *
     * @param title The title to display on the file chooser dialog.
     * @param suggestedFileName A suggested initial file name for save operations, can be {@code null}.
     * @param initialDirectory A {@link File} object representing the preferred starting directory, can be {@code null}.
     * @return A configured {@link FileChooser} instance.
     */
    public FileChooser createFileChooser(String title, String suggestedFileName, File initialDirectory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        File userHome = new File(System.getProperty("user.home"));
        File downloads = new File(userHome, "Downloads");
        File directory = initialDirectory;

        // Determine the effective initial directory
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            directory = downloads.exists() && downloads.isDirectory() ? downloads : userHome;
        }
        fileChooser.setInitialDirectory(directory);

        // Add file extension filters
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GeoShapes Drawing", "*.geoshapes"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Set suggested file name if provided
        if (suggestedFileName != null && !suggestedFileName.isEmpty()) {
            fileChooser.setInitialFileName(suggestedFileName);
        }
        return fileChooser;
    }

    /**
     * Displays a confirmation dialog to the user.
     *
     * @param title The title of the dialog window.
     * @param header The header text of the dialog.
     * @param content The main content message of the dialog.
     * @return {@code true} if the user clicks OK, {@code false} otherwise.
     */
    public boolean showConfirmDialog(String title, String header, String content) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle(title);
        confirmDialog.setHeaderText(header);
        confirmDialog.setContentText(content);
        Optional<ButtonType> result = confirmDialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Displays an informational success dialog to the user.
     *
     * @param title The title of the dialog window.
     * @param content The main content message of the dialog.
     */
    public void showSuccessDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header for simple info dialogs
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays an error dialog to the user.
     *
     * @param title The title of the dialog window.
     * @param content The main content message of the dialog.
     */
    public void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header for simple error dialogs
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Hides a given properties HBox from the UI.
     * Sets its visibility to false and removes it from layout management.
     *
     * @param propertiesHBox The {@link HBox} to hide.
     */
    public void hidePropertiesHBox(HBox propertiesHBox) {
        if (propertiesHBox == null) {
            return;
        }
        propertiesHBox.setVisible(false);
        propertiesHBox.setManaged(false);
    }

    /**
     * Shows a given properties HBox on the UI, positioning it relative to a toggle button.
     * The HBox is positioned to the right of the tools VBox and vertically aligned
     * with the corresponding edit button, with adjustments for panel height and viewport clamping.
     *
     * @param toolsVBox The {@link VBox} containing the tool buttons, used as a horizontal reference.
     * @param zoomGroup The {@link Group} that applies zoom transformations, used for coordinate conversion.
     * @param scrollPane The {@link ScrollPane} containing the drawing area, used for viewport clamping.
     * @param propertiesHBoxToShow The {@link HBox} to display.
     * @param correspondingEditButton The {@link ToggleButton} that triggered the display of this HBox, used for vertical positioning.
     */
    public void showPropertiesHBox(VBox toolsVBox, Group zoomGroup, ScrollPane scrollPane, HBox propertiesHBoxToShow, ToggleButton correspondingEditButton) {
        if (propertiesHBoxToShow == null || correspondingEditButton == null ||
                toolsVBox == null || zoomGroup == null || scrollPane == null) {
            // Log an error or handle invalid input
            if (propertiesHBoxToShow != null) {
                propertiesHBoxToShow.setVisible(false);
                propertiesHBoxToShow.setManaged(false);
            }
            return;
        }

        // 1. Calculate X position (to the right of toolsVBox)
        Bounds toolsVBoxBoundsOnScreen = toolsVBox.localToScreen(toolsVBox.getBoundsInLocal());
        if (toolsVBoxBoundsOnScreen == null) {
            propertiesHBoxToShow.setVisible(false); propertiesHBoxToShow.setManaged(false); return;
        }
        double toolsVBoxRightEdgeOnScreen = toolsVBoxBoundsOnScreen.getMaxX();
        Point2D xReferenceScreenPoint = new Point2D(toolsVBoxRightEdgeOnScreen, toolsVBoxBoundsOnScreen.getMinY());
        Point2D xReferenceInZoomGroup = zoomGroup.screenToLocal(xReferenceScreenPoint);

        if (xReferenceInZoomGroup == null) {
            propertiesHBoxToShow.setVisible(false); propertiesHBoxToShow.setManaged(false); return;
        }
        double targetX = xReferenceInZoomGroup.getX() + 10; // 10px spacing

        // 2. Calculate the top edge of the "Edit" button in zoomGroup coordinates
        Bounds editButtonBoundsOnScreen = correspondingEditButton.localToScreen(correspondingEditButton.getBoundsInLocal());
        if (editButtonBoundsOnScreen == null) {
            propertiesHBoxToShow.setVisible(false); propertiesHBoxToShow.setManaged(false); return;
        }
        Point2D buttonTopScreenPoint = new Point2D(editButtonBoundsOnScreen.getMinX(), editButtonBoundsOnScreen.getMinY());
        Point2D buttonTopInZoomGroup = zoomGroup.screenToLocal(buttonTopScreenPoint);

        if (buttonTopInZoomGroup == null) {
            propertiesHBoxToShow.setVisible(false); propertiesHBoxToShow.setManaged(false); return;
        }
        double baseFinalTargetY = buttonTopInZoomGroup.getY(); // Base: Align TOP HBox with TOP of Edit button

        // 3. Determine the actual height of the HBox
        propertiesHBoxToShow.applyCss();
        propertiesHBoxToShow.layout(); // Ensure layout is computed
        double hBoxActualHeight = propertiesHBoxToShow.prefHeight(-1);
        if (hBoxActualHeight <= 0) {
            hBoxActualHeight = propertiesHBoxToShow.getHeight();
        }

        if (hBoxActualHeight <= 0 && propertiesHBoxToShow.getChildren().size() > 0) {
            double maxChildHeight = 0;
            for (Node child : propertiesHBoxToShow.getChildren()) {
                child.applyCss();
                double childPrefHeight = (child instanceof Region) ?
                        ((Region) child).prefHeight(-1) :
                        child.getBoundsInLocal().getHeight();
                if (childPrefHeight > maxChildHeight) {
                    maxChildHeight = childPrefHeight;
                }
            }
            if (maxChildHeight > 0) {
                hBoxActualHeight = maxChildHeight + propertiesHBoxToShow.getPadding().getTop() + propertiesHBoxToShow.getPadding().getBottom();
            }
        }
        if (hBoxActualHeight <= 0) {
            hBoxActualHeight = 50; // Fallback height if unable to determine
        }

        // 4. Apply a specific visual offset based on panel type/height
        double visualCorrectionOffset;
        String hboxId = propertiesHBoxToShow.getId();
        double editButtonHeight = editButtonBoundsOnScreen.getHeight();

        if ("linePropertiesHBox".equals(hboxId) || "rectanglePropertiesHBox".equals(hboxId) || "ellipsePropertiesHBox".equals(hboxId)) {
            // For shorter panels: align the bottom of the HBox near the center of the button, then shift up
            visualCorrectionOffset = (editButtonHeight / 2.0) - hBoxActualHeight - 10.0;
        } else if ("polygonPropertiesHBox".equals(hboxId)) {
            // For polygon panel: center the HBox with the button, then raise it a bit more.
            visualCorrectionOffset = (editButtonHeight / 2.0) - (hBoxActualHeight / 2.0) - 15.0;
        } else if ("textPropertiesHBox".equals(hboxId)) {
            // For text panel: center the HBox with the button
            visualCorrectionOffset = (editButtonHeight / 2.0) - (hBoxActualHeight / 2.0);
        } else {
            visualCorrectionOffset = -5.0; // Default offset for any other panels
        }

        double finalTargetY = baseFinalTargetY + visualCorrectionOffset;

        // 5. Clamp to Viewport bounds
        Bounds viewportBounds = scrollPane.getViewportBounds();
        if (viewportBounds != null && viewportBounds.getHeight() > 0 && hBoxActualHeight > 0) {
            double scrollPaneViewportHeight = viewportBounds.getHeight();
            double scrollPaneViewportMinY = viewportBounds.getMinY();

            // Prevent HBox from going below viewport bottom
            if (finalTargetY + hBoxActualHeight > scrollPaneViewportMinY + scrollPaneViewportHeight - 5) {
                finalTargetY = scrollPaneViewportMinY + scrollPaneViewportHeight - hBoxActualHeight - 5;
            }
            // Prevent HBox from going above viewport top
            if (finalTargetY < scrollPaneViewportMinY + 5) {
                finalTargetY = scrollPaneViewportMinY + 5;
            }
        }

        // 6. Apply position and visibility
        propertiesHBoxToShow.setLayoutX(targetX);
        propertiesHBoxToShow.setLayoutY(finalTargetY);
        // Reset translations to ensure absolute positioning is respected
        propertiesHBoxToShow.setTranslateX(0);
        propertiesHBoxToShow.setTranslateY(0);

        propertiesHBoxToShow.setVisible(true);
        propertiesHBoxToShow.setManaged(true);

        // Add a style class for visual effects if not already present
        if (!propertiesHBoxToShow.getStyleClass().contains("properties-panel-effect")) {
            propertiesHBoxToShow.getStyleClass().add("properties-panel-effect");
        }
    }

    /**
     * Updates the CSS style of a {@link TextField} used for text preview,
     * applying colors and font size from respective {@link ColorPicker} and {@link ComboBox} controls.
     *
     * @param textPreview The {@link TextField} to style.
     * @param textFillColorPicker The {@link ColorPicker} for the text's fill color.
     * @param textColorPicker The {@link ColorPicker} for the actual text color.
     * @param textBorderColorPicker The {@link ColorPicker} for the text's border color.
     * @param fontSizeComboBox The {@link ComboBox} for the text's font size.
     */
    public void updateTextPreviewStyle(TextField textPreview, ColorPicker textFillColorPicker, ColorPicker textColorPicker, ColorPicker textBorderColorPicker, ComboBox<Integer> fontSizeComboBox) {
        if (textPreview == null || textFillColorPicker == null || textColorPicker == null || textBorderColorPicker == null || fontSizeComboBox == null) {
            return;
        }
        StringBuilder style = new StringBuilder();

        // Set background color for the text field
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

        // Set text color
        style.append("-fx-text-fill: ");
        style.append(String.format("rgb(%d,%d,%d)",
                (int) (textColorPicker.getValue().getRed() * 255),
                (int) (textColorPicker.getValue().getGreen() * 255),
                (int) (textColorPicker.getValue().getBlue() * 255)));
        style.append("; ");

        // Set border color
        style.append("-fx-border-color: ");
        style.append(String.format("rgb(%d,%d,%d)",
                (int) (textBorderColorPicker.getValue().getRed() * 255),
                (int) (textBorderColorPicker.getValue().getGreen() * 255),
                (int) (textBorderColorPicker.getValue().getBlue() * 255)));
        style.append("; ");

        // Set font size
        style.append("-fx-font-size: ").append(fontSizeComboBox.getValue()).append("px; ");

        // Ensure background is transparent for the preview TextField itself
        style.append("-fx-background-color: transparent;");

        textPreview.setStyle(style.toString());
    }

    /**
     * Initializes the preview display for a polygon shape.
     * Sets up a default triangle shape with a fixed size for visual representation
     * in the UI, typically for a tool button or properties panel.
     *
     * @param polygonShapePreview The {@link Polygon} object to initialize as a preview.
     */
    public void initializePolygonPreview(Polygon polygonShapePreview) {
        if (polygonShapePreview != null) {
            double centerX = 25;
            double centerY = 25;
            double radius = 12;
            int sides = 3; // Default to a triangle for preview
            double angleStep = 2 * Math.PI / sides;
            ObservableList<Double> points = FXCollections.observableArrayList();

            // Calculate vertices for an equilateral triangle
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
}