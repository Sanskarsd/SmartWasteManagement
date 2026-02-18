package com.example.smartwastemanagement;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartwastemanagement.HistoryDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class History extends AppCompatActivity {
    private TextView t1;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<String> itemList;
    private ImageButton imageButton;
    private Calendar calendar;
    private String selectedMonth,selectedMonthNo;
    private int selectedYear;
    HistoryDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        calendar = Calendar.getInstance();
        String[] months = new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        selectedMonth = months[calendar.get(Calendar.MONTH)];

        selectedYear = calendar.get(Calendar.YEAR);


        // Set up the current date display
        t1 = findViewById(R.id.currentDateHistory);

        // Set up the RecyclerView


        // Set up the button to open the dialog
        imageButton = findViewById(R.id.historyButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new HistoryDialog();
                dialog.setOnDismissListener(new HistoryDialog.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        onResume(); // Refresh data if necessary
                    }
                });
                dialog.show(getSupportFragmentManager(), null);
            }
        });
    }
    private void setupRecyclerView() {
        // Initialize the item list
        itemList = new ArrayList<>();

        // Calculate total days in the selected month/year
        int totalDays = calculateDaysInMonth(selectedMonth, selectedYear);
        Log.d("History", "Total days in " + selectedMonth + " " + selectedYear + ": " + totalDays);

        for (int i = 1; i <= totalDays; i++) {
            itemList.add("Day " + i);
        }

        // Set up the RecyclerView with a GridLayoutManager
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns
        adapter = new MyAdapter(itemList,getMonthIndex(selectedMonth)+1, selectedYear); // Pass month and year
        recyclerView.setAdapter(adapter);
    }


    private int calculateDaysInMonth(String month, int year) {
        int monthIndex = getMonthIndex(month);

        if (monthIndex == -1) {
            Log.e("History", "Invalid month: " + month);
            return 0; // Return 0 for invalid month
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthIndex);
        cal.set(Calendar.DAY_OF_MONTH, 1); // Start on the first day of the month

        // Get the maximum day of the month
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.d("History", "Days in month: " + daysInMonth);
        return daysInMonth;
    }

    private int getMonthIndex(String month) {
        switch (month.toLowerCase(Locale.getDefault())) {
            case "january": return Calendar.JANUARY;
            case "february": return Calendar.FEBRUARY;
            case "march": return Calendar.MARCH;
            case "april": return Calendar.APRIL;
            case "may": return Calendar.MAY;
            case "june": return Calendar.JUNE;
            case "july": return Calendar.JULY;
            case "august": return Calendar.AUGUST;
            case "september": return Calendar.SEPTEMBER;
            case "october": return Calendar.OCTOBER;
            case "november": return Calendar.NOVEMBER;
            case "december": return Calendar.DECEMBER;
            default: return -1; // Invalid month
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Show the toast message every time the History tab is resumed
        if (HistoryDialog.historyflag == 0) {
            String[] months = new String[]{
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
            };
            selectedMonth = months[calendar.get(Calendar.MONTH)];
            selectedYear = calendar.get(Calendar.YEAR);

        } else {
            selectedYear = dialog.getYearHistory();
            selectedMonth = dialog.getMonthHistory();
        }
        t1.setText(selectedMonth+", "+String.valueOf(selectedYear));

        recyclerView = findViewById(R.id.recyclerView);
        setupRecyclerView();
    }


}










class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<String> itemList;
    private int selectedMonth;
    private int selectedYear;

    public MyAdapter(List<String> itemList, int selectedMonth, int selectedYear) {
        this.itemList = itemList;
        this.selectedMonth = selectedMonth;
        this.selectedYear = selectedYear;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.itemTextView.setText(itemList.get(position));

        // Set the image resource for the ImageView
        holder.itemImageView.setImageResource(R.drawable.checked__); // Replace with your image

        // Set the click listener
        holder.itemView.setOnClickListener(v -> {
            // Create an Intent to start the WasteDataActivity
            Intent intent = new Intent(holder.itemView.getContext(), WasteDataActivity.class);

            // Pass the selected day, month, and year to the new activity
            intent.putExtra("selected_day", itemList.get(position));
            intent.putExtra("selected_month", selectedMonth);
            intent.putExtra("selected_year", selectedYear);

            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;
        ImageView itemImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
        }
    }

}