package com.example.just_hungry.activities;

import static com.example.just_hungry.Utils.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Utils utilsInstance = Utils.getInstance();
    FragmentManager fragmentManager = getSupportFragmentManager();
    private static final String LAST_CHECK_KEY = "last_check";

    private Handler handler;
    private Runnable runnable;

    private void checkForUpdates() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        long lastCheck = preferences.getLong(LAST_CHECK_KEY, 0);
        Date lastCheckDate = new Date(lastCheck);
        String lastCheckDateString = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(lastCheckDate);


        Query newOrdersQuery = db.collection("posts").whereGreaterThanOrEqualTo("dateCreated", lastCheckDateString);
        newOrdersQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    Log.d(TAG, "checkForUpdates: " + querySnapshot.size() + " new orders");
                    // Send notification for new orders
                    sendNotification(querySnapshot.size());
                }
            } else {
                Log.e(TAG, "checkForUpdates: Error getting documents: ", task.getException());
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
            }
        };
        // Cancel any previously scheduled executions of checkForUpdates()
        handler.removeCallbacks(runnable);
        // Schedule a new execution of checkForUpdates() every 5 seconds
        handler.postDelayed(runnable, 5000);
    }

    private void stopCheckingForUpdates() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
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
//        startCheckingForUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCheckingForUpdates();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Pull the location and store it to shared preferences

        utilsInstance.getDeviceLocation(MainActivity.this, locationModel -> {
            System.out.println("Location: " + locationModel.getLatitude() + ' ' + locationModel.getLongitude());
            utilsInstance.saveLocationToSharedPreferencesAndFirestore(MainActivity.this, locationModel);
        });

//        createNotificationChannel();
        // scheduleNewOrderWorker(); // remove this line
        // no need to call now because we already specify it in onResume, which is called when the app is created anyways
//        startCheckingForUpdates();
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
    private void sendNotification(int newOrdersCount) {
        String id = "HungryBeesChannelId";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(id);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(id, "HungryBees", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("HungryBees");
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id)
                    .setSmallIcon(R.drawable.hungrybees)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.hungrybees))
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.hungrybees))
                            .bigLargeIcon(null))
                    .setContentTitle("HungryBees")
                    .setContentText(newOrdersCount + " new order(s) posted on HungryBees")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setAutoCancel(false) // true douch on notifcaiont menu dismissed, but swipe to dismiss
                    .setTicker("HungryBees");
            builder.setContentIntent(contentIntent);
            NotificationManagerCompat m = NotificationManagerCompat.from(getApplicationContext());
            // id to generate new notification in notification tray
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);

                return;
            }
            m.notify(1, builder.build());
            notificationManager.notify(new Random().nextInt(10),builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HungryBeesChannel";
            String description = "Channel for HungryBees notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("HungryBeesChannelId", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
