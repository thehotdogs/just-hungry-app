package com.example.just_hungry.your_order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.just_hungry.R;
import com.example.just_hungry.Utils;
import com.example.just_hungry.models.PostModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YourOrderFragment extends Fragment {

    public ArrayList<PostModel> posts = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView postRecyclerView;
    YourOrderRecyclerAdapter adapter;
    SharedPreferences preferences;
    Utils.OnGetPostByUserDataListener yourJoinedOrderListener;

    //scrolling stuff
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());

    /** onCreateView mainly handles firestore database post getting
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_order, container, false);

        preferences= getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        postRecyclerView = (RecyclerView) rootView.findViewById(R.id.postRecyclerView);

        // firebase has its own threading operations
        Task<QuerySnapshot> postsQuery = db.collection("posts").get();
        yourJoinedOrderListener = new Utils.OnGetPostByUserDataListener() {
            @Override
            public void onSuccess(List<DocumentSnapshot> dataSnapshotValue) {
                    posts.clear();
                    // create a new posts ArrayList which stores all the PostModel objects
                    if (dataSnapshotValue != null){
                        for (int i = 0; i < dataSnapshotValue.size(); i++) {
                            HashMap<String, Object> post = (HashMap<String, Object>) dataSnapshotValue.get(i).getData();
                            posts.add(new PostModel((DocumentSnapshot) dataSnapshotValue.get(i)));
                            //posts.add(new PostModel(queryDocumentSnapshots.getDocuments().get(i).getData()));
                            System.out.println(dataSnapshotValue.get(i).getData());
                        }
                    }
                    adapter = new YourOrderRecyclerAdapter(rootView.getContext(), posts, yourJoinedOrderListener, getParentFragmentManager());
                    System.out.println("SETTING UP ADAPTER DONE" + posts);
                    postRecyclerView.setLayoutManager(mLayoutManager);
                    postRecyclerView.setAdapter(adapter);
            }
        };
        String userId = preferences.getString("userId", "");
        final SwipeRefreshLayout pullToRefresh = rootView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Utils.getAllPostsThatUserJoined(userId, yourJoinedOrderListener); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
        Utils.getAllPostsThatUserJoined(userId, yourJoinedOrderListener);
        return rootView;
    }

}