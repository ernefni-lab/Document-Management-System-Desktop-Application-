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
import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class usuarioController extends BusqCarp {
    @FXML private Button btnCU;
    @FXML private TextField BuscText;
    @FXML private VBox vboxRes;

    @FXML private Button btnCarp;
    private int idCarpetaSeleccionada;
    @FXML private Button btnRep1;
    @FXML private Button btnPEditarCarpeta;
    private String nombreCarpetaSeleccionada;
        @FXML private Button btnEditarCarpeta;
    private static final String BOTON_OSCURO = "-fx-background-color: #5aaedb; -fx-background-radius: 20;";
    @FXML private Button btnPre;
    private static final String BOTON_NORMAL = "-fx-background-color: #2a9df4; -fx-background-radius: 20;";

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Scene scene = btnCU.getScene();
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

    @FXML
    private void btnCarpOnAction() {
        buscador = BuscText;
        contenedorResultados = vboxRes;

        String texto = buscador.getText().trim();
        buscarCarpetas(texto);
    }

    public void btnCUOnAction(ActionEvent actionEvent) {
        try {
            new Bloqueo().abrirVentana("CearUsuario.fxml", "Usuario Premium");
            Stage currentStage = (Stage) btnCU.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void btnPreAction(ActionEvent actionEvent) {
        try {
            new Bloqueo().abrirVentana("AutorizarPrestamos.fxml", "Usuario Premium");
            Stage currentStage = (Stage) btnPre.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void btnEditarCarpetaAction(ActionEvent event) {
        try {
            new Bloqueo().abrirVentana("EditC.fxml", "Usuario Premium");
            Stage currentStage = (Stage) btnEditarCarpeta.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void btnCarp2OnAction(ActionEvent actionEvent) {
        try {
            new Bloqueo().abrirVentana("SBCarp.fxml", "Usuario Premium");
            Stage currentStage = (Stage) btnCarp.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnRepOnAction(ActionEvent actionEvent) {

    }

    public void btnPreCom(ActionEvent actionEvent) {
        try {
            new Bloqueo().abrirVentana("PrestamosCompletos.fxml", "Usuario Premium");
            Stage currentStage = (Stage) btnPre.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void btnEditU(ActionEvent actionEvent) {
        try {
            new Bloqueo().abrirVentana("EditarUsuario.fxml", "Usuario Premium");
            Stage currentStage = (Stage) btnPre.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnCambiar(ActionEvent actionEvent) {
        try {
            new Bloqueo().abrirVentana("CambiarContraPremium.fxml", "Usuario Premium");
            Stage currentStage = (Stage) btnRep1.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
