package com.example.smartwastemanagement;


import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EmployeeHome extends Fragment {

    private EditText  userId, wasteEwaste, wastePaper, wasteGlass, wasteMetal, wastePlastic, wasteOrganic, wasteMixed;
     Button submitButton;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference;
    String emp_managmentId,userIdInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        databaseReference = FirebaseDatabase.getInstance().getReference("statistics");
        sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        // Retrieve the userID and user_employeeId from the Intent
        String employeeID = sharedPreferences.getString("employeeID","emoloyeeId");

        // Optionally, use these values (e.g., display in a Toast or log)
        if (employeeID != null) {
            Toast.makeText(getActivity(), " Fragment employeeID : " + employeeID , Toast.LENGTH_SHORT).show();
        }

        // Check if employeeId matches a unique ID in Firebase
        checkEmployeeId(employeeID);
        sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        emp_managmentId = sharedPreferences.getString("empp_managmentId","emp_managmentId");
        Toast.makeText(getActivity(), "fragment,Manage ID: " + emp_managmentId, Toast.LENGTH_SHORT).show();


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee_home, container, false);
        userId = view.findViewById(R.id.useridEmpHome); // User ID field (third field in XML)
        wasteEwaste = view.findViewById(R.id.wasteewasteEmpHome);
        wastePaper = view.findViewById(R.id.wastepaperEmpHome);
        wasteGlass = view.findViewById(R.id.wasteglassEmpHome);
        wasteMetal = view.findViewById(R.id.wastemetalEmpHome);
        wastePlastic = view.findViewById(R.id.wasteplasticEmpHome);
        wasteOrganic = view.findViewById(R.id.wasteorganicEmpHome);
        wasteMixed = view.findViewById(R.id.wastemixedEmpHome);
        submitButton = view.findViewById(R.id.wastesubmitButtonEmpHome);

        // Set submit button listener
        submitButton.setOnClickListener(v -> {
            try {
                // Fetch user inputs
                String employeeId = employeeID;
                String managerId = emp_managmentId;
                userIdInput = userId.getText().toString().trim(); // Fetch user ID
                String ewaste = wasteEwaste.getText().toString().trim();
                String paper = wastePaper.getText().toString().trim();
                String glass = wasteGlass.getText().toString().trim();
                String metal = wasteMetal.getText().toString().trim();
                String plastic = wastePlastic.getText().toString().trim();
                String organic = wasteOrganic.getText().toString().trim();
                String mixed = wasteMixed.getText().toString().trim();

                // Parse waste amounts to integers
                int ewasteAmount = Integer.parseInt(ewaste);
                int paperAmount = Integer.parseInt(paper);
                int glassAmount = Integer.parseInt(glass);
                int metalAmount = Integer.parseInt(metal);
                int plasticAmount = Integer.parseInt(plastic);
                int organicAmount = Integer.parseInt(organic);
                int mixedAmount = Integer.parseInt(mixed);

                // Validate inputs
                if (employeeId.isEmpty() || managerId.isEmpty() || userIdInput.isEmpty()) {
                    Toast.makeText(getActivity(), "All IDs (Employee, Manager, User) must be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Calculate total points
                int totalPoints = ewasteAmount * 5 + paperAmount * 2 + glassAmount * 5 +
                        metalAmount * 3 + plasticAmount * 7 + organicAmount * 4 +
                        mixedAmount * 1;

                // Get current date information
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String currentMonthYear = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
                String currentDay = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());

                // Initialize Firebase references
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference leaderboardRef = database.getReference("LeaderBoard").child(userIdInput);
                DatabaseReference userRef = database.getReference("PrimaryData").child("UserData").child(userIdInput);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String fName = snapshot.child("fName").getValue(String.class);
                            String lName = snapshot.child("lName").getValue(String.class);

                            // Save leaderboard data under userId
                            DatabaseReference dailyRef = leaderboardRef.child(currentMonthYear).child(currentDay);
                            dailyRef.child("EmployeeId").setValue(employeeId);
                            dailyRef.child("ManagerId").setValue(managerId);
                            dailyRef.child("EWaste").setValue(ewasteAmount);
                            dailyRef.child("EWastePoints").setValue(ewasteAmount * 5);
                            dailyRef.child("Paper").setValue(paperAmount);
                            dailyRef.child("PaperPoints").setValue(paperAmount * 2);
                            dailyRef.child("Plastic").setValue(plasticAmount);
                            dailyRef.child("PlasticPoints").setValue(plasticAmount * 7);
                            dailyRef.child("Organic").setValue(organicAmount);
                            dailyRef.child("OrganicPoints").setValue(organicAmount * 4);
                            dailyRef.child("Mixed").setValue(mixedAmount);
                            dailyRef.child("MixedPoints").setValue(mixedAmount * 1);
                            dailyRef.child("Glass").setValue(glassAmount);
                            dailyRef.child("GlassPoints").setValue(glassAmount * 5);
                            dailyRef.child("Metal").setValue(metalAmount);
                            dailyRef.child("MetalPoints").setValue(metalAmount * 3);

                            // Store fName and lName in the leaderboard
                            leaderboardRef.child("fName").setValue(fName);
                            leaderboardRef.child("lName").setValue(lName);

                            leaderboardRef.child("EmployeeId").setValue(employeeId);
                            leaderboardRef.child("ManagerId").setValue(managerId);

                            leaderboardRef.child(currentMonthYear).child("TotalPoints").get()
                                    .addOnSuccessListener(dataSnapshot -> {
                                        long previousPoints = 0;
                                        if (dataSnapshot.exists()) {
                                            Object value = dataSnapshot.getValue();
                                            if (value instanceof Long) {
                                                previousPoints = (Long) value;
                                            } else if (value instanceof String) {
                                                try {
                                                    previousPoints = Long.parseLong((String) value);
                                                } catch (NumberFormatException e) {
                                                    Toast.makeText(getActivity(), "Error parsing points: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        leaderboardRef.child(currentMonthYear).child("TotalPoints").setValue(previousPoints + totalPoints)
                                                .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Points updated successfully", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error updating points: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                    }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Error fetching TotalPoints: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                         }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Error fetching user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                // Add data to "Statistics" database
                DatabaseReference statisticsRef = database.getReference("Statistics").child(managerId).child(currentMonthYear);

                statisticsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Initialize variables with current values or set them to 0 if they don't exist
                        long totalPoints = snapshot.child("TotalPoints").exists() ? snapshot.child("TotalPoints").getValue(Long.class) : 0;
                        long totalEWaste = snapshot.child("TotalEWaste").exists() ? snapshot.child("TotalEWaste").getValue(Long.class) : 0;
                        long totalPaper = snapshot.child("TotalPaper").exists() ? snapshot.child("TotalPaper").getValue(Long.class) : 0;
                        long totalPlastic = snapshot.child("TotalPlastic").exists() ? snapshot.child("TotalPlastic").getValue(Long.class) : 0;
                        long totalOrganic = snapshot.child("TotalOrganic").exists() ? snapshot.child("TotalOrganic").getValue(Long.class) : 0;
                        long totalMixed = snapshot.child("TotalMixed").exists() ? snapshot.child("TotalMixed").getValue(Long.class) : 0;
                        long totalGlass = snapshot.child("TotalGlass").exists() ? snapshot.child("TotalGlass").getValue(Long.class) : 0;
                        long totalMetal = snapshot.child("TotalMetal").exists() ? snapshot.child("TotalMetal").getValue(Long.class) : 0;

                        // Add new values to existing ones
                        long updatedPoints = totalPoints + totalPoints; // Total points for this transaction already calculated
                        long updatedEWaste = totalEWaste + ewasteAmount;
                        long updatedPaper = totalPaper + paperAmount;
                        long updatedPlastic = totalPlastic + plasticAmount;
                        long updatedOrganic = totalOrganic + organicAmount;
                        long updatedMixed = totalMixed + mixedAmount;
                        long updatedGlass = totalGlass + glassAmount;
                        long updatedMetal = totalMetal + metalAmount;

                        // Update the database with new totals
                        statisticsRef.child("TotalPoints").setValue(updatedPoints);
                        statisticsRef.child("TotalEWaste").setValue(updatedEWaste);
                        statisticsRef.child("TotalPaper").setValue(updatedPaper);
                        statisticsRef.child("TotalPlastic").setValue(updatedPlastic);
                        statisticsRef.child("TotalOrganic").setValue(updatedOrganic);
                        statisticsRef.child("TotalMixed").setValue(updatedMixed);
                        statisticsRef.child("TotalGlass").setValue(updatedGlass);
                        statisticsRef.child("TotalMetal").setValue(updatedMetal);

                        Toast.makeText(getActivity(), "Statistics updated successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }





            Map<String, Integer> wasteData = collectWasteData();
            if (wasteData == null) {
                Toast.makeText(getActivity(), "Enter valid waste data", Toast.LENGTH_SHORT).show();
                return;
            }
            String currentDate = new SimpleDateFormat("yyyy-MM").format(Calendar.getInstance().getTime());

            updateFirebaseData(userIdInput, currentDate, wasteData);






        });





        return view;
    }

    private Map<String, Integer> collectWasteData() {
        try {
            Map<String, Integer> wasteData = new HashMap<>();
            wasteData.put("TotalEWaste", Integer.parseInt(wasteEwaste.getText().toString().trim()));
            wasteData.put("TotalPaper", Integer.parseInt(wastePaper.getText().toString().trim()));
            wasteData.put("TotalGlass", Integer.parseInt(wasteGlass.getText().toString().trim()));
            wasteData.put("TotalMetal", Integer.parseInt(wasteMetal.getText().toString().trim()));
            wasteData.put("TotalPlastic", Integer.parseInt(wastePlastic.getText().toString().trim()));
            wasteData.put("TotalOrganic", Integer.parseInt(wasteOrganic.getText().toString().trim()));
            wasteData.put("TotalMixed", Integer.parseInt(wasteMixed.getText().toString().trim()));
            return wasteData;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private void updateFirebaseData(String userId, String date, Map<String, Integer> newData) {
        DatabaseReference userRef = databaseReference.child(emp_managmentId).child(userId).child(date);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> updatedData = new HashMap<>(newData);

                if (snapshot.exists()) {
                    // Update existing data
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String key = child.getKey();
                        Integer existingValue = child.getValue(Integer.class);
                        if (key != null && existingValue != null && updatedData.containsKey(key)) {
                            updatedData.put(key, existingValue + (Integer) updatedData.get(key));
                        }
                    }
                }

                // Save updated data back to Firebase
                userRef.setValue(updatedData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed to update data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEmployeeId(String user_employeeId) {
        // Reference to Firebase
        DatabaseReference allEmployeeRef = FirebaseDatabase.getInstance().getReference("AllEmployee");

        // Query Firebase to check if any employee's unique ID matches the user_employeeId
        allEmployeeRef.orderByKey().equalTo(user_employeeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                // If the user_employeeId exists in Firebase
                DataSnapshot snapshot = task.getResult().child(user_employeeId);

                // Retrieve the employeeManagmentId from the matched entry
                String emp_managmentId = snapshot.child("employeeManagmentId").getValue(String.class);

                // Display a Toast with the employeeManagmentId
                if (emp_managmentId != null)
                {
                    Toast.makeText(getActivity(), "hey,,Management ID "+emp_managmentId, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("empp_managmentId", emp_managmentId);
                    editor.apply();  // Apply the changes
                }
                else
                {
                    Toast.makeText(getActivity(), "Management ID not found!", Toast.LENGTH_SHORT).show();
                }
            } else {
                // If the user_employeeId does not match any Firebase ID
                Toast.makeText(getActivity(), "Employee ID does not match", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            // Handle the error
            Toast.makeText(getActivity(), "Error checking employee ID", Toast.LENGTH_SHORT).show();
        });
    }

}