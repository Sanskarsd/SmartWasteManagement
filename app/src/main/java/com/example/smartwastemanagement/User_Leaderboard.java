package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class User_Leaderboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Management_Leaderboard_Adapter adapter;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_leaderboard);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with an empty list
        adapter = new Management_Leaderboard_Adapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Reference to the Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference("LeaderBoard");

        // Fetch and display leaderboard data
        fetchLeaderboardData();
    }

    private void fetchLeaderboardData() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("PrimaryData/UserData");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot leaderboardSnapshot) {
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                        List<User> userList = new ArrayList<>();

                        // Map to store user details for quick lookup
                        HashMap<String, String> userNames = new HashMap<>();
                        for (DataSnapshot user : userSnapshot.getChildren()) {
                            String userId = user.getKey();
                            String fName = user.child("fName").getValue(String.class);
                            String lName = user.child("lName").getValue(String.class);
                            userNames.put(userId, fName + " " + lName);
                        }

                        // Iterate through "LeaderBoard" and calculate points
                        for (DataSnapshot user : leaderboardSnapshot.getChildren()) {
                            String userId = user.getKey();
                            int totalPoints = 0;

                            // Calculate total points from the "TotalPoints" field
                            for (DataSnapshot month : user.getChildren()) {
                                if (month.hasChild("TotalPoints")) {
                                    totalPoints += month.child("TotalPoints").getValue(Integer.class);
                                }
                            }

                            // Add user with points and name if available
                            String userName = userNames.getOrDefault(userId, "Unknown User");
                            userList.add(new User(userId, userName, totalPoints));
                        }

                        // Sort users by points in descending order
                        Collections.sort(userList, (u1, u2) -> u2.getPoints() - u1.getPoints());

                        // Update adapter with the sorted user list
                        adapter.updateData(userList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(User_Leaderboard.this, "Error fetching user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_Leaderboard.this, "Error fetching leaderboard data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

class Management_Leaderboard_Adapter extends RecyclerView.Adapter<Management_Leaderboard_Adapter.StudentViewHolder> {
    private Context context;
    private List<User> userList; // List to store User data

    // Constructor with context and initial data
    public Management_Leaderboard_Adapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    // Update the data list when new data is fetched
    public void updateData(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged(); // Notify adapter to refresh the data
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView userId;
        TextView rank;
        TextView name;
        TextView points;
        ImageView rankImage;
        CardView cardView;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            userId = itemView.findViewById(R.id.userId);
            rank = itemView.findViewById(R.id.rank);
            points = itemView.findViewById(R.id.points);
            rankImage = itemView.findViewById(R.id.rankImage);
            cardView = itemView.findViewById(R.id.cardview);
            name=itemView.findViewById(R.id.name);
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
        User user = userList.get(position);
        holder.userId.setText(user.getUserId()); // Set UserId (hidden or optional)
        holder.points.setText(String.valueOf(user.getPoints())); // Set Points
        holder.name.setText(user.getName()); // Display full name

        // Handle rank display and rank image
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
        return userList.size(); // Return the number of users
    }
}

class User {
    private String userId;
    private String name;
    private int points;

    // Constructor with userId, name, and total points
    public User(String userId, String name, int points) {
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