package com.example.application.backgroundTasks;

import static androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID;

import static com.example.application.Activities.MainActivity.CHANNEL_ID;
import static com.example.application.Activities.MainActivity.waterNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.application.Activities.CalculateCaloriesRequirement;
import com.example.application.Activities.MainActivity;
import com.example.application.CaloriesCalculatorContext;
import com.example.application.R;
import com.example.application.broadcastReceivers.WaterBroadcastReceiver;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.repositories.DaysRepository;

public class NotifyAboutWater extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private DaysRepository repo;
    private int prevWaterGlasses;
    private int currentWaterGlasses;
    private int startId;
    private int delayTime = 7200000; //2 hours


    private static final String name = NotifyAboutWater.class.getSimpleName();

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            repo.getOrCreateToday().thenAccept((day) -> {
                prevWaterGlasses = day.day.glassesOfWater;

            });

            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            repo.getOrCreateToday().thenAccept((day) -> {

                currentWaterGlasses = day.day.glassesOfWater;
            }).join();

            if(prevWaterGlasses == currentWaterGlasses){
                addNotification();
            }
            serviceHandler.handleMessage(msg);

        }



    }

    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        createNotificationChannel();
        serviceLooper = thread.getLooper();

        serviceHandler = new ServiceHandler(serviceLooper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        repo = new DaysRepository(CaloriesDatabase.getDatabase(CaloriesCalculatorContext.getAppContext()));
        Message msg = serviceHandler.obtainMessage();
        this.startId = startId;
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void addNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(CaloriesCalculatorContext.getAppContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground_cc)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.waterNotificationText))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.waterNotificationBigText)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent resultIntent = new Intent(CaloriesCalculatorContext.getAppContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(CaloriesCalculatorContext.getAppContext());
        stackBuilder.addParentStack(CalculateCaloriesRequirement.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        Intent registerGlassIntent = new Intent(CaloriesCalculatorContext.getAppContext(), WaterBroadcastReceiver.class);
        registerGlassIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent registerGlassPendingIntent = PendingIntent.getBroadcast(CaloriesCalculatorContext.getAppContext(), 0,
                registerGlassIntent, 0);

        builder.addAction(R.drawable.ic_register_water,
                getString(R.string.registerOneGlass), registerGlassPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
        mNotificationManager.notify(waterNotification, builder.build());
    }
}
