package org.example.residencia2025ernesto;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.stage.Stage;
import org.example.residencia2025ernesto.Correo;

import java.io.IOException;
import java.sql.*;
import java.util.Random;

public class RecuController {

    @FXML private TextField correoTxt;
    @FXML private Label msgLabel;

    @FXML

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Loggin.class.getResource("Loggin.fxml"));

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.setResizable(false);

        stage.show();
    }


    private static final String BOTON_OSCURO = "-fx-background-color: #5aaedb; -fx-background-radius: 20;";
    private static final String BOTON_NORMAL = "-fx-background-color: #2a9df4; -fx-background-radius: 20;";

    public void restaurarBoton(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_NORMAL);
    }

    public void oscurecerBoton(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_OSCURO);
    }

    @FXML
    public void aceptarOnAction() {
        String correo = correoTxt.getText().trim().toLowerCase();
        if (correo.isEmpty()) {
            msgLabel.setText("Ingresa un correo");
            msgLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "1346")) {
            PreparedStatement stmt = conn.prepareStatement("SELECT idUsuario, NombreUsuario FROM users WHERE UserCorreo = ?");
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("idUsuario");
                String nombre = rs.getString("NombreUsuario");

                // Generar contraseña aleatoria de 8 dígitos (rellena con ceros si es necesario)
                String nuevaContrasena = String.format("%08d", new Random().nextInt(100_000_000));

                PreparedStatement update = conn.prepareStatement("UPDATE users SET contraseñaUsuario = ? WHERE idUsuario = ?");
                update.setString(1, nuevaContrasena);
                update.setInt(2, id);
                update.executeUpdate();

                String asunto = "Recuperación de contraseña";
                String cuerpo = "Hola " + nombre + ",\n\nTu nueva contraseña temporal es: " + nuevaContrasena + "\n\nPor favor, cámbiala al iniciar sesión.";

                Correo.enviar(correo, asunto, cuerpo);

                msgLabel.setText("Correo enviado con éxito");
                msgLabel.setStyle("-fx-text-fill: green;");
            } else {
                msgLabel.setText("Correo no registrado");
                msgLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            msgLabel.setText("Error de conexión");
            msgLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
