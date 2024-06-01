package com.example.agronet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mariadb://192.168.2.102:3306/agronetdb";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;
    private Context context;


    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                throw new SQLException("Failed to create database connection", e);
            }
        }
        return connection;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT product.id, name, price, farmer_id, prod_image, prof_image FROM product " +
                "LEFT JOIN farmer ON product.farmer_id = farmer.id";

        Log.d("DatabaseManager", "About to get database connection.");

        Connection connection = null;
        try {
            connection = getConnection();
            Log.d("DatabaseManager", "Database connection obtained.");

            if (connection == null || connection.isClosed()) {
                Log.e("DatabaseManager", "Database connection is null or closed.");
                return products;
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                Log.d("DatabaseManager", "PreparedStatement created.");

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    Log.d("DatabaseManager", "Executing query: " + sql);

                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("name");
                        String price = resultSet.getString("price");
                        int farmerId = resultSet.getInt("farmer_id");
                        Blob productBlob = resultSet.getBlob("prod_image");
                        Blob farmerBlob = resultSet.getBlob("prof_image");

                        Bitmap imageBitmap = loadImageFromBlob(productBlob);
                        Bitmap postedByImageBitmap = loadImageFromBlob(farmerBlob);

                        if (imageBitmap == null) {
                            Log.e("DatabaseManager", "imageBitmap is null after processing for product: " + name);
                        }
                        if (postedByImageBitmap == null) {
                            Log.e("DatabaseManager", "postedByImageBitmap is null after processing for product: " + name);
                        }

                        Product product = new Product(
                                id,
                                name,
                                price,
                                farmerId,
                                imageBitmap,
                                postedByImageBitmap
                        );

                        products.add(product);
                    }

                    Log.d("DatabaseManager", "Products fetched: " + products.size());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("DatabaseManager", "Error fetching products: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    Log.e("DatabaseManager", "Error closing connection: " + e.getMessage());
                }
            }
        }

        return products;
    }

    private Bitmap loadImageFromBlob(Blob blob) {
        try (InputStream inputStream = blob.getBinaryStream()) {
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e("DatabaseManager", "Error decoding blob to bitmap: " + e.getMessage());
        }
        return null;
    }


    public static Customer fetchCustomerInfo(String userId) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            String query = "SELECT * FROM customer WHERE customer_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("telephone")
                    );
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    Log.e("DatabaseManager", "Error closing connection: " + e.getMessage());
                }
            }
        }
        return null;
    }
}
