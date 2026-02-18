package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagementMainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView title;
    List<HashMap<String, Object>> eventDataList = new ArrayList<>();
    EventRequestedAdapter adapter;
    DatabaseReference eventRef,managementInfo;
    GridView gridView;
    String[] name = {"View Map","Customize alert", "Statistics", "Customize dustbin", "Register","Customize points"};
    int[] images = {R.drawable.manage_map, R.drawable.manage_alert, R.drawable.manage_statastics, R.drawable.manage_dustbin, R.drawable.manage_addemployee, R.drawable.manage_points};
    SharedPreferences sharedPreferences,sharedPreferencesManagementProfile;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_management_main);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        // Retrieve the userID and user_employeeId from the Intent
        String managementID = sharedPreferences.getString("managementID","managementID");
        String managementName = sharedPreferences.getString("managementName","managementName");

        // Optionally, use these values (e.g., display in a Toast or log)
        if (managementID != null) {
            Toast.makeText(this, "Managment ID: " + managementID , Toast.LENGTH_SHORT).show();
        }
        title=findViewById(R.id.managementTitle);
        title.setText("Hey , "+managementName);


        // Set up GridView
        gridView = findViewById(R.id.gridView);
        Adapter2 adapter2 = new Adapter2(getApplicationContext(), images, name);
        gridView.setAdapter(adapter2);

        // Set up item click listener for the GridView
        gridView.setOnItemClickListener((parent, view, position, id) -> {

            if (name[position].equals("Customize dustbin")) {
                // Start the Customize_user_Activity when the "Customize user" item is clicked
                Intent intent = new Intent(ManagementMainActivity.this, Management_dustbin_Activity.class);
                startActivity(intent);
            }
            if (name[position].equals("View Map")) {
                // Start the Customize_user_Activity when the "Customize user" item is clicked
                Intent intent = new Intent(ManagementMainActivity.this, Management_View_Map.class);
                startActivity(intent);
            }
            if (name[position].equals("Customize points")) {
                // Start the Customize_user_Activity when the "Customize user" i tem is clicked
                Intent intent = new Intent(ManagementMainActivity.this, ManagementCustomizePoints.class);
                startActivity(intent);
            }
            if (name[position].equals("Statistics")) {
                // Start the Customize_user_Activity when the "Customize user" item is clicked
                Intent intent = new Intent(ManagementMainActivity.this, Management_Statistics.class);
                startActivity(intent);
            }
            if (name[position].equals("Register")) {
                // Start the Customize_user_Activity when the "Customize user" item is clicked
                Intent intent = new Intent(ManagementMainActivity.this, RegisterEmployee1.class);
                startActivity(intent);
            }
            if (name[position].equals("Customize alert")) {
                // Start the Customize_user_Activity when the "Customize user" item is clicked
                Intent i1=new Intent(ManagementMainActivity.this,Management_Waste_Alert.class);
                startActivity(i1);
            }

            // You can handle other cases for different grid items here if needed
        });

        recyclerView = findViewById(R.id.recyclerView);

        // Set up Firebase reference
        eventRef = FirebaseDatabase.getInstance().getReference("EventRegisterManagement").child(managementID);

        // Set up the adapter for the RecyclerView
        adapter = new EventRequestedAdapter(eventDataList, new EventRequestedAdapter.OnEventActionListener() {
            @Override
            public void onAcceptClick(int position, String eventName, String location, String eventDate, String eventTime, String eventDesc, String eventId) {
                showAcceptEventDialog(position, eventName, location, eventDate, eventTime, eventDesc, eventId);
            }

            @Override
            public void onDeclineClick(int position, String eventId) {
                // Handle decline event logic
                declineEvent(eventId);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Fetch events from Firebase
        fetchEventDataFromFirebase();

        managementInfo = FirebaseDatabase.getInstance().getReference("Admin").child(managementID);
        // Fetch data from Firebase
        managementInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the employee data exists
                if (dataSnapshot.exists()) {
                    // Populate fields with Firebase data
                    String managementName = dataSnapshot.child("managementName").getValue(String.class);
                    String managementUsername = dataSnapshot.child("managementUsername").getValue(String.class);
                    String managementMobile = dataSnapshot.child("managementMobile").getValue(String.class);
                    String managementEmail = dataSnapshot.child("managementEmail").getValue(String.class);
                    String managementVillageCity = dataSnapshot.child("managementVillageCity").getValue(String.class);
                    String managementPin = dataSnapshot.child("managementPin").getValue(String.class);
                    String managementPassword = dataSnapshot.child("managementPassword").getValue(String.class);

                    // Logging values to Logcat
                    Log.d("ManagementData", "managementName: " + managementName);
                    Log.d("ManagementData", "managementUsername: " + managementUsername);
                    Log.d("ManagementData", "managementMobile: " + managementMobile);
                    Log.d("ManagementData", "managementEmail: " + managementEmail);
                    Log.d("ManagementData", "managementVillageCity: " + managementVillageCity);
                    Log.d("ManagementData", "managementPin: " + managementPin);
                    Log.d("ManagementData", "managementPassword: " + managementPassword);
                    saveManagementProfile(managementName,managementUsername,managementMobile,managementEmail,managementVillageCity,managementPin,managementPassword,managementID);

                } else {
                    // If the employee data doesn't exist
                    Toast.makeText(ManagementMainActivity.this, "No data found for the Management.", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
                Toast.makeText(ManagementMainActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private  void saveManagementProfile(String managementName, String managementUsername,String managementMobile,String managementEmail,String managementVillageCity,String managementPin,String managementPassword,String managementID){

        sharedPreferencesManagementProfile = getSharedPreferences("ManagementProfileInfo", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferencesManagementProfile.edit();
        editor.putString("managementName", managementName);
        editor.putString("managementUsername", managementUsername);
        editor.putString("managementMobile", managementMobile);
        editor.putString("managementEmail", managementEmail);
        editor.putString("managementVillageCity", managementVillageCity);
        editor.putString("managementPin", managementPin);
        editor.putString("managementPassword", managementPassword);
        editor.putString("managementID", managementID);
        editor.apply();


    }
    // Method to show the accept event dialog
    private void showAcceptEventDialog(int position, String eventName, String location, String eventDate, String eventTime, String eventDesc, String eventId) {
        Context context = recyclerView.getContext();
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout for the dialog
        View dialogView = inflater.inflate(R.layout.accept_event_dialog, null);
        builder.setView(dialogView);

        // Initialize dialog components
        TextView eventNameTextView = dialogView.findViewById(R.id.d_eventName);
        TextView locationTextView = dialogView.findViewById(R.id.d_eventLoc);
        TextView dateTextView = dialogView.findViewById(R.id.d_eventDate);
        TextView timeTextView = dialogView.findViewById(R.id.d_eventTime);
        TextView eventDescTextView = dialogView.findViewById(R.id.d_eventDesc);
        EditText employeeIdEditText = dialogView.findViewById(R.id.employeeIdEditText);
        EditText employeeDescriptionEditText = dialogView.findViewById(R.id.employeeDescriptionEditText);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button okButton = dialogView.findViewById(R.id.okButton);

        // Set the event details in the dialog
        eventNameTextView.setText("Event: " + eventName);
        locationTextView.setText("Location: " + location);
        dateTextView.setText("Date: " + eventDate);
        timeTextView.setText("Time: " + eventTime);
        eventDescTextView.setText("Description: " + eventDesc);

        // Create the dialog
        android.app.AlertDialog dialog = builder.create();

        // Set button actions
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        okButton.setOnClickListener(v -> {
            String employeeId = employeeIdEditText.getText().toString();
            String employeeDescription = employeeDescriptionEditText.getText().toString();

            if (!employeeId.isEmpty() && !employeeDescription.isEmpty()) {
                // Reference to the specific event in Firebase using eventId
                DatabaseReference eventToUpdateRef = eventRef.child(eventId);

                // Prepare data to update
                Map<String, Object> updatedEventData = new HashMap<>();
                updatedEventData.put("employeeId", employeeId);
                updatedEventData.put("employeeDesc", employeeDescription);
                updatedEventData.put("eventStatus", "accepted");

                // Update the event in Firebase
                eventToUpdateRef.updateChildren(updatedEventData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Event accepted successfully!", Toast.LENGTH_SHORT).show();
                        fetchEventDataFromFirebase();  // Re-fetch events to reflect the changes

                    } else {
                        Toast.makeText(context, "Failed to accept event. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

                // Close the dialog after the update
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void declineEvent(String eventId) {
        // Remove the event from Firebase
        DatabaseReference eventToDeleteRef = eventRef.child(eventId);
        eventToDeleteRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ManagementMainActivity.this, "Event declined and deleted successfully", Toast.LENGTH_SHORT).show();
                // Optionally, update your UI or data list
                fetchEventDataFromFirebase();  // Re-fetch events to reflect the changes
            } else {
                Toast.makeText(ManagementMainActivity.this, "Failed to decline event. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchEventDataFromFirebase() {
        eventRef.orderByChild("eventStatus").equalTo("pending") // Filter by "pending" status
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        eventDataList.clear();  // Clear existing data

                        // Loop through the event snapshots from Firebase
                        for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                            HashMap<String, Object> eventData = (HashMap<String, Object>) eventSnapshot.getValue();

                            if (eventData != null) {
                                eventData.put("eventId", eventSnapshot.getKey());
                                eventDataList.add(eventData);
                            }
                        }
                        adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ManagementMainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void openManagementLeaderboard(View view) {
        Intent i1=new Intent(ManagementMainActivity.this,Management_Leaderboard.class);
        startActivity(i1);
    }

    public void openManagementProfile(View view) {
        Intent i1=new Intent(ManagementMainActivity.this,ManagementProfileActivity.class);
        startActivity(i1);
    }

    public void openRegisterEmp(View view) {
        Intent i1=new Intent(ManagementMainActivity.this,RegisterEmployee1.class);
        startActivity(i1);
    }
}


class Adapter2 extends BaseAdapter {
    Context context;
    int data[];
    LayoutInflater inflater;
    String dataname[];
    Adapter2(Context context,int[] data, String name[]){
        this.context=context;
        this.data=data;
        this.dataname=name;
        inflater=(LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return dataname.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView=inflater.inflate(R.layout.layoutgrid,null);

        ImageView imageView=(ImageView) convertView.findViewById(R.id.image);
        imageView.setImageResource(data[position]);
        TextView textView=(TextView) convertView.findViewById(R.id.item);
        textView.setText(dataname[position]);
        return convertView;
    }
}


class EventRequestedAdapter extends RecyclerView.Adapter<EventRequestedAdapter.EventViewHolder> {

    private List<HashMap<String, Object>> eventDataList;
    private OnEventActionListener eventActionListener;

    public EventRequestedAdapter(List<HashMap<String, Object>> eventDataList, OnEventActionListener eventActionListener) {
        this.eventDataList = eventDataList;
        this.eventActionListener = eventActionListener;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_requested_item, parent, false); // Inflate the layout
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        HashMap<String, Object> eventData = eventDataList.get(position);

        String eventName = (String) eventData.get("eventName");
        String location = (String) eventData.get("location");
        Long timestamp = (Long) eventData.get("eventTimestamp");
        String eventDesc = (String) eventData.get("eventDesc");

        String eventDate = DateFormat.format("MM/dd/yyyy", timestamp).toString();
        String eventTime = DateFormat.format("hh:mm a", timestamp).toString();

        holder.eventNameTextView.setText("Event: " + eventName);
        holder.locationTextView.setText("Location: " + location);
        holder.dateTextView.setText("Date: " + eventDate);
        holder.timeTextView.setText("Time: " + eventTime);

        holder.acceptButton.setOnClickListener(v -> {
            if (eventActionListener != null) {
                eventActionListener.onAcceptClick(position, eventName, location, eventDate, eventTime, eventDesc, (String) eventData.get("eventId"));
            }
        });

        holder.declineButton.setOnClickListener(v -> {
            if (eventActionListener != null) {
                eventActionListener.onDeclineClick(position, (String) eventData.get("eventId"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventDataList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView eventNameTextView, locationTextView, dateTextView, timeTextView;
        Button acceptButton, declineButton;
        CardView cardView;

        public EventViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            acceptButton = itemView.findViewById(R.id.acceptEventbtn);
            declineButton = itemView.findViewById(R.id.declineEventbtn);
        }
    }

    public interface OnEventActionListener {
        void onAcceptClick(int position, String eventName, String location, String eventDate, String eventTime, String eventDesc, String eventId);
        void onDeclineClick(int position, String eventId);
    }
}
