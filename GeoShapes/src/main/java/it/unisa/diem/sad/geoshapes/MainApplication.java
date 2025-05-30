package it.unisa.diem.sad.geoshapes;

import it.unisa.diem.sad.geoshapes.controller.MainController;
import it.unisa.diem.sad.geoshapes.model.DrawingModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/it/unisa/diem/sad/geoshapes/view/MainView.fxml"));
        DrawingModel model = new DrawingModel();

        Scene scene = new Scene(fxmlLoader.load());
        MainController controller = fxmlLoader.getController();
        controller.setDrawingModel(model);

        Image icon = new Image(getClass().getResourceAsStream("/styles/icons/logo.png"));
        stage.getIcons().add(icon);
        scene.setOnKeyPressed(event -> {
            controller.handleKeyPressed(event);  // solo gestore tasti qui
        });
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

