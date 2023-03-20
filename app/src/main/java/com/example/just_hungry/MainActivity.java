package com.example.just_hungry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostsFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.action_posts);
        }
    }
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = new PostsFragment();

        switch (item.getItemId()) {
            case R.id.action_posts:
                selectedFragment = new PostsFragment();
                break;
            case R.id.action_order:
                //selectedFragment = new OrderFragment();
                break;
            case R.id.action_account:
                //selectedFragment = new AccountFragment();
                break;
            default:
                return false;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    }

    public void onClickHelloWorldButton(View view) {
        // give a toast message
        Toast.makeText(this, "Hello World!", Toast.LENGTH_SHORT).show();
    }
}