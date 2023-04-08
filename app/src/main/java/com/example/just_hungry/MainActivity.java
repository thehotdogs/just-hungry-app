package com.example.just_hungry;

import static com.example.just_hungry.Utils.deleteOutdatedPosts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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
            deleteOutdatedPosts();
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
            default:
                return false;
        }

        fragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    }

}
