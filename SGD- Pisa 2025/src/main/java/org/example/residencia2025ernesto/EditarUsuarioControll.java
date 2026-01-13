package org.example.residencia2025ernesto;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class EditarUsuarioControll {
    @FXML private TextField txtBuscarUsuario;
    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtOrganizacion;
    @FXML private ComboBox<String> comboTipoUsuario;
    @FXML private TextField txtContraseñaActual;
    @FXML private TextField txtNuevaContraseña;
    @FXML private Label lblMensaje;

    private int idUsuario;

    @FXML
    private void buscarUsuario() {
        String criterio = txtBuscarUsuario.getText().trim();

        if (criterio.isEmpty()) {
            lblMensaje.setText("Por favor ingresa una Firma Pisa o nombre para buscar.");
            return;
        }

        String sql = "SELECT * FROM users WHERE NombreUsuario = ? OR Nombre_Completo LIKE ?";

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try {
                int id = Integer.parseInt(criterio);
                ps.setString(1, criterio);
                ps.setString(2, "%" + criterio + "%");
            } catch (NumberFormatException e) {
                ps.setString(1, criterio);
                ps.setString(2, "%" + criterio + "%");
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                idUsuario = rs.getInt("idUsuario");
                txtNombreCompleto.setText(rs.getString("Nombre_Completo"));
                txtCorreo.setText(rs.getString("UserCorreo"));
                txtTelefono.setText(rs.getString("TelefonoUsuario"));
                txtDireccion.setText(rs.getString("DireccionUsuario"));
                txtOrganizacion.setText(rs.getString("NombreOrganizacion"));
                txtContraseñaActual.setText(rs.getString("contraseñaUsuario"));
                comboTipoUsuario.setValue(rs.getString("TipoUsuario"));
                lblMensaje.setText("Usuario encontrado.");
            } else {
                lblMensaje.setText("No se encontró ningún usuario.");
            }
        } catch (SQLException e) {
            lblMensaje.setText("Error en la base de datos.");
            e.printStackTrace();
        }
    }

    @FXML
    private void guardarCambios() {
        String nuevaContra = txtNuevaContraseña.getText().trim();
        String actual = txtContraseñaActual.getText().trim();

        String contraseñaFinal = nuevaContra.isEmpty() ? actual : nuevaContra;
        boolean cambioContrasena = !nuevaContra.isEmpty() && !nuevaContra.equals(actual);

        String sql = "UPDATE users SET Nombre_Completo = ?, UserCorreo = ?, TelefonoUsuario = ?, DireccionUsuario = ?, NombreOrganizacion = ?, TipoUsuario = ?, contraseñaUsuario = ? WHERE idUsuario = ?";

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, txtNombreCompleto.getText().trim());
            ps.setString(2, txtCorreo.getText().trim());
            ps.setString(3, txtTelefono.getText().trim());
            ps.setString(4, txtDireccion.getText().trim());
            ps.setString(5, txtOrganizacion.getText().trim());
            ps.setString(6, comboTipoUsuario.getValue());
            ps.setString(7, contraseñaFinal);
            ps.setInt(8, idUsuario);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Haz modificado el usuario.");
                if (cambioContrasena) {
                    String destinatario = txtCorreo.getText().trim();
                    String asunto = "Cambio de contraseña realizado";
                    String mensaje = "Hola " + txtNombreCompleto.getText().trim() + ",\n\nTu contraseña ha sido actualizada correctamente: " + contraseñaFinal.trim() + ".\n\nSi no realizaste este cambio o pediste realizarlo por favor contacta al administrador de inmediato.\n\nAtentamente,\nSistema de Validación PiSA";

                    Correo.enviar(destinatario, asunto, mensaje);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "usuario editado y correo enviado.");
                }
            } else {
                lblMensaje.setText("No se pudo actualizar el usuario.");
            }

        } catch (SQLException e) {
            lblMensaje.setText("Error al actualizar usuario.");
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
    private Button btnVolver;

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
