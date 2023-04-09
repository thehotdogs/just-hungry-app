package com.example.just_hungry.browse_order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.just_hungry.BaseRecyclerAdapter;
import com.example.just_hungry.R;
import com.example.just_hungry.Utils;
import com.example.just_hungry.models.PostModel;


import java.util.ArrayList;

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
            View headerView = inflater.inflate(R.layout.view_header_browse_order, parent, false);

            return new PostHeaderViewHolder(headerView);
        } else {
            View itemView = inflater.inflate(R.layout.item_order_post_row, parent, false);
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
