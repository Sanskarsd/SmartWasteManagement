package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserCarLocationActivity extends AppCompatActivity {

    private TextView locationDisplay;
    private WebView webView;
    private FirebaseDatabase database;
    private String userEmployeeId;
    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_car_location);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        // Retrieve the user_employeeId from SharedPreferences
        userEmployeeId = sharedPreferences.getString("user_employeeId", "defaultEmployeeId");

        locationDisplay = findViewById(R.id.locationDisplay);  // TextView to display coordinates

        // Initialize Firebase instance
        database = FirebaseDatabase.getInstance();

        // Initialize WebView for showing the map
        webView = findViewById(R.id.webViewCarLocation);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set up WebViewClient to handle page loading and JavaScript execution
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        // Set up WebChromeClient to capture console messages from the WebView
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                Log.d("WebViewConsole", message + " -- From line " + lineNumber + " of " + sourceID);
            }
        });

        // Add JavaScript interface to interact with WebView
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        // Load the HTML file for displaying car location map
        webView.loadUrl("file:///android_asset/user_map_car_location.html");

        // Check if the user has a valid employee ID
        if (!userEmployeeId.isEmpty()) {
            listenToLocationUpdates(userEmployeeId);  // Listen for updates on the driver's location
        } else {
            Toast.makeText(this, "Employee ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void listenToLocationUpdates(String employeeId) {
        // Get the current date in "yyyy-MM-dd" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Reference to the "EmployeeLocation" node for this employee and today's date
        DatabaseReference employeeLocationRef = database.getReference("EmployeeLocation").child(employeeId).child(currentDate);

        // Listen for location updates for the given employee on the current date
        employeeLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double latestLatitude = null;
                    Double latestLongitude = null;

                    // Iterate through all location entries for today
                    for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                        Double latitude = locationSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = locationSnapshot.child("longitude").getValue(Double.class);

                        // Update the latest latitude and longitude
                        if (latitude != null && longitude != null) {
                            latestLatitude = latitude;
                            latestLongitude = longitude;
                        }
                    }

                    // If valid coordinates are found, update the UI
                    if (latestLatitude != null && latestLongitude != null) {
                        // Display coordinates in the TextView
                        locationDisplay.setText("Latitude: " + latestLatitude + "\nLongitude: " + latestLongitude);

                        // Update map coordinates in WebView
                        updateMapCoordinates(latestLatitude, latestLongitude);
                    } else {
                        Toast.makeText(UserCarLocationActivity.this, "No valid location data found for today.", Toast.LENGTH_SHORT).show();
                        locationDisplay.setText("No valid location data found for today.");
                    }
                } else {
                    Toast.makeText(UserCarLocationActivity.this, "No location data found for today.", Toast.LENGTH_SHORT).show();
                    locationDisplay.setText("No location data found for today.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("UserCarLocation", "Database Error: " + databaseError.getMessage());
                Toast.makeText(UserCarLocationActivity.this, "Error retrieving location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMapCoordinates(double latitude, double longitude) {
        // Send the coordinates to the WebView using JavaScript interface
        webView.evaluateJavascript("showCarLocation(" + latitude + ", " + longitude + ");", null);
    }

    // JavaScript Interface to update map in WebView
    public class WebAppInterface {
        @JavascriptInterface
        public void updateLocation(double latitude, double longitude) {
            // This method can be used to handle any additional operations if needed.
        }
    }
}
