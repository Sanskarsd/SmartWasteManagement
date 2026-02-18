package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity3 extends AppCompatActivity {

    private TextView locationTextView;
    private EditText passwordEditText;
    private EditText reEnterPasswordEditText;
    private EditText usernameEditText;
    private Button submitButton;

    private DatabaseReference databaseReference;

    SharedPreferences sharedPreferences;
    static final int REQUEST_CODE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration3);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        // Retrieve the userID and user_employeeId from the Intent
        String employeeID = sharedPreferences.getString("employeeID","emoloyeeId");

        // Bind views
        locationTextView = findViewById(R.id.lat);
        passwordEditText = findViewById(R.id.pass);
        reEnterPasswordEditText = findViewById(R.id.repass);
        usernameEditText = findViewById(R.id.employeeIdAll);
        submitButton = findViewById(R.id.submit);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("PrimaryData").child("UserData");

        // Fetch data passed from previous activities
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String mobileNo = getIntent().getStringExtra("mobile");
        String email = getIntent().getStringExtra("email");
        String dob = getIntent().getStringExtra("dob");
        String gender = getIntent().getStringExtra("gender");

        // Set up the submit button click listener
        submitButton.setOnClickListener(v -> {
            // Fetch input data
            String password = passwordEditText.getText().toString().trim();
            String reEnteredPassword = reEnterPasswordEditText.getText().toString().trim();
            String location = locationTextView.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();


            String usernameRegex = ".*@.*";

            if (!username.matches(usernameRegex)) {
                usernameEditText.setError("Username must contain at least one '@' character.");
                usernameEditText.requestFocus();
                return;
            }



            if (TextUtils.isEmpty(password)) {
                passwordEditText.setError("Password is required");
                passwordEditText.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(reEnteredPassword)) {
                reEnterPasswordEditText.setError("Please re-enter your password");
                reEnterPasswordEditText.requestFocus();
                return;
            }

            if (!password.equals(reEnteredPassword)) {
                reEnterPasswordEditText.setError("Passwords do not match");
                reEnterPasswordEditText.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(location) || location.equals("No location provided")) {
                Toast.makeText(this, "Please select a valid location", Toast.LENGTH_SHORT).show();
                return;
            }

            if (firstName == null || lastName == null || mobileNo == null || email == null || dob == null || gender == null) {
                Toast.makeText(this, "Some required fields are missing", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save data to Firebase
            getNextUserIdAndSaveData(username,firstName, lastName, mobileNo, email, dob, gender, password, location,employeeID);
        });
    }

    private void getNextUserIdAndSaveData(String UserName,String firstName, String lastName, String mobileNo, String email, String dob, String gender, String password, String location,String employeeID) {
        databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int newUserId = 1;

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        newUserId = Integer.parseInt(dataSnapshot.getKey()) + 1;
                    }
                }

                saveUserData(newUserId,UserName, firstName, lastName, mobileNo, email, dob, gender, password, location,employeeID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegistrationActivity3.this, "Error fetching user ID: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(int userId, String UserName,String firstName, String lastName, String mobileNo, String email, String dob, String gender, String password, String location, String employeeID) {
        databaseReference.child(String.valueOf(userId)).setValue(new User(UserName,firstName, lastName, mobileNo, email, dob, gender, password, location,employeeID))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity3.this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegistrationActivity3.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getUserCoordinates(View view) {
        Intent intent = new Intent(RegistrationActivity3.this, Signup_Map_Activity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String location = data.getStringExtra("location");
            if (location != null && !location.isEmpty()) {
                locationTextView.setText(location);
            } else {
                locationTextView.setText("No location provided");
            }
        }
    }

    public static class User {
        public String Username,fName, lName, mobileNo, email, dob, gender, password, location,employeeID;

        public User(String Username,String fName, String lName, String mobileNo, String email, String dob, String gender, String password, String location, String employeeID) {
            this.Username = Username;
            this.fName = fName;
            this.lName = lName;
            this.mobileNo = mobileNo;
            this.email = email;
            this.dob = dob;
            this.gender = gender;
            this.password = password;
            this.location = location;
            this.employeeID = employeeID;
        }
    }
}
