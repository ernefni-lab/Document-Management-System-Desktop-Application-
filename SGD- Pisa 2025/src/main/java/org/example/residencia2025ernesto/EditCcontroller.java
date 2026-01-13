package org.example.residencia2025ernesto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EditCcontroller {
    @FXML private Button btnVolver;
    @FXML private TextField txtBuscar;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCodigo;
    @FXML private ComboBox<String> comboArea;
    @FXML private ListView<String> listaReportes;
    @FXML private Label lblMensaje;

    private int idCarpeta;
    private final List<ReporteNuevo> nuevosReportes = new ArrayList<>();
    private ObservableList<String> reportesActuales = FXCollections.observableArrayList();
    private final List<String> reportesEliminados = new ArrayList<>();

    @FXML
    public void initialize() {
        comboArea.setItems(FXCollections.observableArrayList(
                "HVAC", "Ampolletas vidrio", "Viales Ampula", "Generales",
                "Jeringas", "Polvos inyectables", "Sueros Bolsa", "Sueros Vidrio"
        ));
        listaReportes.setItems(reportesActuales);
    }

    public void buscarCarpeta() {
        String buscar = txtBuscar.getText().trim();
        if (buscar.isEmpty()) return;

        String sql = "SELECT * FROM carpetas_s_b WHERE NombreCarpetas = ? OR CodigoCarpeta = ?";

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, buscar);
            ps.setString(2, buscar);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                idCarpeta = rs.getInt("idCarpetas");
                txtNombre.setText(rs.getString("NombreCarpetas"));
                txtCodigo.setText(rs.getString("CodigoCarpeta"));
                comboArea.setValue(obtenerArea(rs.getInt("TipoCarpeta_idTipoCarpeta")));
                cargarReportes(idCarpeta);
                nuevosReportes.clear();
                reportesEliminados.clear();
                lblMensaje.setText("");
            } else {
                lblMensaje.setText("Carpeta no encontrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void guardarCambios() {
        String sql = "UPDATE carpetas_s_b SET NombreCarpetas = ?, CodigoCarpeta = ?, TipoCarpeta_idTipoCarpeta = ? WHERE idCarpetas = ?";

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtCodigo.getText());
            ps.setInt(3, obtenerIdArea(comboArea.getValue()));
            ps.setInt(4, idCarpeta);
            ps.executeUpdate();

            insertarNuevosPDFs(con);
            eliminarReportesDeBD(con);
            actualizarCantidadReportes(idCarpeta);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Ëxito", "Carpetas y reportes actualizados correctamente");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void insertarNuevosPDFs(Connection con) throws SQLException, IOException {
        String sql = "INSERT INTO reportes (descripcion, archivo, carpetas_idCarpetas) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (ReporteNuevo reporte : nuevosReportes) {
                try (FileInputStream fis = new FileInputStream(reporte.archivo)) {
                    ps.setString(1, reporte.descripcion);
                    ps.setBinaryStream(2, fis, (int) reporte.archivo.length());
                    ps.setInt(3, idCarpeta);
                    ps.executeUpdate();
                }
            }
        }
    }


    private void eliminarReportesDeBD(Connection con) throws SQLException {
        String sql = "DELETE FROM reportes WHERE descripcion = ? AND carpetas_idCarpetas = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (String desc : reportesEliminados) {
                ps.setString(1, desc);
                ps.setInt(2, idCarpeta);
                ps.executeUpdate();
            }
        }
    }

    private void actualizarCantidadReportes(int idCarpeta) {
        String sql = "UPDATE carpetas_s_b SET CantidadReportes = (SELECT COUNT(*) FROM reportes WHERE carpetas_idCarpetas = ?) WHERE idCarpetas = ?";

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCarpeta);
            ps.setInt(2, idCarpeta);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void cargarReportes(int idCarpeta) throws SQLException {
        reportesActuales.clear();
        String sql = "SELECT descripcion FROM reportes WHERE carpetas_idCarpetas = ?";
        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCarpeta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportesActuales.add(rs.getString("descripcion"));
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void btndescargar(ActionEvent actionEvent) {
        if (idCarpeta <= 0) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Error", "Primero busca una carpeta");
            return;
        }

        List<byte[]> archivos = new ArrayList<>();
        List<String> nombres = new ArrayList<>();

        String sql = "SELECT descripcion, archivo FROM reportes WHERE carpetas_idCarpetas = ?";

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCarpeta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                nombres.add(rs.getString("descripcion").replaceAll("[\\\\/:*?\"<>|]", "_"));
                archivos.add(rs.getBytes("archivo"));
            }

            if (archivos.isEmpty()) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sin archivos", "No hay reportes para descargar en esta carpeta.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            Stage stage = new Stage();

            if (archivos.size() == 1) {
                fileChooser.setTitle("Guardar PDF");
                fileChooser.setInitialFileName(txtNombre.getText() + ".pdf");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));

                File selectedFile = fileChooser.showSaveDialog(stage);
                if (selectedFile != null) {
                    try (FileOutputStream fos = new FileOutputStream(selectedFile)) {
                        fos.write(archivos.get(0));
                    }
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "PDF descargado con éxito.");
                }

            } else {
                fileChooser.setTitle("Guardar ZIP");
                fileChooser.setInitialFileName(txtNombre.getText() + "_reportes.zip");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo ZIP", "*.zip"));

                File zipFile = fileChooser.showSaveDialog(stage);
                if (zipFile != null) {
                    try (FileOutputStream fos = new FileOutputStream(zipFile);
                         java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(fos)) {

                        for (int i = 0; i < archivos.size(); i++) {
                            byte[] pdf = archivos.get(i);
                            String nombre = nombres.get(i) + ".pdf";
                            java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(nombre);
                            zos.putNextEntry(zipEntry);
                            zos.write(pdf);
                            zos.closeEntry();
                        }

                        zos.finish();
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Archivos ZIP descargados con éxito.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al descargar los reportes.");
        }
    }


    private static class ReporteNuevo {
        String descripcion;
        File archivo;

        ReporteNuevo(String descripcion, File archivo) {
            this.descripcion = descripcion;
            this.archivo = archivo;
        }
    }

    public void agregarPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar PDF a agregar");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File archivo = fileChooser.showOpenDialog(new Stage());

        if (archivo != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Descripción del PDF");
            dialog.setHeaderText("Agrega una descripción para el PDF seleccionado:");
            dialog.setContentText("Descripción:");

            dialog.showAndWait().ifPresent(descripcion -> {
                if (descripcion.trim().isEmpty()) {
                    Alert alerta = new Alert(Alert.AlertType.WARNING, "La descripción no puede estar vacía.");
                    alerta.showAndWait();
                } else {
                    nuevosReportes.add(new ReporteNuevo(descripcion, archivo));
                    reportesActuales.add(descripcion);
                }
            });
        }
    }



    public void eliminarPDF() {
        String seleccionado = listaReportes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            reportesActuales.remove(seleccionado);
            reportesEliminados.add(seleccionado);
        }
    }

    private String obtenerArea(int idTipo) throws SQLException {
        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT AreaCarpeta FROM tipocarpeta WHERE idTipoCarpeta = ?")) {
            ps.setInt(1, idTipo);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("AreaCarpeta") : null;
        }
    }

    private int obtenerIdArea(String area) throws SQLException {
        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT idTipoCarpeta FROM tipocarpeta WHERE AreaCarpeta = ?")) {
            ps.setString(1, area);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("idTipoCarpeta") : 0;
        }
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
