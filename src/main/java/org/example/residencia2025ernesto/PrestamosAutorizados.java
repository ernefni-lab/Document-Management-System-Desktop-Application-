package org.example.residencia2025ernesto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PrestamosAutorizados {
    private final SimpleStringProperty nombreCarpeta;
    private final SimpleStringProperty fechaAutorizado;
    private final int idCarpeta;
    private final byte[] pdfData;

    public PrestamosAutorizados(String nombreCarpeta, String fechaAutorizado, int idCarpeta, byte[] pdfData) {
        this.nombreCarpeta = new SimpleStringProperty(nombreCarpeta);
        this.fechaAutorizado = new SimpleStringProperty(fechaAutorizado);
        this.idCarpeta = idCarpeta;
        this.pdfData = pdfData;
    }
    public StringProperty nombreCarpetaProperty() { return nombreCarpeta; }
    public StringProperty fechaAutorizadoProperty() { return fechaAutorizado; }
    public String getNombreCarpeta() { return nombreCarpeta.get(); }
    public int getIdCarpeta() { return idCarpeta; }
}
