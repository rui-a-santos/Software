package com.example.softwareproject;

public class User {

    private String firstName;
    private String lastName;
    private String email = null;
    private double weight = 0.0;
    private double lat = 0.0;
    private double lng = 0.0;
    private String id = null;


    public User() {}

    public User(String id, String firstName, String lastName, String email, double weight) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.weight = weight;
        this.lat = 0;
        this.lng = 0;
        this.id = id;
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

    public String getId() { return id; }
}
