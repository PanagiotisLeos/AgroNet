package com.example.agronet;

import android.content.Context;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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


    public static User fetchUserDetails(String userId, String userType) throws SQLException {
        Connection conn = getConnection();
        String query = userType.equals("0") ?
                "SELECT * FROM customer WHERE customer_id = ?" :
                "SELECT * FROM farmer WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if ("0".equals(userType)) {
                    return new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name")
                    );
                } else if ("1".equals(userType)) {
                    return new Farmer(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("location"),
                            rs.getBytes("prof_image"),
                            rs.getString("farmer_type"),
                            rs.getString("description")

                    );
                }
            }
        }
        return null;
    }
}



