package com.example.just_hungry.your_order;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.just_hungry.BaseRecyclerAdapter;
import com.example.just_hungry.R;
import com.example.just_hungry.Utils;
import com.example.just_hungry.models.PostModel;


import java.util.ArrayList;

public class YourOrderRecyclerAdapter extends BaseRecyclerAdapter {

    Utils utilsInstance = Utils.getInstance();

    Utils.OnGetPostByUserDataListener listener;

    public YourOrderRecyclerAdapter(Context context, ArrayList<PostModel> posts, Utils.OnGetPostByUserDataListener listener, FragmentManager supportFragmentManager) {
        super(context, posts, supportFragmentManager);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == HEADER_VIEW_TYPE) {
            View headerView = inflater.inflate(R.layout.view_header_your_order, parent, false);
            return new YourOrderHeaderViewHolder(headerView);
        } else {
            View itemView = inflater.inflate(R.layout.item_order_post_row, parent, false);
            return new YourOrderPostViewHolder(itemView, listener);
        }
    }
    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        // Add any header-related data binding here if needed, otherwise leave empty
    }

    protected void onBindPostViewHolder(RecyclerView.ViewHolder holder, int position) {
        YourOrderPostViewHolder postHolder = (YourOrderPostViewHolder) holder;
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

        // Add any other specific onBindPostViewHolder logic here
    }

    static class YourOrderHeaderViewHolder extends HeaderViewHolder {
        TextView textViewHelloUser;
        TextView textViewFancySomeFood;

        public YourOrderHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHelloUser = itemView.findViewById(R.id.textViewOrderYouJoined);
            textViewFancySomeFood = itemView.findViewById(R.id.textViewYourOrderHint);
        }
    }

    static class YourOrderPostViewHolder extends PostViewHolder {
        Button joinButton;
        Button chatButton;
        ConstraintLayout buttonContainer;

        public YourOrderPostViewHolder(@NonNull View itemView,  Utils.OnGetPostByUserDataListener listener) {
            super(itemView);
            joinButton = itemView.findViewById(R.id.joinButton);
            chatButton = itemView.findViewById(R.id.chatButton);
            joinButton.setText("Leave");
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences preferences = itemView.getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
                    String userId = preferences.getString("userId", "");

                    Utils utilsInstance = Utils.getInstance();

                    // Call the method here
                    utilsInstance.getAllPostsByUserId(userId, listener);
                }
            });
            buttonContainer = itemView.findViewById(R.id.button_container);

        }
    }
}
