package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmployeeProfile extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference pointRef;
    SharedPreferences sharedPreferences;

    // Declare TextViews to show employee data
    TextView managmentId, fname, lname, mobile, email, dob, gender, location, username;
    ImageView imageView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee_profile, container, false);


        // Initialize UI elements
        managmentId = view.findViewById(R.id.employeeManagementid);
        fname = view.findViewById(R.id.fname);
        lname = view.findViewById(R.id.lname);
        mobile = view.findViewById(R.id.mobile);
        email = view.findViewById(R.id.email);
        dob = view.findViewById(R.id.dob);
        gender = view.findViewById(R.id.gender);
        location = view.findViewById(R.id.location);
        username = view.findViewById(R.id.username);
        imageView=view.findViewById(R.id.profileImage);

        // Get employee ID from SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", getActivity().MODE_PRIVATE);
        String employeeID = sharedPreferences.getString("employeeID", "employeeId");

        if (employeeID != null) {
            Toast.makeText(getActivity(), "Fragment Profile employeeID : " + employeeID, Toast.LENGTH_SHORT).show();
        }

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        pointRef = database.getReference("AllEmployee").child(employeeID);

        // Fetch data from Firebase
        pointRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the employee data exists
                if (dataSnapshot.exists()) {
                    // Populate fields with Firebase data
                    String managementIdValue = dataSnapshot.child("employeeManagmentId").getValue(String.class);
                    String fnameValue = dataSnapshot.child("employeeFname").getValue(String.class);
                    String lnameValue = dataSnapshot.child("employeeLname").getValue(String.class);
                    String mobileValue = dataSnapshot.child("employeeMobile").getValue(String.class);
                    String emailValue = dataSnapshot.child("employeeEmail").getValue(String.class);
                    String dobValue = dataSnapshot.child("employeeDob").getValue(String.class);
                    String genderValue = dataSnapshot.child("employeeGender").getValue(String.class);
                    String locationValue = dataSnapshot.child("employeeLocation").getValue(String.class);
                    String usernameValue = dataSnapshot.child("employeeUsername").getValue(String.class);

                    // Set the data to the TextViews
                    managmentId.setText(managementIdValue);
                    fname.setText(fnameValue);
                    lname.setText(lnameValue);
                    mobile.setText(mobileValue);
                    email.setText(emailValue);
                    dob.setText(dobValue);
                    gender.setText(genderValue);
                    location.setText(locationValue);
                    username.setText(usernameValue);
                    changeProfile(genderValue);
                } else {
                    // If the employee data doesn't exist
                    Toast.makeText(getActivity(), "No data found for the employee.", Toast.LENGTH_SHORT).show();
                }
            }

            private void changeProfile(String gender) {

                if(gender.equals("Male"))
                {
                    imageView.setBackgroundResource(R.drawable.profile);

                } else if (gender.equals("Female"))
                {
                    imageView.setBackgroundResource(R.drawable.profile2);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
                Toast.makeText(getActivity(), "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
