package org.example.residencia2025ernesto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamosTablaCompletos {
        public static List<Prestamo> obtenerTodosLosPrestamosAutorizados() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT " +
                "p.idPrestamos AS id, " +
                "u.Nombre_Completo AS usuario, " +
                "p.motivo, " +
                "c.NombreCarpetas AS nombreCarpeta, " +
                "p.Tipo AS tipo, " +
                "u.UserCorreo AS correo, " +
                "p.FechaS AS fecha, " +
                "p.FechaR AS fechaR " +
                "FROM prestamos p " +
                "JOIN users u ON p.Users_idUsuario = u.idUsuario " +
                "JOIN carpetas_s_b c ON p.Carpetas_S_B_idCarpetas = c.idCarpetas " +
                "WHERE p.FechaR IS NOT NULL " +
                "ORDER BY p.FechaR DESC";

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prestamo prestamo = new Prestamo(
                        rs.getInt("id"),
                        rs.getString("usuario"),
                        rs.getString("motivo"),
                        rs.getString("nombreCarpeta"),
                        rs.getTimestamp("fecha"),
                        rs.getString("tipo"),
                        rs.getString("correo")
                );
                lista.add(prestamo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
