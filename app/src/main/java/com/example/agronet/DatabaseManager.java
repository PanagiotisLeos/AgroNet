package com.example.agronet;

import android.content.Context;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:mariadb://192.168.2.7/agronetdb";
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
                            rs.getString("last_name"),
                            rs.getInt("phone")
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

    public static Customer fetchCustomerInfo(String userId) throws SQLException {
        Connection conn = getConnection();
        String query = "SELECT * FROM customer WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("telephone")
                );
            }
        }
        return null;
    }

    public static void updateCustomerInfo(Customer customer) throws SQLException {
        Connection conn = getConnection();
        String query = "UPDATE customer SET first_name = ?, last_name = ?, email = ? WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, customer.getfName());
            stmt.setString(2, customer.getlName());
            stmt.setInt(3, customer.getphone());
            stmt.setInt(4, customer.getUserId());
            stmt.executeQuery();
        }
    }
}



