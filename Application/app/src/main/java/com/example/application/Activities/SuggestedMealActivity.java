package com.example.application.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.example.application.Activities.Scanner.MealInfo;
import com.example.application.Activities.Scanner.MealSuggestionService;
import com.example.application.Activities.Scanner.RetrofitInstance;
import com.example.application.Fragments.MealSuggestionsFragment;
import com.example.application.R;
import com.example.application.database.repositories.MealsRepository;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuggestedMealActivity extends AppCompatActivity {

    public static final String MEAL_INFO = "MEAL_INFO";

    TextView name_TextView;
    TextView summary_TextView;
    ImageView productImage;

    Button saveProductBTN;
    Long id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_meal);

        name_TextView = findViewById(R.id.nameTextView);
        summary_TextView = findViewById(R.id.summaryTextView);
        productImage = findViewById(R.id.productImageView);

        Intent intent = getIntent();
        id = (Long) intent.getSerializableExtra(MealSuggestionsFragment.MEAL_ID);

        MealSuggestionService mealSuggestionService = RetrofitInstance.getSpoonacularClientInstance().create(MealSuggestionService.class);
        try {
            Call<MealInfo> productsApiCall = mealSuggestionService.loadMeal(id);

            productsApiCall.enqueue(new Callback<MealInfo>() {
                @Override
                public void onResponse(@NonNull Call<MealInfo> call, @NonNull Response<MealInfo> response) {
                    if (response.body() != null) {
                        name_TextView.setText(  response.body().getTitle() );
                        summary_TextView.setText(HtmlCompat.fromHtml( response.body().getInstructions(),HtmlCompat.FROM_HTML_MODE_COMPACT) );
                        name_TextView.setText(  response.body().getTitle() );

                        if(response.body().getImage() != null) {
                            String url = response.body().getImage();
                            Picasso.with(getApplicationContext())
                                    .load(url)
                                    .placeholder(R.drawable.ic_baseline_product).into(productImage);
                        }
                        else {
                            productImage.setImageResource(R.drawable.ic_baseline_product);
                        }

                        findViewById(R.id.saveProductButton).setOnClickListener((view) -> {
                            runOnUiThread(()-> {
                                Intent newIntent = new Intent(SuggestedMealActivity.this, AddingMealActivity.class);
                                newIntent.putExtra(SuggestedMealActivity.MEAL_INFO, response.body());

                                startActivity(newIntent);
                            });
                        });

                        findViewById(R.id.openBrowser).setOnClickListener((view) -> {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.body().getSourceUrl()));
                            startActivity(browserIntent);
                        });

                    }
                }

                @Override
                public void onFailure(@NonNull Call<MealInfo> call, @NonNull Throwable t) {
                    finish();
                }
            });
        }
        catch (Exception ignored) {
            finish();
        }

    }
}