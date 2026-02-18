package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button submit1;
    String userID, managementID, employeeID,user_employeeId,user_gender,user_fname;

    // SharedPreferences to store the IDs
     SharedPreferences sharedPreferences,sharedPreferencesDirectLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);         EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize views
        username = findViewById(R.id.usernameS);
        password = findViewById(R.id.passwordS);
        submit1 = findViewById(R.id.submitS1);


        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        sharedPreferencesDirectLogin = getSharedPreferences("DirectLogin", MODE_PRIVATE);
        String DirectLoginUser = sharedPreferencesDirectLogin.getString("DirectLoginUser", "DirectLoginUser");
        if(DirectLoginUser.equals("true"))
        {
           Intent intent=new Intent(LoginActivity.this, MainActivity.class);
           startActivity(intent);
        }

        // Set the initial values in SharedPreferences as blank
//        setInitialSharedPreferences();


        // Set the button click listener
        submit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String usernameRegexx1 = ".*@.*";
                String usernameRegexx2 = ".*#.*";
                String usernameRegexx3 = ".*\\$.*";


                if(user.contains("admin@123") && pass.contains("admin@123"))
                {
                    Toast.makeText(LoginActivity.this, "admin@123", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(LoginActivity.this,AdminActivity.class);
                    startActivity(i);
                }
                else
                {
                    if (user.matches(usernameRegexx1))
                    {
                        Toast.makeText(LoginActivity.this, "user @ login", Toast.LENGTH_SHORT).show();
                        checkSignIN1();
                    }
                    else if (user.matches(usernameRegexx2))
                    {
                        Toast.makeText(LoginActivity.this, "Employee # login", Toast.LENGTH_SHORT).show();
                        checkSignIN2();
                    }
                    else if (user.matches(usernameRegexx3))
                    {
                        Toast.makeText(LoginActivity.this, "Management $ login", Toast.LENGTH_SHORT).show();
                        checkSignIN3();
                    }
                }

            }
        });


    }


    // Function to set initial values of the IDs in SharedPreferences to blank
//    private void setInitialSharedPreferences() {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("userID", "");  // Set userID to blank
//        editor.putString("managementID", "");  // Set managementID to blank
//        editor.putString("employeeID", "");  // Set employeeID to blank
//        editor.putString("user_employeeId", "");
//        editor.putString("user_gender", "");
//        editor.putString("user_fname", "");
//        editor.apply();
//    }

    private void checkSignIN1() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        // Check if both fields are not empty
        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the reference to Firebase's PrimaryData
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PrimaryData").child("UserData");

        // Query the database to find the matching username
        databaseReference.orderByChild("Username").equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if data is found
                if (dataSnapshot.exists()) {
                    // Iterate through the matching records
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Retrieve the stored password from Firebase
                        String storedPassword = snapshot.child("password").getValue(String.class);
                        if (storedPassword != null && storedPassword.equals(pass)) {
                            // Retrieve the userId (which is the parent key of the matched record)
                            userID = snapshot.getKey(); // This will give you the userId (the key of the child node)

                            // Retrieve the employeeID (if it exists)
                             user_employeeId = snapshot.child("employeeID").getValue(String.class);
                             user_gender = snapshot.child("gender").getValue(String.class);
                            user_fname = snapshot.child("fName").getValue(String.class);
                            // Save the userID in SharedPreferences
                            saveIDInSharedPreferences("userID", userID);
                            saveIDInSharedPreferences("user_employeeId", user_employeeId);
                            saveIDInSharedPreferences("user_gender", user_gender);
                            saveIDInSharedPreferences("user_fname", user_fname);
                            // Display the userId in a Toast
                            Toast.makeText(LoginActivity.this, "User ID: " + userID, Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "User's employee ID: " + user_employeeId, Toast.LENGTH_SHORT).show();

                            sharedPreferencesDirectLogin = getSharedPreferences("DirectLogin", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferencesDirectLogin.edit();
                            editor.putString("DirectLoginUser", "true");
                            editor.apply();  // Apply the changes

                            // Navigate to MainActivity after successful login
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();  // Close the LoginActivity so the user can't go back to it

                            return;  // Stop iterating once the match is found
                        }
                    }

                    // If no password match is found
                    Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                } else {
                    // If no user with the given username exists
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(LoginActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkSignIN2() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        // Check if both fields are not empty
        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the reference to Firebase's AllEmployee
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("AllEmployee");

        // Query the database to find the matching username (employeeUsername)
        databaseReference.orderByChild("employeeUsername").equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if data is found
                if (dataSnapshot.exists()) {
                    // Iterate through the matching records
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Retrieve the stored password from Firebase
                        String storedPassword = snapshot.child("employeePassword").getValue(String.class);
                        if (storedPassword != null && storedPassword.equals(pass)) {
                            // Retrieve the employeeID (which is the parent key of the matched record)
                            employeeID = snapshot.getKey(); // This will give you the employeeID (the key of the child node)

                            // Save the managementID in SharedPreferences
                            saveIDInSharedPreferences("employeeID", employeeID);
                            // Display the employeeID in a Toast
                            Toast.makeText(LoginActivity.this, "Employee ID: " + employeeID, Toast.LENGTH_SHORT).show();

                            // Navigate to MainActivity after successful login
                            Intent intent = new Intent(LoginActivity.this, Employee_Activity.class);
                            startActivity(intent);
                            finish();  // Close the LoginActivity so the user can't go back to it

                            return;  // Stop iterating once the match is found
                        }
                    }

                    // If no password match is found
                    Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                } else {
                    // If no user with the given username exists
                    Toast.makeText(LoginActivity.this, "Employee not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(LoginActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkSignIN3() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        // Check if both fields are not empty
        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the reference to Firebase's Admin
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Admin");

        // Query the database to find the matching username (managementUsername)
        databaseReference.orderByChild("managementUsername").equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if data is found
                if (dataSnapshot.exists()) {
                    // Iterate through the matching records
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Retrieve the stored password from Firebase
                        String storedPassword = snapshot.child("managementPassword").getValue(String.class);
                        if (storedPassword != null && storedPassword.equals(pass)) {
                            // Retrieve the managementID (which is the parent key of the matched record)
                            managementID = snapshot.getKey(); // This will give you the managementID (the key of the child node)
                            String managementName = snapshot.child("managementName").getValue(String.class);

                            // Save the managementID in SharedPreferences
                            saveIDInSharedPreferences("managementID", managementID);
                            saveIDInSharedPreferences("managementName", managementName);
                            // Display the managementID in a Toast
                            Toast.makeText(LoginActivity.this, "Management ID: " + managementID, Toast.LENGTH_SHORT).show();


                            // Navigate to MainActivity after successful login
                            Intent intent = new Intent(LoginActivity.this, ManagementMainActivity.class);
                            startActivity(intent);
                            finish();  // Close the LoginActivity so the user can't go back to it

                            return;  // Stop iterating once the match is found
                        }
                    }

                    // If no password match is found
                    Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                } else {
                    // If no user with the given username exists
                    Toast.makeText(LoginActivity.this, "Admin not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(LoginActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Method to save ID in SharedPreferences
    private void saveIDInSharedPreferences(String key, String id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, id);
        editor.apply();  // Apply the changes
    }

}
