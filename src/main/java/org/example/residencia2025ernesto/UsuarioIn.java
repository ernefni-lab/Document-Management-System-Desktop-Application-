package org.example.residencia2025ernesto;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import javafx.scene.control.Label;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class UsuarioIn extends BusqCarp {
    @FXML private TextField BuscText;
    @FXML private VBox vboxRes;

    private int idCarpetaSeleccionada;
    private String nombreCarpetaSeleccionada;

    private static final String BOTON_OSCURO = "-fx-background-color: #5aaedb; -fx-background-radius: 20;";
    private static final String BOTON_NORMAL = "-fx-background-color: #2a9df4; -fx-background-radius: 20;";

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Scene scene = BuscText.getScene();
            Window window = scene.getWindow();

            scene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
                if (!BuscText.isHover() && !vboxRes.isHover()) {
                    vboxRes.getChildren().clear();
                }
            });
        });
    }

    public void restaurarBoton(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_NORMAL);
    }

    public void oscurecerBoton(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_OSCURO);
    }

    public void btnJobOnAction(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().browse(new URI("https://selectia.pisa.com.mx/content/Vacantes/?locale=es_MX"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnRepOnAction(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().browse(new URI("https://www.pisa.com.mx/formulario-contacto/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnCarpOnAction() {
        String texto = BuscText.getText().trim();

        if (texto.isEmpty()) {
            Label alerta = new Label("⚠️ Ingresa una carpeta.");
            alerta.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            vboxRes.getChildren().setAll(alerta);
            return;
        }

        buscador = BuscText;
        contenedorResultados = vboxRes;
        buscarCarpetas(texto);
    }

    @FXML private Button btnPrest;

    public void btnPrestOnAction(ActionEvent actionEvent) {
        try {
            Bloqueo bloqueo = new Bloqueo();
            SolicitarPrestamos controller = bloqueo.abrirVentana("SolicitarPrestamos.fxml", "Solicitud de Préstamo");
            controller.initDatos(idCarpetaSeleccionada, nombreCarpetaSeleccionada);
            Stage currentStage = (Stage) btnPrest.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button btnAuPrest;

    @FXML
    public void AuPrestA(ActionEvent event) {
        try {
            new Bloqueo().abrirVentana("PrestamosAutorizados.fxml", "Prestamos Autorizados");
            Stage currentStage = (Stage) btnAuPrest.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btncambiar(ActionEvent actionEvent) {
        try {
            new Bloqueo().abrirVentana("EditarInvitado.fxml", "Cambiar Contraseña");
            Stage currentStage = (Stage) btnAuPrest.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}