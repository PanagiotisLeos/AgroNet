package com.example.agronet;

public class Star {
    private int customerId;
    private int farmerId;
    private long createdAt;

    public Star(int customerId, int farmerId, long createdAt) {
        this.customerId = customerId;
        this.farmerId = farmerId;
        this.createdAt = createdAt;
    }

    // Getters
    public int getCustomerId() {
        return customerId;
    }

    public int getFarmerId() {
        return farmerId;
    }

}

