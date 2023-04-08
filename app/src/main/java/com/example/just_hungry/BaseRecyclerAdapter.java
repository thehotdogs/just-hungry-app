package com.example.just_hungry;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.just_hungry.chat.ChatFragment;
import com.example.just_hungry.models.PostModel;
import com.example.just_hungry.models.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // ...
    protected Context context;
    protected ArrayList<PostModel> posts;
    protected UserModel resultUser = null;
    protected static final int HEADER_VIEW_TYPE = 0;
    protected static final int ITEM_VIEW_TYPE = 1;
    protected SharedPreferences preferences;
    protected FragmentManager fragmentManager;

    public BaseRecyclerAdapter(Context context, ArrayList<PostModel> posts, FragmentManager supportFragmentManager) {
        this.context = context;
        this.posts = posts;
        this.preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        this.fragmentManager = supportFragmentManager;
    }

    protected abstract void onBindHeaderViewHolder(RecyclerView.ViewHolder holder);

    @NonNull
    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == HEADER_VIEW_TYPE) {
            onBindHeaderViewHolder(holder);
            return;
        }

        // Common onBindViewHolder logic for item view type
        position = position -1 ;  // Adjust the position for the header view
        PostViewHolder postHolder = (PostViewHolder) holder;
        PostModel targetPost = posts.get(position);
        postHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //System.out.println("TOGGLED");
                if (postHolder.buttonContainer.getVisibility() == View.GONE) {
                    postHolder.buttonContainer.setVisibility(View.VISIBLE);
                } else {
                    postHolder.buttonContainer.setVisibility(View.GONE);
                }
            }
        });
        String userId = preferences.getString("userId", "");
        String postId = targetPost.getPostId();
        postHolder.joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String postName = targetPost.getStoreName();
                if (postHolder.joinButton.getText().toString().equalsIgnoreCase("Join")) {
                    // Join function call
                    Utils.addUserToPostParticipants(postId,userId, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Joined post" + postName, Toast.LENGTH_SHORT).show();
                        }
                    });

                    postHolder.joinButton.setText("Leave");
                } else {
                    // Leave function call
                    Utils.removeUserFromPostParticipants(postId,userId, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Left post" + postName, Toast.LENGTH_SHORT).show();
                        }
                    });
                    postHolder.joinButton.setText("Join");
                }
            }
        });

        int finalPosition = position;
        postHolder.chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //!TODO UNCOMMENT FOR CHAT
                //Toast.makeText(context, "Chat button clicked", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new ChatFragment(postId)).commit();
//                Intent intent = new Intent(context, ChatActivity.class);
//                intent.putExtra("invitationId", posts.get(finalPosition).getPosterId());
//                context.startActivity(intent);
            }
        });

        // This is where you set the data to the views, assigning values to the views we created in the onCreateViewHolder in recycler view row layout file
        // based on the position of the row
        postHolder.storeName.setText(posts.get(position).getStoreName());

        // we only want to show the hours and minutes of the timelimit
        Date timingDate = posts.get(position).getTiming();
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        postHolder.timing.setText(dt.format(timingDate));
        if (posts.get(position).getLocation() != null) postHolder.location.setText(posts.get(position).getLocation().getStringLocation());
        if (posts.get(position).getDateCreated() != null) postHolder.dateCreated.setText(posts.get(position).getDateCreated());
        // holder.participantCount.setText(posts.get(position).getParticipantCount());

        // get image from firebase db

        if (posts.get(position).getAssets() != null) {
            String asset = String.valueOf(posts.get(position) // get the postModel
                    .getAssets().get(0)); // get the assets arraylist <AssetModel>
            String[] assetArray = asset.split("=");
            String assetUrl = assetArray[Arrays.asList(assetArray).indexOf("assetTitle, assetUrl") + 1];

            if (Utils.isNetworkAvailable(context)) {
                Glide.with(context)
                        .load(assetUrl)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
                        .into(postHolder.postImage);
            }
        }
        try {
            if (posts.get(position).getPosterId() != null) {
                String posterId = posts.get(position).getPosterId();

                Utils.getUserById(posterId, poster -> {
                    if (poster == null) {
                        return;
                    }
                    resultUser = poster;
                    String name = resultUser.getName();
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
        // the recycler vie just wants to know the number of rows you want to display (add 1 for header)
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
    protected static class HeaderViewHolder extends RecyclerView.ViewHolder {
//        public TextView textViewHelloUser;
//        public TextView textViewFancySomeFood;

        public HeaderViewHolder(View itemView) {
            super(itemView);
//            textViewHelloUser = itemView.findViewById(R.id.textViewHelloUser);
//            textViewFancySomeFood = itemView.findViewById(R.id.textViewFancySomeFood);
        }
    }

    protected static class PostViewHolder extends RecyclerView.ViewHolder{
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
        ConstraintLayout buttonContainer;

        Button joinButton;
        Button chatButton;


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
            joinButton = itemView.findViewById(R.id.joinButton);
            chatButton = itemView.findViewById(R.id.chatButton);
//            joinButton.setVisibility(View.GONE);
//            chatButton.setVisibility(View.GONE);
            buttonContainer = itemView.findViewById(R.id.button_container);
        }
    }
}