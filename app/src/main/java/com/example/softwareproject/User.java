package com.example.softwareproject;

public class User {

    public String firstName;
    public String lastName;
    public String email = null;
    public double weight = 0.0;
    public double lat = 0.0;
    public double lng = 0.0;


    public User() {}

    public User(String firstName, String lastName, String email, double weight) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.weight = weight;
        this.lat = 0;
        this.lng = 0;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
