package com.example.agronet;

public class Customer extends User{
    int userId;
    String fname;
    String lname;
    String phone;


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
        return fname;
    }

    public void setlame(String lname) {
        this.lname = lname;
    }

    public String getphone() {
        return phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }
}
