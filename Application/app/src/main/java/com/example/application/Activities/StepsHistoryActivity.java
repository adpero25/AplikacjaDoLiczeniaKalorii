package com.example.application.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.Fragments.StepsDetailsFragment;
import com.example.application.R;
import com.example.application.SurfaceViews.StepProgressSurfaceView;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.Day;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StepsHistoryActivity extends DrawerActivity {

    private List<Day> days;
    private StepsAdapter adapter;
    private RecyclerView recyclerView;
    private double totalDistance = 0;
    private int totalSteps = 0;
    private StepsDetailsFragment stepsDetailsFragment;
    FragmentManager fragmentManager;

    TextView totalDistanceTextView;
    TextView totalStepsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_history);
        totalDistanceTextView = findViewById(R.id.totalDistanceEverMade);
        totalStepsTextView = findViewById(R.id.totalStepsEverMade);

        fragmentManager = getSupportFragmentManager();
        stepsDetailsFragment = (StepsDetailsFragment) fragmentManager.findFragmentById(R.id.stepsFragment);
        if(stepsDetailsFragment == null) {
            stepsDetailsFragment = new StepsDetailsFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.stepsFragment, stepsDetailsFragment)
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .addToBackStack(null)
                    .hide(stepsDetailsFragment)
                    .commit();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);

        CaloriesDatabase db = CaloriesDatabase.getDatabase(getApplicationContext());
        days = db.dayDao().getAll();

        recyclerView.scrollToPosition(days.size() - 1);

        if(adapter == null) {
            adapter = new StepsAdapter(days);
            recyclerView.setAdapter(adapter);
        }
        else {
            adapter.notifyDataSetChanged();
        }

        GetTotalStats();
    }

    private class StepsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView StepsTextView;
        TextView DateTextView;
        StepProgressSurfaceView ProgressBar;
        Day currentDay;

        public StepsHolder(@NonNull View itemView) {
            super(itemView);

            StepsTextView = itemView.findViewById(R.id.stepsValue);
            DateTextView = itemView.findViewById(R.id.dateValue);
            ProgressBar = itemView.findViewById(R.id.progressValue);
            ProgressBar.setMax(MainActivity.STEPS_TARGET);
            ProgressBar.setOnClickListener(this);
        }

        public void bind(Day _day){
            this.currentDay = _day;

            StepsTextView.setText(currentDay.stepsCount.toString());
            DateTextView.setText(currentDay.dayId.getMonth() + "-" + currentDay.dayId.getDate());
            ProgressBar.setProgress(currentDay.stepsCount);
        }

        @Override
        public void onClick(View v) {
            if(!stepsDetailsFragment.isHidden()) { // hide old day
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .hide(stepsDetailsFragment)
                        .commit();
            }

            // show current day
            stepsDetailsFragment.setDate(currentDay);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .show(stepsDetailsFragment)
                    .commit();
        }
    }

    private class StepsAdapter extends RecyclerView.Adapter<StepsHolder> {
        private List<Day> days;

        public StepsAdapter(List<Day> _days) {
            this.days = _days;
        }

        @NonNull
        @Override
        public StepsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.steps_history_list_item, parent, false);

            return new StepsHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StepsHolder holder, int position) {
            Day day = days.get(position);

            holder.bind(day);
        }

        @Override
        public int getItemCount() {
            return days.size();
        }
    }

    private void GetTotalStats() {

        for (Day _day: days) {
            totalSteps += _day.stepsCount;
            totalDistance += _day.stepsCount;
        }

        totalDistanceTextView.setText(getResources().getString(R.string.totalDistanceEverMade, totalDistance / 1000));
        totalStepsTextView.setText(getResources().getString(R.string.totalStepsEverMade, totalSteps));
    }
}