package com.example.smartwastemanagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

public class WasteAlertActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference userRef;
    String latitude,longitude;
    ImageView imageview;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    String user, managementId, userId;
    SharedPreferences sharedPreferences;
    Bitmap capturedImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waste_alert);

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("WasteAlert");

        imageview = findViewById(R.id.imageview);









        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();  // Permission granted, fetch location
            } else {
                Toast.makeText(this, "Permission Denied. Cannot access location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        // Create LocationRequest to request location updates every 20 seconds
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20000)  // Update interval (20 seconds)
                .setFastestInterval(10000);  // Fastest interval (10 seconds) for more frequent updates

        // Create LocationCallback to handle the location result
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(com.google.android.gms.location.LocationResult locationResult) {
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    // Use the latest location
                    Location location = locationResult.getLocations().get(0);
                    double latitude1 = location.getLatitude();
                    double longitude1 = location.getLongitude();
                    latitude=String.valueOf(latitude1);
                    longitude=String.valueOf(longitude1);
                    // Display latitude and longitude in a Toast
                    String locationMessage = "Latitude: " + latitude + "\nLongitude: " + longitude;
                    Toast.makeText(WasteAlertActivity.this, locationMessage, Toast.LENGTH_SHORT).show();

                } else {
                }
            }
        };

        // Request location updates
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper());  // Ensure it runs on the main thread










        // Retrieve userId and managementId from SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", "userId");
        Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();// Updated key to match
        managementId = sharedPreferences.getString("user_managmentId", "DefaultManagementId");
    }

    // Handle Image Capture
    public void btnCapture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    // Handle Form Submission
    public void sentAlert(View view) {
        String time = getCurrentTimestamp(); // Get the current timestamp in milliseconds

        if (capturedImage != null) {
            String imageBase64 = encodeImageToBase64(capturedImage);
            saveAlertData(userId, managementId, time, imageBase64,latitude,longitude);
        } else {
            Toast.makeText(this, "Please capture an image first!", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle Captured Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            capturedImage = (Bitmap) extras.get("data");
            imageview.setImageBitmap(capturedImage);
            imageview.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    // Convert Bitmap to Base64 String
    private String encodeImageToBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Get Current Timestamp in Milliseconds
    private String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    // Save Alert Data to Realtime Database
    private void saveAlertData(String userId, String managementId, String timestamp, String imageBase64, String latitude, String longitude) {
        DatabaseReference alertRef = userRef.child(managementId).push(); // Create unique push ID under managementId
        WasteAlert alert = new WasteAlert( userId, managementId, timestamp, imageBase64,latitude,longitude);
        alertRef.setValue(alert).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Alert submitted successfully!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to submit alert: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Model Class for Alert Data
    public static class WasteAlert {
        public String userId;
        public String managementId;
        public String timestamp; // Now stores timestamp in milliseconds
        public String imageBase64;
        public String latitude, longitude;

        public WasteAlert() {
        }

        public WasteAlert(String userId, String managementId, String timestamp, String imageBase64, String latitude, String longitude) {
            this.userId = userId; // Ensure consistent field name
            this.managementId = managementId;
            this.timestamp = timestamp; // Milliseconds timestamp
            this.imageBase64 = imageBase64;
            this.latitude=latitude;
            this.longitude=longitude;
        }
    }
}