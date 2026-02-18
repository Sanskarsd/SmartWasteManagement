package com.example.smartwastemanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterEmployee3 extends AppCompatActivity {

    private TextView locationTextView;
    private EditText passwordEditText;
    private EditText reEnterPasswordEditText;
    private EditText usernameEditText;
    private Button submitButton;

    SharedPreferences sharedPreferences;
    private FirebaseDatabase database;
    private DatabaseReference eventRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_employee3);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        // Retrieve the userID and user_employeeId from the Intent
        String managementID = sharedPreferences.getString("managementID","managementID");

        // Bind views
        locationTextView = findViewById(R.id.address);
        passwordEditText = findViewById(R.id.pass);
        reEnterPasswordEditText = findViewById(R.id.repass);
        usernameEditText = findViewById(R.id.employeeIdAll);
        submitButton = findViewById(R.id.submit);

        database = FirebaseDatabase.getInstance();
        eventRef = database.getReference("AllEmployee");

        // Fetch data passed from previous activities
        String fname1 = getIntent().getStringExtra("firstName");
        String lname1 = getIntent().getStringExtra("lastName");
        String mobile1 = getIntent().getStringExtra("mobile");
        String email1 = getIntent().getStringExtra("email");
        String dob1 = getIntent().getStringExtra("dob");
        String gender1 = getIntent().getStringExtra("gender");


        // Set up the submit button click listener

        submitButton.setOnClickListener(v -> {
            // Fetch input data after user interaction
            String username1 = usernameEditText.getText().toString().trim();
            String password1 = passwordEditText.getText().toString().trim();
            String reEnteredPassword = reEnterPasswordEditText.getText().toString().trim();
            String location1 = locationTextView.getText().toString().trim();


            String usernameRegex1 = ".*#.*";

            if (!username1.matches(usernameRegex1)) {
                usernameEditText.setError("Username must contain at least one '#' character.");
                usernameEditText.requestFocus();
                return;
            }


            if (TextUtils.isEmpty(password1)) {
                passwordEditText.setError("Password is required");
                passwordEditText.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(reEnteredPassword)) {
                reEnterPasswordEditText.setError("Please re-enter your password");
                reEnterPasswordEditText.requestFocus();
                return;
            }

            if (!password1.equals(reEnteredPassword)) {
                reEnterPasswordEditText.setError("Passwords do not match");
                reEnterPasswordEditText.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(location1) || location1.equals("No location provided")) {
                Toast.makeText(this, "Please select a valid location", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fname1 == null || lname1 == null || mobile1 == null || email1 == null || dob1 == null || gender1 == null) {
                Toast.makeText(this, "Some required fields are missing", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                // Generate a unique ID using Firebase's push() method
                DatabaseReference newEventRef = eventRef.push();

                // Prepare event data
                Map<String, Object> empData = new HashMap<>();
                empData.put("employeeManagmentId", managementID);
                empData.put("employeeFname", fname1);
                empData.put("employeeLname", lname1);
                empData.put("employeeMobile", mobile1);
                empData.put("employeeEmail", email1);
                empData.put("employeeDob", dob1);
                empData.put("employeeGender", gender1);
                empData.put("employeeLocation", location1);
                empData.put("employeeUsername", username1);  // Save the username entered by the user
                empData.put("employeePassword", password1);

                // Store the event data under the new unique ID
                newEventRef.setValue(empData);

                // Notify the user
                Toast.makeText(RegisterEmployee3.this, "Employee added successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(RegisterEmployee3.this, "Employee not added", Toast.LENGTH_SHORT).show();
            }
        });

    }
}