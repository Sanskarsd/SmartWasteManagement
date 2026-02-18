package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Management_View_Map extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private FirebaseDatabase database;
    private DatabaseReference employeeRef;
    private ArrayList<String> matchingEmployeeIds;  // List to store employee IDs that match the management ID

    WebView webView;
    Spinner employeeSpinner;  // Spinner for selecting employee
    TextView textView;

    @SuppressLint({"MissingInflatedId", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_management_view_map);

        textView=findViewById(R.id.locationCarManagement);
        // Initialize SharedPreferences to get the management ID
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String managementID = sharedPreferences.getString("managementID", "managementID");

        // Initialize Firebase references
        database = FirebaseDatabase.getInstance();
        employeeRef = database.getReference("AllEmployee");

        // Initialize the list to store employee IDs
        matchingEmployeeIds = new ArrayList<>();

        // Initialize Spinner for employee selection
        employeeSpinner = findViewById(R.id.employeeSpinner);

        // Compare managementID with employeeManagmentId and retrieve employee IDs
        fetchEmployeeIdsForManagement(managementID);

        // Initialize WebView for showing the map
        webView = findViewById(R.id.webViewCarLocationManagement);
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
        webView.loadUrl("file:///android_asset/management_map_car_location.html");


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
                        // Update map coordinates in WebView
                        updateMapCoordinates(latestLatitude, latestLongitude);
                    } else {
                        Toast.makeText(Management_View_Map.this, "No valid location data found for today.", Toast.LENGTH_SHORT).show();
                        textView.setText("No valid location data found for today.");

                    }
                } else {
                    Toast.makeText(Management_View_Map.this, "No location data found for today.", Toast.LENGTH_SHORT).show();
                    textView.setText("No location data found for today.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("UserCarLocation", "Database Error: " + databaseError.getMessage());
                Toast.makeText(Management_View_Map.this, "Error retrieving location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMapCoordinates(double latitude, double longitude) {
        textView.setText(latitude+","+longitude);
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

    private void fetchEmployeeIdsForManagement(final String managementID) {
        // Add a listener to get all employees in the "AllEmployee" node
        employeeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Iterate through all the employees
                for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
                    // Get the employeeManagmentId for the current employee
                    String employeeManagmentId = employeeSnapshot.child("employeeManagmentId").getValue(String.class);

                    // Compare with the managementID from SharedPreferences
                    if (employeeManagmentId != null && employeeManagmentId.equals(managementID)) {
                        // If they match, retrieve the unique employee ID (parent node key)
                        String employeeId = employeeSnapshot.getKey(); // This is the unique ID of the employee

                        // Add the employee ID to the list of matching employee IDs
                        matchingEmployeeIds.add(employeeId);
                    }
                }

                // After checking all employees, set up the Spinner
                if (!matchingEmployeeIds.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Management_View_Map.this,
                            android.R.layout.simple_spinner_item, matchingEmployeeIds);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    employeeSpinner.setAdapter(adapter);

                    // Set an item selected listener for the Spinner
                    employeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            String selectedEmployeeId = matchingEmployeeIds.get(position);
                            Toast.makeText(Management_View_Map.this, "Selected Employee ID: " + selectedEmployeeId, Toast.LENGTH_SHORT).show();
                            // Optionally, you can call listenToLocationUpdates(selectedEmployeeId) to listen for that employee's location

                            listenToLocationUpdates(selectedEmployeeId);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // Handle case when nothing is selected, if needed
                        }
                    });
                } else {
                    Toast.makeText(Management_View_Map.this, "No matching employees found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible database errors
                Toast.makeText(Management_View_Map.this, "Failed to load employee data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

