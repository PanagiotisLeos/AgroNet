package com.example.agronet;
public class Farmer {
    private String name;
    private String location;
    private String id;
    private int imageId;
    private String farmerType;

    public Farmer(String name, String location, int imageId, String farmerType
    ) {
        this.name = name;
        this.location = location;
        this.imageId = imageId;
        this.farmerType = farmerType;

    }

    // Getters and setters for each field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) { this.imageId = imageId; }

    public String getFarmerType() {
        return farmerType;
    }

    public void setFarmerType(String farmerType) {
        this.farmerType = farmerType;
    }

}
