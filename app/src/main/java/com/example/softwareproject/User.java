package com.example.softwareproject;

public class User {

    public String firstName;
    public String lastName;
    public String email = null;
    public double weight = 0;

    public User() {}

    public User(String firstName, String lastName, String email, double weight) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.weight = weight;
    }
}
