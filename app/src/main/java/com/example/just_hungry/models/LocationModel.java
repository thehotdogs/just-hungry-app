package com.example.just_hungry.models;

import java.util.HashMap;

public class LocationModel {

    public double latitude;
    public double longitude;

    public LocationModel() {
        //set to default value
        this.latitude = 0;
        this.longitude = 0;
    }
    public LocationModel(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public LocationModel(HashMap<String, Double> input){
        this.latitude = Double.parseDouble(String.valueOf(input.get("latitude")));
        this.longitude = Double.parseDouble(String.valueOf(input.get("longitude")));
    }
    // getter and setter methods
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String getStringLocation() {
       return ("Latitude: " + latitude + " Longitude: " + longitude);
    }
}
