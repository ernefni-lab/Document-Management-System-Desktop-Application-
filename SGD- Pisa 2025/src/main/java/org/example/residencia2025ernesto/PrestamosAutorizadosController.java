package org.example.residencia2025ernesto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.example.residencia2025ernesto.Session.idUsuario;

public class PrestamosAutorizadosController {
    @FXML private TableView<PrestamosAutorizados> tablaPrestamos;
    @FXML private TableColumn<PrestamosAutorizados, String> colNombre;
    @FXML private TableColumn<PrestamosAutorizados, String> colFecha;
    @FXML private TableColumn<PrestamosAutorizados, Void> colDescargar;

    private ObservableList<PrestamosAutorizados> prestamosList = FXCollections.observableArrayList();

    private static final String BOTON_OSCURO = "-fx-background-color: #5aaedb; -fx-background-radius: 20;";
    private static final String BOTON_NORMAL = "-fx-background-color: #2a9df4; -fx-background-radius: 20;";
    @FXML
    public void initialize() {
        System.out.println("ID de usuario actual: " + idUsuario);

        colNombre.setCellValueFactory(data -> data.getValue().nombreCarpetaProperty());
        colFecha.setCellValueFactory(data -> data.getValue().fechaAutorizadoProperty());

        colDescargar.setCellFactory(new Callback<>() {
            public TableCell<PrestamosAutorizados, Void> call(final TableColumn<PrestamosAutorizados, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Descargar PDF");

                    {
                        btn.setOnAction(event -> {
                            PrestamosAutorizados prestamo = getTableView().getItems().get(getIndex());
                            descargarPDF(prestamo);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
            }
        });

        prestamosList.setAll(PrestamosTabla.obtenerPrestamosAutorizados(idUsuario));

        tablaPrestamos.setItems(prestamosList);
        System.out.println("Registros encontrados: " + prestamosList.size());
    }

    public void restaurarBotonV(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_NORMAL);
    }

    public void oscurecerBotonV(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_OSCURO);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void descargarPDF(PrestamosAutorizados prestamo) {
        try (Connection con = BDconect.getConnection()) {
            int idCarpeta = prestamo.getIdCarpeta();
            String nombreCarpeta = prestamo.getNombreCarpeta();

            String sql = "SELECT descripcion, archivo FROM reportes WHERE carpetas_idCarpetas = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idCarpeta);
            ResultSet rs = ps.executeQuery();

            List<byte[]> archivos = new ArrayList<>();
            List<String> nombres = new ArrayList<>();

            while (rs.next()) {
                archivos.add(rs.getBytes("archivo"));
                nombres.add(rs.getString("descripcion").replaceAll("[^a-zA-Z0-9-_ ]", "_"));
            }

            if (archivos.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "No hay reportes para descargar").show();
                return;
            }

            FileChooser fileChooser = new FileChooser();
            Stage stage = new Stage();

            if (archivos.size() == 1) {
                fileChooser.setTitle("Guardar PDF");
                fileChooser.setInitialFileName(nombreCarpeta + ".pdf");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));

                File selectedFile = fileChooser.showSaveDialog(stage);
                if (selectedFile != null) {
                    try (FileOutputStream fos = new FileOutputStream(selectedFile)) {
                        fos.write(archivos.get(0));
                    }
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "PDF descargado con exito");
                }
            } else {
                fileChooser.setTitle("Guardar ZIP");
                fileChooser.setInitialFileName(nombreCarpeta + "_reportes.zip");
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
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "PDF descargado con exito");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Error", "Error al descargar");
        }
    }

    @FXML
    private Button btnVolver;

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

