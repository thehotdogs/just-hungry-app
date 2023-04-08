package com.example.just_hungry.new_order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.just_hungry.BaseRecyclerAdapter;
import com.example.just_hungry.R;
import com.example.just_hungry.Utils;
import com.example.just_hungry.models.PostModel;

import java.util.ArrayList;


public class NewOrderRecyclerAdapter extends BaseRecyclerAdapter {

    Utils.OnGetPostByUserDataListener newOrderPostslistener;

    public NewOrderRecyclerAdapter(Context context, ArrayList<PostModel> posts, FragmentManager supportFragmentManager, Utils.OnGetPostByUserDataListener newOrderPostslistener) {
        super(context, posts, supportFragmentManager);
        this.newOrderPostslistener = newOrderPostslistener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == HEADER_VIEW_TYPE) {
            View headerView = inflater.inflate(R.layout.view_header_new_order, parent, false);
            return new NewOrderHeaderViewHolder(headerView);
        } else {
            View itemView = inflater.inflate(R.layout.item_order_post_row, parent, false);
            return new NewOrderPostViewHolder(itemView);
        }
    }

    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        NewOrderHeaderViewHolder headerHolder = (NewOrderHeaderViewHolder) holder;
        headerHolder.newOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("HELLO");
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new NewOrderFormFragment()).commit();

                // Additional logic you had in your original code
            }
        });
    }

    static class NewOrderHeaderViewHolder extends HeaderViewHolder {
        TextView textViewHelloUser;
        TextView textViewFancySomeFood;
        Button newOrderButton;

        public NewOrderHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHelloUser = itemView.findViewById(R.id.textViewNewOrder);
            textViewFancySomeFood = itemView.findViewById(R.id.textViewNewOrderHint);
            newOrderButton = itemView.findViewById(R.id.buttonAddOrder);
        }
    }

    static class NewOrderPostViewHolder extends PostViewHolder {
        // Define specific components for NewOrderRecyclerAdapter here

        public NewOrderPostViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize components here
        }
    }
}
