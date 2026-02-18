package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

public class SellActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText nameEditText, mobileEditText, priceEditText, locationEditText, stateEditText;
    private ImageView imageView1, imageView2, imageView3;
    private Button selectImageBtn1, selectImageBtn2, selectImageBtn3, submitBtn;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private Bitmap imageBitmap1, imageBitmap2, imageBitmap3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        nameEditText = findViewById(R.id.name);
        mobileEditText = findViewById(R.id.mobile);
        priceEditText = findViewById (R.id.price);
        locationEditText = findViewById(R.id.loc);
        stateEditText = findViewById(R.id.state);

        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);

        selectImageBtn1 = findViewById(R.id.selectImageBtn1);
        selectImageBtn2 = findViewById(R.id.selectImageBtn2);
        selectImageBtn3 = findViewById(R.id.selectImageBtn3);
        submitBtn = findViewById(R.id.submit);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("products");

        selectImageBtn1.setOnClickListener(v -> openFileChooser(1));
        selectImageBtn2.setOnClickListener(v -> openFileChooser(2));
        selectImageBtn3.setOnClickListener(v -> openFileChooser(3));

        submitBtn.setOnClickListener(v -> submitData());
    }

    private void openFileChooser(int imageNumber) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, imageNumber);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                if (requestCode == 1) {
                    imageBitmap1 = imageBitmap;
                    imageView1.setImageBitmap(imageBitmap);
                } else if (requestCode == 2) {
                    imageBitmap2 = imageBitmap;
                    imageView2.setImageBitmap(imageBitmap);
                } else if (requestCode == 3) {
                    imageBitmap3 = imageBitmap;
                    imageView3.setImageBitmap(imageBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void submitData() {
        String name = nameEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();
        String price = priceEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String state = stateEditText.getText().toString();

        if (name.isEmpty() || mobile.isEmpty() || price.isEmpty() || location.isEmpty() || state.isEmpty()) {
            Toast.makeText(SellActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageBitmap1 == null || imageBitmap2 == null || imageBitmap3 == null) {
            Toast.makeText(SellActivity.this, "Please select all images", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert images to Base64 strings
        String imageBase64_1 = encodeImageToBase64(imageBitmap1);
        String imageBase64_2 = encodeImageToBase64(imageBitmap2);
        String imageBase64_3 = encodeImageToBase64(imageBitmap3);

        storeProductData(name, mobile, price, location, state, imageBase64_1, imageBase64_2, imageBase64_3);
    }

    private String encodeImageToBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void storeProductData(String name, String mobile, String price, String location, String state, String imageBase64_1, String imageBase64_2, String imageBase64_3) {
        Product product = new Product(name, mobile, price, location, state, imageBase64_1, imageBase64_2, imageBase64_3);

        DatabaseReference productRef = myRef.push();
        productRef.setValue(product)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SellActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SellActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                });
    }
}