package org.example.residencia2025ernesto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


public class Loggin extends Application {
    @Override
    public void start(Stage stage) throws IOException {
       FXMLLoader fxmlLoader = new FXMLLoader(Loggin.class.getResource("Loggin.fxml"));
       // FXMLLoader fxmlLoader = new FXMLLoader(Loggin.class.getResource("SBCarp.fxml"));
     //  FXMLLoader fxmlLoader = new FXMLLoader(Loggin.class.getResource("usuario.fxml"));
       // FXMLLoader fxmlLoader = new FXMLLoader(Loggin.class.getResource("UsuarioIn.fxml"));
       // FXMLLoader fxmlLoader = new FXMLLoader(Loggin.class.getResource("SolicitarPrestamos.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(Loggin.class.getResource("EditarUsuario.fxml"));
        Parent root = fxmlLoader.load();



     //   Scene scene = new Scene(root, 768, 600);
     //   stage.setTitle("Inicio de Sistema PISA");
   //     stage.setScene(scene);
        stage.setTitle("Login");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);  //Bloquear redimensionamiento

    }

    public static void main(String[] args) {
        try {
            Connection con = BDconect.getConnection();
            System.out.println("Conexión exitosa");
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        launch(args);
    }
}
