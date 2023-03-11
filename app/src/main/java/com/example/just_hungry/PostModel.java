package com.example.just_hungry;

import java.util.ArrayList;
import java.util.List;

public class PostModel {
    private String title;
    private ArrayList<AssetModel> assets;
    private UserModel user;
    private String time;
    private String location;
    private String storeName;
    private String userId;
    private String id;

    public PostModel() {
        //set to default value
        this.title = "judul";
        this.assets = new ArrayList<AssetModel>();
        this.time = "besok";
        this.location = "pasar";
        this.storeName = "sebelah";
        this.userId = "oi";
        this.id = "sdfgsdfg";
    }
    public PostModel(String storeName) {
        //set to default value
        this.title = "judul";
        this.assets = new ArrayList<AssetModel>();
        this.time = "besok";
        this.location = "pasar";
        this.storeName = storeName;
        this.userId = "oi";
        this.id = "sdfgsdfg";
    }

    // getters and setters
    public String getTitle() {
        return title;
    }

    public ArrayList<AssetModel> getAssets() {
        return assets;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

//        public String getDescription() {
//            return description;
//        }

    public String getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getLocation() {
        return location;
    }

    public String getStoreName() {
        return storeName;
    }

    public UserModel getUser() {
        return user;
    }

}




