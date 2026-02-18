package com.example.smartwastemanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MarketPlace extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SimpleAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private DatabaseReference databaseReference;
    com.google.android.material.floatingactionbutton.FloatingActionButton open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_place);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        open = findViewById(R.id.open);
        open.setOnClickListener(v -> {
            Intent i1 = new Intent(MarketPlace.this, SellActivity.class);
            startActivity(i1);
        });

        // Fetch data from Firebase
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Product> updatedList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        product.setId(snapshot.getKey()); // Firebase push ID सेट करा
                        updatedList.add(product);
                    }
                }
                productList.clear();
                productList.addAll(updatedList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MarketPlace.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new SimpleAdapter(productList);
        recyclerView.setAdapter(adapter);
    }

    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

        private List<Product> products;

        public SimpleAdapter(List<Product> products) {
            this.products = products;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView, priceTextView, locationTextView;
            ImageView productImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.name);
                priceTextView = itemView.findViewById(R.id.price);
                locationTextView = itemView.findViewById(R.id.location);
                productImageView = itemView.findViewById(R.id.product_image);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.marketplace_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Product product = products.get(position);
            holder.nameTextView.setText(product.getName());
            holder.priceTextView.setText(product.getPrice());
            holder.locationTextView.setText(product.getLocation());

            // Base64 इमेज डिकोड करून सेट करा
            String base64Image = product.getImage1();
            if (base64Image != null && !base64Image.isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.productImageView.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(MarketPlace.this, ShowAllInfoSellActivity.class);
                intent.putExtra("productId", product.getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }
    }
}
