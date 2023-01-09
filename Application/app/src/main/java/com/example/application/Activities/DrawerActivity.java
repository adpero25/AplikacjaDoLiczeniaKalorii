package com.example.application.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.application.CaloriesCalculatorContext;
import com.example.application.R;
import com.example.application.backgroundTasks.NotifyAboutWater;
import com.example.application.backgroundTasks.StepCounterService;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.junctions.DayWithServings;
import com.example.application.database.repositories.DaysRepository;
import com.google.android.material.navigation.NavigationView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public abstract class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout fullLayout;
    public FrameLayout frameLayout;

    @Override
    public void setContentView(int layoutResID) {

        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer, null);
        frameLayout = (FrameLayout) fullLayout.findViewById(R.id.drawer_frame);

        getLayoutInflater().inflate(layoutResID, frameLayout, true);

        super.setContentView(fullLayout);

        ((NavigationView)findViewById(R.id.navigationView)).setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullLayout.closeDrawer(Gravity.LEFT, false);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent newIntent = null;

        switch(item.getItemId()) {
            case R.id.inbox_item:
                newIntent = new Intent( this, AddingMealActivity.class);
                break;
            case R.id.outbox_item:
                newIntent = new Intent( this, StepsHistoryActivity.class);
                break;
            default:
                break;
        }

        if(newIntent != null) {
            Intent baseIntent = new Intent( this, MainActivity.class);
            baseIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            TaskStackBuilder.create( getApplicationContext() )
                    .addNextIntent( baseIntent )
                    .addNextIntent( newIntent )
                    .startActivities();
        }

        return false;
    }
}