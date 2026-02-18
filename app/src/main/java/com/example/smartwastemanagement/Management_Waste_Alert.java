package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Management_Waste_Alert extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WasteAlertAdapter adapter;
    private List<WasteAlert> wasteAlertList;
    private FirebaseDatabase database;
    private DatabaseReference wasteAlertRef;
    private String managementID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_waste_alert);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        wasteAlertRef = database.getReference("WasteAlert");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        wasteAlertList = new ArrayList<>();

        // Retrieve managementID from SharedPreferences
        managementID = getSharedPreferences("LoginPrefs", MODE_PRIVATE).getString("managementID", "managementID");

        // Pass the managementID to the adapter
        adapter = new WasteAlertAdapter(wasteAlertList, managementID);
        recyclerView.setAdapter(adapter);

        // Fetch and display waste alerts for the current managementID
        fetchWasteAlerts();
    }

    private void fetchWasteAlerts() {
        // Directly reference the managementID node in the Firebase structure
        wasteAlertRef.child(managementID)  // Directly reference the managementID node
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        wasteAlertList.clear();
                        // Loop through each alert node under the managementID
                        for (DataSnapshot alertSnapshot : dataSnapshot.getChildren()) {
                            WasteAlert alert = alertSnapshot.getValue(WasteAlert.class);
                            // Add alert to the list along with its unique key (ID)
                            alert.id = alertSnapshot.getKey();
                            wasteAlertList.add(alert);
                        }
                        Log.d("WasteAlert", "Fetched alerts: " + wasteAlertList.size()); // Debugging
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Management_Waste_Alert.this, "Failed to load alerts: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // WasteAlert Model Class
    public static class WasteAlert {
        public String userId;
        public String timestamp;
        public String latitude;
        public String longitude;
        public String imageBase64;
        public String id; // Add ID field to store the unique key

        public WasteAlert() {
            // Default constructor required for calls to DataSnapshot.getValue(WasteAlert.class)
        }

        public WasteAlert(String userId, String timestamp, String latitude, String longitude, String imageBase64) {
            this.userId = userId;
            this.timestamp = timestamp;
            this.latitude = latitude;
            this.longitude = longitude;
            this.imageBase64 = imageBase64;
        }
    }

    // WasteAlertAdapter for RecyclerView
    public static class WasteAlertAdapter extends RecyclerView.Adapter<WasteAlertAdapter.WasteAlertViewHolder> {

        private List<WasteAlert> alertList;
        private DatabaseReference wasteAlertRef;
        private String managementID;

        // Add managementID to the constructor
        public WasteAlertAdapter(List<WasteAlert> alertList, String managementID) {
            this.alertList = alertList;
            this.managementID = managementID;
            this.wasteAlertRef = FirebaseDatabase.getInstance().getReference("WasteAlert");
        }

        @NonNull
        @Override
        public WasteAlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_item, parent, false);
            return new WasteAlertViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WasteAlertViewHolder holder, int position) {
            WasteAlert alert = alertList.get(position);
            Log.d("WasteAlert", "Binding alert at position: " + position); // Debugging

            holder.txtUserId.setText("User ID: " + alert.userId);
            holder.txtTimestamp.setText("Timestamp: " + alert.timestamp);
            holder.txtLatitude.setText("Latitude: " + alert.latitude);
            holder.txtLongitude.setText("Longitude: " + alert.longitude);

            // Decode Base64 image string into Bitmap and set to ImageView
            byte[] decodedString = Base64.decode(alert.imageBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imgAlertImage.setImageBitmap(decodedByte);

            // Set click listeners for the OK and Cancel buttons
            holder.btnOk.setOnClickListener(v -> {
                // Get the position of the clicked item
                int positionClicked = holder.getAdapterPosition();
                if (positionClicked != RecyclerView.NO_POSITION) {
                    // Remove the entire alert node from Firebase using the unique ID
                    wasteAlertRef.child(managementID).child(alert.id).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(v.getContext(), "Alert Accepted", Toast.LENGTH_SHORT).show();
                                alertList.remove(positionClicked);  // Remove from list at the clicked position
                                notifyItemRemoved(positionClicked);  // Notify the adapter to refresh the RecyclerView
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(v.getContext(), "Failed to remove alert: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            });

            holder.btnCancel.setOnClickListener(v -> {
                // Get the position of the clicked item
                int positionClicked = holder.getAdapterPosition();
                if (positionClicked != RecyclerView.NO_POSITION) {
                    // Optionally, you can simply notify the user that the alert is not removed
                    wasteAlertRef.child(managementID).child(alert.id).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(v.getContext(), "Alert Rejected", Toast.LENGTH_SHORT).show();
                                alertList.remove(positionClicked);  // Remove from list at the clicked position
                                notifyItemRemoved(positionClicked);  // Notify the adapter to refresh the RecyclerView
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(v.getContext(), "Failed to remove alert: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            });
        }

        @Override
        public int getItemCount() {
            return alertList.size();
        }

        public static class WasteAlertViewHolder extends RecyclerView.ViewHolder {
            TextView txtUserId, txtTimestamp, txtLatitude, txtLongitude;
            ImageView imgAlertImage;
            Button btnOk, btnCancel;

            public WasteAlertViewHolder(View itemView) {
                super(itemView);
                txtUserId = itemView.findViewById(R.id.txtUserId);
                txtTimestamp = itemView.findViewById(R.id.txtTimestamp);
                txtLatitude = itemView.findViewById(R.id.txtLatitude);
                txtLongitude = itemView.findViewById(R.id.txtLongitude);
                imgAlertImage = itemView.findViewById(R.id.imgAlertImage);
                btnOk = itemView.findViewById(R.id.btnOk);
                btnCancel = itemView.findViewById(R.id.btnCancel);
            }
        }
    }
}