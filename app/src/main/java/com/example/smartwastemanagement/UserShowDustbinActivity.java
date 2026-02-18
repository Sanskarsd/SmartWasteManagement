package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserShowDustbinActivity extends AppCompatActivity {

    WebView webView;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_show_dustbin);

        // Initialize the WebView
        webView = findViewById(R.id.webViewSample);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();

        // Enable JavaScript in WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set WebViewClient to handle URL loading within the WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Once the page is fully loaded, fetch and display locations
                fetchAllLocationsFromFirebase();
            }
        });

        // Load the HTML file from the assets folder
        webView.loadUrl("file:///android_asset/user_dustbin_map.html");
    }

    private void fetchAllLocationsFromFirebase() {
        // Reference to the "Dustbin" locations node in Firebase
        DatabaseReference locationsRef = database.getReference("Dustbin").child("locations");

        // Listen for location updates
        locationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Location> allLocations = new ArrayList<>();

                // Iterate through all location entries and convert to Location objects
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    double latitude = locationSnapshot.child("latitude").getValue(Double.class);
                    double longitude = locationSnapshot.child("longitude").getValue(Double.class);

                    // Create a Location object and add to the list
                    Location location = new Location(latitude, longitude);
                    allLocations.add(location);
                }

                // If locations exist, update the map with all locations
                if (!allLocations.isEmpty()) {
                    updateMapWithAllLocations(allLocations);
                } else {
                    Toast.makeText(UserShowDustbinActivity.this, "No locations found in Firebase.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserShowDustbinActivity.this, "Error fetching data from Firebase", Toast.LENGTH_SHORT).show();
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
            // Ensure the WebView has loaded before evaluating JavaScript
            webView.evaluateJavascript(javascriptCode.toString(), null);
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
