package com.example.just_hungry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    public ArrayList<PostModel> posts = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView postRecyclerView;
    Query query = FirebaseFirestore.getInstance()
            .collection("posts")
            .orderBy("timestamp")
            .limit(50);
    FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
            .setQuery(query, PostModel.class)
            .build();

    PostRecyclerAdapter adapter;
//    FirestoreRecyclerAdapter adapterFirestore = new FirestoreRecyclerAdapter<PostModel, PostViewHolder>(options) {
//
//        @Override
//        protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostModel post) {
//            System.out.println(post);
//            System.out.println("YESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSKDJLKFJKLJFL:JLKJLJFL:JFKLJFL:KJFL:J");
//            holder.storeName.setText(post.getStoreName());
//            holder.time.setText(post.getTime());
//            holder.posterName.setText("budi");
//        }
//
//        @Override
//        public PostViewHolder onCreateViewHolder(ViewGroup group, int i) {
//            // Create a new instance of the ViewHolder, in this case we are using a custom
//            // layout called R.layout.message for each item
//            View view = LayoutInflater.from(group.getContext())
//                    .inflate(R.layout.post_row, group, false);
//
//            return new PostViewHolder(view);
//        }
//    };


//    private class PostViewHolder extends RecyclerView.ViewHolder{
//        // This is where you declare the views you want to use in the recycler view row layout file
//        // grabbing the views from our post row layout file kinda like onCreate method
//
//        ImageView postImage;
//        TextView storeName;
//        TextView time;
//        TextView location;
//        TextView posterName;
//        TextView userImage;
//
//
//        public PostViewHolder(@NonNull View itemView) {
//            super(itemView);
//            // This is where you initialize the views
//            postImage = itemView.findViewById(R.id.postImage);
//            storeName = itemView.findViewById(R.id.storeNameText);
//            time = itemView.findViewById(R.id.timeText);
//            posterName = itemView.findViewById(R.id.posterName);
//
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_posts_main);
        postRecyclerView = (RecyclerView) findViewById(R.id.postRecyclerView);
        setUpPosts();
        Task<QuerySnapshot> postsQuery = db.collection("posts").get();
        readData(postsQuery, new OnGetDataListener() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                System.out.println("QuerySnapshot: " + queryDocumentSnapshots);
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    HashMap<String, Object> post = (HashMap<String, Object>) queryDocumentSnapshots.getDocuments().get(i).getData();
                    posts.add(new PostModel(post.get("storeName").toString()));
                    //posts.add(new PostModel(queryDocumentSnapshots.getDocuments().get(i).getData()));
                    System.out.println(queryDocumentSnapshots.getDocuments().get(i).getData());
                }
                adapter = new PostRecyclerAdapter(PostActivity.this, posts);
                System.out.println("SETTONG UP ADAPTER DONE"+ posts);
                postRecyclerView.setLayoutManager(new LinearLayoutManager(PostActivity.this));
                postRecyclerView.setAdapter(adapter);
            }
        });
        System.out.println("POSTSSSSSSSSS"+ posts);
        //adapter = new PostRecyclerAdapter(this, posts);




        //Button listeningButton = (Button) findViewById(R.id.buttonlisten);


    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        adapterFirestore.startListening();
//    }
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapterFirestore.stopListening();
//    }


    private void setUpPosts(){
        //System.out.println("POSTSSSSSSSS"+ posts);
//        for (int i = 0; i < 5; i++) {
//            posts.add(new PostModel());
//        }
//        db.collection("posts")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        System.out.println("QuerySnapshot: " + queryDocumentSnapshots);
//                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
//                            HashMap<String, Object> post = (HashMap<String, Object>) queryDocumentSnapshots.getDocuments().get(i).getData();
//                            posts.add(new PostModel(post.get("storeName").toString()));
//                            //posts.add(new PostModel(queryDocumentSnapshots.getDocuments().get(i).getData()));
//                            System.out.println(queryDocumentSnapshots.getDocuments().get(i).getData());
//                        }
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(PostActivity.this, "", Toast.LENGTH_SHORT).show();.makeText(PostActivity.this, "Error querying Firestore", Toast.LENGTH_SHORT).show();
//                    }
//                });

    }


    // FIREBASE STACK OVER FLOW STUFF
    public interface OnGetDataListener {
        //this is for callbacks
        void onSuccess(QuerySnapshot dataSnapshotValue);
    }
    public void readData(Task<QuerySnapshot> querySnapshotTask, final OnGetDataListener listener) {

        querySnapshotTask.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listener.onSuccess(queryDocumentSnapshots);
            }
        });
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                // This is how you use the value once it is loaded! Make sure to pass the
//                // value of the DataSnapshot object, not the object itself (this was the
//                // original answerer's mistake!
//                listener.onSuccess(dataSnapshot.getValue());
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {}
//        });
    }

}