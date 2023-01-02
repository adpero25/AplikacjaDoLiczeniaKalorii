package com.example.application.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.application.Activities.Scanner.Product;
import com.example.application.R;
import com.example.application.database.models.Category;
import com.example.application.database.models.Meal;
import com.example.application.database.models.junctions.CategoryWithMeals;
import com.example.application.database.repositories.CategoriesRepository;
import com.example.application.database.repositories.DaysRepository;
import com.example.application.database.repositories.MealsRepository;

import java.util.Arrays;
import java.util.function.Function;

public class AddingMealActivity extends AppCompatActivity {

    Meal EditMeal;

    EditText NameField;
    EditText DescField;

    EditText CaloriesField;
    EditText CarbohydratesField;
    EditText ProteinsField;
    EditText FatField;

    Button ConfirmButton;
    Button AddCategoryButton;

    Spinner CategorySpinner;

    String selectedCategory = "";
    String[] categories;

    String barcode;
    Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_meal);

        Intent intent = getIntent();

        try {
            product = (Product) intent.getSerializableExtra(BarcodeScanningActivity.PRODUCT_DETAILS);
            barcode = (String) intent.getSerializableExtra(BarcodeScanningActivity.BARCODE);
        } catch (Exception ignored) {}

        try {
            EditMeal = (Meal) intent.getSerializableExtra(BarcodeScanningActivity.PRODUCT_DETAILS);
        } catch (Exception ignored) {}

        NameField = findViewById(R.id.nameField);
        DescField = findViewById(R.id.descField);

        CaloriesField = findViewById(R.id.caloriesInput);
        CarbohydratesField = findViewById(R.id.carbohydratesInput);
        ProteinsField = findViewById(R.id.proteinsInput);
        FatField = findViewById(R.id.fatInput);

        ConfirmButton = findViewById(R.id.addNewMealBTN);
        AddCategoryButton = findViewById(R.id.addCategoryBTN);

        CategorySpinner = findViewById(R.id.categorySpinner);


        if(product!=null){

            if(product.getProduct_name_en()!= null && !product.getProduct_name_en().isEmpty()){
                NameField.setText(product.getProduct_name_en());
            }else {
                NameField.setText(product.getProduct_name_pl());
            }

            CaloriesField.setText(product.getNutriments().getEnergy_kcal_100g());
            CarbohydratesField.setText(product.getNutriments().getCarbohydrates_100g());
            ProteinsField.setText(product.getNutriments().getProteins_100g());
            FatField.setText(product.getNutriments().getFat_100g());
        }

        updateCategories(0);

        ConfirmButton.setOnClickListener((view)->{
            Meal newMeal = new Meal();

            newMeal.name = NameField.getText().toString();
            newMeal.description = DescField.getText().toString();

            newMeal.nutritionalValues.calories =        Double.parseDouble(CaloriesField.getText().toString().isEmpty()?"0":CaloriesField.getText().toString());
            newMeal.nutritionalValues.carbohydrates =   Double.parseDouble(CarbohydratesField.getText().toString().isEmpty()?"0":CarbohydratesField.getText().toString());
            newMeal.nutritionalValues.proteins =        Double.parseDouble(ProteinsField.getText().toString().isEmpty()?"0":ProteinsField.getText().toString());
            newMeal.nutritionalValues.fats =            Double.parseDouble(FatField.getText().toString().isEmpty()?"0":FatField.getText().toString());

            newMeal.picturePath = "";//TODO ?
            CategoriesRepository repo = new CategoriesRepository(getApplication());

            repo.getByName(selectedCategory).thenAccept((category)->{
                if(category!=null){
                    newMeal.categoryId=category.categoryId;
                }

                if(barcode != null && !barcode.isEmpty()){
                    new MealsRepository(getApplication()).insertWithBarcode(newMeal,barcode);
                }else {
                    new MealsRepository(getApplication()).insert(newMeal);
                }

                Intent newIntent = new Intent(AddingMealActivity.this, AddingServingActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);

            });
        });

        AddCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);
                TextView text = popupView.findViewById(R.id.popupText);
                EditText enteredValue = popupView.findViewById(R.id.WaterEnteredValue);
                Button submitButton = popupView.findViewById(R.id.acceptWaterAmountButton);

                text.setText("Name of a new category");
                enteredValue.setInputType(InputType.TYPE_CLASS_TEXT);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                submitButton.setOnClickListener(view ->{
                    if(enteredValue == null || TextUtils.isEmpty(enteredValue.getText().toString())){
                        return;
                    }
                    if(Arrays.stream(categories).anyMatch((s)->s.equals(enteredValue.getText().toString()))){
                        int index = Arrays.asList(categories).indexOf(enteredValue.getText().toString());
                        CategorySpinner.setSelection(index);
                        selectedCategory = categories[index];
                    } else {
                       CategoriesRepository repo = new CategoriesRepository(getApplication());

                        Category newCategory = new Category();

                        newCategory.name = enteredValue.getText().toString();

                        repo.insert(newCategory).thenAccept((a) -> {
                            updateCategories(categories.length);
                        });
                    }
                    popupWindow.dismiss();
                });
            }
        });
    }

    public void updateCategories(int newCategory){
        CategoriesRepository repo = new CategoriesRepository(getApplication());
        repo.getAll().thenAccept((list)->{  runOnUiThread(()-> {

            Category category = new Category();
            category.name = getString(R.string.nullCategory);

            CategoryWithMeals categoryWithMeals = new CategoryWithMeals();
            categoryWithMeals.category=category;

            list.add(0,categoryWithMeals);

            categories =  list.stream().map((elem) -> elem.category.name).toArray(String[]::new);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);

            CategorySpinner.setAdapter(adapter);
            CategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    selectedCategory = categories[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            CategorySpinner.setSelection(newCategory);
            selectedCategory = categories[newCategory];
        });
        });
    }
}