package org.example.residencia2025ernesto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;

public class PrestamosCompletos {

    @FXML private TableView<Prestamo> tablaPrestamos;
    @FXML private TableColumn<Prestamo, Integer> colId;
    @FXML private TableColumn<Prestamo, String> colNombre;
    @FXML private TableColumn<Prestamo, String> colMotivo;
    @FXML private TableColumn<Prestamo, String> colCarpeta;
    @FXML private TableColumn<Prestamo, String> colTipo;
    @FXML private TableColumn<Prestamo, String> colUserCorreo;
    @FXML private TableColumn<Prestamo, Date> colFechaR;
    @FXML private TableColumn<Prestamo, Date> colFechaS;
    @FXML private Label lblMensaje;
    @FXML private Button btnVolver;

    private final ObservableList<Prestamo> prestamosList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colUserCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCarpeta.setCellValueFactory(new PropertyValueFactory<>("carpeta"));
        colFechaS.setCellValueFactory(new PropertyValueFactory<>("fechaDate"));
        colFechaR.setCellValueFactory(new PropertyValueFactory<>("fechaDate"));

        actualizarTabla();
    }

    private void actualizarTabla() {
        prestamosList.setAll(PrestamosTablaCompletos.obtenerTodosLosPrestamosAutorizados());
        prestamosList.sort(Comparator.comparing(Prestamo::getFechaDate).reversed()); // más recientes arriba
        tablaPrestamos.setItems(prestamosList);
        lblMensaje.setText("Mostrando préstamos completados.");
    }

    public void oscurecerBoton(MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle("-fx-background-color: #5aaedb; -fx-background-radius: 20;");
    }

    public void restaurarBoton(MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle("-fx-background-color: #2a9df4; -fx-background-radius: 20;");
    }

    public void oscurecerBotonV(MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle("-fx-background-color: #28a745; -fx-background-radius: 20;");
    }

    public void restaurarBotonV(MouseEvent event) {
        Button boton = (Button) event.getSource();
        boton.setStyle("-fx-background-color: #218838; -fx-background-radius: 20;");
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
