package com.example.smartwastemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegistrationActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration2);
        EditText mobileEditText = findViewById(R.id.mob);
        EditText emailEditText = findViewById(R.id.email);
        Button nextButton = findViewById(R.id.next); // Make sure your XML Button has this ID

        // Set click listener for the Submit button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch data from EditText fields
                String mobile = mobileEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();

                Intent i1 = new Intent(RegistrationActivity2.this, RegistrationActivity3.class);
                i1.putExtra("firstName", getIntent().getStringExtra("firstName"));
                i1.putExtra("lastName", getIntent().getStringExtra("lastName"));
                i1.putExtra("dob", getIntent().getStringExtra("dob"));
                i1.putExtra("gender", getIntent().getStringExtra("gender"));
                i1.putExtra("mobile", mobile);
                i1.putExtra("email", email);
                startActivity(i1);


            }
        });
    }

    }


