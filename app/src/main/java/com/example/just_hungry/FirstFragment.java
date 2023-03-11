package com.example.just_hungry;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//public class FirstFragment extends Fragment {
//
//    private RecyclerView recyclerView;
//    private FirebaseFirestore db;
//    private FirestoreRecyclerAdapter<Post, PostViewHolder> adapter;
//    public class Asset {
//        private String imageUrl;
//        private String title;
//
//        public String getImageUrl() {
//            return imageUrl;
//        }
//
//    }
//
//    public class Post {
//        private String title;
//        private String description;
//        private String time;
//        private Asset[] assets;
//        private String id;
//
//        public String getTitle() {
//            return title;
//        }
//
//        public Asset[] getAssets() {
//            return assets;
//        }
//
//        public String getDescription() {
//            return description;
//        }
//
//        public String getTime() {
//            return time;
//        }
//
//        public String getId() {
//            return id;
//        }
//    }
////    public class PostViewHolder extends RecyclerView.ViewHolder {
////        private TextView title;
////        private TextView description;
////        private TextView time;
////        private ImageView image;
////        private StorageReference storageReference;
////        public PostViewHolder(@NonNull View itemView) {
////            super(itemView);
////            title = itemView.findViewById(R.id.title);
////            description = itemView.findViewById(R.id.description);
////            time = itemView.findViewById(R.id.time);
////            image = itemView.findViewById(R.id.image);
////            storageReference = FirebaseStorage.getInstance().getReference();
////        }
////        public void bind(Post post) {
////            title.setText(post.getTitle());
////            description.setText(post.getDescription());
////            time.setText(post.getTime());
////            Asset[] assets = post.getAssets();
////            Glide.with(image.getContext())
////                    .load(assets[0].getImageUrl())
////                    .into(image);
////        }
////    }
//
//
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_first, container, false);
//
//        recyclerView = view.findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        db = FirebaseFirestore.getInstance();
//
//        Query query = db.collection("posts").orderBy("time");
//
//        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
//                .setQuery(query, Post.class)
//                .build();
//
//        adapter = new FirestoreRecyclerAdapter<Post, PostViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post post) {
//                holder.bind(post);
//            }
//
//            @NonNull
//            @Override
//            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
//                return new PostViewHolder(view);
//            }
//        };
//
//        recyclerView.setAdapter(adapter);
//
//        return view;
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }
//
//    private class PostViewHolder extends RecyclerView.ViewHolder {
//        private ImageView imageView;
//        private TextView titleView;
//        private ImageView posterImageView;
//        private TextView posterNameView;
//        private TextView timeView;
//        private TextView locationView;
//        private TextView storeNameView;
//
//        public PostViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.image_view);
//            titleView = itemView.findViewById(R.id.title_view);
//            posterImageView = itemView.findViewById(R.id.poster_image_view);
//            posterNameView = itemView.findViewById(R.id.poster_name_view);
//            timeView = itemView.findViewById(R.id.time_view);
////            locationView = itemView.findViewById(R.id.location_view);
////            storeNameView = itemView.findViewById(R.id.store_name_view);
//        }
//
//        public void bind(Post post) {
//            // 1. Image of the post
//            if (post.getAssets() != null && post.getAssets().length > 0) {
//                Glide.with(itemView)
//                        .load(post.getAssets()[0].getImageUrl())
//                        .into(imageView);
//            }
//
//            // 2. Title of the post
//            titleView.setText(post.getTitle());
//
//            // 3. Poster of the posts
////            db.collection("users").document(post.getId()).get().addOnCompleteListener(task -> {
////                if (task.isSuccessful() && task.getResult() != null) {
////                    User user = task.getResult().toObject(User.class);
////                    if (user != null) {
////                        posterNameView.setText(user.getName());
////                        Glide.with(itemView)
////                                .load(user.getProfileImageUrl())
////                                .into(posterImageView);
////                    }
////                }
////            });
//
//            // 4. Time
//            timeView.setText(post.getTime());
//
//            //
//        }
//    }
//}



public class FirstFragment extends Fragment {
    public class Post {
        private String title;
        private List<Asset> assets;
        private User user;
        private String time;
        private String location;
        private String storeName;
        private String userId;
        private String id;

        public Post() {
            //set to default value
            this.title = "judul";
            Asset asset1 = new Asset();
            this.assets = new ArrayList<>();
            this.assets.add(asset1);
            this.assets.add(asset1);
            this.user = new User();
            this.time = "besok";
            this.location = "pasar";
            this.storeName = "sebelah";
            this.userId = "oi";
            this.id = "sdfgsdfg";
        }

