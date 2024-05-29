package com.example.agronet;

public class Star {
    private int customerId;
    private int farmerId;

    public Star(int customerId, int farmerId, long createdAt) {
        this.customerId = customerId;
        this.farmerId = farmerId;
    }

    // Getters
    public int getCustomerId() {
        return customerId;
    }

    public int getFarmerId() {
        return farmerId;
    }

}

