package com.example.application.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.application.adapters.TwoButtonListItemAdapter;
import com.example.application.database.models.Category;
import com.example.application.database.models.junctions.CategoryWithMeals;
import com.example.application.database.models.junctions.MealWithOpenFoodFact;
import com.example.application.database.repositories.CategoriesRepository;
import com.example.application.database.repositories.MealsRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ManageCategoriesActivity extends DrawerActivity {

    RecyclerView listRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_categories);

        findViewById(R.id.addNewMealBTN).setOnClickListener((v) -> {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.popup_window, null);
                    TextView text = popupView.findViewById(R.id.popupText);
                    EditText enteredValue = popupView.findViewById(R.id.WaterEnteredValue);
                    Button submitButton = popupView.findViewById(R.id.acceptWaterAmountButton);

                    text.setText(R.string.name_of_category);
                    enteredValue.setInputType(InputType.TYPE_CLASS_TEXT);

                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true;
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                    submitButton.setOnClickListener(view -> {
                        if (TextUtils.isEmpty(enteredValue.getText().toString())) {
                            return;
                        } else {
                            CategoriesRepository repo = new CategoriesRepository(getApplication());

                            Category newCategory = new Category();

                            newCategory.name = enteredValue.getText().toString();

                            repo.insert(newCategory).thenAccept((cat)->loadMealList());
                        }
                        popupWindow.dismiss();
                    });
                }
        );


        listRoot = findViewById(R.id.meal_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        listRoot.setLayoutManager(layoutManager);
        listRoot.setItemAnimator(null);
        loadMealList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadMealList();
    }

    void loadMealList() {
        CategoriesRepository repo = new CategoriesRepository(getApplication());
        LayoutInflater inflater = getLayoutInflater();


        repo.getAll().thenAccept((list) -> {
            listRoot.post(()->{
            Map<String, List<CategoryWithMeals>> map = new HashMap<>();
            map.put("",list);
            TwoButtonListItemAdapter<CategoryWithMeals> adapter = new TwoButtonListItemAdapter<CategoryWithMeals>(map,
                    (result) -> result.category.name,
                    () -> getString(R.string.edit),
                    (context) ->
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    View popupView = inflater.inflate(R.layout.popup_window, null);
                                    TextView text = popupView.findViewById(R.id.popupText);
                                    EditText enteredValue = popupView.findViewById(R.id.WaterEnteredValue);
                                    Button submitButton = popupView.findViewById(R.id.acceptWaterAmountButton);

                                    text.setText(R.string.name_of_category);
                                    enteredValue.setInputType(InputType.TYPE_CLASS_TEXT);
                                    enteredValue.setText(context.object.category.name);
                                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                    boolean focusable = true;
                                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                                    submitButton.setOnClickListener(view -> {
                                            if (TextUtils.isEmpty(enteredValue.getText().toString())) {
                                                return;
                                            } else {
                                                CategoriesRepository repo = new CategoriesRepository(getApplication());

                                                context.object.category.name = enteredValue.getText().toString();

                                                repo.update(context.object.category).thenAccept((cat)->loadMealList());
                                            }
                                            popupWindow.dismiss();
                                    });
                                }
                            },
                    () -> getString(R.string.delete),
                    (context) ->
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CategoriesRepository repo = new CategoriesRepository(getApplication());
                                    repo.delete(context.object.category);
                                    context.thisAdapter.removeAt(context.position);
                                }
                            }

            );
            listRoot.setAdapter(adapter);
            listRoot.smoothScrollToPosition(0);
        });
        });
    }
}
