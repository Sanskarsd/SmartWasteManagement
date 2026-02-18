package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WasteDataActivity extends AppCompatActivity {
    private TextView dayWasteData;
    private LinearLayout dataContainer, otherInfoDataContainer;
    private ImageButton expandButton, expandOtherInfoButton;
    private boolean isExpanded = false;
    private boolean isOtherInfoExpanded = false;

    String userId = "45";
    String datePath = "";
    String day = "";

    // Variables to store the fetched data
    String eWaste = "0";
    String paper = "0";
    String glass = "0";
    String metal = "0";
    String plastic = "0";
    String organic = "0";
    String mixed = "0";
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    BarChart leaderboardBarChart;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waste_data);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", "userID");

        leaderboardBarChart = findViewById(R.id.leaderboardBarChart);

        // Initialize UI elements for cards
        initializeCardViews();
        fetchData();
    }

    private void fetchData() {
        databaseReference.child("LeaderBoard").child(userId).child(datePath).child(day)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<BarEntry> leaderboardEntries = new ArrayList<>();
                        int index = 0;
                        if (dataSnapshot.exists()) {

                            // Fetch individual fields
                            eWaste = String.valueOf(dataSnapshot.child("EWaste").getValue(Long.class));
                            paper = String.valueOf(dataSnapshot.child("Paper").getValue(Long.class));
                            glass = String.valueOf(dataSnapshot.child("Glass").getValue(Long.class));
                            metal = String.valueOf(dataSnapshot.child("Metal").getValue(Long.class));
                            plastic = String.valueOf(dataSnapshot.child("Plastic").getValue(Long.class));
                            organic = String.valueOf(dataSnapshot.child("Organic").getValue(Long.class));
                            mixed = String.valueOf(dataSnapshot.child("Mixed").getValue(Long.class));
                            leaderboardEntries.add(new BarEntry(index++, Long.parseLong((String) eWaste)));
                            leaderboardEntries.add(new BarEntry(index++, Long.parseLong((String) paper)));
                            leaderboardEntries.add(new BarEntry(index++, Long.parseLong((String) glass)));
                            leaderboardEntries.add(new BarEntry(index++, Long.parseLong((String) metal)));
                            leaderboardEntries.add(new BarEntry(index++, Long.parseLong((String) plastic)));
                            leaderboardEntries.add(new BarEntry(index++, Long.parseLong((String) organic)));
                            leaderboardEntries.add(new BarEntry(index++, Long.parseLong((String) mixed)));

                        } else {
                            eWaste = "0";
                            paper = "0";
                            glass = "0";
                            metal = "0";
                            plastic = "0";
                            organic = "0";
                            mixed = "0";

                        }

                        updateLeaderboardBarChart(leaderboardEntries);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FirebaseError", "Database error: " + databaseError.getMessage());
                    }
                });
    }
    private void updateLeaderboardBarChart(List<BarEntry> entries) {
        String[] categories = {"E-Waste", "Paper", "Glass", "Metal", "Plastic", "Organic", "Mixed"};

        BarDataSet dataSet = new BarDataSet(entries, "Waste Contributions");
        dataSet.setColor(Color.parseColor("#4CAF50")); // Green color for bars
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.8f); // Set bar width

        leaderboardBarChart.setData(data);
        leaderboardBarChart.setFitBars(true);

        // Customize X-Axis
        XAxis xAxis = leaderboardBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(entries.size(), true);

        // Set custom ValueFormatter for X-Axis
        xAxis.setValueFormatter(new IndexAxisValueFormatter(categories));

        // Customize Y-Axis
        YAxis leftAxis = leaderboardBarChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Always start at zero
        YAxis rightAxis = leaderboardBarChart.getAxisRight();
        rightAxis.setEnabled(false);




        // Refresh the chart
        leaderboardBarChart.invalidate();
    }

    private void initializeCardViews() {
        dayWasteData = findViewById(R.id.dayWasteData);
        expandButton = findViewById(R.id.expandButton);
        dataContainer = findViewById(R.id.dataContainer);
        expandOtherInfoButton = findViewById(R.id.expandOtherInfoButton);
        otherInfoDataContainer = findViewById(R.id.otherInfoDataContainer);

        // Set the selected day text (handle null case)
        String selectedDayString = getIntent().getStringExtra("selected_day");
        String selectedDay = selectedDayString.split(" ")[1];
        String selectedMonth = String.valueOf(getIntent().getIntExtra("selected_month", 0));
        String selectedYear = String.valueOf(getIntent().getIntExtra("selected_year", 0));
        if (selectedDay == null) {
            selectedDay = "Default Day"; // Fallback value
        }
        if (selectedDay.length() == 1) {
            selectedDay = "0" + selectedDay;
        }
        if (selectedMonth.length() == 1) {
            selectedMonth = "0" + selectedMonth;
        }
        datePath = selectedYear + "-" + selectedMonth;
        dayWasteData.setText(selectedYear + "-" + selectedMonth + "-" + selectedDay);
        day = selectedDay;

        // Expand/collapse listeners
        expandButton.setOnClickListener(this::toggleCardExpansion);
        expandOtherInfoButton.setOnClickListener(this::toggleOtherInfoCardExpansion);
    }

    private void toggleCardExpansion(View view) {
        if (isExpanded) {
            dataContainer.setVisibility(View.GONE);
        } else {
            if (dataContainer.getChildCount() == 0) {
                addRows();
            }
            dataContainer.setVisibility(View.VISIBLE);
        }
        isExpanded = !isExpanded;
    }

    private void toggleOtherInfoCardExpansion(View view) {
        if (isOtherInfoExpanded) {
            otherInfoDataContainer.setVisibility(View.GONE);
        } else {
            if (otherInfoDataContainer.getChildCount() == 0) {
                addOtherInfoRows();
            }
            otherInfoDataContainer.setVisibility(View.VISIBLE);
        }
        isOtherInfoExpanded = !isOtherInfoExpanded;
    }

    private void addRows() {
        String[][] wasteData = {
                {"E-Waste", eWaste},
                {"Paper", paper},
                {"Glass", glass},
                {"Metal", metal},
                {"Plastic", plastic},
                {"Organic", organic},
                {"Mixed", mixed}
        };

        for (String[] row : wasteData) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(0, 8, 0, 8);

            TextView typeText = new TextView(this);
            typeText.setText(row[0]);
            typeText.setTextSize(18);
            typeText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView weightText = new TextView(this);
            weightText.setText(row[1]);
            weightText.setTextSize(18);
            weightText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            rowLayout.addView(typeText);
            rowLayout.addView(weightText);
            dataContainer.addView(rowLayout);
        }
    }

    private void addOtherInfoRows() {
        String[][] otherInfoData = {
                {"Driver Id", "6"},
                {"Vehicle Number", "MH50N1162"},
                {"Pickup Time", "3:30 PM"}
        };

        for (String[] row : otherInfoData) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(0, 8, 0, 8);

            TextView titleText = new TextView(this);
            titleText.setText(row[0]);
            titleText.setTextSize(18);
            titleText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView valueText = new TextView(this);
            valueText.setText(row[1]);
            valueText.setTextSize(18);
            valueText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            rowLayout.addView(titleText);
            rowLayout.addView(valueText);
            otherInfoDataContainer.addView(rowLayout);
        }
    }
}