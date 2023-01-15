package com.example.application.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.application.R;
import com.example.application.webservices.RetrofitInstance;
import com.example.application.webservices.openfoodfacts.ProductService;
import com.example.application.webservices.openfoodfacts.model.Product;
import com.example.application.webservices.openfoodfacts.model.ProductContainer;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarcodeScanningActivity extends DrawerActivity {

    public static final String PRODUCT_DETAILS = "PRODUCT_DETAILS";
    public static final String BARCODE = "BARCODE";
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button searchBtn;
    int howManySamples = 15;
    int howManySamplesRead;
    boolean activityRunning;
    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private TextView barcodeText;
    private String barcodeData;
    private ToneGenerator toneGen1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanning);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);
        searchBtn = findViewById(R.id.searchBTN);
        searchBtn.setVisibility(View.INVISIBLE);
        barcodeText.setVisibility(View.INVISIBLE);
        howManySamplesRead = 0;
    }

    private void initialiseDetectorsAndSources() {

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 640)
                .setAutoFocusEnabled(true)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(BarcodeScanningActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                        holder.lockCanvas();
                    } else {
                        ActivityCompat.requestPermissions(BarcodeScanningActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    howManySamplesRead++;

                    if (howManySamplesRead >= howManySamples) { // display product details view
                        barcodeData = barcodes.valueAt(0).displayValue;
                        fetchProductData(barcodeData);
                        searchBtn.setVisibility(View.VISIBLE);
                    }

                    searchBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            barcodeData = barcodes.valueAt(0).displayValue;
                            fetchProductData(barcodeData);
                        }
                    });

                    barcodeText.post(new Runnable() {
                        @Override
                        public void run() {
                            barcodeText.setVisibility(View.VISIBLE);
                            barcodeData = barcodes.valueAt(0).displayValue;
                            barcodeText.setText(barcodeData);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
        activityRunning = false;
    }

    private void fetchProductData(String query) {
        howManySamplesRead = 0;
        ProductService productService = RetrofitInstance.getOpenFoodFactsClientInstance().create(ProductService.class);
        try {

            Call<ProductContainer> productsApiCall = productService.findProducts(query + ".json");

            productsApiCall.enqueue(new Callback<ProductContainer>() {
                @Override
                public void onResponse(@NonNull Call<ProductContainer> call, @NonNull Response<ProductContainer> response) {
                    if (response.body() != null) {
                        setupProductView(response.body().getProduct(), query);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductContainer> call, @NonNull Throwable t) {
                    Snackbar.make(findViewById(R.id.scanner_view), "Something went wrong! Check your internet connection and try again later.",
                            BaseTransientBottomBar.LENGTH_LONG).show();
                    timeout(3);
                }
            });
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.scanner_view), "No such product in database, sorry!",
                    BaseTransientBottomBar.LENGTH_LONG).show();
            timeout(3);
        }
    }

    private void setupProductView(Product product, String barcode) {

        if (!activityRunning) {
            if (product != null) {
                activityRunning = true;
                Intent intent = new Intent(BarcodeScanningActivity.this, ScannedProductDetailsActivity.class);
                toneGen1.startTone(ToneGenerator.TONE_SUP_PIP, 120);
                intent.putExtra(PRODUCT_DETAILS, product);
                intent.putExtra(BARCODE, barcode);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Product not found", Toast.LENGTH_SHORT).show();
                toneGen1.startTone(ToneGenerator.TONE_CDMA_DIAL_TONE_LITE, 120);
                timeout(1.5);
            }
        }
    }

    private void timeout(double seconds) {
        long start = System.currentTimeMillis();
        double end = start + seconds * 1000;
        while (System.currentTimeMillis() < end) {
        }
    }
}