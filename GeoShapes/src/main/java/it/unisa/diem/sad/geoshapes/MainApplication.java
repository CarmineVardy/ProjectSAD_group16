package it.unisa.diem.sad.geoshapes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/it/unisa/diem/sad/geoshapes/view/MainView.fxml"));
        Image icon = new Image(getClass().getResourceAsStream("/styles/icons/logo.png"));
        stage.getIcons().add(icon);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle("GeoShapes");
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.sizeToScene();
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

