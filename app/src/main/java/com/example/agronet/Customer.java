package com.example.agronet;

public class Customer extends User{
    int userId;
    String fname;
    String lname;
    String email;


    public Customer(int userId, String fname, String lname) {
        this.userId = userId;
        this.fname = fname;
        this.lname = lname;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
