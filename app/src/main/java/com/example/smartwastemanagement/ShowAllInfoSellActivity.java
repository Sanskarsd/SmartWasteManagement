package com.example.smartwastemanagement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowAllInfoSellActivity extends AppCompatActivity {

    private TextView nameTextView, mobileTextView, priceTextView, locationTextView, stateTextView;
    private ViewPager2 imageSlider;
    private DatabaseReference databaseReference;
    private ValueEventListener productListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_info_sell);

        // Initialize Views
        nameTextView = findViewById(R.id.nameTextView);
        mobileTextView = findViewById(R.id.mobileTextView);
        priceTextView = findViewById(R.id.priceTextView);
        locationTextView = findViewById(R.id.locationTextView);
        stateTextView = findViewById(R.id.stateTextView);
        imageSlider = findViewById(R.id.viewPager);

        // Retrieve product ID passed via Intent
        String productId = getIntent().getStringExtra("productId");

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("products").child(productId);

        // Fetch product data from Firebase
        fetchProductData(productId);
    }

    private void fetchProductData(String productId) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    // Set data to the views
                    nameTextView.setText(product.getName());
                    mobileTextView.setText(product.getMobile());
                    priceTextView.setText(product.getPrice());
                    locationTextView.setText(product.getLocation());
                    stateTextView.setText(product.getState());

                    // Set up image slider (ViewPager2)
                    List<String> imageBase64Strings = new ArrayList<>();
                    if (product.getImage1() != null && !product.getImage1().isEmpty()) {
                        imageBase64Strings.add(product.getImage1());
                    }
                    if (product.getImage2() != null && !product.getImage2().isEmpty()) {
                        imageBase64Strings.add(product.getImage2());
                    }
                    if (product.getImage3() != null && !product.getImage3().isEmpty()) {
                        imageBase64Strings.add(product.getImage3());
                    }

                    if (!imageBase64Strings.isEmpty()) {
                        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(imageBase64Strings);
                        imageSlider.setAdapter(imageSliderAdapter);
                    } else {
                        Toast.makeText(ShowAllInfoSellActivity.this, "No images available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShowAllInfoSellActivity.this, "Product not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ShowAllInfoSellActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override

    protected void onDestroy() {
        super.onDestroy();
        if (databaseReference != null && productListener != null) {
            databaseReference.removeEventListener(productListener);
        }
    }
}








class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {

    private List<String> imageBase64Strings;

    public ImageSliderAdapter(List<String> imageBase64Strings) {
        this.imageBase64Strings = imageBase64Strings;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slide_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String base64String = imageBase64Strings.get(position);
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.imageView.setImageBitmap(decodedBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return imageBase64Strings.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }
    }
}