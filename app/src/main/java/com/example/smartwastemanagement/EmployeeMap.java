package com.example.smartwastemanagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EmployeeMap extends Fragment {

    Switch switchButton;
    WebView webView;
    String employeeID;
    private FirebaseDatabase database1;
    private DatabaseReference userRef1;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee_map, container, false);

        // Get employee ID from SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", getActivity().MODE_PRIVATE);
         employeeID = sharedPreferences.getString("employeeID", "employeeId");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        // Initialize Firebase
        database1 = FirebaseDatabase.getInstance();
        userRef1 = database1.getReference("Employee_Map");

        // Initialize UI components
        switchButton = view.findViewById(R.id.switchButton);  // Assuming the Switch ID is 'switchButton'
        webView = view.findViewById(R.id.webView);  // Assuming the WebView ID is 'webView'



        // Set up WebView to load the map from assets
        setUpWebView();

        // Set the listener for the switch button
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Get driver ID input from the EditText
                database = FirebaseDatabase.getInstance();
                userRef = database.getReference("EmployeeLocation").child(employeeID);

                // Update UI based on switch status
                if (isChecked) {

                    // Check if permission is granted
                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE);
                    } else {
                        checkLocationServices();
                        startLocationUpdates();  // Start getting location updates
                    }

                    sendMapToFirebase(true);
                    // Show WebView when the switch is ON
                    webView.setVisibility(WebView.VISIBLE);
                } else {
                    sendMapToFirebase(false);
                    // Hide WebView when the switch is OFF
                    webView.setVisibility(WebView.GONE);
                }
            }
        });

        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebView() {
        // Enable JavaScript in WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Prevent external browsers from opening URLs
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // Load the HTML map from assets
        webView.loadUrl("file:///android_asset/employee_map.html");
    }

    private void sendMapToFirebase(boolean flag) {
        // Get the current date in "yyyy-MM-dd" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Get the current timestamp (in milliseconds)
        long timestamp = System.currentTimeMillis();

        // Reference to the Map node for today's date
        DatabaseReference dateRef = userRef1.child(currentDate);

        // Create a Map data structure to hold flag, timestamp, and driverId
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("flag", flag);
        mapData.put("timestamp", timestamp);

        // Push the map data under the driverId key for today's date
        dateRef.child(employeeID).setValue(mapData)
                .addOnSuccessListener(aVoid -> {
                    // Success Toast
                    Toast.makeText(getActivity(), "Map saved to Firebase", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failure Toast
                    Toast.makeText(getActivity(), "Error saving Map to Firebase", Toast.LENGTH_SHORT).show();
                });
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationServices();
                startLocationUpdates();  // Permission granted, fetch location
            } else {
                Toast.makeText(getActivity(), "Permission Denied. Cannot access location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Check if location services are enabled
    private void checkLocationServices() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isLocationEnabled) {
            Toast.makeText(getActivity(), "Please enable location services", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    // Start requesting location updates every 20 seconds
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
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // Send the location to Firebase
                    sendLocationToFirebase(latitude, longitude);
                    // Call JavaScript function showLocation(lat, lon) in WebView
                    String script = "javascript:showLocation(" + latitude + ", " + longitude + ")";
                    webView.evaluateJavascript(script, null);
                } else {
                    Toast.makeText(getActivity(), "Locations are not available.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Request location updates
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper());  // Ensure it runs on the main thread
    }

    // Send location data to Firebase Realtime Database (with unique numerical key)
    private void sendLocationToFirebase(double latitude, double longitude) {
        // Get current date in "yyyy-MM-dd" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Reference to the location data node for today
        DatabaseReference dateRef = userRef.child(currentDate);

        // Get the counter from the Firebase database
        dateRef.child("counter").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the current counter value, or default to 0 if it doesn't exist
                long counter = task.getResult().exists() ? task.getResult().getValue(Long.class) : 0;

                // Create a new location entry using the counter value
                LocationData locationData = new LocationData(latitude, longitude, System.currentTimeMillis());

                // Save the location data with the counter as the key
                dateRef.child(String.valueOf(counter + 1)).setValue(locationData)  // Using the counter as the unique key
                        .addOnSuccessListener(aVoid -> {
                            // Update the counter in Firebase
                            dateRef.child("counter").setValue(counter + 1);

                            Toast.makeText(getActivity(), "Location saved to Firebase", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Error saving location to Firebase", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getActivity(), "Error fetching counter from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // LocationData class to represent location data
    public static class LocationData {
        private double latitude;
        private double longitude;
        private long timestamp;

        public LocationData(double latitude, double longitude, long timestamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.timestamp = timestamp;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
