package org.example.residencia2025ernesto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BDconect {

    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASSWORD = "1346";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}