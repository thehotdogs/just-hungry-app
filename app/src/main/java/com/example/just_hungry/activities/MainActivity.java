package com.example.just_hungry.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.just_hungry.NewOrderWorker;
import com.example.just_hungry.account.AccountManagementFragment;
import com.example.just_hungry.new_order.NewOrderFragment;
import com.example.just_hungry.browse_order.PostsFragment;
import com.example.just_hungry.R;
import com.example.just_hungry.Utils;
import com.example.just_hungry.your_order.YourOrderFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();
    private static final String LAST_CHECK_KEY = "last_check";

    private Handler handler;
    private Runnable runnable;

    private void checkForUpdates() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        long lastCheck = preferences.getLong(LAST_CHECK_KEY, 0);
        Date lastCheckDate = new Date(lastCheck);

        Query newOrdersQuery = db.collection("posts").whereGreaterThanOrEqualTo("dateCreated", lastCheckDate);
        newOrdersQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Send notification for new orders
                    sendNotification(querySnapshot.size());
                }
            }
        });

        // Update last check time
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(LAST_CHECK_KEY, new Date().getTime());
        editor.apply();
    }
    private void startCheckingForUpdates() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                checkForUpdates();
                System.out.println("Checking for updates");
                handler.postDelayed(this, 5000);
                sendNotification(new Random().nextInt(10));
            }
        };
        handler.post(runnable);
    }

    private void stopCheckingForUpdates() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
//    private void sendNotification(int newOrdersCount) {
//        Context context = getApplicationContext();
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "HungryBeesChannelId")
//                .setSmallIcon(R.drawable.hungrybees)
//                .setContentTitle("HungryBees")
//                .setContentText(newOrdersCount + " new order(s) posted on HungryBees")
//                .setLargeIcon(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))
//                        .bigLargeIcon(null))
//                .setAutoCancel(true);
//        Notification notification = new NotificationCompat.Builder(context, "HungryBeesChannelId")
//                .setSmallIcon(R.drawable.hungrybees)
//                .setContentTitle("HungryBees")
//                .setContentText(newOrdersCount + " new order(s) posted on HungryBees")
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)))
//                .build();
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(new Random().nextInt(10), notification);
//    }

    private void sendNotification(int newOrdersCount) {
        Context context = getApplicationContext();

        // Create a custom view for the floating notification
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_main, null);

        // Set the text of the notification
        TextView textView = view.findViewById(R.id.notification_text_view);
        textView.setText(newOrdersCount + " new order(s) posted on HungryBees");

        // Create a layout params object to specify the position of the notification
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        // Set the position of the notification
        layoutParams.gravity = Gravity.TOP | Gravity.END;
        layoutParams.x = 0;
        layoutParams.y = 200;

        // Add the view to the current window
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(view, layoutParams);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HungryBeesChannel";
            String description = "Channel for HungryBees notifications";
            int importance = NotificationManager.IMPORTANCE_MAX;
            NotificationChannel channel = new NotificationChannel("HungryBeesChannelId", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void scheduleNewOrderWorker() {
        PeriodicWorkRequest newOrderWorkRequest = new PeriodicWorkRequest.Builder(NewOrderWorker.class, 1, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("NewOrderWorker", ExistingPeriodicWorkPolicy.KEEP, newOrderWorkRequest);
        Log.d("MainActivity", "NewOrderWorker scheduled");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCheckingForUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCheckingForUpdates();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Pull the location and store it to shared preferences

        Utils.getDeviceLocation(MainActivity.this, locationModel -> {
            System.out.println("Location: " + locationModel.getLatitude() + ' ' + locationModel.getLongitude());
            Utils.saveLocationToSharedPreferencesAndFirestore(MainActivity.this, locationModel);
        });

        createNotificationChannel();
        // scheduleNewOrderWorker(); // remove this line
        startCheckingForUpdates();
        sendNotification(4);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        //ImageView top_nav_imageview = findViewById(R.id.top_navbar_imageview);

        if (savedInstanceState == null) {
            // instantiate the PostsFragment fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new PostsFragment(fragmentManager))
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.action_posts);
        }
        // Load image using glide into top nav bar imageview
//        if (Utils.isNetworkAvailable(this)) {
//            Glide.with(this)
//                    .load("https://preview.redd.it/8sjtjrlmkru41.png?auto=webp&s=ee505e75337336992bb0be14e5ec98978c14f406")
//                    .into(top_nav_imageview);
//        }
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
