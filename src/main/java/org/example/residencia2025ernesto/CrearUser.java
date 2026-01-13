package org.example.residencia2025ernesto;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class CrearUser {
    @FXML private TextField NUserN;
    @FXML private TextField NUserPa;
    @FXML private TextField NUserC;
    @FXML private TextField NUserT;
    @FXML private TextField NuserO;
    @FXML private Button btnREU;
    @FXML private Button btnVolver;


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

    public void btnREUOnAction(javafx.event.ActionEvent actionEvent) {
        String nombre = NUserN.getText().trim();
        String contrasena = NUserPa.getText().trim();
        String correo = NUserC.getText().trim();
        String tipo = NUserT.getText().trim();
        String nombreCompleto = NuserO.getText().trim();

        if (nombre.isEmpty() || contrasena.isEmpty() || correo.isEmpty() || tipo.isEmpty() || nombreCompleto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Completa todos los campos.");
            return;
        }

        if (CrearUserController.correoExiste(correo)) {
            mostrarAlerta(Alert.AlertType.ERROR, "El correo ya está registrado.");
            return;
        }

        if (CrearUserController.nombreCompletoExiste(nombreCompleto)) {
            mostrarAlerta(Alert.AlertType.ERROR, "El nombre completo ya está registrado.");
            return;
        }

        try {
            int idUsuario = CrearUserController.registrarUsuario(tipo, nombre, correo, nombreCompleto, contrasena);
            CrearUserController.asignarTipoUsuario(idUsuario, tipo);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Usuario creado correctamente.");
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al registrar usuario: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Registro de Usuario");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void btnVolver(ActionEvent actionEvent) {
        try {
            new Bloqueo().abrirVentana("usuario.fxml", "Usuario Premium");
            Stage currentStage = (Stage) btnVolver.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
