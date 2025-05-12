module com.example.geoshapes {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.example.geoshapes to javafx.fxml;
    exports com.example.geoshapes;
}