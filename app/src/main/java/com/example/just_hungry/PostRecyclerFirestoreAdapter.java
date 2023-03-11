//package com.example.just_hungry;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//
//import java.util.ArrayList;
//
//public class PostRecyclerFirestoreAdapter {
//    Query query = FirebaseFirestore.getInstance()
//            .collection("posts")
//            .orderBy("timestamp")
//            .limit(50);
//    FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
//            .setQuery(query, PostModel.class)
//            .build();
//    ArrayList<PostModel> posts;
//
//    FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<PostModel, PostViewHolder>(options) {
////        @Override
////        protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostModel posts) {
////            holder.storeName.setText(posts.get(position).getStoreName());
////            holder.time.setText(posts.get(position).getTime());
////            holder.posterName.setText("budi");
////        }
//
//        @Override
//        protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostModel model) {
//            holder.storeName.setText(posts.get(position).getStoreName());
//            holder.time.setText(posts.get(position).getTime());
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
//            return new PostRecyclerFirestoreAdapter.PostViewHolder(view);
//        }
//    };
//
//}
