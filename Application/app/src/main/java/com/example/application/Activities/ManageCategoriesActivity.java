package com.example.application.Activities;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.application.R;
import com.example.application.database.models.Category;
import com.example.application.database.models.junctions.CategoryWithMeals;
import com.example.application.database.repositories.CategoriesRepository;


public class ManageCategoriesActivity extends DrawerActivity {

    ViewGroup listRoot;

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

                    text.setText("Name of a category");
                    enteredValue.setInputType(InputType.TYPE_CLASS_TEXT);

                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true;
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                    submitButton.setOnClickListener(view -> {
                        if (enteredValue == null || TextUtils.isEmpty(enteredValue.getText().toString())) {
                            return;
                        } else {
                            CategoriesRepository repo = new CategoriesRepository(getApplication());

                            Category newCategory = new Category();

                            newCategory.name = enteredValue.getText().toString();

                            repo.insert(newCategory);
                            CategoryWithMeals elem = new CategoryWithMeals();
                            elem.category = newCategory;
                            addListElements(elem, inflater);
                        }
                        popupWindow.dismiss();
                    });
                }
        );


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
        });
    }

    void addListElements(CategoryWithMeals elem, LayoutInflater inflater) {

        listRoot.post(() -> {

            View categoryListItem = inflater.inflate(R.layout.two_buttons_list_item, null);
            ((TextView) categoryListItem.findViewById(R.id.name)).setText(elem.category.name);
            listRoot.addView(categoryListItem);

            ((Button) categoryListItem.findViewById(R.id.button)).setOnClickListener((v) -> {

                        View popupView = inflater.inflate(R.layout.popup_window, null);
                        TextView text = popupView.findViewById(R.id.popupText);
                        EditText enteredValue = popupView.findViewById(R.id.WaterEnteredValue);
                        Button submitButton = popupView.findViewById(R.id.acceptWaterAmountButton);

                        text.setText("Name of a category");
                        enteredValue.setInputType(InputType.TYPE_CLASS_TEXT);
                        enteredValue.setText(elem.category.name);
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true;
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                        submitButton.setOnClickListener(view -> {
                            if (enteredValue == null || TextUtils.isEmpty(enteredValue.getText().toString())) {
                                return;
                            } else {
                                CategoriesRepository repo = new CategoriesRepository(getApplication());

                                Category newCategory = new Category();

                                newCategory.name = enteredValue.getText().toString();

                                repo.update(newCategory);
                                ((TextView) categoryListItem.findViewById(R.id.name)).setText(newCategory.name);
                            }
                            popupWindow.dismiss();
                        });
                    }
            );

            ((Button) categoryListItem.findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listRoot.removeView(categoryListItem);
                    CategoriesRepository repo = new CategoriesRepository(getApplication());
                    repo.delete(elem.category);
                }
            });

        });

    }
}