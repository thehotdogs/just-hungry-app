package com.example.just_hungry;

import static android.view.View.*;
import static com.example.just_hungry.Utils.TAG;
import static com.example.just_hungry.Utils.getDeviceLocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
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
import com.example.just_hungry.models.LocationModel;
import com.example.just_hungry.models.PostModel;
import com.example.just_hungry.models.UserModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;import android.transition.TransitionManager;
import android.transition.ChangeBounds;
import androidx.constraintlayout.widget.ConstraintLayout;


public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // ...
    protected Context context;
    protected ArrayList<PostModel> posts;
    protected UserModel resultUser = null;
    protected static final int HEADER_VIEW_TYPE = 0;
    protected static final int ITEM_VIEW_TYPE = 1;
    protected SharedPreferences preferences;
    protected FragmentManager fragmentManager;
    private SharedPreferences sharedPreferences;
    private SparseArray<Boolean> buttonContainerVisibilityStates = new SparseArray<>();


    Utils utilsInstance = Utils.getInstance();

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
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            ImageView profilePictureImageView = headerHolder.itemView.findViewById(R.id.profilePictureImageView);
            sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString("userId", "");
            utilsInstance.getUserById(userId, poster -> {
                if (poster == null) {
                    return;
                }
                String currentUserId = poster.getUserId();
                String userProfileUrl = poster.getProfilePictureUrl().getAssetUrl();
                System.out.println("userProfileUrl: " + userProfileUrl );
                if (!userProfileUrl.equalsIgnoreCase("")) {
                    if (utilsInstance.isNetworkAvailable(context)) {
                        Glide.with(context)
                                .load(userProfileUrl)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
                                .into(profilePictureImageView);
                    }
                }
            });

            return;
        }

        // Common onBindViewHolder logic for item view type
        position = position -1 ;  // Adjust the position for the header view
        PostViewHolder postHolder = (PostViewHolder) holder;
        PostModel targetPost = posts.get(position);

        Boolean isVisible = buttonContainerVisibilityStates.get(position);
        if (isVisible == null || !isVisible) {
            postHolder.buttonContainer.setVisibility(View.GONE);
        } else {
            postHolder.buttonContainer.setVisibility(View.VISIBLE);
        }

        final int finalPosition = position;


        postHolder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //System.out.println("TOGGLED");
                //TransitionManager.beginDelayedTransition((ViewGroup) v.getRootView(), new ChangeBounds());
                //int position = postHolder.getLayoutPosition();
                if (postHolder.buttonContainer.getVisibility() == View.GONE) {
                    postHolder.buttonContainer.setVisibility(View.VISIBLE);
                    buttonContainerVisibilityStates.put(finalPosition, true);

                } else {
                    postHolder.buttonContainer.setVisibility(View.GONE);
                    buttonContainerVisibilityStates.put(finalPosition, false);

                }
//                notifyDataSetChanged();
                // notifyItemChanged(position);
            }
        });
        String userId = preferences.getString("userId", "");
        String postId = targetPost.getPostId();
        String postTitle = targetPost.getStoreName();
        postHolder.joinButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                String postName = targetPost.getStoreName();
                if (postHolder.joinButton.getText().toString().equalsIgnoreCase("Join")) {
                    // Join function call
                    utilsInstance.addUserToPostParticipants(postId,userId, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Joined post: " + postName, Toast.LENGTH_SHORT).show();
                        }
                    });
                    postHolder.joinButton.setText("Leave");

                    Intent grabIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetPost.getGrabFoodUrl()));
                    Log.d(TAG, "onClick: " + targetPost.getGrabFoodUrl());
