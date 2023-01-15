package com.example.application.activities;

import static com.example.application.activities.MainActivity.SHARED_PREFERENCES_FILE_NAME;
import static com.example.application.fragments.ServingsFragment.MEAL_TYPE;
import static com.example.application.activities.ServingsActivity.SERVING_DATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.application.adapters.TwoButtonListItemAdapter;
import com.example.application.database.models.Category;
import com.example.application.database.models.enums.MealType;
import com.example.application.database.models.junctions.CategoryWithMeals;
import com.example.application.database.models.junctions.MealWithOpenFoodFact;
import com.example.application.database.repositories.CategoriesRepository;


import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;
import com.example.application.database.repositories.DaysRepository;
import com.example.application.database.repositories.MealsRepository;
import com.example.application.database.repositories.ServingsRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AddingServingActivity extends DrawerActivity {

    public static final String CALORIES_REQUIREMENT = "CALORIES_REQUIREMENT";
    RecyclerView listRoot;
    TextView caloriesRequirementTextView;

    LocalDate day;
    MealType type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_adding_serving);

        Intent intent = getIntent();
        day = (LocalDate) intent.getSerializableExtra(SERVING_DATE);
        type = (MealType) intent.getSerializableExtra(MEAL_TYPE);

        caloriesRequirementTextView = findViewById(R.id.caloriesRequirementTextView);
        setCaloriesTextView();

        findViewById(R.id.previousDateBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddingServingActivity.this, BarcodeScanningActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.addNewMealBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddingServingActivity.this, AddingMealActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.changeCaloriesRequirementBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);
                TextView text = popupView.findViewById(R.id.popupText);
                EditText enteredValue = popupView.findViewById(R.id.WaterEnteredValue);
                Button submitButton = popupView.findViewById(R.id.acceptWaterAmountButton);

                text.setText(R.string.calories);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                submitButton.setOnClickListener(view ->{
                    if(enteredValue == null || TextUtils.isEmpty(enteredValue.getText().toString())){
                        popupWindow.dismiss();
                        return;
                    }
                    int value = Integer.parseInt(enteredValue.getText().toString());

                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                    preferencesEditor.putInt(CALORIES_REQUIREMENT, value);
                    preferencesEditor.apply();
                    popupWindow.dismiss();

                    setCaloriesTextView();
                });
            }
        });

        listRoot = findViewById(R.id.meal_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        listRoot.setLayoutManager(layoutManager);
        listRoot.setItemAnimator(null);

        loadMealList();
    }

    private void setCaloriesTextView() {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

        int calReq = sharedPreferences.getInt(CALORIES_REQUIREMENT, 2000);

        caloriesRequirementTextView.setText(getResources().getString(R.string.caloriesRequired, calReq));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadMealList();
    }

    void loadMealList(){
        CategoriesRepository repo = new CategoriesRepository(getApplication());
        repo.getAll().thenAccept((list)->{
            new MealsRepository(getApplication()).getAllWithoutCategory().thenAccept((list2)->{

                CategoryWithMeals elem = new CategoryWithMeals();
                elem.category = new Category();

                elem.category.name = "No category";

                elem.meals = list2;
                list.add(elem);
                Map<String, List<MealWithOpenFoodFact>> map = list.stream().collect(Collectors.toMap((categoryWithMeals) -> categoryWithMeals.category.name, (categoryWithMeals)-> categoryWithMeals.meals));

                TwoButtonListItemAdapter<MealWithOpenFoodFact> adapter = new TwoButtonListItemAdapter<MealWithOpenFoodFact>(map,
                        (result) -> result.meal.name,
                        () -> getString(R.string.add),
                        (context) ->
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                                        View popupView = inflater.inflate(R.layout.popup_window, null);
                                        TextView text = popupView.findViewById(R.id.popupText);
                                        EditText enteredValue = popupView.findViewById(R.id.WaterEnteredValue);
                                        Button submitButton = popupView.findViewById(R.id.acceptWaterAmountButton);

                                        text.setText(R.string.amount);
                                        enteredValue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

                                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        boolean focusable = true;
                                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                                        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                                        submitButton.setOnClickListener(view ->{
                                            if(TextUtils.isEmpty(enteredValue.getText().toString())){
                                                return;
                                            }

                                            DaysRepository repo = new DaysRepository(getApplication());

                                            repo.getDayByDate(day).thenAccept((day)-> {

                                                ServingsRepository sr = new ServingsRepository(getApplication());

                                                sr.insert(day.day, context.object.meal, Double.parseDouble(enteredValue.getText().toString()),type);
                                            });

                                            popupWindow.dismiss();
                                            finish();
                                        });
                                    }
                                },
                        () -> "",
                        (context) -> null
                );
                listRoot.setAdapter(adapter);
                listRoot.smoothScrollToPosition(0);
            });
        });
    }
}