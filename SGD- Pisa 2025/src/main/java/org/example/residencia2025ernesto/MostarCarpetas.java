package org.example.residencia2025ernesto;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MostarCarpetas {
    @FXML private ImageView imgPortada;
    @FXML private Label lblNombre, lblCodigo, lblTipo;
    @FXML private VBox vboxReportes;

    public void setDatos(int idCarpeta, String nombre, String codigo, String tipo, byte[] portadaBytes) {

        lblNombre.setText(nombre);
        lblCodigo.setText("🔖 Código: " + codigo);
        lblTipo.setText("️ Área: " + tipo);

        if (portadaBytes != null) {
            imgPortada.setImage(new Image(new ByteArrayInputStream(portadaBytes)));
        }

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT descripcion FROM reportes WHERE carpetas_idCarpetas = ?")) {

            ps.setInt(1, idCarpeta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String descripcion = rs.getString("descripcion");
                Label lbl = new Label("• " + descripcion);
                lbl.setStyle("-fx-font-size: 13px;");
                vboxReportes.getChildren().add(lbl);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
