package com.example.smartwastemanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserAboutPointsA extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference pointRef;
    private TextView ewasteTextView, metalTextView, plasticTextView, glassTextView, paperTextView, organicTextView, mixedTextView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_about_points);

        // Initialize SharedPreferences to retrieve the management ID
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String user_managmentId = sharedPreferences.getString("user_managmentId", "user_managmentId");

        // Optionally, use these values (e.g., display in a Toast or log)
        if (user_managmentId != null) {
            Toast.makeText(this, "Management ID: " + user_managmentId, Toast.LENGTH_SHORT).show();
        }

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        pointRef = database.getReference("Points").child(user_managmentId);

        // Initialize the TextViews
        ewasteTextView = findViewById(R.id.ewaste);
        metalTextView = findViewById(R.id.metal);
        plasticTextView = findViewById(R.id.plastic);
        glassTextView = findViewById(R.id.glass);
        paperTextView = findViewById(R.id.paper);
        organicTextView = findViewById(R.id.organic);
        mixedTextView = findViewById(R.id.mixed);

        // Fetch the data from Firebase and update the TextViews
        pointRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the data is available
                if (dataSnapshot.exists()) {
                    // Get the PointsData object
                    PointsData pointsData = dataSnapshot.getValue(PointsData.class);

                    if (pointsData != null) {
                        // Set the values from PointsData object to the TextViews
                        ewasteTextView.setText("E-waste - "+pointsData.getEwaste());
                        metalTextView.setText("Metal - "+pointsData.getMetal());
                        plasticTextView.setText("Plastic - "+pointsData.getPlastic());
                        glassTextView.setText("Glass - "+pointsData.getGlass());
                        paperTextView.setText("Paper - "+pointsData.getPaper());
                        organicTextView.setText("Organic - "+pointsData.getOrganic());
                        mixedTextView.setText("Mixed - "+pointsData.getMixed());
                    }
                } else {
                    // If no data is found, show a message
                    Toast.makeText(UserAboutPointsA.this, "No data found for this management ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(UserAboutPointsA.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

 class PointsData {
    private String Ewaste;
    private String Metal;
    private String Plastic;
    private String Glass;
    private String Paper;
    private String Organic;
    private String Mixed;

    // Empty constructor required for Firebase
    public PointsData() {
    }

    // Getters and Setters
    public String getEwaste() {
        return Ewaste;
    }

    public void setEwaste(String ewaste) {
        Ewaste = ewaste;
    }

    public String getMetal() {
        return Metal;
    }

    public void setMetal(String metal) {
        Metal = metal;
    }

    public String getPlastic() {
        return Plastic;
    }

    public void setPlastic(String plastic) {
        Plastic = plastic;
    }

    public String getGlass() {
        return Glass;
    }

    public void setGlass(String glass) {
        Glass = glass;
    }

    public String getPaper() {
        return Paper;
    }

    public void setPaper(String paper) {
        Paper = paper;
    }

    public String getOrganic() {
        return Organic;
    }

    public void setOrganic(String organic) {
        Organic = organic;
    }

    public String getMixed() {
        return Mixed;
    }

    public void setMixed(String mixed) {
        Mixed = mixed;
    }
}

