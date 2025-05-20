module com.example.geoshapes {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.materialdesign;
    requires org.kordamp.ikonli.bootstrapicons;
    requires javafx.graphics;

    opens com.example.geoshapes to javafx.fxml;
    exports com.example.geoshapes;
    exports com.example.geoshapes.controller;
    opens com.example.geoshapes.controller to javafx.fxml;
}