//                    grabIntent.setPackage("com.grabtaxi.passenger");
                    try {
                        context.startActivity(grabIntent);
                    } catch (Exception e) {
                        Toast.makeText(context, "Grab app not installed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Leave function call
                    utilsInstance.removeUserFromPostParticipants(postId,userId, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Left post" + postName, Toast.LENGTH_SHORT).show();
                        }
                    });
                    postHolder.joinButton.setText("Join");
                }
            }
        });

        postHolder.chatButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new ChatFragment(postId, postTitle)).addToBackStack("BaseRecyclerAdapter").commit();
            }
        });

        // This is where you set the data to the views, assigning values to the views we created in the onCreateViewHolder in recycler view row layout file
        // based on the position of the row
        postHolder.storeName.setText(posts.get(position).getStoreName());

        // halalChip set visibility
        if (posts.get(position).isHalal()) {
            postHolder.halalChip.setVisibility(VISIBLE);
        } else {
            postHolder.halalChip.setVisibility(GONE);
        }


        // text view of cuisine
        String cuisineString = "Cuisine: "+posts.get(position).getCuisine();
        postHolder.textViewCuisine.setText(cuisineString);

        // we only want to show the hours and minutes of the timelimit
        Date timingDate = posts.get(position).getTiming();
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        postHolder.timing.setText(dt.format(timingDate));
        String participantCountString = posts.get(position).getParticipants().size()+"/"+posts.get(position).getMaxParticipants();
        postHolder.participantCount.setText(participantCountString);
        if (posts.get(position).getDateCreated() != null) postHolder.dateCreated.setText(posts.get(position).getDateCreated());
        // holder.participantCount.setText(posts.get(position).getParticipantCount());

        postHolder.gmapsScrenshot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat = String.valueOf(targetPost.getLocation().getLatitude());
                String lon = String.valueOf(targetPost.getLocation().getLongitude());
                String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lon + " (" + targetPost.getStoreName() + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                context.startActivity(intent);
            }
        });

        // get image from firebase db
        if (posts.get(position).getAssets() != null) {
            String asset = String.valueOf(posts.get(position) // get the postModel
                    .getAssets().get(0)); // get the assets arraylist <AssetModel>
            String[] assetArray = asset.split("=");
            String assetUrl = assetArray[Arrays.asList(assetArray).indexOf("assetTitle, assetUrl") + 1];

            if (utilsInstance.isNetworkAvailable(context)) {
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

                utilsInstance.getUserById(posterId, poster -> {
                    if (poster == null) {
                        return;
                    }
                    resultUser = poster;
                    String name = resultUser.getName();
                    String userProfileUrl = resultUser.getProfilePictureUrl().getAssetUrl();
                    if (resultUser != null && !name.equalsIgnoreCase("")) {
                        postHolder.posterName.setText(name);
                        if (utilsInstance.isNetworkAvailable(context)) {
                            Glide.with(context)
                                    .load(userProfileUrl)
                                    .into(postHolder.posterImage);
                        }
                    }
                });
            }
        }catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
        //attach the distance
        postHolder.location.setText(String.valueOf(posts.get(position).distanceFromDevice) + " km away");
        PostModel currPost = posts.get(position);
        LocationModel postLocation = currPost.getLocation();
        String gmapsUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" + postLocation.getLatitude() + ","
                + postLocation.getLongitude() + "&zoom=16&size=520x300&maptype=roadmap&markers=color:red%7Clabel:C%7C"
                + postLocation.getLatitude() + "," + postLocation.getLongitude() + "&key=AIzaSyBMr4Hb8-qc05vI3ScH8Qy85Fc3_PVKA5Q";
        if (utilsInstance.isNetworkAvailable(context)) {
            Glide.with(context)
                    .load(gmapsUrl)
                    .into(postHolder.gmapsScrenshot);
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
        ImageView gmapsScrenshot;

        TextView textViewParticipants;

        Button joinButton;
        Button chatButton;

        Chip halalChip;
        TextView textViewCuisine;

        ImageView profilePictureImageView;



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
            halalChip = itemView.findViewById(R.id.halalChip);
            textViewParticipants = itemView.findViewById(R.id.textViewParticipants);
            textViewCuisine = itemView.findViewById(R.id.textViewCuisine);
            gmapsScrenshot = itemView.findViewById(R.id.gmapsScreenshot);
            profilePictureImageView = itemView.findViewById(R.id.profilePictureImageView);
        }
    }
}
