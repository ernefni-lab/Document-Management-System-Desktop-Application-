package org.example.residencia2025ernesto;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.util.Date;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;

public class SolicitarPrestamos {
    @FXML private Label lblCarpeta;
    @FXML private ImageView imgPortada;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCarpeta;
    @FXML private TextArea txtMotivo;
    @FXML private Label txtMensaje;
    @FXML private Label lblArea;
    @FXML private Label lblCodigo;
    @FXML private Label lblCantidad;

    private int idCarpeta = -1;

    public void initDatos(int idCarpeta, String nombreCarpeta) {
        this.idCarpeta = idCarpeta;
        lblCarpeta.setText("Carpeta: " + nombreCarpeta);
    }

    @FXML
    private void buscarCarpeta() {
        String nombreCarpeta = txtCarpeta.getText();
        if (nombreCarpeta.isEmpty()) {
            lblCarpeta.setText("Carpeta: (escriba un nombre)");
            return;
        }

        try (Connection con = BDconect.getConnection()) {
            String sql = "SELECT c.idCarpetas, c.SubirPortadaCarpeta, c.CodigoCarpeta, c.CantidadReportes, t.AreaCarpeta " +
                    "FROM carpetas_s_b c " +
                    "JOIN tipocarpeta t ON c.TipoCarpeta_idTipoCarpeta = t.idTipoCarpeta " +
                    "WHERE c.NombreCarpetas = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nombreCarpeta);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                idCarpeta = rs.getInt("idCarpetas");
                lblCarpeta.setText("Carpeta: " + nombreCarpeta);

                byte[] portadaBytes = rs.getBytes("SubirPortadaCarpeta");
                if (portadaBytes != null) {
                    Image portada = new Image(new ByteArrayInputStream(portadaBytes));
                    imgPortada.setImage(portada);
                }
                idCarpeta = rs.getInt("idCarpetas");
                String codigo = rs.getString("CodigoCarpeta");
                int cantidad = rs.getInt("CantidadReportes");
                String area = rs.getString("AreaCarpeta");

                lblCarpeta.setText("Carpeta: " + nombreCarpeta);
                lblCodigo.setText("Código: " + codigo);
                lblCantidad.setText("Reportes: " + cantidad);
                lblArea.setText("Área: " + area);;

            } else {
                lblCarpeta.setText("Carpeta no encontrada");
                imgPortada.setImage(null);
                lblArea.setText("Área: -");
                lblCodigo.setText("Código: -");
                lblCantidad.setText("Reportes: -");
                idCarpeta = -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static final String BOTON_VerdeO = "-fx-background-color: #28a745; -fx-background-radius: 20;";
    private static final String BOTON_VerveN = "-fx-background-color: #218838; -fx-background-radius: 20;";


    public void restaurarBotonV(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_VerveN);
    }

    public void oscurecerBotonV(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_VerdeO);
    }


    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void enviarSolicitud() {
        String nombre = txtNombre.getText().trim();
        String carpeta = txtCarpeta.getText().trim();
        String motivo = txtMotivo.getText().trim();

        if (nombre.isEmpty() || carpeta.isEmpty() || motivo.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Todos los campos son obligatorios.").showAndWait();
            return;
        }

        String tipoPrestamo = btnTipo.getValue();
        if (tipoPrestamo == null || tipoPrestamo.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Selecciona el tipo de préstamo.").showAndWait();
            return;
        }


        try (Connection con = BDconect.getConnection()) {
            if (con == null) {
                new Alert(Alert.AlertType.ERROR, "Error al conectar con la base de datos.").showAndWait();
                return;
            }

            int tipoCarpeta = -1;
            String sqlTipo = "SELECT TipoCarpeta_idTipoCarpeta FROM carpetas_s_b WHERE idCarpetas = ?";
            try (PreparedStatement pst = con.prepareStatement(sqlTipo)) {
                pst.setInt(1, idCarpeta);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        tipoCarpeta = rs.getInt("TipoCarpeta_idTipoCarpeta");
                    } else {
                        new Alert(Alert.AlertType.WARNING, "No se pudo obtener el tipo de carpeta.").showAndWait();
                        return;
                    }
                }
            }

            if (tipoPrestamo.equalsIgnoreCase("Fisico")) {
                String sqlVerificar = """
        SELECT FechaS FROM prestamos
        WHERE Tipo = 'Fisico'
          AND Carpetas_S_B_idCarpetas = ?
          AND FechaS >= NOW() - INTERVAL 7 DAY
        ORDER BY FechaS DESC
        LIMIT 1
    """;

                try (PreparedStatement verificarStmt = con.prepareStatement(sqlVerificar)) {
                    verificarStmt.setInt(1, idCarpeta);
                    ResultSet rsVerificar = verificarStmt.executeQuery();

                    if (rsVerificar.next()) {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Solicitud duplicada", "Esta carpeta ya fue solicitada de forma física en los últimos 7 días.\n" +
                                "Solo se puede solicitar de forma **Online** durante ese periodo.");
                        return;
                    }
                }
            }

            String sql = "INSERT INTO prestamos (usuario_invitado, motivo, Users_idUsuario, idUsuario, idCarpeta, FechaR, Carpetas_S_B_idCarpetas, Carpetas_S_B_Users_idUsuario, Carpetas_S_B_TipoCarpeta_idTipoCarpeta, Tipo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            int idUsuarioGeneral = Session.idUsuario;
            int idUsuarioEspecifico = Session.idEspecifico;

            ps.setString(1, nombre);
            ps.setString(2, motivo);
            ps.setInt(3, idUsuarioGeneral);
            ps.setInt(4, idUsuarioEspecifico);
            ps.setInt(5, idCarpeta);
            ps.setNull(6, java.sql.Types.DATE);
            ps.setInt(7, idCarpeta);
            ps.setInt(8, idUsuarioGeneral);
            ps.setInt(9, tipoCarpeta);
            ps.setString(10, tipoPrestamo);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                Prestamo nueva = new Prestamo(0, txtNombre.getText().trim(), motivo, carpeta, new Date(), "", "");
                PreCorreo.enviarCorreos(nueva);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Solicitud enviada correctamente.\n");
            } else {
                new Alert(Alert.AlertType.ERROR, "No se pudo enviar la solicitud.").showAndWait();
            }


        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al guardar la solicitud.").showAndWait();
        }
    }

    @FXML
    private ComboBox<String> btnTipo;
    @FXML
    public void initialize() {
        btnTipo.getItems().addAll("Online", "Fisico");

        txtNombre.setText(Session.NombreUsuario);
        txtNombre.setEditable(false);
        txtNombre.setStyle("-fx-opacity: 0.8; -fx-background-color: #f0f0f0;");
    }


    @FXML
    private javafx.scene.control.Button btnVolver;

    public void btnVolver(ActionEvent actionEvent) {
        try {
            new Bloqueo().abrirVentana("UsuarioIn.fxml", "Usuario Premium");
            Stage currentStage = (Stage) btnVolver.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
