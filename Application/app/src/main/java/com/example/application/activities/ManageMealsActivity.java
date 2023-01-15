package com.example.application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;
import com.example.application.adapters.TwoButtonListItemAdapter;
import com.example.application.database.models.Category;
import com.example.application.database.models.junctions.CategoryWithMeals;
import com.example.application.database.models.junctions.MealWithOpenFoodFact;
import com.example.application.database.repositories.CategoriesRepository;
import com.example.application.database.repositories.MealsRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManageMealsActivity extends DrawerActivity {

    RecyclerView listRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_meals);

        findViewById(R.id.addNewMealBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageMealsActivity.this, AddingMealActivity.class);
                startActivity(intent);
            }
        });

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

        repo.getAll().thenAccept((list) -> {
            new MealsRepository(getApplication()).getAllWithoutCategory().thenAccept((list2) -> {

                CategoryWithMeals elem = new CategoryWithMeals();
                elem.category = new Category();
                elem.category.name = getString(R.string.no_category);
                elem.meals = list2;

                list.add(elem);

                Map<String, List<MealWithOpenFoodFact>> map = list.stream().collect(Collectors.toMap((categoryWithMeals) -> categoryWithMeals.category.name, (categoryWithMeals) -> categoryWithMeals.meals));

                TwoButtonListItemAdapter<MealWithOpenFoodFact> adapter = new TwoButtonListItemAdapter<MealWithOpenFoodFact>(map,
                        (result) -> result.meal.name,
                        () -> getString(R.string.edit),
                        (context) ->
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ManageMealsActivity.this, AddingMealActivity.class);
                                        intent.putExtra(BarcodeScanningActivity.PRODUCT_DETAILS, context.object.meal);
                                        startActivity(intent);
                                    }
                                },
                        () -> getString(R.string.delete),
                        (context) ->
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new MealsRepository(getApplication()).delete(context.object.meal);
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