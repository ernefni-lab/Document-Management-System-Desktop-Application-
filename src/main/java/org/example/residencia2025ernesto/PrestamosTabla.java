package org.example.residencia2025ernesto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamosTabla {

    public static List<Prestamo> obtenerPrestamosPendientes() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT  \n" +
                "    p.idPrestamos AS id,\n" +
                "    u.Nombre_Completo AS usuario,\n" +
                "    p.motivo,\n" +
                "    c.NombreCarpetas AS carpeta,\n" +
                "    p.FechaS AS fecha,\n" +
                "    p.Tipo AS tipo,\n" +
                "    u.UserCorreo AS correo\n" +
                "FROM prestamos p\n" +
                "JOIN users u ON p.Users_idUsuario = u.idUsuario\n" +
                "JOIN carpetas_s_b c ON p.Carpetas_S_B_idCarpetas = c.idCarpetas\n" +
                "WHERE p.FechaR IS NULL\n" +
                "ORDER BY p.FechaS ASC;\n";

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Prestamo(
                        rs.getInt("id"),
                        rs.getString("usuario"),
                        rs.getString("motivo"),
                        rs.getString("carpeta"),
                        rs.getTimestamp("fecha"),
                        rs.getString("tipo"),
                        rs.getString("correo")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static List<PrestamosAutorizados> obtenerPrestamosAutorizados(int idUsuario) {
        List<PrestamosAutorizados> lista = new ArrayList<>();
        String sql = "SELECT c.NombreCarpetas, p.FechaR, c.idCarpetas, c.SubirCarpeta FROM prestamos p JOIN carpetas_s_b c ON p.Carpetas_S_B_idCarpetas = c.idCarpetas WHERE p.Users_idUsuario = ? AND p.FechaR IS NOT NULL AND p.Tipo = \"Online\"";

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                        lista.add(new PrestamosAutorizados(
                        rs.getString("NombreCarpetas"),
                        rs.getString("FechaR"),
                        rs.getInt("idCarpetas"),
                        rs.getBytes("SubirCarpeta")
                        ));
            }

            System.out.println("ID de usuario actual: " + idUsuario);
            System.out.println("Registros encontrados: " + lista.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static void autorizarPrestamo(int idPrestamo) {
        String updateSQL = "UPDATE prestamos SET FechaR = ? WHERE idPrestamos = ?";

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(updateSQL)) {

            ps.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setInt(2, idPrestamo);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
