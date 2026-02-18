package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Management_dustbin_Activity extends AppCompatActivity {

    EditText locationInput;
    Button enter;
    WebView webView;
    FirebaseDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_dustbin);

        // Initialize Firebase instance
        database = FirebaseDatabase.getInstance();

        locationInput = findViewById(R.id.locationInput);
        enter = findViewById(R.id.enter);
        webView = findViewById(R.id.webView3);

        // Enable JavaScript in WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set up WebViewClient to handle URL loading
        webView.setWebViewClient(new WebViewClient());

        // Add JavaScript Interface to interact with WebView
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        // Load the HTML file for the map
        webView.loadUrl("file:///android_asset/manage_dustbin_map.html");

        // Fetch all locations from Firebase and update the map
        fetchAllLocationsFromFirebase();

        // OnClickListener for the enter button
        enter.setOnClickListener(v -> sendLocationToDatabase());
    }

    private void sendLocationToDatabase() {
        // Retrieve the location entered by the user
        String locationValue = locationInput.getText().toString().trim();

        // Check if the location is not empty and contains a comma
        if (!locationValue.isEmpty() && locationValue.contains(",")) {
            String[] locationParts = locationValue.split(",");
            if (locationParts.length == 2) {
                try {
                    double latitude = Double.parseDouble(locationParts[0].trim());
                    double longitude = Double.parseDouble(locationParts[1].trim());

                    // Reference to the "Dustbin" node in Firebase
                    DatabaseReference locationsRef = database.getReference("Dustbin").child("locations");

                    // Create a location object to save in Firebase
                    Location location = new Location(latitude, longitude);

                    // Store the location as a new entry in Firebase
                    locationsRef.push().setValue(location)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Optionally, notify the user
                                    Toast.makeText(Management_dustbin_Activity.this, "Location saved successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle failure case
                                    Toast.makeText(Management_dustbin_Activity.this, "Failed to save location!", Toast.LENGTH_SHORT).show();
                                }
                            });

                    // Fetch updated locations from Firebase after adding the new one
                    fetchAllLocationsFromFirebase();
                } catch (NumberFormatException e) {
                    Toast.makeText(Management_dustbin_Activity.this, "Invalid coordinates format!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Management_dustbin_Activity.this, "Please enter a valid latitude and longitude separated by a comma!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Management_dustbin_Activity.this, "Please enter a location in the format: latitude,longitude", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAllLocationsFromFirebase() {
        // Reference to the "Dustbin" locations node in Firebase
        DatabaseReference locationsRef = database.getReference("Dustbin").child("locations");

        // Listen for location updates
        locationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Location> allLocations = new ArrayList<>();

                // Iterate through all location entries
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    Location location = locationSnapshot.getValue(Location.class);

                    if (location != null) {
                        allLocations.add(location);
                    }
                }

                // If locations exist, update the map with all locations
                if (!allLocations.isEmpty()) {
                    updateMapWithAllLocations(allLocations);
                } else {
                    Toast.makeText(Management_dustbin_Activity.this, "No locations found in Firebase.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Management_dustbin_Activity.this, "Error fetching data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMapWithAllLocations(List<Location> allLocations) {
        // Prepare JavaScript code to add markers for all locations
        StringBuilder javascriptCode = new StringBuilder();

        // Iterate through all the locations and build JavaScript to display them
        for (Location location : allLocations) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            // Generate JavaScript code to add the marker
            javascriptCode.append("addMarker(").append(latitude).append(", ").append(longitude).append(");");
        }

        // Execute JavaScript to add all markers to the map
        if (javascriptCode.length() > 0) {
            webView.evaluateJavascript(javascriptCode.toString(), null);
        }
    }

    // JavaScript Interface to interact with WebView
    public class WebAppInterface {
        @JavascriptInterface
        public void updateLocation(double latitude, double longitude) {
            // This method can be used for any additional operations in Android if needed
        }
    }

    // Location class to represent latitude and longitude
    public static class Location {
        private double latitude;
        private double longitude;

        // Default constructor required for Firebase
        public Location() {}

        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
