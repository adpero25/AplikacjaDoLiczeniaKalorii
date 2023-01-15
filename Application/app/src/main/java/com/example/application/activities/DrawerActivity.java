package com.example.application.activities;

import android.annotation.SuppressLint;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.application.R;
import com.google.android.material.navigation.NavigationView;

public abstract class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout fullLayout;
    public FrameLayout frameLayout;

    @Override
    public void setContentView(int layoutResID) {

        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer, null);
        frameLayout = (FrameLayout) fullLayout.findViewById(R.id.drawer_frame);

        getLayoutInflater().inflate(layoutResID, frameLayout, true);

        super.setContentView(fullLayout);

        ((NavigationView) findViewById(R.id.navigationView)).setNavigationItemSelectedListener(this);
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

        switch (item.getItemId()) {
            case R.id.navItemCalculateCalories:
                newIntent = new Intent(this, CalculateCaloriesRequirement.class);
                break;
            case R.id.navItemManageCategories:
                newIntent = new Intent(this, ManageCategoriesActivity.class);
                break;
            case R.id.navItemManageMeals:
                newIntent = new Intent(this, ManageMealsActivity.class);
                break;
            case R.id.navItemDailyRequirements:
                newIntent = new Intent(this, ManuallyAddDailyRequirements.class);
                break;
            case R.id.navItemUserParameters:
                newIntent = new Intent(this, UserParametersList.class);
                break;
            default:
                break;
        }

        if (newIntent != null) {
            Intent baseIntent = new Intent(this, MainActivity.class);
            baseIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            TaskStackBuilder.create(getApplicationContext())
                    .addNextIntent(baseIntent)
                    .addNextIntent(newIntent)
                    .startActivities();
        }

        return false;
    }
}