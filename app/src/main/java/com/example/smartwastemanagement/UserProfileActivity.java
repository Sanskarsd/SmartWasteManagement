package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserProfileActivity extends AppCompatActivity {

    TextView password, fname, lname, mobile, email, dob, gender, location, username;
    ImageView imageView;
    Button logout;
    SharedPreferences sharedPreferencesUserProfile,sharedPreferencesDirectLogin;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        // Initialize UI elements
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        dob = findViewById(R.id.dob);
        gender = findViewById(R.id.gender);
        location = findViewById(R.id.location);
        password = findViewById(R.id.password);
        imageView=findViewById(R.id.profileImage);
        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        sharedPreferencesUserProfile = getSharedPreferences("UserProfileInfo", MODE_PRIVATE);
        String _fname = sharedPreferencesUserProfile.getString("fname", "Default Name");
        String _lname = sharedPreferencesUserProfile.getString("lname", "Default Last Name");
        String _mobile = sharedPreferencesUserProfile.getString("mobile", "Default Mobile");
        String _email = sharedPreferencesUserProfile.getString("email", "Default Email");
        String _dob = sharedPreferencesUserProfile.getString("dob", "Default DOB");
        String _gender = sharedPreferencesUserProfile.getString("gender", "Default Gender");
        String _location = sharedPreferencesUserProfile.getString("location", "Default Location");
        String _password = sharedPreferencesUserProfile.getString("password", "Default Password");


        fname.setText(_fname);
        lname.setText(_lname);
        mobile.setText(_mobile);
        email.setText(_email);
        dob.setText(_dob);
        gender.setText(_gender);
        location.setText(_location);
        password.setText(_password);

        if(_gender.equals("Male"))
        {
            imageView.setBackgroundResource(R.drawable.profile);
        }
        else
        {
            imageView.setBackgroundResource(R.drawable.profile2);
        }

    }

    private void showLogoutDialog() {
        {

            AlertDialog.Builder builder=new AlertDialog.Builder(UserProfileActivity.this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure to logout");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    sharedPreferencesDirectLogin = getSharedPreferences("DirectLogin", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferencesDirectLogin.edit();
                    editor.putString("DirectLoginUser", "false");
                    editor.apply();  // Apply the changes
                    Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                    startActivity(intent);

                    dialog.dismiss();
                    finish();

                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog=builder.create();
            dialog.show();

        }
    }
}