package com.example.agronet;

public class Customer extends User{
    String userId;
    String fname;
    String lname;
    String email;


    public Customer(String userId, String fname, String lname) {
        this.userId = userId;
        this.fname = fname;
        this.lname = lname;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
