package com.example.just_hungry.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.just_hungry.account.AccountManagementFragment;
import com.example.just_hungry.new_order.NewOrderFragment;
import com.example.just_hungry.browse_order.PostsFragment;
import com.example.just_hungry.R;
import com.example.just_hungry.Utils;
import com.example.just_hungry.your_order.YourOrderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        ImageView top_nav_imageview = findViewById(R.id.top_navbar_imageview);

        if (savedInstanceState == null) {
            // instantiate the PostsFragment fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new PostsFragment(fragmentManager))
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.action_posts);
        }
        // Load image using glide into top nav bar imageview
        if (Utils.isNetworkAvailable(this)) {
            Glide.with(this)
                    .load("https://preview.redd.it/8sjtjrlmkru41.png?auto=webp&s=ee505e75337336992bb0be14e5ec98978c14f406")
                    .into(top_nav_imageview);
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = new PostsFragment(fragmentManager);

        switch (item.getItemId()) {
            case R.id.action_posts:
                selectedFragment = new PostsFragment(fragmentManager);
                break;
            case R.id.action_addorder:
                selectedFragment = new NewOrderFragment();
                break;
            case R.id.action_yourorder:
                selectedFragment = new YourOrderFragment();
                break;
            case R.id.action_account_management: // Add this case
                selectedFragment = new AccountManagementFragment();
                break;
            default:
                return false;
        }

        fragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    }

}
