package com.example.just_hungry.browse_order;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.example.just_hungry.PostsByDistanceComparator;
import com.example.just_hungry.R;
import com.example.just_hungry.Utils;
import com.example.just_hungry.models.LocationModel;
import com.example.just_hungry.models.PostModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
    ToggleButton chipHalalOnly;
    Spinner spinnerCuisineFilter;
    RecyclerView postRecyclerView;
    PostRecyclerAdapter adapter;

    //scrolling stuff
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
    private final static int COARSE_LOCATION_REQUEST_CODE = 100;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationModel deviceLocation = new LocationModel(0, 0); // instantiate on SUTD coordinates
    FragmentManager fragmentManager;
    Activity activity;

//
    public PostsFragment(FragmentManager fragmentManager) {
        // Required empty public constructor
        this.fragmentManager = fragmentManager;
    }

    public PostsFragment() {

    }


    /** onCreateView mainly handles firestore database post getting
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_order, container, false);

        postRecyclerView = (RecyclerView) rootView.findViewById(R.id.postRecyclerView);
        chipHalalOnly = (ToggleButton) rootView.findViewById(R.id.chipHalalFilter);
        spinnerCuisineFilter = (Spinner) rootView.findViewById(R.id.spinnerCuisineFilter);

        // firebase has its own threading operations
        Task<QuerySnapshot> postsQuery = db.collection("posts").get();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        deviceLocation = Utils.getDeviceLocation(this.getActivity(), fusedLocationProviderClient, deviceLocation);

        Utils.OnGetDataListener listener = queryDocumentSnapshots -> {
//            System.out.println("QuerySnapshot: " + queryDocumentSnapshots);
            posts.clear();
            // create a new posts ArrayList which stores all the PostModel objects
            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                HashMap<String, Object> post = (HashMap<String, Object>) queryDocumentSnapshots.getDocuments().get(i).getData();
                posts.add(new PostModel((DocumentSnapshot) queryDocumentSnapshots.getDocuments().get(i)));
                //posts.add(new PostModel(queryDocumentSnapshots.getDocuments().get(i).getData()));
//                System.out.println(queryDocumentSnapshots.getDocuments().get(i).getData());
            }
            Collections.sort(posts, new PostsByDistanceComparator(deviceLocation));


            System.out.println("SETTING UP ADAPTER DONE" + posts);
            adapter = new PostRecyclerAdapter(rootView.getContext(), posts, fragmentManager);
            postRecyclerView.setLayoutManager(mLayoutManager);
            postRecyclerView.setAdapter(adapter);

        };

        final SwipeRefreshLayout pullToRefresh = rootView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Utils.getAllPostsFirestore(listener); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
        Utils.getAllPostsFirestore(listener);
        return rootView;
    }


    // FIREBASE STACK OVER FLOW STUFF

}