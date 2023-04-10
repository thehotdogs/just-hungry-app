package com.example.just_hungry;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.just_hungry.activities.MainActivity;
import com.example.just_hungry.browse_order.PostQueryBuilder;
import com.example.just_hungry.models.LocationModel;
import com.example.just_hungry.models.UserModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {
    public static final int COARSE_LOCATION_REQUEST_CODE = 100;
    public static final String TAG = "UtilsTag";
    public static final String UTILS_TAG = "UtilsTag";
    public FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    public static double calculateDistance(LocationModel deviceLocation, LocationModel location) {
        Location device = new Location("");
        device.setLatitude(deviceLocation.getLatitude());
        device.setLongitude(deviceLocation.getLongitude());

        Location postLocation = new Location("");
        postLocation.setLatitude(location.getLatitude());
        postLocation.setLongitude(location.getLongitude());

        double distanceInMeters = device.distanceTo(postLocation);
        double distanceInKilometers = distanceInMeters / 1000.0;

        // Round the distance to at most 2 decimal points
        double roundedDistance = Math.round(distanceInKilometers * 100.0) / 100.0;

        return roundedDistance;
    }


    public static void saveLocationToSharedPreferencesAndFirestore(Activity activity, LocationModel locationModel) {
        // Save location to SharedPreferences
        SharedPreferences sharedPreferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latitude", String.valueOf(locationModel.getLatitude()));
        editor.putString("longitude", String.valueOf(locationModel.getLongitude()));
        editor.apply();

        String userId = sharedPreferences.getString("userId", "");
        // Update the location in Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.update("location", locationModel.getHashMapForFirestore())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Location updated successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating location", e));
    }


    public interface OnGetDataListener {
        //this is for callbacks
        void onSuccess(QuerySnapshot dataSnapshotValue);

//        void onFailure(Exception exception);
    }

    
    public static void getAllPostsFirestore(final OnGetDataListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Timestamp currentTime = new Timestamp(new Date());

        // Query the "posts" collection for posts with "timing" field greater than the current time
        Query query = db.collection("posts").whereGreaterThan("timing", currentTime);
        Task<QuerySnapshot> querySnapshotTask = query.get();
        querySnapshotTask.addOnSuccessListener(queryDocumentSnapshots -> listener.onSuccess(queryDocumentSnapshots));
    }
    public interface OnGetPostByUserDataListener {
        //this is for callbacks
        void onSuccess(List<DocumentSnapshot> dataSnapshotValue);
    }
    public interface OnGetChatFromPostIdListener {
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

        // Query the 'posts' collection for the post with the specified ID.
        db.collection("posts").document(postId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    ArrayList<String> participants = (ArrayList<String>) documentSnapshot.get("participants");
                    int currentSize = participants.size();
                    int maxSize = Integer.parseInt(documentSnapshot.get("maxParticipants").toString());

                    if (currentSize >= maxSize) {
                        // Capacity is full
                        System.err.println("Error adding user to post participants: capacity is full");
                        successListener.onSuccess(null);
                    } else {
                        // Add the user to the 'participants' array.
                        db.collection("posts").document(postId).update("participants", FieldValue.arrayUnion(userId))
                                .addOnSuccessListener(aVoid -> {
                                    successListener.onSuccess(null);
                                })
                                .addOnFailureListener(e -> {
                                    // Handle any errors.
                                    System.err.println("Error adding user to post participants " + e.getMessage());
                                    successListener.onSuccess(null);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors.
                    System.err.println("Error getting post document: " + e.getMessage());
                    successListener.onSuccess(null);
                });

//        // Query the 'users' collection for the user with the specified ID.
//        db.collection("posts").document(postId).update("participants", FieldValue.arrayUnion(userId))
//                .addOnSuccessListener(documentSnapshot -> {
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
        db.collection("posts").document(postId).update("participants", FieldValue.arrayRemove(userId))
                .addOnSuccessListener(documentSnapshot -> {
                    successListener.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    // Handle any errors.
                    System.err.println("Error removing user from post participants " + e.getMessage());
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

    public static void getAllChatsInsidePost(String postId, OnGetChatFromPostIdListener successListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Query the 'users' collection for the user with the specified ID.
        db.collection("posts").whereEqualTo("postId", postId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.isEmpty()) {
                        // User with the specified ID does not exist.
                        successListener.onSuccess(null);
                        return;
                    }
                    List<DocumentSnapshot> results = documentSnapshot.getDocuments();

                    if (results.size() > 0) {
                        successListener.onSuccess(results);
                    } else {
                        successListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors.
                    System.err.println("Error getting posts by user Id " + e.getMessage());
                    successListener.onSuccess(null);
                });
    }

    /**
     * This method checks if an Activity has a network connection
     * @param  context a Context object (Context is the superclass of AppCompatActivity
     * @return a boolean object
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean haveNetwork = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        return haveNetwork;
    }

    public static void getDeviceLocation(Activity activity, OnSuccessListener<LocationModel> onSuccessListener) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LocationModel currentLocation = new LocationModel(latitude, longitude);
                            onSuccessListener.onSuccess(currentLocation);
                        } else {
                            Log.e("TAG", "Location is null");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TAG", "Exception: " + e.getMessage());
                    });
        } else {
            askPermission(activity, fusedLocationClient);
        }
    }

    public static double distFrom(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lon2-lon1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }

    public static void askPermission(Activity activity, FusedLocationProviderClient fusedLocationClient) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, COARSE_LOCATION_REQUEST_CODE);
    }

    public static int distFrom(LocationModel loc1, LocationModel loc2) {
        double earthRadius = 6371; // kilometers

        double lat1 = loc1.getLatitude();
        double lat2 = loc2.getLatitude();
        double lon1 = loc1.getLongitude();
        double lon2 = loc2.getLongitude();

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lon2-lon1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return (int) dist;
    }

}
