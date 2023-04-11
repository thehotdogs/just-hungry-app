//package com.example.just_hungry;
//
//import static com.example.just_hungry.Utils.TAG;
//
//import android.Manifest;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.graphics.BitmapFactory;
//import android.os.Build;
//import android.os.Handler;
//import android.util.Log;
//
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//
//import com.example.just_hungry.activities.MainActivity;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.Date;
//import java.util.Random;
//
//public class UpdateChecker {
//
//    private static UpdateChecker instance;
//    private Handler handler;
//    private Runnable runnable;
//
//    private UpdateChecker() {
//        handler = new Handler();
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                checkForUpdates();
//                System.out.println("Checking for updates");
//                handler.postDelayed(this, 5000);
//            }
//        };
//    }
//
//    public static synchronized UpdateChecker getInstance() {
//        if (instance == null) {
//            instance = new UpdateChecker();
//        }
//        return instance;
//    }
//
//    public void startCheckingForUpdates() {
//        handler.post(runnable);
//    }
//
//    public void stopCheckingForUpdates() {
//        if (handler != null && runnable != null) {
//            handler.removeCallbacks(runnable);
//        }
//
////        handler.removeCallbacks(runnable);
//    }
//
//    private void checkForUpdates() {
//        // your code for checking for updates
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        SharedPreferences preferences = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
//        long lastCheck = preferences.getLong(LAST_CHECK_KEY, 0);
//        Date lastCheckDate = new Date(lastCheck);
//        Log.d(TAG, "checkForUpdates: last check: " + lastCheckDate + "checkForUpdates: current date: " + new Date());
//
//        Query newOrdersQuery = db.collection("posts").whereGreaterThanOrEqualTo("dateCreated", lastCheckDate);
//        newOrdersQuery.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                QuerySnapshot querySnapshot = task.getResult();
//                Log.d(TAG, "checkForUpdates: " + querySnapshot.getDocuments() + " new orders" + querySnapshot.isEmpty());
//                if (querySnapshot != null && !querySnapshot.isEmpty()) {
//                    Log.d(TAG, "checkForUpdates: " + querySnapshot.size() + " new orders");
//                    // Send notification for new orders
//                    MainActivity.sendNotification(querySnapshot.size());
//                }
//            }
//        });
//
//        // Update last check time
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putLong(LAST_CHECK_KEY, new Date().getTime());
//        editor.apply();
//    }
//}