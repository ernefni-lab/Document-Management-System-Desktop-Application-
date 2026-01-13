package org.example.residencia2025ernesto;
import javafx.scene.Parent;
import org.example.residencia2025ernesto.Session;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class LogginController implements Initializable {
    @FXML private Button canbtn;
    @FXML private Button btnInsta;
    @FXML private Button btnYT;
    @FXML private Button btnfacec;
    @FXML private Button btnTw;
    @FXML private Button btnEn;
    @FXML private Label logmsj;
    @FXML private ImageView imgLog;
    @FXML private TextField usertxt;
    @FXML private TextField contxt;

    private static final String BOTON_OSCURO = "-fx-background-color: #5aaedb; -fx-background-radius: 20;";
    private static final String BOTON_NORMAL = "-fx-background-color: #2a9df4; -fx-background-radius: 20;";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logmsj.setVisible(false);
        URL imageUrl = getClass().getResource("/images/LOGR.png");
        if (imageUrl != null) {
            Image image = new Image(imageUrl.toExternalForm());
            imgLog.setImage(image);
        } else {
            System.out.println("No se encontro la imagen");
        }
    }

    public void canbtnOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Recu.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Recuperar contraseña");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnEnOnAction(ActionEvent event) {
        String usuarioIngresado = usertxt.getText().trim();
        String contraseñaIngresada = contxt.getText().trim();

        if (usuarioIngresado.isEmpty() || contraseñaIngresada.isEmpty()) {
            mostrarMensaje("Por favor, completa ambos campos", "red");
            return;
        }

        try (Connection con = new BDconect().getConnection()) {
            if (con == null) {
                mostrarMensaje("Error al conectar con la base de datos", "red");
                return;
            }

            String sql = "SELECT idUsuario, tipoUsuario, UserPremium_idUser, NombreUsuario, UserInvitado_idUser " + "FROM users WHERE TRIM(NombreUsuario) = ? AND TRIM(contraseñaUsuario) = ?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, usuarioIngresado);
                stmt.setString(2, contraseñaIngresada);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Session.idUsuario = rs.getInt("idUsuario");
                        Session.tipoUsuario = rs.getString("tipoUsuario").trim().toLowerCase();
                        Session.NombreUsuario = rs.getString("NombreUsuario");

                        if (Session.tipoUsuario.equals("premium")) {
                            Session.idEspecifico = rs.getInt("UserPremium_idUser");
                        } else if (Session.tipoUsuario.equals("invitado")) {
                            Session.idEspecifico = rs.getInt("UserInvitado_idUser");
                        }

                        mostrarMensaje("Sesion iniciada con exito", "green");

                        switch (Session.tipoUsuario) {
                            case "premium":
                                new Bloqueo().abrirVentana("usuario.fxml", "Usuario Premium");
                                break;

                            case "invitado":
                                new Bloqueo().abrirVentana("UsuarioIn.fxml", "Usuario Invitado");
                                break;
                            default:
                                mostrarMensaje("Tipo de usuario no reconocido.", "red");
                                return;
                        }
                        cerrarVentanaActual();
                    } else {
                        mostrarMensaje("Datos incorrectos", "red");
                    }
                }
            }

        } catch (Exception e) {
            mostrarMensaje("Error al validar usuario: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }

    private void cerrarVentanaActual() {
        Stage currentStage = (Stage) btnEn.getScene().getWindow();
        currentStage.close();
    }

    public void btnInstaOnAction(ActionEvent event) {
        abrirEnlace("https://www.instagram.com/PisaFarmaceutica/");
    }

    public void btnYTOnAction(ActionEvent event) {
        abrirEnlace("https://www.youtube.com/@pisamx");
    }

    public void btnTwOnAction(ActionEvent event) {
        abrirEnlace("https://x.com/Grupo_Pisa");
    }

    public void btnfacecOnAction(ActionEvent event) {
        abrirEnlace("https://www.facebook.com/pisafarmaceutica/");
    }

    private void abrirEnlace(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String mensaje, String color) {
        logmsj.setVisible(true);
        logmsj.setText(mensaje);
        logmsj.setStyle("-fx-text-fill: " + color + ";");
    }

    public void restaurarBoton(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_NORMAL);
    }

    public void oscurecerBoton(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_OSCURO);
    }
}
