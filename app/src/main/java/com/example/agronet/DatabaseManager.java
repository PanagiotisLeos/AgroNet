package com.example.agronet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:mariadb://192.168.2.7:3306/agronetdb";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection;

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                // Handle errors more gracefully in a real application
            }
        }
        return connection;
    }
}
