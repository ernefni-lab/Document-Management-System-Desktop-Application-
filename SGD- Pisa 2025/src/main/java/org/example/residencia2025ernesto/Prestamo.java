package org.example.residencia2025ernesto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Prestamo {
    private int id;
    private String usuario;
    private String motivo;
    private String carpeta;
    private Date fecha;
    private String tipo;
    private String UserCorreo;

    public Prestamo(int id, String usuario, String motivo, String carpeta, Date fecha, String tipo, String UserCorreo) {
        this.id = id;
        this.usuario = usuario;
        this.motivo = motivo;
        this.carpeta = carpeta;
        this.fecha = fecha;
        this.tipo = tipo;
        this.UserCorreo = UserCorreo;
    }

    public int getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getCarpeta() {
        return carpeta;
    }

    public Date getFechaDate() {
        return fecha;
    }

    public String getFechaS() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(fecha);
    }

    public String getTipo() {
        return tipo;
    }

    public String getCorreo() {
        return UserCorreo;
    }
}
