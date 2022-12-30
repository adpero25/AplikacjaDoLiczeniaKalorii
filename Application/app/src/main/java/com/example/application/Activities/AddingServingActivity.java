package com.example.application.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.application.database.models.junctions.CategoryWithMeals;
import com.example.application.database.models.junctions.MealWithOpenFoodFact;
import com.example.application.database.repositories.CategoriesRepository;


import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.application.R;




public class AddingServingActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_adding_serving);

        findViewById(R.id.scanCodeBTN).setOnClickListener(new View.OnClickListener() {
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

        CategoriesRepository repo = new CategoriesRepository(getApplication());
        LayoutInflater inflater=getLayoutInflater();
        LinearLayout v = findViewById(R.id.meal_list);
        repo.getAll().thenAccept((list)->{
            for(CategoryWithMeals elem : list){

                TextView categoryListItem = (TextView) inflater.inflate(R.layout.category_list_item, null);
                categoryListItem.setText(elem.category.name);
                v.addView(categoryListItem);
                for(MealWithOpenFoodFact meal : elem.meals){
                    View mealListItem = inflater.inflate(R.layout.meal_list_item, null);

                    TextView text =  mealListItem.findViewById(R.id.name);
                    text.setText(meal.meal.name);
                    v.addView(mealListItem);
                }

            }

        });

       /* Button loadDay = findViewById(R.id.button2);
        loadDay.setOnClickListener((a)->{});

        loadDay = findViewById(R.id.scanCodeBTN23);
        loadDay.setOnClickListener((a)->{});
*/
    }
}