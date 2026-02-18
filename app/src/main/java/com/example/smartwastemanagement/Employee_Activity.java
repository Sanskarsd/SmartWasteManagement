package com.example.smartwastemanagement;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Employee_Activity extends AppCompatActivity {
     ImageButton adduser;

    ViewPager2 viewPager2;
    Employee_Page_Adapter employeePageAdapter;
    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        // Retrieve the userID and user_employeeId from the Intent
        String employeeID = sharedPreferences.getString("employeeID","emoloyeeId");

        // Optionally, use these values (e.g., display in a Toast or log)
        if (employeeID != null) {
            Toast.makeText(this, "employeeID : " + employeeID , Toast.LENGTH_SHORT).show();
        }


        adduser= findViewById(R.id.adduser);
        adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Employee_Activity.this, RegistrationActivity1.class);
                startActivity(intent);
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNav2);
        viewPager2 = findViewById(R.id.viewPager22);
        employeePageAdapter=new Employee_Page_Adapter(this);
        viewPager2.setAdapter(employeePageAdapter);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.employee_home) {
                    viewPager2.setCurrentItem(0);
                } else if (id == R.id.employee_map) {
                    viewPager2.setCurrentItem(1);
                } else if (id == R.id.employee_profile) {
                    viewPager2.setCurrentItem(2);
                } else {
                    return false;
                }
                return true; // Return true for successful selection
            }
        });


        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: bottomNavigationView.getMenu().findItem(R.id.employee_home).setChecked(true); break;
                    case 1: bottomNavigationView.getMenu().findItem(R.id.employee_map).setChecked(true); break;
                    case 2: bottomNavigationView.getMenu().findItem(R.id.employee_profile).setChecked(true); break;
                }
                super.onPageSelected(position);
            }
        });

        hideSystemUI();
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, use View.SYSTEM_UI_FLAG_FULLSCREEN
        // For "sticky immersive mode," use View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Ensure the system UI remains hidden when the activity resumes
        hideSystemUI();
    }
}