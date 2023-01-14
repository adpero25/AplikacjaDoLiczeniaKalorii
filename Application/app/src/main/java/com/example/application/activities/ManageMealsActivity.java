package com.example.application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.application.R;
import com.example.application.database.models.Category;
import com.example.application.database.models.junctions.CategoryWithMeals;
import com.example.application.database.models.junctions.MealWithOpenFoodFact;
import com.example.application.database.repositories.CategoriesRepository;
import com.example.application.database.repositories.MealsRepository;


public class ManageMealsActivity extends DrawerActivity {

    ViewGroup listRoot;

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
        loadMealList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadMealList();
    }

    void loadMealList() {
        CategoriesRepository repo = new CategoriesRepository(getApplication());
        listRoot.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        repo.getAll().thenAccept((list) -> {
            for (CategoryWithMeals elem : list) {

                addListElements(elem, inflater);
            }

            new MealsRepository(getApplication()).getAllWithoutCategory().thenAccept((list2) -> {

                CategoryWithMeals elem = new CategoryWithMeals();
                elem.category = new Category();

                elem.category.name = getString(R.string.no_category);

                elem.meals = list2;

                addListElements(elem, inflater);

            });
        });
    }

    void addListElements(CategoryWithMeals elem, LayoutInflater inflater) {
        if (elem.meals != null && elem.meals.size() != 0) {

            listRoot.post(() -> {
                TextView categoryListItem = (TextView) inflater.inflate(R.layout.category_list_item, listRoot);
                categoryListItem.setText(elem.category.name);

                for (MealWithOpenFoodFact meal : elem.meals) {
                    View mealListItem = inflater.inflate(R.layout.two_buttons_list_item, listRoot);
                    ((TextView) mealListItem.findViewById(R.id.name)).setText(meal.meal.name);

                    ((Button) mealListItem.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ManageMealsActivity.this, AddingMealActivity.class);
                            intent.putExtra(BarcodeScanningActivity.PRODUCT_DETAILS, meal.meal);
                            startActivity(intent);
                        }
                    });

                    ((Button) mealListItem.findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listRoot.removeView(mealListItem);
                            MealsRepository repo = new MealsRepository(getApplication());
                            elem.meals.remove(meal);
                            if (elem.meals.size() == 0) {
                                listRoot.removeView(categoryListItem);
                            }
                            repo.delete(meal.meal);
                        }
                    });
                }
            });
        }
    }
}