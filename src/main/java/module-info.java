module org.example.residencia2025ernesto {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires mysql.connector.j;
    requires com.google.protobuf;
    requires jakarta.mail;
    requires org.simplejavamail;
    requires java.desktop;

    opens org.example.residencia2025ernesto to javafx.fxml;
    exports org.example.residencia2025ernesto;
}