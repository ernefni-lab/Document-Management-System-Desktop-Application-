package org.example.residencia2025ernesto;

import java.sql.*;

public class CorrS {

    public static void registrarSolicitudYEnviarCorreo(Prestamo prestamo) {
        int idUsuario = Integer.parseInt(prestamo.getUsuario());
        String correoUsuario = obtenerCorreoUsuario(idUsuario);

        if (correoUsuario == null) {
            System.out.println("No se encontró el correo del usuario.");
            return;
        }

        String sql = "INSERT INTO prestamos (usuario_id, motivo, carpeta, fecha) VALUES (?, ?, ?, ?)";

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setString(2, prestamo.getMotivo());
            ps.setString(3, prestamo.getCarpeta());
            ps.setDate(4, new java.sql.Date(prestamo.getFechaDate().getTime()));
            ps.executeUpdate();

            Correo.enviar(
                    "ernefn.i@gmail.com",
                    "Nueva solicitud de préstamo",
                    "El usuario con ID " + idUsuario + " ha pedido un préstamo. Ingresa a la app para ver el reporte."
            );

            Correo.enviar(
                    correoUsuario,
                    "Tu solicitud ha sido creada",
                    "Se ha creado tu solicitud. En un máximo de 5 días te enviaremos un correo con la confirmación."
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String obtenerCorreoUsuario(int idUsuario) {
        String sql = "SELECT Correo FROM usuarios WHERE idusuarios = ?";
        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("Correo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
