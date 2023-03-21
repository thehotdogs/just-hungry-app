package com.example.just_hungry.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class PostModel {
    private String posterId;
    private String dateCreated;
    private String timing;
    private ArrayList<ParticipantModel> participants;
    private ArrayList<AssetModel> assets;
    private LocationModel location;
    private String storeName;
    private Integer maxParticipants;
    SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

    public PostModel() {
        //set to default value
        this.posterId = UUID.randomUUID().toString();
        this.dateCreated =ISO_8601_FORMAT.format(new Date()).toString();
        this.timing = "oi";
        this.participants = new ArrayList<ParticipantModel>();
        this.assets = new ArrayList<AssetModel>();
        this.location = new LocationModel();
        this.storeName = "oi";
        this.maxParticipants = 10;
    }

    public PostModel(String posterId, String dateCreated, String timing, ArrayList<ParticipantModel> participants, ArrayList<AssetModel> assets, LocationModel location, String storeName, Integer maxParticipants) {
        this.posterId = posterId;
        this.dateCreated = dateCreated;
        this.timing = timing;
        this.participants = participants;
        this.assets = assets;
        this.location = location;
        this.storeName = storeName;
        this.maxParticipants = maxParticipants;
    }

    public PostModel(DocumentSnapshot documentSnapshot) {
        this.posterId = documentSnapshot.getString("posterId");
        this.dateCreated = documentSnapshot.getString("dateCreated");
        this.timing = documentSnapshot.getString("timing");
        this.participants = (ArrayList<ParticipantModel>) documentSnapshot.get("participants");
        this.assets = (ArrayList<AssetModel>) documentSnapshot.get("assets");
        this.location = (LocationModel) documentSnapshot.get("location");
        this.storeName = documentSnapshot.getString("storeName");
        if (documentSnapshot.getLong("maxParticipants") != null) this.maxParticipants = Math.toIntExact((documentSnapshot.getLong("maxParticipants")));
        else this.maxParticipants = 10;
    }

    // getters and setters
    public String getPosterId() {
        return posterId;
    }
    public String getDateCreated() {
        return dateCreated;
    }
    public String getTiming() {
        return timing;
    }
    public ArrayList<ParticipantModel> getParticipants() {
        return participants;
    }
    public ArrayList<AssetModel> getAssets() {
        return assets;
    }
    public LocationModel getLocation() {
        return location;
    }
    public String getStoreName() {
        return storeName;
    }
    public Integer getMaxParticipants() {
        return maxParticipants;
    }
    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
    public void setTiming(String timing) {
        this.timing = timing;
    }
    public void setParticipants(ArrayList<ParticipantModel> participants) {
        this.participants = participants;
    }
    public void setAssets(ArrayList<AssetModel> assets) {
        this.assets = assets;
    }
    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
    public void setLocation(LocationModel location) {
        this.location = location;
    }
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }






}




