package it.unisa.diem.sad.geoshapes;

import it.unisa.diem.sad.geoshapes.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/it/unisa/diem/sad/geoshapes/view/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 790);
        MainController controller = fxmlLoader.getController();
        scene.setOnKeyPressed(controller::handleKeyPressed);
        scene.setOnKeyTyped(controller::handleKeyTyped);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setTitle("GeoShapes");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

