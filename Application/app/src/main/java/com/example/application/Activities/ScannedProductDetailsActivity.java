package com.example.application.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.application.Activities.Scanner.Product;
import com.example.application.R;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ScannedProductDetailsActivity extends AppCompatActivity {

    Product product;
    TextView code_TextView;
    TextView allergens_TextView;
    TextView categories_TextView;
    TextView ingredients_text_TextView;
    TextView product_name_TextView;
    TextView quantity_TextView;
    TextView nutriments_TextView;
    ImageView productImage;
    String noData = "No data!";
    Button addMacroBTN;
    Button saveProductBTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_product_details);

        //brands_TextView = findViewById(R.id.brandsTextView);
        //expiration_date_TextView = findViewById(R.id.expirationDateTextView);
        //labels_TextView = findViewById(R.id.labelsTextView);
        //purchase_places_TextView = findViewById(R.id.purchasePlaceTextView);
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

        if(product != null) {

            code_TextView.setText( product.getId()  );

            if (product.getProduct_name_pl() != null)
                product_name_TextView.setText(  product.getProduct_name_pl() );
            else if (product.getProduct_name_en() != null)
                product_name_TextView.setText(  product.getProduct_name_en() );
            else
                product_name_TextView.setText(  noData );

            if(product.getCategories() != null)
                categories_TextView.setText(   product.getCategories() );
            else
                categories_TextView.setText( noData  );

            if(product.getIngredients_text_pl() != null)
                ingredients_text_TextView.setText( product.getIngredients_text_pl().toUpperCase(Locale.ROOT)  );
            else if (product.getIngredients_text_en() != null)
                ingredients_text_TextView.setText( product.getIngredients_text_en().toUpperCase(Locale.ROOT)  );
            else
                ingredients_text_TextView.setText( noData.toUpperCase(Locale.ROOT) );

            if(product.getAllergens() != null)
                allergens_TextView.setText( product.getAllergens().replace(',', '\n') );
            else
                allergens_TextView.setText( noData );

            if(product.getQuantity() != null)
                quantity_TextView.setText( product.getQuantity() );
            else
                quantity_TextView.setText( noData );

            if(product.getNutriments().toStringPl() != null)
                nutriments_TextView.setText( product.getNutriments().toStringPl() );
            else if(product.getNutriments().toStringEn() != null)
                nutriments_TextView.setText(  product.getNutriments().toStringEn() );
            else
                nutriments_TextView.setText( noData );

            if(product.getImages() != null) {
                String url = product.getImages().getFront().getDisplay().getPlURL();
                if(url == null)
                    url = product.getImages().getFront().getDisplay().getEnURL();
                Picasso.with(this)
                        .load(url)
                        .placeholder(R.drawable.ic_baseline_product).into(productImage);
            }
            else {
                productImage.setImageResource(R.drawable.ic_baseline_product);
            }
        }

        addMacroBTN = findViewById(R.id.addMacroButton);
        saveProductBTN = findViewById(R.id.saveProductButton);

        addMacroBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add product
            }
        });

        addMacroBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add macro to calculations
            }
        });
    }
}