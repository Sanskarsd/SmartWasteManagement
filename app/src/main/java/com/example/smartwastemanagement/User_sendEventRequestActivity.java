package com.example.smartwastemanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User_sendEventRequestActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference eventRef;
    private DatabaseReference lastEventIdRef;
    private DatabaseReference adminIdsRef;

    Button submitButton;
    EditText eventNameEditText, locationEditText, eventDescEditText, eventDateEditText, eventTimeEditText;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_send_event_request);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", "userID");
        String user_managmentId = sharedPreferences.getString("user_managmentId", "user_managmentId");

        // Initialize Firebase references
        database = FirebaseDatabase.getInstance();
        eventRef = database.getReference("EventRegisterManagement");
        lastEventIdRef = database.getReference("lastEventId");
        adminIdsRef = database.getReference("EventRegisterManagement"); // Reference for admin IDs

        eventNameEditText = findViewById(R.id.eventName);
        locationEditText = findViewById(R.id.location); // Location should be in lat,long format
        eventDescEditText = findViewById(R.id.eventDesc);
        eventDateEditText = findViewById(R.id.eventDate);
        eventTimeEditText = findViewById(R.id.eventTime);
        submitButton = findViewById(R.id.submit);

        submitButton.setOnClickListener(v -> {
            String eventName = eventNameEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim(); // Format: "lat,long"
            String eventDesc = eventDescEditText.getText().toString().trim();
            String eventDate = eventDateEditText.getText().toString().trim();
            String eventTime = eventTimeEditText.getText().toString().trim();

            // Validate fields
            if (TextUtils.isEmpty(userID) || TextUtils.isEmpty(eventName) || TextUtils.isEmpty(location) ||
                    TextUtils.isEmpty(eventDesc) || TextUtils.isEmpty(eventDate) || TextUtils.isEmpty(eventTime)) {
                Toast.makeText(User_sendEventRequestActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            String dateTimeString = eventDate + " " + eventTime;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                // Parse the date and time string to timestamp
                Date eventDateTime = sdf.parse(dateTimeString);
                long eventTimestamp = eventDateTime != null ? eventDateTime.getTime() : 0;

                // Check if user_managmentId matches newAdminId
                adminIdsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isAdmin = false;
                        String matchedAdminId = null;

                        // Loop through existing admins to check for a match
                        for (DataSnapshot adminSnapshot : dataSnapshot.getChildren()) {
                            String adminId = adminSnapshot.getKey();
                            if (user_managmentId.equals(adminId)) {
                                isAdmin = true;
                                matchedAdminId = adminId;
                                break;
                            }
                        }

                        if (isAdmin && matchedAdminId != null) {
                            // Reference the specific admin node using the matched adminId
                            DatabaseReference adminEventRef = eventRef.child(matchedAdminId);

                            // Generate a new event ID under the matched admin ID
                            DatabaseReference newEventRef = adminEventRef.push();

                            // Prepare event data
                            Map<String, Object> eventData = new HashMap<>();
                            eventData.put("userID", userID);
                            eventData.put("location", location); // Save the location as "lat,long"
                            eventData.put("eventName", eventName);
                            eventData.put("eventDesc", eventDesc);
                            eventData.put("eventTimestamp", eventTimestamp); // Store the event timestamp for sorting

                            // Add new fields with default values
                            eventData.put("eventStatus", "pending"); // Default status
                            eventData.put("employeeId", "empid");    // Default employeeId
                            eventData.put("employeeDesc", "empDesc"); // Default employee description

                            // Store the event data under the matched admin's unique ID
                            newEventRef.setValue(eventData);

                            // Notify the user
                            Toast.makeText(User_sendEventRequestActivity.this, "Event submitted successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Create a new admin node with the user_managmentId if not found
                            DatabaseReference newAdminRef = adminIdsRef.child(user_managmentId);

                            // Create a new event entry under the new admin
                            DatabaseReference newEventRef = newAdminRef.push();

                            // Prepare event data
                            Map<String, Object> eventData = new HashMap<>();
                            eventData.put("userID", userID);
                            eventData.put("location", location);
                            eventData.put("eventName", eventName);
                            eventData.put("eventDesc", eventDesc);
                            eventData.put("eventTimestamp", eventTimestamp);

                            // Add new fields with default values
                            eventData.put("eventStatus", "pending");
                            eventData.put("employeeId", "empid");
                            eventData.put("employeeDesc", "empDesc");

                            // Store the event data under the new admin's ID
                            newEventRef.setValue(eventData);

                            // Notify the user
                            Toast.makeText(User_sendEventRequestActivity.this, "Event submitted successfully, new admin created!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(User_sendEventRequestActivity.this, "Error checking admin ID", Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(User_sendEventRequestActivity.this, "Invalid date or time format", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
