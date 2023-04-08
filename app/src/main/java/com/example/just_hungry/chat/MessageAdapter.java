package com.example.just_hungry.chat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.just_hungry.R;
import com.example.just_hungry.Utils;
import com.example.just_hungry.models.ChatModel;
import com.example.just_hungry.models.UserModel;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<ChatModel> chatList;
    private LayoutInflater inflater;

    public MessageAdapter(List<ChatModel> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        View view = inflater.inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        System.out.println("CHATLIST"+ chatList.get(0).message);
        ChatModel chat = chatList.get(position);

        // Load the sender's name and profile picture
        // TODO You can use a library like Glide or Picasso to load the profile image from a URL
//        holder.senderNameTextView.setText(getUserByUserId(chat.getSenderId()).getName());
        Utils.getUserById(chat.getSenderId(), poster -> {
            if (poster == null) {
                return;
            }
            UserModel resultUser = null;
            resultUser = poster;
            String name = resultUser.getName();
            String userProfileUrl = resultUser.getProfilePictureUrl().getAssetUrl();
            if (!name.equalsIgnoreCase("")) {
                holder.senderNameTextView.setText(resultUser.getName());
                if (Utils.isNetworkAvailable(inflater.getContext())) {
                    Glide.with(inflater.getContext())
                            .load(userProfileUrl)
                            .into(holder.profileImageView);
                }
            }
        });

        // Set the message text
        System.out.println("onbind view holder, CHAT GET MESSAGE: " + chat.getMessage());
        holder.messageTextView.setText(chat.getMessage());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImageView;
        TextView senderNameTextView;
        TextView messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            senderNameTextView = itemView.findViewById(R.id.senderNameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
