package org.example.residencia2025ernesto;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;

public class AutorizarPrestamos {

    @FXML private TableView<Prestamo> tablaPrestamos;
    @FXML private TableColumn<Prestamo, Integer> colId;
    @FXML private TableColumn<Prestamo, String> colNombre;
    @FXML private TableColumn<Prestamo, String> colMotivo;
    @FXML private TableColumn<Prestamo, String> colCarpeta;
    @FXML private TableColumn<Prestamo, String> colTipo;
    @FXML private TableColumn<Prestamo, String> colUserCorreo;
    @FXML private TableColumn<Prestamo, Date> colFecha;
    @FXML private Label lblMensaje;

    private ObservableList<Prestamo> prestamosList = FXCollections.observableArrayList();

    private static final String BOTON_OSCURO = "-fx-background-color: #5aaedb; -fx-background-radius: 20;";
    private static final String BOTON_NORMAL = "-fx-background-color: #2a9df4; -fx-background-radius: 20;";

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colUserCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCarpeta.setCellValueFactory(new PropertyValueFactory<>("carpeta"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaDate"));

        actualizarTabla();
    }

    @FXML
    private void autorizarSeleccionado() {
        Prestamo prestamo = tablaPrestamos.getSelectionModel().getSelectedItem();
        if (prestamo == null) {
            lblMensaje.setText("Selecciona un préstamo.");
            return;
        }

        PrestamosTabla.autorizarPrestamo(prestamo.getId());

        String asunto = "Tu solicitud de préstamo ha sido autorizada";
        String cuerpo;

        if ("Fisico".equalsIgnoreCase(prestamo.getTipo())) {
            cuerpo = "Hola " + prestamo.getUsuario() + ",\n\n" +
                    " Tu préstamo de la carpeta \"" + prestamo.getCarpeta() + "\" ha sido autorizado.\n" +
                    "Puedes pasar al área de validación para recogerla.\n" +
                    "Gracias por usar nuestro sistema.\nÁrea de Validación.";
        } else {
            cuerpo = "Hola " + prestamo.getUsuario() + ",\n\n" +
                    " Tu préstamo de la carpeta \"" + prestamo.getCarpeta() + "\" ha sido autorizado.\n" +
                    "Ya puedes acceder al contenido desde la aplicación en la sección de préstamos autorizados.\n" +
                    "Gracias por usar nuestro sistema.\nÁrea de Validación.";
        }

        Correo.enviar(prestamo.getCorreo(), asunto, cuerpo);

        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Prestamo autorizado y correo enviado correctamente.");
        actualizarTabla();
    }


    private void actualizarTabla() {
        prestamosList.setAll(PrestamosTabla.obtenerPrestamosPendientes());
        prestamosList.sort(Comparator.comparing(Prestamo::getFechaDate));
        tablaPrestamos.setItems(prestamosList);
    }

    public void oscurecerBoton(MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_OSCURO);
    }

    public void restaurarBoton(MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle(BOTON_NORMAL);
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
