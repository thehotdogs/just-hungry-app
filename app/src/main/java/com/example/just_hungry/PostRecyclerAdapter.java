package com.example.just_hungry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.just_hungry.models.PostModel;
import com.example.just_hungry.models.UserModel;

import java.util.ArrayList;

public class PostRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<PostModel> posts;
    UserModel resultUser = null;
    private static final int HEADER_VIEW_TYPE = 0;
    private static final int ITEM_VIEW_TYPE = 1;

    //constructor
    public PostRecyclerAdapter(Context context, ArrayList<PostModel> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // This is where you inflate the layout (giving look to our rows)
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == HEADER_VIEW_TYPE) {
            View headerView = inflater.inflate(R.layout.post_header_view, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View itemView = inflater.inflate(R.layout.post_row, parent, false);
            return new PostRecyclerAdapter.PostViewHolder(itemView);
        }
//
//        // This is where you inflate the layout (giving look to our rows)
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.post_row, parent, false);
//        return new PostRecyclerAdapter.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == HEADER_VIEW_TYPE) {
            // No need to set any data for the header view
            return;
        }
        position = position -1 ;  // Adjust the position for the header view
        // This is where you set the data to the views, assigning values to the views we created in the onCreateViewHolder in recycler view row layout file
        // based on the position of the row
        PostViewHolder postHolder = (PostViewHolder) holder;
        postHolder.storeName.setText(posts.get(position).getStoreName());
        postHolder.timing.setText(posts.get(position).getTiming());
        if (posts.get(position).getLocation() != null) postHolder.location.setText(posts.get(position).getLocation().getStringLocation());
        //holder.posterName.setText(posts.get(position).getPosterName());
        if (posts.get(position).getDateCreated() != null) postHolder.dateCreated.setText(posts.get(position).getDateCreated());
//        holder.participantCount.setText(posts.get(position).getParticipantCount());
        //UserModel user = new UserModel();
        // get image from firebase db
        if (Utils.isNetworkAvailable(context)) {
            Glide.with(context)
                    .load("https://preview.redd.it/8sjtjrlmkru41.png?auto=webp&s=ee505e75337336992bb0be14e5ec98978c14f406")
                    .into(postHolder.postImage);
        }
        try {
            if (posts.get(position).getPosterId() != null) {
                String posterId = posts.get(position).getPosterId();
//                System.out.println("POSTER ID: " + posterId);

                Utils.getUserById(posterId, poster -> {
                    if (poster == null) {
                        System.out.println("POSTER IS NULL");
                        return;
                    }
                    resultUser = poster;
//                    System.out.println("RESULT USER: " + resultUser);
                    String name = resultUser.getName();
//                    System.out.println("RESULT USERNAME: " + name);
                        if (resultUser != null && !name.equalsIgnoreCase("")) {
                            postHolder.posterName.setText(name);
                        }
                    });
                }
        }catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

    }

    @Override
    public int getItemCount() {
        // This is where you return the number of rows
        // the recycler vie just wants to know the number of rows you want to display
        return posts.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_VIEW_TYPE;
        } else {
            return ITEM_VIEW_TYPE;
        }
    }
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewHelloUser;
        public TextView textViewFancySomeFood;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            textViewHelloUser = itemView.findViewById(R.id.textViewHelloUser);
            textViewFancySomeFood = itemView.findViewById(R.id.textViewFancySomeFood);
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        // This is where you declare the views you want to use in the recycler view row layout file
        // grabbing the views from our post row layout file kinda like onCreate method

        ImageView postImage;
        TextView storeName;
        TextView timing;
        TextView location;
        TextView posterName;
        ImageView posterImage;
        TextView dateCreated;
        TextView participantCount;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            // This is where you initialize the views
            postImage = itemView.findViewById(R.id.postImage);
            storeName = itemView.findViewById(R.id.storeNameCardText);
            timing = itemView.findViewById(R.id.timingCardText);
            posterName = itemView.findViewById(R.id.posterNameCardText);
            location = itemView.findViewById(R.id.locationCardText);
            posterImage = itemView.findViewById(R.id.posterCardImage);
            dateCreated = itemView.findViewById(R.id.dateCreatedCardText);
            participantCount = itemView.findViewById(R.id.participantCountCardText);

        }
    }
}
