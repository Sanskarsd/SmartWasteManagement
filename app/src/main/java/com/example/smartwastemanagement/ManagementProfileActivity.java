package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManagementProfileActivity extends AppCompatActivity {

    TextView Mname, MVillageCity, MMobile, Memail, Mpin, Mpass, Musername, Mid;
    ImageView imageView;
    SharedPreferences sharedPreferencesManagementProfile;
    Switch switcher;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_management_profile);

        // Initialize UI elements
        Musername = findViewById(R.id.Managementusername);
        Mid = findViewById(R.id.Managementid);
        Mname = findViewById(R.id.Managementname);
        MVillageCity = findViewById(R.id.ManagementVillageCity);
        MMobile = findViewById(R.id.Managementmobile);
        Memail = findViewById(R.id.Managementemail);
        Mpin = findViewById(R.id.Managementpin);
        Mpass = findViewById(R.id.Managementpassword);
        imageView=findViewById(R.id.profileImage);

        switcher=findViewById(R.id.switchButton1);


        sharedPreferencesManagementProfile = getSharedPreferences("ManagementProfileInfo", MODE_PRIVATE);

        String _managementName = sharedPreferencesManagementProfile.getString("managementName", "Default Name");
        String _managementUsername = sharedPreferencesManagementProfile.getString("managementUsername", "Default Username");
        String _managementMobile = sharedPreferencesManagementProfile.getString("managementMobile", "Default Mobile");
        String _managementEmail = sharedPreferencesManagementProfile.getString("managementEmail", "Default Email");
        String _managementVillageCity = sharedPreferencesManagementProfile.getString("managementVillageCity", "Default Village/City");
        String _managementPin = sharedPreferencesManagementProfile.getString("managementPin", "Default Pin");
        String _managementPassword = sharedPreferencesManagementProfile.getString("managementPassword", "Default Password");
        String _managementID = sharedPreferencesManagementProfile.getString("managementID", "Default ID");


        Mname.setText(_managementName);
        Musername.setText(_managementUsername);
        MMobile.setText(_managementMobile);
        Memail.setText(_managementEmail);
        MVillageCity.setText(_managementVillageCity);
        Mpin.setText(_managementPin);
        Mpass.setText(_managementPassword);
        Mid.setText(_managementID);


        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                DatabaseReference certificateRef = FirebaseDatabase.getInstance().getReference("Certificate");

                if (isChecked) {
                    Log.d("managementID", "managementID: " + _managementID);

                    // Assuming "Imgname" is some static value or you get it from somewhere
                    String imgname = "image_name_example.jpg"; // Replace with your actual image name
                    boolean flag = true; // Set the flag value based on your requirement

                    // Creating a data map
                    certificateRef.child(_managementID).child("flag").setValue(flag);
                    certificateRef.child(_managementID).child("Imgname").setValue(imgname);

                    Toast.makeText(ManagementProfileActivity.this, "Flag on", Toast.LENGTH_SHORT).show();

                } else {
                    // Optionally, you can also remove the data or reset the values when the switch is turned off
                    certificateRef.child(_managementID).child("flag").setValue(false);
                    Toast.makeText(ManagementProfileActivity.this, "Flag off!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}