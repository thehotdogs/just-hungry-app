package com.example.just_hungry;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.just_hungry.models.LocationModel;
import com.example.just_hungry.models.ParticipantModel;
import com.example.just_hungry.models.UserModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Utils {
    static final int COARSE_LOCATION_REQUEST_CODE = 100;
    static final String TAG = "UtilsTag";
    static final String UTILS_TAG = "UtilsTag";
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
//    private void GetAllPostsFirestore(final PostActivity.OnGetDataListener listener, String userId) {
//        Task<QuerySnapshot> querySnapshotTask = db.collection("users").whereEqualTo("userId", userId).get();
//        querySnapshotTask.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){
//
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                listener.onSuccess(queryDocumentSnapshots);
//            }
//        });
//    }

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
    public interface OnGetPostByUserDataListener {
        //this is for callbacks
        void onSuccess(List<DocumentSnapshot> dataSnapshotValue);
    }
    public static void getAllPostsByUserId(String userId, OnGetPostByUserDataListener successListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Query the 'users' collection for the user with the specified ID.
        db.collection("posts").whereEqualTo("posterId", userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.isEmpty()) {
                        // User with the specified ID does not exist.
                        successListener.onSuccess(null);
                        return;
                    }
                    // Convert the Firestore document to a User object.
                    List<DocumentSnapshot> results = documentSnapshot.getDocuments();

                    if (results.size() > 0) {
                        // User with the specified ID exists.
                        successListener.onSuccess(results);
                    } else {
                        // User with the specified ID does not exist.
                        successListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors.
                    System.err.println("Error getting posts by user Id " + e.getMessage());
                    successListener.onSuccess(null);
                });
    }
    public static void addUserToPostParticipants(String postId, String userId, OnSuccessListener<Void> successListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ParticipantModel userParticipant = new ParticipantModel(UUID.randomUUID().toString(), userId, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'").toString());

        // retrieve the 'post' document with the specified id
        db.collection("posts").document(postId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // get the participants array from teh document
                    List<Map<String, Object>> participants = (List<Map<String, Object>>) documentSnapshot.get("participants");
                    Boolean userExists = false;

                    for (Map<String, Object> participant:participants) {
                        if (participant.get("userId").equals(userId)) {
                            userExists = true;
                            break;
                        }
                    }

                    // if userExists is false, add the user to the document
                    if (!userExists) {
                        db.collection("posts").document(postId).update("participants", FieldValue.arrayUnion(userParticipant))
                                .addOnSuccessListener(aVoid -> {
                                    successListener.onSuccess(null);
                                })
                                .addOnFailureListener(e -> {
                                    System.err.println("Error adding user to post participants: " + e.getMessage());
                                    successListener.onSuccess(null);
                                });
                    } else {
                        System.out.println("User already exists in the post participants");
                        successListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error retrieving post document: " + e.getMessage());
                    successListener.onSuccess(null);
                });


        // Query the 'users' collection for the user with the specified ID.
//        db.collection("posts").document(postId).update("participants", FieldValue.arrayUnion(participant))
//                .addOnSuccessListener(documentSnapshot -> {
//                    System.out.println(documentSnapshot);
//                    successListener.onSuccess(null);
//                })
//                .addOnFailureListener(e -> {
//                    // Handle any errors.
//                    System.err.println("Error adding user to post participants " + e.getMessage());
//                    successListener.onSuccess(null);
//                });
    }
    public static void removeUserFromPostParticipants(String postId, String userId, OnSuccessListener<Void> successListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Query the 'users' collection for the user with the specified ID.
//        db.collection("posts").document(postId).update("participants", FieldValue.arrayRemove(userId))
//                .addOnSuccessListener(documentSnapshot -> {
//                    successListener.onSuccess(null);
//                })
//                .addOnFailureListener(e -> {
//                    // Handle any errors.
//                    System.err.println("Error removing user from post participants " + e.getMessage());
//                    successListener.onSuccess(null);
//                });

        db.collection("posts").document(postId).get() // get the document with postId
                .addOnSuccessListener(documentSnapshot -> { // if successful in finding
                    // get the participants array from the document
                    List<Map<String, Object>> participants = (List<Map<String,Object>>) documentSnapshot.get("participants");
                    Map<String, Object> matchingParticipant = null;

                    // search for the ParticipantModel with the matching userId
                    for (Map<String, Object> participant: participants){
                        if (participant.get("userId").equals(userId)) {
                            matchingParticipant = participant;
                            break;
                        }
                    }
                    // If a matching ParticipantModel is found, remove it from the 'participants' array.
                    if (matchingParticipant != null) {
                        participants.remove(matchingParticipant);

                        // Update the 'participants' field with the modified array.
                        db.collection("posts").document(postId).update("participants", participants)
                                .addOnSuccessListener(aVoid -> {
                                    successListener.onSuccess(null);
                                })
                                .addOnFailureListener(e -> {
                                    System.err.println("Error updating post participants: " + e.getMessage());
                                    successListener.onSuccess(null);
                                });
                    } else {
                        System.err.println("Matching ParticipantModel not found.");
                        successListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error retrieving post document: " + e.getMessage());
                    successListener.onSuccess(null);
                });
    }
    public static void getAllPostsThatUserJoined(String userId, OnGetPostByUserDataListener successListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Query the 'users' collection for the user with the specified ID.
        db.collection("posts").whereArrayContains("participants", userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.isEmpty()) {
                        // User with the specified ID does not exist.
                        successListener.onSuccess(null);
                        return;
                    }
                    // Convert the Firestore document to a User object.
                    List<DocumentSnapshot> results = documentSnapshot.getDocuments();

                    if (results.size() > 0) {
                        // User with the specified ID exists.
                        successListener.onSuccess(results);
                    } else {
                        // User with the specified ID does not exist.
                        successListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors.
                    System.err.println("Error getting posts by user Id " + e.getMessage());
                    successListener.onSuccess(null);
                });
    }

    public static void deleteOutdatedPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'", Locale.getDefault());

        db.collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            Date currentDate = new Date();
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                String postTimeLimit = documentSnapshot.getString("timeLimit");
                                try {
                                    Log.d(TAG, "deleteOutdatedPosts: Successfully got document: " + documentSnapshot.getId() + " with timeLimit: " + postTimeLimit + ".");
                                    if (postTimeLimit == null) {
                                        Log.d(TAG, "deleteOutdatedPosts: Post with ID: " + documentSnapshot.getId() + " has no timeLimit.");
                                        continue;
                                    }
                                    Date postTimeLimitDate = simpleDateFormat.parse(postTimeLimit);
                                    if (postTimeLimitDate != null && currentDate.after(postTimeLimitDate)) {
                                        // The post has exceeded its time limit.
                                        db.collection("posts").document(documentSnapshot.getId())
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    System.out.println("Post with ID: " + documentSnapshot.getId() + " deleted.");
                                                })
                                                .addOnFailureListener(e -> {
                                                    System.err.println("Error deleting post: " + e.getMessage());
                                                });
                                    }
                                } catch (ParseException e) {
                                    System.err.println("Error parsing timeLimit: " + e.getMessage());
                                }
                            }
                        }
                    } else {
                        System.err.println("Error getting posts: " + task.getException().getMessage());
                    }
                });
    }

    /**
     * This method checks if an Activity has a network connection
     * @param  context a Context object (Context is the superclass of AppCompatActivity
     * @return a boolean object
     */
    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean haveNetwork = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        return haveNetwork;
    }

    // location access
    public static LocationModel getDeviceLocation(Activity activity, FusedLocationProviderClient fusedLocationClient, LocationModel currentLocation){
        System.out.println("FUSED LOCATION CLIENT INSIDE getLastLocation()" + fusedLocationClient);

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation() // 100 is HIGH_ACCURACY
                    .addOnSuccessListener(new OnSuccessListener<Location>() { // try addOnCompleteListener
                        @Override
                        public void onSuccess(Location location) {
                            System.out.println("location object " + location);
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                System.out.println(String.valueOf(latitude) + " " + String.valueOf(longitude));
                                currentLocation.setLatitude(latitude);
                                currentLocation.setLongitude(longitude);
                            }
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("TAG", "Exception: " + e.getMessage());
                            System.out.println("KEGAGALAN HAKIKI " + e.getMessage());
                        }
                    })
            ;
        } else {
            askPermission(activity, fusedLocationClient);
            return getDeviceLocation(activity, fusedLocationClient, currentLocation);
        }
        return currentLocation;
    }
    public static void askPermission(Activity activity, FusedLocationProviderClient fusedLocationClient) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, COARSE_LOCATION_REQUEST_CODE);
    }

}
