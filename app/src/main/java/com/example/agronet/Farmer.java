package com.example.agronet;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Farmer extends User implements Parcelable {

    int id;
    String fname;
    String lname;
     String location;
     byte[] profile_img;
     String farmerType;
     String description;
    private List<Star> stars;


    protected Farmer(Parcel in) {
        id = in.readInt();
        fname = in.readString();
        lname = in.readString();
        location = in.readString();
        profile_img = in.createByteArray();
        farmerType = in.readString();
        description = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(fname);
        dest.writeString(lname);
        dest.writeString(location);
        dest.writeByteArray(profile_img);
        dest.writeString(farmerType);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Farmer> CREATOR = new Creator<Farmer>() {
        @Override
        public Farmer createFromParcel(Parcel in) {
            return new Farmer(in);
        }

        @Override
        public Farmer[] newArray(int size) {
            return new Farmer[size];
        }
    };

    public Farmer(int id, String fname, String lname, String location, byte[] profile_img, String farmerType, String description) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.location = location;
        this.profile_img = profile_img;
        this.farmerType = farmerType;
        this.description = description;
        this.stars = new ArrayList<>();
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

    public int getFarmerId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getProfileImg() {
        return profile_img;
    }

    public void setProfileImg(byte[] profile_img) {
        this.profile_img = profile_img;
    }

    public String getFarmerType() {
        return farmerType;
    }

    public void setFarmerType(String farmerType) {
        this.farmerType = farmerType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Star> getStars() {
        return stars;
    }



}
