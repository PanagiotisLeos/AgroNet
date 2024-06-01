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
    int total_stars;
    boolean customerHasStarred;

    protected Farmer(Parcel in) {
        id = in.readInt();
        fname = in.readString();
        lname = in.readString();
        location = in.readString();
        profile_img = in.createByteArray();
        farmerType = in.readString();
        description = in.readString();
        total_stars = in.readInt();
        customerHasStarred = in.readByte() != 0;


        if (profile_img == null || profile_img.length == 0) {
            // Set a placeholder for default image
            profile_img = new byte[] {0};
        }
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
        dest.writeInt(total_stars);
        dest.writeByte((byte) (customerHasStarred ? 1 : 0));
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

    public Farmer(int id, String fname, String lname, String location, byte[] profile_img, String farmerType, String description, int total_stars, boolean customerHasStarred) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.location = location;
        this.profile_img = (profile_img == null || profile_img.length == 0) ? new byte[] {0} : profile_img;  // Use placeholder if null
        this.farmerType = farmerType;
        this.description = description;
        this.stars = new ArrayList<>();
        this.total_stars = total_stars;
        this.customerHasStarred = customerHasStarred;
    }

    // Getters and setters
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

    public void setProfileImg(byte[] profileImg) {
        this.profile_img = (profileImg == null || profileImg.length == 0) ? new byte[] {0} : profileImg;  // Use placeholder if null
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

    public int getTotalStars() {
        return total_stars;
    }

    public void setTotalStars(int total_stars) {
        this.total_stars = total_stars;
    }

    public boolean isCustomerHasStarred() {
        return customerHasStarred;
    }

    public void setCustomerHasStarred(boolean customerHasStarred) {
        this.customerHasStarred = customerHasStarred;
    }
}
