package org.example.residencia2025ernesto;

import java.sql.*;

public class CrearUserController {

    public static boolean correoExiste(String correo) {
        String query = "SELECT 1 FROM users WHERE TRIM(LOWER(UserCorreo)) = ?";
        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, correo.trim().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int registrarUsuario(String tipo, String nombre, String correo, String nombreCompleto, String contrasena) throws SQLException {
        try (Connection con = BDconect.getConnection()) {
            String insertSql = "INSERT INTO users (TipoUsuario, NombreUsuario, UserCorreo, Nombre_Completo, contraseñaUsuario) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tipo);
            ps.setString(2, nombre);
            ps.setString(3, correo);
            ps.setString(4, nombreCompleto);
            ps.setString(5, contrasena);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);

                try {
                    String asunto = "Bienvenido al sistema PISA";
                    String cuerpo = "Hola " + nombreCompleto + ",\n\n"
                            + "Tu cuenta ha sido creada exitosamente en el sistema PISA.\n"
                            + "Puedes iniciar sesión con tu usuario: " + nombre + "\n\n" +
                            "y con tu contraseña: " + contrasena +  " recuerda completar tu informacion de usuario cuando entres a la aplicacion en la seccion de Cambiar " + "\n\n" +
                         "La app te servira para solicitar los préstamos de las carpetas que necesites ya sean online o fisicos.\n\n"
                            + "¡Bienvenido!\n"
                            + "Atentamente,\nÁrea de Validación";

                    Correo.enviar(correo, asunto, cuerpo);

                } catch (Exception e) {
                    System.err.println("Error al enviar el correo:");
                    e.printStackTrace();
                }

                return id;
            } else {
                throw new SQLException("No se pudo obtener el ID");
            }
        } catch (SQLException e) {
            System.err.println("Error en el registro de usuario:");
            e.printStackTrace();
            throw e;
        }
    }


    public static boolean nombreCompletoExiste(String nombreCompleto) {
        String query = "SELECT 1 FROM users WHERE TRIM(LOWER(Nombre_Completo)) = ?";
        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nombreCompleto.trim().toLowerCase());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static void asignarTipoUsuario(int id, String tipo) throws SQLException {
        String sql = switch (tipo.toLowerCase()) {
            case "premium" -> "UPDATE users SET UserPremium_idUser = ? WHERE idUsuario = ?";
            case "invitado" -> "UPDATE users SET UserInvitado_idUser = ? WHERE idUsuario = ?";
            default -> throw new SQLException("Tipo de usuario inválido.");
        };

        try (Connection con = BDconect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}
