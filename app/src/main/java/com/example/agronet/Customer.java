package com.example.agronet;

import static com.example.agronet.DatabaseManager.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Customer extends User{
    int userId;
    String fname;
    String lname;
    String  phone;


    public Customer(int userId, String fname, String lname, String phone) {
        this.userId = userId;
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public String getfName() {
        return fname;
    }

    public void setName(String fname) {
        this.fname = fname;
    }

    public String getlName() {
        return lname;
    }

    public void setlName(String lname) {
        this.lname = lname;
    }

    public String getphone() {
        return phone;
    }

    public void setphone(String  phone) {
        this.phone = phone;
    }

    public void addStar(Farmer farmer) {
        Star star = new Star(this.userId, farmer.getFarmerId(), System.currentTimeMillis());
        farmer.getStars().add(star);
    }

    public void removeStar(Farmer farmer) {
        Star starToRemove = null;
        for (Star star : farmer.getStars()) {
            if (star.getCustomerId() == this.userId) {
                starToRemove = star;
                break;
            }
        }
        if (starToRemove != null) {
            farmer.getStars().remove(starToRemove);
            System.out.println(this.fname + " " + this.lname + " removed a star from " + farmer.getName());
        } else {
            System.out.println("com.example.agronet.Star not found for " + farmer.getName());
        }
    }

    public void updateCustomerInfo(Customer customer) throws SQLException {
        Connection conn = getConnection();
        String query = "UPDATE customer SET first_name = ?, last_name = ?, telephone = ? WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, customer.getfName());
            stmt.setString(2, customer.getlName());
            stmt.setString(3, customer.getphone());
            stmt.setInt(4, customer.getUserId());
            stmt.executeUpdate();
        }
    }
}
