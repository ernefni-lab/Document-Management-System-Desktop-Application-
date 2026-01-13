package org.example.residencia2025ernesto;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;

public abstract class BusqCarp {

    protected TextField buscador;
    protected VBox contenedorResultados;

    public void buscarCarpetas(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            contenedorResultados.getChildren().clear();
            contenedorResultados.getChildren().add(new Label("Por favor ingresa una carpeta para buscar."));
            return;
        }

        String sql = "SELECT * FROM carpetas_s_b WHERE NombreCarpetas LIKE ? OR CodigoCarpeta LIKE ? OR idCarpetas IN (SELECT carpetas_idCarpetas FROM reportes WHERE descripcion LIKE ?)";

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String wildcard = "%" + filtro + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);
            ps.setString(3, wildcard);

            ResultSet rs = ps.executeQuery();
            contenedorResultados.getChildren().clear();

            if (!rs.isBeforeFirst()) {
                contenedorResultados.getChildren().add(new Label("No se encontraron carpetas"));
                return;
            }

            while (rs.next()) {
                int tipoId = rs.getInt("TipoCarpeta_idTipoCarpeta");
                int idCarpeta = rs.getInt("idCarpetas");
                String nombre = rs.getString("NombreCarpetas");
                String codigo = rs.getString("CodigoCarpeta");
                byte[] portadaBytes = rs.getBytes("SubirPortadaCarpeta");

                VBox tarjeta = crearTarjetaCarpeta(idCarpeta, tipoId, nombre, codigo, portadaBytes);
                contenedorResultados.getChildren().add(tarjeta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox crearTarjetaCarpeta(int idCarpeta, int tipoId, String nombre, String codigo, byte[] portadaBytes) {
        VBox tarjeta = new VBox(5);
        tarjeta.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #ccc; -fx-border-radius: 10;");
        tarjeta.setPrefWidth(500);

        if (portadaBytes != null) {
            ImageView imgView = new ImageView(new Image(new ByteArrayInputStream(portadaBytes)));
            imgView.setFitHeight(100);
            imgView.setPreserveRatio(true);
            tarjeta.getChildren().add(imgView);
        }

        Label nombreLabel = new Label( nombre + " (" + codigo + ")");
        nombreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        tarjeta.getChildren().add(nombreLabel);

        try (Connection con = BDconect.getConnection();
             PreparedStatement psRep = con.prepareStatement("SELECT descripcion FROM reportes WHERE carpetas_idCarpetas = ?")) {
            psRep.setInt(1, idCarpeta);
            ResultSet rsRep = psRep.executeQuery();
            while (rsRep.next()) {
                tarjeta.getChildren().add(new Label("📄 " + rsRep.getString("descripcion")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tarjeta.setOnMouseClicked(event -> abrirDetalleCarpeta(idCarpeta, nombre, codigo, tipoId, portadaBytes));

        return tarjeta;
    }

    private void abrirDetalleCarpeta(int idCarpeta, String nombre, String codigo, int tipoId, byte[] portadaBytes) {
        try (Connection con = BDconect.getConnection()) {

            String area = "";
            try (PreparedStatement psTipo = con.prepareStatement(
                    "SELECT AreaCarpeta FROM tipocarpeta WHERE idTipoCarpeta = ?")) {
                psTipo.setInt(1, tipoId);
                ResultSet rsTipo = psTipo.executeQuery();
                if (rsTipo.next()) area = rsTipo.getString("AreaCarpeta");
            }

            Bloqueo bloqueo = new Bloqueo();
            MostarCarpetas controller = bloqueo.abrirVentana(
                    "MostarCarpetas.fxml",
                    "Detalle de Carpeta"
            );

            controller.setDatos(idCarpeta, nombre, codigo, area, portadaBytes);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}