module it.unisa.diem.sad.geoshapes {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.materialdesign;
    requires org.kordamp.ikonli.bootstrapicons;
    requires javafx.graphics;
    requires java.desktop;

    opens it.unisa.diem.sad.geoshapes to javafx.fxml;
    exports it.unisa.diem.sad.geoshapes;
    exports it.unisa.diem.sad.geoshapes.controller;
    opens it.unisa.diem.sad.geoshapes.controller to javafx.fxml;
}