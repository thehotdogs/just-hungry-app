package com.example.just_hungry;

import android.util.Log;

import com.example.just_hungry.models.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.concurrent.Future;

public class Utils {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static Utils instance;

    public Utils(){

    }

    public static synchronized Utils getInstance() {
        if (null != instance){
            return instance;
        } else {
            instance = new Utils();
            return instance;
        }
    }
    private interface OnGetDataListener {
        //this is for callbacks
        void onSuccess(QuerySnapshot dataSnapshotValue);
    }
    private void GetAllPostsFirestore(final PostActivity.OnGetDataListener listener, String userId) {
        Task<QuerySnapshot> querySnapshotTask = db.collection("users").whereEqualTo("userId", userId).get();
        querySnapshotTask.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listener.onSuccess(queryDocumentSnapshots);
            }
        });
    }

//    public static UserModel getUserInstanceFromId(String userId){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference docRef = db.collection("users").document(userId);
//        DocumentSnapshot docSnapshot = docRef.get().getResult();
//
//        try {
//            Query query = db.collection("users").whereEqualTo("userId", userId);
//            Future<QuerySnapshot> querySnapshot = query.get();
//            if (doc.exists()) {
//                UserModel user = doc.toObject(UserModel.class);
//                return user;
//            }
//            else {
//                return null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static void getUserById(String userId, OnSuccessListener<UserModel> successListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Query the 'users' collection for the user with the specified ID.
        db.collection("users").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.isEmpty()) {
                        // User with the specified ID does not exist.
                        successListener.onSuccess(null);
                        return;
                    }
                    // Convert the Firestore document to a User object.
                    DocumentSnapshot result = documentSnapshot.getDocuments().get(0);
                    UserModel user = new UserModel(result);
                    if (user != null) {
                        // User with the specified ID exists.
                        successListener.onSuccess(user);
                    } else {
                        // User with the specified ID does not exist.
                        successListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors.
                    System.err.println("Error getting user by ID: " + e.getMessage());
                    successListener.onSuccess(null);
                });
    }
}
