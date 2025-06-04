// File: main/java/it/unisa/diem/sad/geoshapes/MainApplication.java
package it.unisa.diem.sad.geoshapes;

import it.unisa.diem.sad.geoshapes.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * The main application class for GeoShapes.
 * This class extends {@link Application} and serves as the entry point for the JavaFX application.
 * It is responsible for initializing the primary stage and loading the main user interface.
 */
public class MainApplication extends Application {

    /**
     * Initializes the primary stage and sets up the main application scene.
     * This method is called after the init method has returned, and after the system is ready
     * for the application to begin running.
     *
     * @param stage The primary stage for this application, onto which
     * the application scene can be set.
     * @throws IOException If the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file for the main view
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/it/unisa/diem/sad/geoshapes/view/MainView.fxml"));

        // Create the scene with specified dimensions
        Scene scene = new Scene(fxmlLoader.load(), 1200, 790);

        // Get the controller instance from the FXML loader
        MainController controller = fxmlLoader.getController();

        // Register event handlers for keyboard input
        scene.setOnKeyPressed(controller::handleKeyPressed);
        scene.setOnKeyTyped(controller::handleKeyTyped);

        // Configure the stage
        stage.initStyle(StageStyle.UNDECORATED); // Set stage style to undecorated
        stage.setScene(scene); // Set the scene to the stage
        stage.setTitle("GeoShapes"); // Set the title of the application window
        stage.show(); // Display the stage
    }

    /**
     * The main method is ignored in correctly deployed JavaFX applications.
     * It serves as a fallback in case the application is launched
     * as a regular Java application (e.g., via a JAR file).
     *
     * @param args The command line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch();
    }
}