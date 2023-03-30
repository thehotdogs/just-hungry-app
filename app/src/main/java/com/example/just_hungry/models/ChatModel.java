package com.example.just_hungry.models;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.internal.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class ChatModel {
    public String chatId;
    public String dateCreated;
    public ArrayList<AssetModel> assets;
    public String message;
    public Boolean read;
    public String senderId;
    public String receiverId;
    SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

    public HashMap<String, Object> getHashMapForFirestore() {
        HashMap<String, Object> output = new HashMap<>();
        output.put("chatId", chatId);
        output.put("dateCreated", dateCreated);
        output.put("assets", assets);
        output.put("message", message);
        output.put("read", read);
        output.put("senderId", senderId);
        output.put("receiverId", receiverId);
        return output;
    }

    public ChatModel(String senderId, String receiverId, String message){
        this.chatId = UUID.randomUUID().toString();
        this.dateCreated = ISO_8601_FORMAT.format(new Date()).toString();
        this.assets = null;
        this.message = message;
        this.read = false;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public ChatModel(QueryDocumentSnapshot queryDocumentSnapshot){
        this.chatId = queryDocumentSnapshot.getString("chatId");
        this.dateCreated = queryDocumentSnapshot.getString("dateCreated");
        this.assets = (ArrayList<AssetModel>) queryDocumentSnapshot.get("assets");
        this.message = queryDocumentSnapshot.getString("message");
        this.read = queryDocumentSnapshot.getBoolean("read");
        this.senderId = queryDocumentSnapshot.getString("senderId");
        this.receiverId = queryDocumentSnapshot.getString("receiverId");
    }
    public ChatModel(HashMap<String, Object> HM){
        this.chatId = (String) HM.get("chatId");
        this.dateCreated = (String) HM.get("dateCreated");
        this.assets = (ArrayList<AssetModel>) HM.get("assets");
        this.message = (String) HM.get("message");
        this.read = (Boolean) HM.get("read");
        this.senderId = (String) HM.get("senderId");
        this.receiverId = (String) HM.get("receiverId");
    }

    //getter and setter
    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    public String getDateCreated() {
        return dateCreated;
    }
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
    public ArrayList<AssetModel> getAssets() {
        return assets;
    }
    public void setAssets(ArrayList<AssetModel> assets) {
        this.assets = assets;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Boolean getRead() {
        return read;
    }
    public void setRead(Boolean read) {
        this.read = read;
    }
    public String getSenderId() {
        return senderId;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    public String getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

}
