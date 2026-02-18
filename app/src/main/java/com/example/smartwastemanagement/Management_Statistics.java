package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Management_Statistics extends AppCompatActivity {

    FirebaseDatabase database;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_management_statistics);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        // Retrieve the managementID from SharedPreferences
        String managementID = sharedPreferences.getString("managementID", "defaultManagementID");

        String currentMonthYear = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

        database = FirebaseDatabase.getInstance();
        DatabaseReference statisticsRef = database.getReference("Statistics").child(managementID).child(currentMonthYear);

        // Find the BarChart and PieChart
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) BarChart barChart = findViewById(R.id.barChart);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) PieChart pieChart = findViewById(R.id.pieChart);

        // Listen for data changes in the database
        statisticsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Create data points for waste categories
                    List<BarEntry> categoryEntries = new ArrayList<>();
                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
                    ArrayList<Integer> colors = new ArrayList<>();

                    int index = 1; // To map categories to positions
                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                        String key = categorySnapshot.getKey(); // Key (e.g., "TotalEWaste", "TotalPaper")

                        // Skip TotalPoints
                        if ("TotalPoints".equals(key)) continue;

                        // Get the waste category name (e.g., "E-waste", "Paper")
                        String categoryName = key.replace("Total", ""); // Remove "Total" prefix
                        float quantity = categorySnapshot.getValue(Float.class); // Quantity value

                        // Add data points
                        categoryEntries.add(new BarEntry(index, quantity));
                        pieEntries.add(new PieEntry(quantity, categoryName));
                        colors.add(generateColor(index)); // Generate unique colors
                        index++;
                    }

                    // Update BarChart
                    BarDataSet categoryDataSet = new BarDataSet(categoryEntries, "Waste Categories");
                    categoryDataSet.setColor(Color.parseColor("#ffb3b4")); // Set bar color
                    BarData barData = new BarData(categoryDataSet);
                    barData.setBarWidth(0.6f);
                    barData.setValueTextSize(10f);

                    // Customize X-Axis
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1f);
                    xAxis.setAxisMinimum(0.5f);
                    xAxis.setAxisMaximum(index - 0.5f);
                    xAxis.setLabelCount(index - 1, true);

                    // Customize Y-Axis
                    YAxis leftAxis = barChart.getAxisLeft();
                    leftAxis.setAxisMinimum(0f);
                    leftAxis.setGranularity(10f);
                    leftAxis.setAxisMaximum(1000f); // Adjust max as per your needs
                    YAxis rightAxis = barChart.getAxisRight();
                    rightAxis.setEnabled(false);

                    // Set data to BarChart and refresh
                    barChart.setData(barData);
                    barChart.setFitBars(true);
                    barChart.setDrawValueAboveBar(true);
                    barChart.getBarData().setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.format("%.1f", value); // Display raw values (quantity)
                        }
                    });
                    barChart.invalidate();

                    // Update PieChart
                    PieDataSet pieDataSet = new PieDataSet(pieEntries, "Waste Categories");
                    pieDataSet.setColors(colors);
                    PieData pieData = new PieData(pieDataSet);

                    pieChart.setData(pieData);
                    pieChart.setDrawHoleEnabled(true);
                    pieChart.setUsePercentValues(false); // Show raw values instead of percentages
                    pieChart.setEntryLabelColor(Color.BLACK);
                    pieChart.setEntryLabelTextSize(12f);
                    pieChart.setCenterText("Waste Categories");
                    pieChart.setCenterTextSize(16f);
                    pieChart.setDrawSliceText(true);
                    pieChart.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
            }
        });
    }

    /**
     * Helper method to generate unique colors for PieChart
     */
    private int generateColor(int index) {
        // A simple method to generate unique colors based on the index
        String[] colorPalette = {
                "#ffb3b4", "#ffcccc", "#ff99cc", "#ff6699", "#ff3366", "#ff0066", "#cc0055"
        };
        return Color.parseColor(colorPalette[index % colorPalette.length]);
    }
}