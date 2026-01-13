package org.example.residencia2025ernesto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PreCorreo {

    public static void enviarCorreos(Prestamo prestamo) {
        int idUsuario = Session.idUsuario;

        String correoUsuario = obtenerCorreoUsuario(idUsuario);
        String NombreUsuario = obtenerNombreUsuario(idUsuario);
        if (correoUsuario == null) {
            System.out.println("No se encontró el correo del usuario con ID: " + idUsuario);
            return;
        }

        Correo.enviar(
                "ernefn.i@gmail.com",
                "Nueva solicitud de préstamo",
                "El usuario " + NombreUsuario + " ha solicitado un préstamo para la carpeta '" + prestamo.getCarpeta() + "' con el motivo:\n\n" + prestamo.getMotivo() + "\n\nEl dia: " + prestamo.getFechaS()+ "\n\nPor favor, ingresa a la app para ver el reporte.");

        Correo.enviar(
                correoUsuario,
                "Tu solicitud ha sido registrada",
                "Hola," + NombreUsuario +  "tu solicitud para la carpeta '" + prestamo.getCarpeta() + "' se ha registrado correctamente.\n" + "En un máximo de 5 días recibirás una confirmación por este medio.\n" + "Gracias por usar el sistema.");

        System.out.println("Correos enviados correctamente.");
    }

    private static String obtenerNombreUsuario(int idUsuario) {
        String sql = "SELECT Nombre_Completo FROM users WHERE idusuario = ?";
        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("Nombre_Completo");
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener nombre del usuario:");
            e.printStackTrace();
        }
        return null;
    }


    private static String obtenerCorreoUsuario(int idUsuario) {
        String sql = "SELECT UserCorreo FROM users WHERE idusuario = ?";

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Session.idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("UserCorreo");
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener correo del usuario:");
            e.printStackTrace();
        }

        return null;
    }
}