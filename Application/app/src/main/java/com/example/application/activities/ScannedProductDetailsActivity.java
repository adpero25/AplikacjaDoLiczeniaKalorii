package com.example.application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.application.R;
import com.example.application.database.repositories.MealsRepository;
import com.example.application.webservices.openfoodfacts.model.Product;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ScannedProductDetailsActivity extends DrawerActivity {

    Product product;
    String barcode;
    TextView code_TextView;
    TextView allergens_TextView;
    TextView categories_TextView;
    TextView ingredients_text_TextView;
    TextView product_name_TextView;
    TextView quantity_TextView;
    TextView nutriments_TextView;
    ImageView productImage;
    String noData;
    Button saveProductBTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_product_details);

        noData = getString(R.string.no_data);

        code_TextView = findViewById(R.id.codeTextView);
        product_name_TextView = findViewById(R.id.productNameTextView);
        categories_TextView = findViewById(R.id.categoriesTextView);
        ingredients_text_TextView = findViewById(R.id.ingredientsTextView);
        allergens_TextView = findViewById(R.id.allergensTextView);
        productImage = findViewById(R.id.productImageView);
        quantity_TextView = findViewById(R.id.quantityTextView);
        nutriments_TextView = findViewById(R.id.nutrimentsTextView);

        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra(BarcodeScanningActivity.PRODUCT_DETAILS);
        barcode = (String) intent.getSerializableExtra(BarcodeScanningActivity.BARCODE);

        if (product != null) {

            code_TextView.setText(product.getId());

            if (product.getProduct_name_pl() != null)
                product_name_TextView.setText(product.getProduct_name_pl());
            else if (product.getProduct_name_en() != null)
                product_name_TextView.setText(product.getProduct_name_en());
            else
                product_name_TextView.setText(noData);

            if (product.getCategories() != null)
                categories_TextView.setText(product.getCategories());
            else
                categories_TextView.setText(noData);

            if (product.getIngredients_text_pl() != null)
                ingredients_text_TextView.setText(product.getIngredients_text_pl().toUpperCase(Locale.ROOT));
            else if (product.getIngredients_text_en() != null)
                ingredients_text_TextView.setText(product.getIngredients_text_en().toUpperCase(Locale.ROOT));
            else
                ingredients_text_TextView.setText(noData.toUpperCase(Locale.ROOT));

            if (product.getAllergens() != null)
                allergens_TextView.setText(product.getAllergens().replace(',', '\n'));
            else
                allergens_TextView.setText(noData);

            if (product.getQuantity() != null)
                quantity_TextView.setText(product.getQuantity());
            else
                quantity_TextView.setText(noData);

            if (product.getNutriments().toStringPl() != null)
                nutriments_TextView.setText(product.getNutriments().toStringPl());
            else if (product.getNutriments().toStringEn() != null)
                nutriments_TextView.setText(product.getNutriments().toStringEn());
            else
                nutriments_TextView.setText(noData);

            if (product.getImages() != null) {
                String url = product.getImages().getFront().getDisplay().getPlURL();
                if (url == null)
                    url = product.getImages().getFront().getDisplay().getEnURL();
                Picasso.with(this)
                        .load(url)
                        .placeholder(R.drawable.ic_baseline_product).into(productImage);
            } else {
                productImage.setImageResource(R.drawable.ic_baseline_product);
            }
        }

        saveProductBTN = findViewById(R.id.saveProductButton);

        MealsRepository repo = new MealsRepository(getApplication());

        repo.getByBarcode(barcode).thenAccept((meal) -> {
            if (meal != null && meal.meal != null) {
                runOnUiThread(() -> {
                    ((ViewGroup) saveProductBTN.getParent()).removeView(saveProductBTN);
                });
            } else {
                saveProductBTN.setOnClickListener((view) -> {
                    runOnUiThread(() -> {
                        Intent newIntent = new Intent(ScannedProductDetailsActivity.this, AddingMealActivity.class);
                        newIntent.putExtra(BarcodeScanningActivity.PRODUCT_DETAILS, product);
                        newIntent.putExtra(BarcodeScanningActivity.BARCODE, barcode);

                        startActivity(newIntent);
                    });
                });
            }
        });
    }
}