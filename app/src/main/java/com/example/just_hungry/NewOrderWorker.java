package com.example.just_hungry;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.just_hungry.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class NewOrderWorker extends Worker {
    private static final String LAST_CHECK_KEY = "last_check";

    public NewOrderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("NewOrderWorker", "doWork() called");
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        long lastCheck = preferences.getLong(LAST_CHECK_KEY, 0);
        Date lastCheckDate = new Date(lastCheck);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query newOrdersQuery = db.collection("posts").whereGreaterThanOrEqualTo("dateCreated", lastCheckDate);
        Log.d("TRIGGERED", "New order worker triggered");
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Result[] workerResult = new Result[1];

        newOrdersQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("New order worker: " + task.getResult().size() + " new orders");
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Send notification for new orders
                    sendNotification(querySnapshot.size());
                }
                workerResult[0] = Result.success();
            } else {
                workerResult[0] = Result.failure();
            }
            countDownLatch.countDown();
        });

        // Update last check time
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(LAST_CHECK_KEY, new Date().getTime());
        editor.apply();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        }

        return workerResult[0];
    }

    private void sendNotification(int newOrdersCount) {
        Context context = getApplicationContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "HungryBeesChannelId")
                .setSmallIcon(R.drawable.hungrybees)
                .setContentTitle("HungryBees")
                .setContentText(newOrdersCount + " new order(s) posted on HungryBees")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }
}
