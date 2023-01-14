package com.example.application.broadcastReceivers;

import static com.example.application.activities.MainActivity.waterNotification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.application.database.CaloriesDatabase;
import com.example.application.database.repositories.DaysRepository;

public class WaterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
        mNotificationManager.cancel(waterNotification);

        DaysRepository repo = new DaysRepository(CaloriesDatabase.getDatabase(context));

        repo.getOrCreateToday().thenAccept((day) -> {
            day.day.glassesOfWater += 1;
            repo.update(day.day);
        });
    }
}
