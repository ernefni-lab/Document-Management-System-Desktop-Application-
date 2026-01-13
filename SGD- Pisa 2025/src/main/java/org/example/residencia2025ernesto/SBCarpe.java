package org.example.residencia2025ernesto;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;

public class SBCarpe {
    @FXML private MenuButton btnRepor;
    @FXML private VBox contenedorReportes;
    @FXML private TextField NCarp, ACarp, Ccarp, PCarp, FACarp;
    @FXML private Button btnPor1;
    @FXML private ComboBox<String> comboAreas;

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Loggin.class.getResource("Loggin.fxml"));

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.setResizable(false);

        stage.show();
    }

    private File portadaSeleccionada = null;
    private final List<TextField> descripcionesReportes = new ArrayList<>();
    private final List<File> archivosReportes = new ArrayList<>();

    private static final String BOTON_OSCURO = "-fx-background-color: #5aaedb; -fx-background-radius: 20;";
    private static final String BOTON_NORMAL = "-fx-background-color: #2a9df4; -fx-background-radius: 20;";

    private static final String BOTON_VerdeO = "-fx-background-color: #28a745; -fx-background-radius: 20;";
    private static final String BOTON_VerveN = "-fx-background-color: #218838; -fx-background-radius: 20;";


    private static final String BOTON_VerdeGO = "-fx-background-color: #dadada; -fx-background-radius: 20;";
    private static final String BOTON_VerveG = "-fx-background-color: #c0c0c0; -fx-background-radius: 20;";


    public void initialize() {
        comboAreas.getItems().addAll("HVAC", "Ampolletas vidrio", "Viales Ampula", "Generales","Jeringas", "Polvos inyectables", "Sueros Bolsa", "Sueros Vidrio");

        for (int i = 1; i <= 10; i++) {
            MenuItem item = new MenuItem(String.valueOf(i));
            item.setOnAction(event -> {
                btnRepor.setText("Reportes: " + item.getText());
                int cantidad = Integer.parseInt(item.getText());
                mostrarSeccionesParaReportes(cantidad);
            });
            btnRepor.getItems().add(item);
        }
    }

    public void restaurarBotonV(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_VerveN);
    }

    public void oscurecerBotonV(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_VerdeO);
    }

    public void restaurarBotonG(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_VerveG);
    }

    public void oscurecerBotonG(javafx.scene.input.MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_VerdeGO);
    }

    private void mostrarSeccionesParaReportes(int cantidad) {
        contenedorReportes.getChildren().clear();
        descripcionesReportes.clear();
        archivosReportes.clear();
        for (int i = 0; i < cantidad; i++) {
            VBox seccion = new VBox(5);
            seccion.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5;");
            TextField descripcion = new TextField();
            descripcion.setPromptText("Descripción del Reporte " + (i + 1));
            descripcionesReportes.add(descripcion);
            archivosReportes.add(null);
            Button btnSubirPDF = new Button("Subir archivo PDF");
            final int index = i;
            btnSubirPDF.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Seleccionar archivo PDF para Reporte " + (index + 1));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
                File selected = fileChooser.showOpenDialog(null);
                if (selected != null) {
                    archivosReportes.set(index, selected);
                    btnSubirPDF.setText("Archivo: " + selected.getName());
                }
            });

            seccion.getChildren().addAll(descripcion, btnSubirPDF);
            contenedorReportes.getChildren().add(seccion);
        }
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
    private void abrirExploradorPortada() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de portada");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        Stage stage = (Stage) btnPor1.getScene().getWindow();
        File archivoSeleccionado = fileChooser.showOpenDialog(stage);
        if (archivoSeleccionado != null) {
            portadaSeleccionada = archivoSeleccionado;
        }
    }

    private void guardarReportes(Connection con, int idCarpeta, int idUsuario, int idTipoCarpeta) {
        String sql = "INSERT INTO reportes (descripcion, archivo, carpetas_idCarpetas, carpetas_Users_idUsuario, carpetas_TipoCarpeta_idTipoCarpeta) VALUES (?, ?, ?, ?, ?)";
        int guardados = 0;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < descripcionesReportes.size(); i++) {
                String descripcion = descripcionesReportes.get(i).getText();
                File archivo = archivosReportes.get(i);

                if (descripcion != null && !descripcion.isEmpty() && archivo != null && archivo.exists()) {
                    try (FileInputStream fis = new FileInputStream(archivo)) {
                        byte[] encabezado = new byte[4];
                        fis.read(encabezado);
                        String firma = new String(encabezado);
                        if (!firma.equals("%PDF")) {
                            throw new IOException("El archivo no es un PDF válido: " + archivo.getName());
                        }

                        ps.setString(1, descripcion);
                        ps.setBinaryStream(2, new FileInputStream(archivo), (int) archivo.length());
                        ps.setInt(3, idCarpeta);
                        ps.setInt(4, idUsuario);
                        ps.setInt(5, idTipoCarpeta);
                        ps.executeUpdate();
                        guardados++;
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private int insertarOTraerIdTipoCarpeta(Connection con, String area) throws SQLException {
        String query = "SELECT idTipoCarpeta FROM tipocarpeta WHERE AreaCarpeta = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, area);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("idTipoCarpeta");
        }

        String insert = "INSERT INTO tipocarpeta (AreaCarpeta) VALUES (?)";
        try (PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, area);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            else throw new SQLException("No se pudo obtener el ID del tipo de carpeta");
        }
    }

    @FXML
    public void guardarDatos() {
        boolean incompleto = false;
        for (int i = 0; i < descripcionesReportes.size(); i++) {
            String descripcion = descripcionesReportes.get(i).getText();
            File archivo = archivosReportes.get(i);
            if (descripcion == null || descripcion.trim().isEmpty() || archivo == null || !archivo.exists()) {
                incompleto = true;
                break;
            }
        }
        if (NCarp.getText().isEmpty() || Ccarp.getText().isEmpty() || FACarp.getText().isEmpty() ||
                PCarp.getText().isEmpty() || comboAreas.getValue() == null || portadaSeleccionada == null ||
                btnRepor.getText().equals("Seleccionar cantidad de reportes")) {

            mostrarAlerta(Alert.AlertType.INFORMATION, "Falta llenar", "Todos Campos deben de estar completos");
        }


        if (incompleto) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Falta llenar", "Todos los reportes deben tener descripción y archivo PDF válido.");
            return;
        }


        String sql = "INSERT INTO carpetas_s_b (NombreCarpetas, CodigoCarpeta, FirmaCarpeta, FechaCarpeta, SubirPortadaCarpeta, Users_idUsuario, TipoCarpeta_idTipoCarpeta, CantidadReportes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = BDconect.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int idUsuario = 1;
            String areaSeleccionada = comboAreas.getValue();
            if (areaSeleccionada == null || areaSeleccionada.isEmpty()) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sin Area", "Seleccione una area antes de guardar la carpeta. ");
                return;
            }
            int idTipoCarpeta = insertarOTraerIdTipoCarpeta(con, areaSeleccionada);
            String verificarCodigo = "SELECT COUNT(*) FROM carpetas_s_b WHERE CodigoCarpeta = ? AND TipoCarpeta_idTipoCarpeta = ?";
            try (PreparedStatement verificarStmt = con.prepareStatement(verificarCodigo)) {
                verificarStmt.setString(1, Ccarp.getText());
                verificarStmt.setInt(2, idTipoCarpeta);
                ResultSet rs = verificarStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    Alert alerta = new Alert(Alert.AlertType.WARNING);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Duplicado", "El codigo ya existe en esta area");
                    return;
                }
            }
            stmt.setString(1, NCarp.getText());
            stmt.setString(2, Ccarp.getText());
            stmt.setString(3, FACarp.getText());
            stmt.setString(4, PCarp.getText());
            String textoReportes = btnRepor.getText();
            String cantReportesStr = textoReportes.replaceAll("[^0-9]", "");
            int cantidadReportes = cantReportesStr.isEmpty() ? 0 : Integer.parseInt(cantReportesStr);
            if (portadaSeleccionada != null) {
                try (FileInputStream portadaStream = new FileInputStream(portadaSeleccionada)) {
                    stmt.setBinaryStream(5, portadaStream, (int) portadaSeleccionada.length());
                    stmt.setInt(6, idUsuario);
                    stmt.setInt(7, idTipoCarpeta);
                    stmt.setInt(8, cantidadReportes);
                    int filasInsertadas = stmt.executeUpdate();
                    if (filasInsertadas > 0) {
                        try (ResultSet rs = stmt.getGeneratedKeys()) {
                            if (rs.next()) {
                                int idCarpeta = rs.getInt(1);
                                guardarReportes(con, idCarpeta, idUsuario, idTipoCarpeta);
                                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Carpeta Guardada en el sistema Pisa.");
                            }
                        }
                    } else {
                        System.out.println("Error al guardar los datos");
                    }
                }
            } else {
                Alert portadaFaltante = new Alert(Alert.AlertType.WARNING);
                portadaFaltante.setContentText("Falta seleccionar la imagen de portada.");
                portadaFaltante.showAndWait();
            }
        } catch (SQLException | IOException e) {
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