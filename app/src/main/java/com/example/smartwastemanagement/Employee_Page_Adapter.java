package com.example.smartwastemanagement;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class Employee_Page_Adapter extends FragmentStateAdapter{
    public Employee_Page_Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {


        switch (position){
            case 0: return new EmployeeHome();
            case 1: return new EmployeeMap();
            case 2: return new EmployeeProfile();

            default: return new EmployeeHome();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
