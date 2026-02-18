package com.example.smartwastemanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManagementCustomizePoints extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference eventRef;
    private EditText ewaste, metal, plastic, glass, paper, organic, mixed;
    private Button submitButton;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_customize_points);

        // Initialize SharedPreferences to retrieve the management ID
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String managementID = sharedPreferences.getString("managementID", "managementID");

        // Optionally, show the management ID in a Toast or log
        if (managementID != null) {
            Toast.makeText(this, "Management ID: " + managementID, Toast.LENGTH_SHORT).show();
        }

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        // Use the management ID in the path to store the data
        eventRef = database.getReference("Points").child(managementID);

        // Initialize UI components
        ewaste = findViewById(R.id.ewaste);
        metal = findViewById(R.id.metal);
        plastic = findViewById(R.id.plastic);
        glass = findViewById(R.id.glass);
        paper = findViewById(R.id.paper);
        organic = findViewById(R.id.organic);
        mixed = findViewById(R.id.mixed);
        submitButton = findViewById(R.id.submit);

        // Set onClickListener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture values from EditTexts
                String ewasteValue = ewaste.getText().toString().trim();
                String metalValue = metal.getText().toString().trim();
                String plasticValue = plastic.getText().toString().trim();
                String glassValue = glass.getText().toString().trim();
                String paperValue = paper.getText().toString().trim();
                String organicValue = organic.getText().toString().trim();
                String mixedValue = mixed.getText().toString().trim();

                // Check if all fields have data
                if (ewasteValue.isEmpty() || metalValue.isEmpty() || plasticValue.isEmpty() ||
                        glassValue.isEmpty() || paperValue.isEmpty() || organicValue.isEmpty() || mixedValue.isEmpty()) {
                    // Show a message if any field is empty
                    Toast.makeText(ManagementCustomizePoints.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Store the data under the management ID node in Firebase
                    eventRef.child("Ewaste").setValue(ewasteValue);
                    eventRef.child("Metal").setValue(metalValue);
                    eventRef.child("Plastic").setValue(plasticValue);
                    eventRef.child("Glass").setValue(glassValue);
                    eventRef.child("Paper").setValue(paperValue);
                    eventRef.child("Organic").setValue(organicValue);
                    eventRef.child("Mixed").setValue(mixedValue);

                    // Show a confirmation message
                    Toast.makeText(ManagementCustomizePoints.this, "Data Submitted Successfully", Toast.LENGTH_SHORT).show();

                    // Optionally, clear the EditText fields after submission
                    ewaste.setText("");
                    metal.setText("");
                    plastic.setText("");
                    glass.setText("");
                    paper.setText("");
                    organic.setText("");
                    mixed.setText("");
                }
            }
        });
    }
}
