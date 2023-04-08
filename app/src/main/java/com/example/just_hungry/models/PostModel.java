package com.example.just_hungry.models;

import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class PostModel {
    public String postId;
    public String posterId;
    public String dateCreated;
    public String timing;
    public ArrayList<String> participants;
    public ArrayList<AssetModel> assets;
    public LocationModel location;
    public String storeName;

    public Integer maxParticipants;
    SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
    public String cuisine;
    public String grabFoodUrl;
    public boolean isHalal;

    public PostModel() {
        //set to default value
        this.postId = UUID.randomUUID().toString();
        this.posterId = UUID.randomUUID().toString();
        this.dateCreated =ISO_8601_FORMAT.format(new Date()).toString();
        this.timing = "oi";
        this.participants = new ArrayList<String>();
        this.assets = new ArrayList<AssetModel>();
        this.location = new LocationModel();
        this.storeName = "oi";
        this.maxParticipants = 10;
        this.cuisine = "western";
        this.grabFoodUrl = "https://www.grabfood.com.sg/";
        this.isHalal = false;
        // TODO add the collection point and lcoationsutd
    }
    public PostModel(String posterId, String postId) {
        //set to default value
        this.postId = postId;
        this.posterId = posterId;
        this.dateCreated =ISO_8601_FORMAT.format(new Date()).toString();
        this.timing = postId;
        this.participants = new ArrayList<String>();
        this.assets = new ArrayList<AssetModel>();
        this.assets.add(new AssetModel());
        this.assets.add(new AssetModel());
        this.location = new LocationModel();
        this.storeName = postId;
        this.maxParticipants = 10;
        this.cuisine = "western";
        this.grabFoodUrl = "https://www.grabfood.com.sg/";
        this.isHalal = false;
    }

    public PostModel(String postId, String posterId, String dateCreated, String timing, ArrayList<String> participants, ArrayList<AssetModel> assets,
                     LocationModel location, String storeName, Integer maxParticipants, String cuisine, String grabFoodUrl, boolean isHalal) {
        this.postId = postId;
        this.posterId = posterId;
        this.dateCreated = dateCreated;
        this.timing = timing;
        this.participants = participants;
        this.assets = assets;
        this.location = location;
        this.storeName = storeName;
        this.maxParticipants = maxParticipants;
        this.cuisine = cuisine;
        this.grabFoodUrl = grabFoodUrl;
        this.isHalal = isHalal;
    }

    public PostModel(DocumentSnapshot documentSnapshot) {
        this.postId = documentSnapshot.getString("postId");
        this.posterId = documentSnapshot.getString("posterId");
        this.dateCreated = documentSnapshot.getString("dateCreated");
        //if (documentSnapshot.getDate("timing") != null) this.timing = documentSnapshot.getDate("timing");
        this.participants = (ArrayList<String>) documentSnapshot.get("participants");
        this.assets = (ArrayList<AssetModel>) documentSnapshot.get("assets");
        this.location = new LocationModel((HashMap<String, Double>) documentSnapshot.get("location"));
        this.storeName = documentSnapshot.getString("storeName");
        if (documentSnapshot.getLong("maxParticipants") != null) this.maxParticipants = Math.toIntExact((documentSnapshot.getLong("maxParticipants")));
        else this.maxParticipants = 10;
        this.cuisine = documentSnapshot.getString("cuisine");
        this.grabFoodUrl = documentSnapshot.getString("grabFoodUrl");
        this.isHalal = Boolean.TRUE.equals(documentSnapshot.getBoolean("isHalal"));
    }

    public HashMap<String, Object> getHashMapForFirestore() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postId", this.postId);
        hashMap.put("posterId", this.posterId);
        hashMap.put("dateCreated", this.dateCreated);
        hashMap.put("timing", this.timing);
        hashMap.put("participants", this.participants);
        hashMap.put("assets", this.assets);
        hashMap.put("location", this.location.getHashMapForFirestore());
        hashMap.put("storeName", this.storeName);
        hashMap.put("maxParticipants", this.maxParticipants);
        hashMap.put("cuisine", this.cuisine);
        hashMap.put("grabFoodUrl", this.grabFoodUrl);
        hashMap.put("isHalal", this.isHalal);
        return hashMap;
    }

    // getters and setters
    public String getPostId() {
        return postId;
    }
    public String getPosterId() {
        return posterId;
    }
    public String getDateCreated() {
        return dateCreated;
    }
    public String getTiming() {
        return timing;
    }
    public ArrayList<String> getParticipants() {
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
    public String getCuisine() {
        return cuisine;
    }
    public String getGrabFoodUrl() {
        return grabFoodUrl;
    }
    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }
    public void setGrabFoodUrl(String grabFoodUrl) {
        this.grabFoodUrl = grabFoodUrl;
    }
    public void setPostId(String postId) {
        this.postId = postId;
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
    public void setParticipants(ArrayList<String> participants) {
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

    public boolean isHalal() {
        return isHalal;
    }

    public void setHalal(boolean halal) {
        isHalal = halal;
    }
}




