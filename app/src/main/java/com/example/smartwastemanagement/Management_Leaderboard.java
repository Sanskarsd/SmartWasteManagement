package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Management_Leaderboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Management_Leaderboard_Adapter1 adapter;
    private DatabaseReference leaderboardRef, userDataRef;
    private Spinner managementSpinner;
    private ArrayAdapter<String> spinnerAdapter;
    private List<String> managementIds;
    private Map<String, String> userNames; // Map to store userId -> Full Name

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_leaderboard);

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        managementSpinner = findViewById(R.id.dropdownSpinner);

        // Initialize data structures
        adapter = new Management_Leaderboard_Adapter1(this, new ArrayList<>(), new HashMap<>());
        recyclerView.setAdapter(adapter);

        managementIds = new ArrayList<>();
        userNames = new HashMap<>();

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, managementIds);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        managementSpinner.setAdapter(spinnerAdapter);

        // Firebase references
        leaderboardRef = FirebaseDatabase.getInstance().getReference("LeaderBoard");
        userDataRef = FirebaseDatabase.getInstance().getReference("PrimaryData/UserData");

        // Fetch data
        fetchUserData(); // Fetch FName and LName for all users
        fetchManagementIds();

        // Handle Spinner selection
        managementSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedManagementId = parent.getItemAtPosition(position).toString();
                fetchLeaderboardData(selectedManagementId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action required
            }
        });
    }

    private void fetchUserData() {
        userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String fName = userSnapshot.child("fName").getValue(String.class);
                    String lName = userSnapshot.child("lName").getValue(String.class);
                    if (userId != null && fName != null && lName != null) {
                        userNames.put(userId, fName + " " + lName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Management_Leaderboard.this, "Error fetching user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchManagementIds() {
        leaderboardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                managementIds.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String managementId = userSnapshot.child("ManagerId").getValue(String.class);
                    if (managementId != null && !managementIds.contains(managementId)) {
                        managementIds.add(managementId);
                    }
                }
                spinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Management_Leaderboard.this, "Error fetching management IDs: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLeaderboardData(String managementId) {
        leaderboardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User1> userList = new ArrayList<>();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userManagementId = userSnapshot.child("ManagerId").getValue(String.class);
                    if (managementId.equals(userManagementId)) {
                        String userId = userSnapshot.getKey();
                        int totalPoints = 0;

                        for (DataSnapshot monthSnapshot : userSnapshot.getChildren()) {
                            if (monthSnapshot.hasChild("TotalPoints")) {
                                totalPoints += monthSnapshot.child("TotalPoints").getValue(Integer.class);
                            }
                        }

                        userList.add(new User1(userId, userNames.getOrDefault(userId, "Unknown User"), totalPoints));
                    }
                }

                Collections.sort(userList, (u1, u2) -> u2.getPoints() - u1.getPoints());
                adapter.updateData(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Management_Leaderboard.this, "Error fetching leaderboard data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

class Management_Leaderboard_Adapter1 extends RecyclerView.Adapter<Management_Leaderboard_Adapter1.StudentViewHolder> {
    private Context context;
    private List<User1> userList;

    public Management_Leaderboard_Adapter1(Context context, List<User1> userList, Map<String, String> userNames) {
        this.context = context;
        this.userList = userList;
    }

    public void updateData(List<User1> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView name, rank, points;
        ImageView rankImage;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            rank = itemView.findViewById(R.id.rank);
            points = itemView.findViewById(R.id.points);
            rankImage = itemView.findViewById(R.id.rankImage);
        }
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        User1 user = userList.get(position);
        holder.name.setText(user.getName());
        holder.points.setText(String.valueOf(user.getPoints()));

        if (position == 0) {
            holder.rank.setVisibility(View.GONE);
            holder.rankImage.setVisibility(View.VISIBLE);
            holder.rankImage.setImageResource(R.drawable.rank1);
        } else if (position == 1) {
            holder.rank.setVisibility(View.GONE);
            holder.rankImage.setVisibility(View.VISIBLE);
            holder.rankImage.setImageResource(R.drawable.rank2);
        } else if (position == 2) {
            holder.rank.setVisibility(View.GONE);
            holder.rankImage.setVisibility(View.VISIBLE);
            holder.rankImage.setImageResource(R.drawable.rank3);
        } else {
            holder.rank.setVisibility(View.VISIBLE);
            holder.rankImage.setVisibility(View.GONE);
            holder.rank.setText(String.valueOf(position + 1)); // Show rank number
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

class User1 {
    private String userId;
    private String name;
    private int points;

    public User1(String userId, String name, int points) {
        this.userId = userId;
        this.name = name;
        this.points = points;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }
}