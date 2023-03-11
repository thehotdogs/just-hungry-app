package com.example.just_hungry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.just_hungry.models.PostModel;
import com.example.just_hungry.models.UserModel;
import com.firebase.ui.auth.data.model.User;

import java.util.ArrayList;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.PostViewHolder> {
    Context context;
    ArrayList<PostModel> posts;
    UserModel resultUser = null;
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
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        // This is where you set the data to the views, assigning values to the views we created in the onCreateViewHolder in recycler vioew row layout file
        // based on the position of the row
        holder.storeName.setText(posts.get(position).getStoreName());
        holder.timing.setText(posts.get(position).getTiming());
        if (posts.get(position).getLocation() != null) holder.location.setText(posts.get(position).getLocation().getStringLocation());
        //holder.posterName.setText(posts.get(position).getPosterName());
        if (posts.get(position).getDateCreated() != null) holder.dateCreated.setText(posts.get(position).getDateCreated());
//        holder.participantCount.setText(posts.get(position).getParticipantCount());
        //UserModel user = new UserModel();

        if (posts.get(position).getPosterId() != null) {
            String posterId = posts.get(position).getPosterId();
            System.out.println("POSTER ID: " + posterId);
            Utils.getUserById(posterId, poster -> {
                resultUser = poster;
                if (resultUser != null) {
                    holder.posterName.setText(resultUser.getName());
                }
            });
        }

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
