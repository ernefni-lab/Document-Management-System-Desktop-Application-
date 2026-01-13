package org.example.residencia2025ernesto;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Bloqueo {

    public <T> T abrirVentana(String fxml, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(scene);

        root.applyCss();
        root.layout();
        double ancho = root.prefWidth(0);
        double alto = root.prefHeight(0);
        stage.setMinWidth(ancho);
        stage.setMaxWidth(ancho);
        stage.setMinHeight(alto);
        stage.setMaxHeight(alto);
        stage.setResizable(false);

        stage.show();

        return loader.getController(); // Devuelve el controlador
    }
}

