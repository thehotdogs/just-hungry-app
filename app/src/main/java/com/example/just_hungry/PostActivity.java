package com.example.just_hungry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import com.example.just_hungry.models.PostModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    public ArrayList<PostModel> posts = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView postRecyclerView;
    PostRecyclerAdapter adapter;

    //scrolling stuff
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_posts_main);
        postRecyclerView = (RecyclerView) findViewById(R.id.postRecyclerView);
        Task<QuerySnapshot> postsQuery = db.collection("posts").get();
        OnGetDataListener listener = new OnGetDataListener() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                System.out.println("QuerySnapshot: " + queryDocumentSnapshots);
                posts.clear();
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    HashMap<String, Object> post = (HashMap<String, Object>) queryDocumentSnapshots.getDocuments().get(i).getData();
                    posts.add(new PostModel((DocumentSnapshot) queryDocumentSnapshots.getDocuments().get(i)));
                    //posts.add(new PostModel(queryDocumentSnapshots.getDocuments().get(i).getData()));
                    System.out.println(queryDocumentSnapshots.getDocuments().get(i).getData());
                }
                adapter = new PostRecyclerAdapter(PostActivity.this, posts);
                System.out.println("SETTONG UP ADAPTER DONE" + posts);
                postRecyclerView.setLayoutManager(mLayoutManager);
                postRecyclerView.setAdapter(adapter);
            }
        };

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetAllPostsFirestore(listener); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
        GetAllPostsFirestore(listener);

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
}