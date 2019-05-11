package com.example.softwareproject;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable, java.io.Serializable{

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

    protected User(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        weight = in.readDouble();
        lat = in.readDouble();
        lng = in.readDouble();
        id = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeDouble(weight);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(id);
    }
}
