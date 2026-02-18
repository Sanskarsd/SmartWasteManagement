package com.example.smartwastemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterEmployee1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_employee1);

        EditText firstNameEditText = findViewById(R.id.fnameemp);
        EditText lastNameEditText = findViewById(R.id.lnameemp);
        EditText dobEditText = findViewById(R.id.dob); // EditText for DOB
        RadioGroup genderGroup = findViewById(R.id.radio_grp);
        Button nextButton = findViewById(R.id.next);

        // Set up the submit button click listener
        nextButton.setOnClickListener(v -> {
            // Fetch data from views
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String dob = dobEditText.getText().toString().trim(); // Fetch DOB as a string
            int selectedGenderId = genderGroup.getCheckedRadioButtonId();
            String gender = "Not selected";

            if (selectedGenderId != -1) {
                RadioButton selectedGenderButton = findViewById(selectedGenderId);
                gender = selectedGenderButton.getText().toString();
            }

            // Validate inputs
            if (firstName.isEmpty() || lastName.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass the data to the next activity
            Intent i1 = new Intent(RegisterEmployee1.this, RegisterEmployee2.class);
            i1.putExtra("firstName", firstName);
            i1.putExtra("lastName", lastName);
            i1.putExtra("dob", dob);
            i1.putExtra("gender", gender);
            startActivity(i1);
        });

    }
}