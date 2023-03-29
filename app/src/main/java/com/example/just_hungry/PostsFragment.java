package com.example.just_hungry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.just_hungry.models.LocationModel;
import com.example.just_hungry.models.PostModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PostsFragment extends Fragment {

    public ArrayList<PostModel> posts = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView postRecyclerView;
    PostRecyclerAdapter adapter;

    //scrolling stuff
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
    private final static int COARSE_LOCATION_REQUEST_CODE = 100;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationModel deviceLocation = new LocationModel(1.3402320075948917, 103.96296752039913); // instantiate on SUTD coordinates



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_main, container, false);


        postRecyclerView = (RecyclerView) rootView.findViewById(R.id.postRecyclerView);


//        // Get current activity
//        Activity currActivity = (Activity) getActivity();
//
//        // FusedLocationProvider client
//        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(currActivity);
//
//        // Get Device Location Here
//        DeviceLocation currDeviceLoc = new DeviceLocation();
//        currDeviceLoc.getLastLocation(currActivity, fusedLocationClient); // must accept activity input
//        System.out.println("LOCATION OF LATITUDE AND LONGITUDE " + String.valueOf(currDeviceLoc.getLatitude()) + " " + String.valueOf(currDeviceLoc.getLongitude()));


        // GET POSTS HERE, SO NEED TO LIMIT THE QUERY IN THIS SPACE
        Task<QuerySnapshot> postsQuery = db.collection("posts").get();


        // Location Calls
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());


        OnGetDataListener listener = new OnGetDataListener() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                System.out.println("QuerySnapshot: " + queryDocumentSnapshots);
//                deviceLocation = getDeviceLocation(PostsFragment.this.getActivity(), fusedLocationProviderClient, deviceLocation);
                posts.clear();
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    HashMap<String, Object> post = (HashMap<String, Object>) queryDocumentSnapshots.getDocuments().get(i).getData();
                    posts.add(new PostModel((DocumentSnapshot) queryDocumentSnapshots.getDocuments().get(i)));
                    //posts.add(new PostModel(queryDocumentSnapshots.getDocuments().get(i).getData()));
                    System.out.println(queryDocumentSnapshots.getDocuments().get(i).getData());
                }

                // sort posts based on distance smallest to largest
//                Collections.sort(posts, new PostsByDistanceComparator(deviceLocation));

                adapter = new PostRecyclerAdapter(rootView.getContext(), posts);
                System.out.println("SETTONG UP ADAPTER DONE" + posts);
                postRecyclerView.setLayoutManager(mLayoutManager);
                postRecyclerView.setAdapter(adapter);
            }
        };

        final SwipeRefreshLayout pullToRefresh = rootView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetAllPostsFirestore(listener); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
        GetAllPostsFirestore(listener);
        return rootView;
    }


    // FIREBASE STACK OVER FLOW STUFF
    public interface OnGetDataListener {
        //this is for callbacks
        void onSuccess(QuerySnapshot dataSnapshotValue);
    }
    public void GetAllPostsFirestore(final OnGetDataListener listener) {
        Task<QuerySnapshot> querySnapshotTask = db.collection("posts").get();
        querySnapshotTask.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listener.onSuccess(queryDocumentSnapshots);
            }
        });
    }



    // location access
    public LocationModel getDeviceLocation(Activity activity, FusedLocationProviderClient fusedLocationClient, LocationModel currentLocation){
        System.out.println("FUSED LOCATION CLIENT INSIDE getLastLocation()" + fusedLocationClient);

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(	100, null) // 100 is HIGH_ACCURACY
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

    public void askPermission(Activity activity, FusedLocationProviderClient fusedLocationClient) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, COARSE_LOCATION_REQUEST_CODE);
    }
}