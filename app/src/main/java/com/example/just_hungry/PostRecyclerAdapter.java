package com.example.just_hungry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.PostViewHolder> {
    Context context;
    ArrayList<PostModel> posts;
    //constructor
    public PostRecyclerAdapter(Context context, ArrayList<PostModel> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostRecyclerAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where you inflate the layout (giving look to our rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_row, parent, false);
        return new PostRecyclerAdapter.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostRecyclerAdapter.PostViewHolder holder, int position) {
        // This is where you set the data to the views, assigning values to the views we created in the onCreateViewHolder in recycler vioew row layout file
        // based on the position of the row
        holder.storeName.setText(posts.get(position).getStoreName());
        holder.time.setText(posts.get(position).getTime());
        holder.posterName.setText("budi");
    }

    @Override
    public int getItemCount() {
        // This is where you return the number of rows
        // the recycler vie just wants to know the number of rows you want to display
        return posts.size();
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder{
        // This is where you declare the views you want to use in the recycler view row layout file
        // grabbing the views from our post row layout file kinda like onCreate method

        ImageView postImage;
        TextView storeName;
        TextView time;
        TextView location;
        TextView posterName;
        TextView userImage;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            // This is where you initialize the views
            postImage = itemView.findViewById(R.id.postImage);
            storeName = itemView.findViewById(R.id.storeNameText);
            time = itemView.findViewById(R.id.timeText);
            posterName = itemView.findViewById(R.id.posterName);

        }
    }
}
