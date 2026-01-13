package org.example.residencia2025ernesto;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CambiarContraP {

    @FXML
    private TextField txtNueva;
    @FXML private PasswordField txtNuevaCon;
    @FXML private Button btnCambiar;
    @FXML private Button btnVolver;
    @FXML private Button canbtn;

    @FXML
    private void btnEnOnAction() {
        String nueva = txtNueva.getText().trim();
        String confirmar = txtNuevaCon.getText().trim();
        int idUsuario = Session.idUsuario;

        if (nueva.isEmpty() || confirmar.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Debes llenar ambos campos.");
            return;
        }

        if (!nueva.equals(confirmar)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Las contraseñas no coinciden.");
            return;
        }

        try (Connection con = BDconect.getConnection()) {
            String obtenerCorreoSQL = "SELECT UserCorreo FROM users WHERE idusuario = ?";
            PreparedStatement psCorreo = con.prepareStatement(obtenerCorreoSQL);
            psCorreo.setInt(1, idUsuario);
            ResultSet rs = psCorreo.executeQuery();

            String correoUsuario = null;
            if (rs.next()) {
                correoUsuario = rs.getString("UserCorreo");
            }
            rs.close();
            psCorreo.close();

            if (correoUsuario == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontró el correo del usuario.");
                return;
            }

            String sql = "UPDATE users SET contraseñaUsuario = ? WHERE idusuario = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nueva);
            ps.setInt(2, idUsuario);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                String asunto = "Cambio de contraseña exitoso";
                String cuerpo = "Hola,\n\nTu contraseña ha sido cambiada exitosamente.\n\nTu nueva contraseña es: " + nueva +
                        "\n\nAtentamente,\nSistema de Validación PiSA";
                Correo.enviar(correoUsuario, asunto, cuerpo);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Contraseña actualizada y correo enviado.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar la contraseña.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }


    @FXML
    private void canbtnOnAction() {
        try {
            new Bloqueo().abrirVentana("usuario.fxml", "Usuario Premium");
            Stage currentStage = (Stage) btnCambiar.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    public void oscurecerBoton(MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle("-fx-background-color: #1b75d0; -fx-text-fill: white; -fx-background-radius: 20;");
    }

    @FXML
    public void restaurarBoton(MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle("-fx-background-color: #2a9df4; -fx-text-fill: white; -fx-background-radius: 20;");
    }
}
