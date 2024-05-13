package com.example.agronet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.sql.Blob;

public class Farmer extends User {
    int id;
    String fname;
    String lname;
    private String location;
    private byte[] profile_img;
    private String farmerType;
    private String description;

    public Farmer(String fname,String lname, String location, byte[] profile_img, String farmerType, String description
    ) {
        this.fname = fname;
        this.lname = lname;
        this.location = location;
        this.profile_img = profile_img;
        this.farmerType = farmerType;
        this.description = description;

    }

    // Getters and setters for each field
    public String getName() {
        return fname;
    }

    public void setName(String fname) {
        this.fname = fname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getprofile_img() {
        return profile_img;
    }
    public void setprofile_img(byte[] profile_img) { this.profile_img = profile_img; }

    public String getFarmerType() {
        return farmerType;
    }

    public void setFarmerType(String farmerType) {
        this.farmerType = farmerType;
    }

    public String getDescription() {
        return description;
    }


}
