package com.example.application.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.application.R;
import com.example.application.database.CaloriesDatabase;
import com.example.application.database.models.DailyRequirements;

import java.util.List;

public class UserParametersList extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_parameters_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final ParameterAdapter adapter = new ParameterAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CaloriesDatabase db = CaloriesDatabase.getDatabase(getApplication());
        List<DailyRequirements> dailyRequirementsList = db.dailyRequirementsDao().listDailyRequirements();

        adapter.setDailyRequirements(dailyRequirementsList);
    }

    private class ParameterHolder extends RecyclerView.ViewHolder{
        private final TextView weightText;
        private final TextView heightText;
        private final TextView ageText;
        private final TextView dateText;

        public ParameterHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.parameter_list_item, parent, false));

            weightText = itemView.findViewById(R.id.weightValue);
            heightText = itemView.findViewById(R.id.heightValue);
            ageText = itemView.findViewById(R.id.ageValue);
            dateText = itemView.findViewById(R.id.entryDateValue);
        }

        public void bind(DailyRequirements dailyRequirements){
            weightText.setText(dailyRequirements.weight.toString());
            heightText.setText(dailyRequirements.height.toString());
            ageText.setText(getString(R.string.age, dailyRequirements.age));
            if(dailyRequirements.entryDate != null){
                dateText.setText(dailyRequirements.entryDate.toString());
            }
            else{
                dateText.setText(R.string.somethingWrongWithDate);
            }
        }
    }

    private class ParameterAdapter extends RecyclerView.Adapter<ParameterHolder>{
        private List<DailyRequirements> dailyRequirementsList;

        @NonNull
        @Override
        public ParameterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new ParameterHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ParameterHolder holder, int position) {
            if(dailyRequirementsList != null){
                DailyRequirements dailyRequirements = dailyRequirementsList.get(position);
                holder.bind(dailyRequirements);
            }
        }

        @Override
        public int getItemCount() {
            if(dailyRequirementsList != null){
                return dailyRequirementsList.size();
            }
            else{
                return 0;
            }
        }

        void setDailyRequirements(List<DailyRequirements> dailyRequirements){

            this.dailyRequirementsList = dailyRequirements;
            notifyDataSetChanged();
        }
    }
}