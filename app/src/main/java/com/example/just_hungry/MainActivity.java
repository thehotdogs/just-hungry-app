package com.example.just_hungry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.just_hungry.models.LocationModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {



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
            case R.id.action_addorder:
                selectedFragment = new AddOrderFragment();
                break;
            case R.id.action_yourorder:
                selectedFragment = new YourOrderFragment();
                break;
            default:
                return false;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        deviceLocationRetriever = new DeviceLocationRetriever();
//
//        if (requestCode == 100) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                currentDeviceLocation = deviceLocationRetriever.getLastLocation(this, fusedLocationClient);
//            } else {
//                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }



}