        // getters and setters
        public String getTitle() {
            return title;
        }

        public List<Asset> getAssets() {
            return assets;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

//        public String getDescription() {
//            return description;
//        }

        public String getTime() {
            return time;
        }

        public String getId() {
            return id;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String getLocation() {
            return location;
        }

        public String getStoreName() {
            return storeName;
        }

        public User getUser() {
            return user;
        }

    }

    public class Asset {
        private String imageUrl;
        private String title;

        public Asset() {
            //set to default value
            this.imageUrl = "https://www.astronauts.id/blog/wp-content/uploads/2022/08/Makanan-Khas-Daerah-tiap-Provinsi-di-Indonesia-Serta-Daerah-Asalnya.jpg";
        }

        // getters and setters
        public String getImageUrl() {
            return imageUrl;
        }

        public String getTitle() {
            return title;
        }
    }

    public class User {
        private String name;
        private String profilePictureUrl;

        public User() {
            //set to default value
            this.name = "nama";
            this.profilePictureUrl = "https://www.astronauts.id/blog/wp-content/uploads/2022/08/Makanan-Khas-Daerah-tiap-Provinsi-di-Indonesia-Serta-Daerah-Asalnya.jpg";
        }

        // getters and setters
        public String getName() {
            return name;
        }

        public String getProfilePictureUrl() {
            return profilePictureUrl;
        }
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        recyclerView = view.findViewById(R.id.postsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();



        // Query the "posts" collection in Firestore and retrieve the data
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    System.out.println("queryDocumentSnapshots.size() = " + queryDocumentSnapshots.size());
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        System.out.println("queryDocumentSnapshot = " + documentSnapshot);
                        Post post = documentSnapshot.toObject(Post.class);
                        System.out.println("post} " + post.getTitle());
                        // Get the user who posted this post
                        db.collection("users").document(post.getUserId())
                                .get()
                                .addOnSuccessListener(userDocumentSnapshot -> {
                                    User user = userDocumentSnapshot.toObject(User.class);
                                    post.setUser(user);
                                    postList.add(post);
                                    postAdapter.notifyDataSetChanged();
                                });
                    }
                });
        Post post = new Post();
        postList.add(post);
        postList.add(post);
        postList.add(post);
        postList.add(post);
        postList.add(post);

        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        return view;
    }

    // ViewHolder class for the PostAdapter
    private static class PostViewHolder extends RecyclerView.ViewHolder {
        private final ImageView postImageView;
        private final TextView titleTextView;
        private final ImageView posterImageView;
        private final TextView posterNameTextView;
        private final TextView timeTextView;
        private final TextView locationTextView;
        private final TextView storeNameTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            postImageView = itemView.findViewById(R.id.postImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
            posterNameTextView = itemView.findViewById(R.id.posterNameTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            storeNameTextView = itemView.findViewById(R.id.storeNameTextView);
        }

        public void bind(Post post) {
            // Load the first image from the array of assets
            if (post.getAssets() != null && post.getAssets().size() > 0) {
                String imageUrl = post.getAssets().get(0).getImageUrl();
//                GlideUrl glideUrl = new GlideUrl(imageUrl, new LazyHeaders.Builder()
//                        .addHeader("User-Agent",
//                                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit / 537.36(KHTML, like Gecko) Chrome  47.0.2526.106 Safari / 537.36")
//                        .build());
//                Glide.with(itemView.getContext())
//                        .load(glideUrl)
//                        .into(postImageView);
                Picasso.get()
                        .load(imageUrl)
                        .into(postImageView);
            }

            titleTextView.setText(post.getTitle());

            // Set the poster's name and profile picture
            if (post.getUser() != null) {
                posterNameTextView.setText(post.getUser().getName());
                Glide.with(itemView.getContext())
                        .load(post.getUser().getProfilePictureUrl())
                        .into(posterImageView);

            }

            // Set the time and location of the post
            timeTextView.setText(post.getTime());
            locationTextView.setText(post.getLocation());

            // Set the store name
            storeNameTextView.setText(post.getStoreName());
        }
    }

    // RecyclerView adapter for displaying the list of posts
    private class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
        private List<Post> postList;

        public PostAdapter(List<Post> postList) {
            this.postList = postList;
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_item, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            Post post = postList.get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            return postList.size();
        }
    }
}