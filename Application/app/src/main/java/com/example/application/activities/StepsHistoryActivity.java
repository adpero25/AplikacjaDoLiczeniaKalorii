package com.example.application.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.database.repositories.DaysRepository;
import com.example.application.fragments.StepsDetailsFragment;
import com.example.application.R;
import com.example.application.surfaceViews.StepProgressSurfaceView;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.Day;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class StepsHistoryActivity extends DrawerActivity {

    private List<Day> days;
    private StepsAdapter adapter;
    private RecyclerView recyclerView;
    private final int MaxVal = 5000;
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
                    .hide(stepsDetailsFragment)
                    .commit();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);

        DaysRepository repo = new DaysRepository(getApplication());

        repo.getAllDays().thenAccept(days->{
            this.days = days;
            recyclerView.scrollToPosition(days.size() - 1);

            if(adapter == null) {
                adapter = new StepsAdapter(days);
                recyclerView.setAdapter(adapter);
            }
            else {
                adapter.notifyDataSetChanged();
            }

            GetTotalStats();
        });
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

            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM - dd", new Locale("PL"));

                StepsTextView.setText(currentDay.stepsCount.toString());
                DateTextView.setText(dateFormatter.format(currentDay.dayId));
                ProgressBar.setProgress(currentDay.stepsCount);
            }
            catch (Exception ignored) {
                Toast.makeText(getApplicationContext(), R.string.unableToLoadStepsHistory, Toast.LENGTH_SHORT).show();
            }

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
        private final List<Day> days;

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