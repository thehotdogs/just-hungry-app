package com.example.just_hungry.chat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.just_hungry.R;
import com.example.just_hungry.Utils;
import com.example.just_hungry.chat.MessageAdapter;
import com.example.just_hungry.models.ChatModel;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText messageEditText;
    private ImageButton sendMessageButton;

    private TextView textTitle;

    private FirebaseFirestore firestore;
    private MessageAdapter adapter;
    private List<ChatModel> chatList = new ArrayList<>();
    private String postId;
    private String postTitle;

    private ImageButton backImageButton;

    public ChatFragment(String postId, String postTitle) {
        this.postId = postId;
        this.postTitle = postTitle;
    }

    Utils utilsInstance = Utils.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendMessageButton = view.findViewById(R.id.sendMessageButton);
        textTitle = view.findViewById(R.id.titletext);
        backImageButton = view.findViewById(R.id.backImageButton);

        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestore.getInstance();
        adapter = new MessageAdapter(chatList);

        textTitle.setText("Chat Room: " + postTitle);


        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    messageEditText.setText("");
                }
            }
        });

        Utils.OnGetChatFromPostIdListener chatFetchListener = dataSnapshotValue -> {
            chatList.clear();
            if (dataSnapshotValue != null) {
                //System.out.println("this is what i get from listener " + dataSnapshotValue);
                ArrayList<HashMap> chats = (ArrayList<HashMap>) dataSnapshotValue.get(0).get("chats");
                if (chats == null || chats.size() == 0) {
                    return;
                }
                for (HashMap hm : chats) {
                    ChatModel chat = new ChatModel(hm);
                    chatList.add(chat);
                }

                adapter = new MessageAdapter(chatList);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.scrollToPosition(chatList.size() - 1);
            }
        };
        loadMessages(chatFetchListener);
        utilsInstance.getAllChatsInsidePost(postId, chatFetchListener);
        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadMessages(Utils.OnGetChatFromPostIdListener chatFetchListener) {
        firestore.collection("posts")
                .orderBy("dateCreated", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                        //System.out.println("on change event is trigered" + value.getDocumentChanges());
                        if (error != null) {
                            // Handle the error
                            return;
                        }

                        for (DocumentChange change : value.getDocumentChanges()) {
                            //System.out.println("for change:" + change.getDocument().getData());
                            ArrayList<DocumentSnapshot> out = new ArrayList<>();
                            out.add(change.getDocument());
                            chatFetchListener.onSuccess(out);
//                            if (change.getType() == DocumentChange.Type.ADDED) {
//                                // System.out.println("ADDED SOMETHING in chats!!" + change.getDocument().getData());
//                                //ChatModel chat = change.getDocument().toObject(ChatModel.class);
//                                ChatModel chat = new ChatModel(change.getDocument());
//                                chatList.add(chat);
//                                adapter.notifyDataSetChanged();
//                                chatFetchListener.onSuccess(null);
//                                recyclerView.scrollToPosition(chatList.size() - 1);
//                            }
                        }
                    }
                });
    }
    private void sendMessage(String message) {
        // Implement the code to send a message
        SharedPreferences preferences = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String senderId = preferences.getString("userId", "");
        //String senderId = "your_sender_id";
        String receiverId = this.postId;
        ChatModel newChat = new ChatModel(senderId, receiverId, message);
        String chatId = newChat.getChatId();
        firestore.collection("posts").document(this.postId).update("chats", FieldValue.arrayUnion(newChat.getHashMapForFirestore()))
                .addOnSuccessListener(documentSnapshot -> {
                    //System.out.println(documentSnapshot);

                })
                .addOnFailureListener(e -> {
                    // Handle any errors.
                    System.err.println("Error adding user to post participants " + e.getMessage());
                });
        //System.out.println("PUSHED TO FIRESTORE");
    }
}