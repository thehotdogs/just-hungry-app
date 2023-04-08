package com.example.just_hungry;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.just_hungry.models.AssetModel;
import com.example.just_hungry.models.PostModel;
import com.example.just_hungry.models.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;

import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PostRecyclerAdapter extends BaseRecyclerAdapter {

    public PostRecyclerAdapter(Context context, ArrayList<PostModel> posts, FragmentManager supportFragmentManager) {
        super(context, posts, supportFragmentManager);
    }
    public PostRecyclerAdapter(Context context, ArrayList<PostModel> posts, Utils.OnGetPostByUserDataListener listener, FragmentManager supportFragmentManager) {
        super(context, posts, supportFragmentManager);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == HEADER_VIEW_TYPE) {
            View headerView = inflater.inflate(R.layout.post_header_view, parent, false);
            return new PostHeaderViewHolder(headerView);
        } else {
            View itemView = inflater.inflate(R.layout.post_row, parent, false);
            return new PostPostViewHolder(itemView);
        }
    }

    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        PostHeaderViewHolder headerHolder = (PostHeaderViewHolder) holder;
        String name = preferences.getString("name", "");
        headerHolder.textViewHelloUser.setText("Hi, " + name + "!");
    }

    static class PostHeaderViewHolder extends HeaderViewHolder {
        TextView textViewHelloUser;

        public PostHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHelloUser = itemView.findViewById(R.id.textViewHelloUser);
        }
    }

    static class PostPostViewHolder extends PostViewHolder {
        // Define specific components for PostRecyclerAdapter here

        public PostPostViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize components here
        }
    }
}
