package com.example.smartwastemanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SampleActivity extends AppCompatActivity {

    private EditText edtWidth1, edtWidth2, edtWidth3, edtWidth4, edtWidth5,edtWidth6,edtWidth7;
    private LinearLayout linearContainer;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sample);

        // Initialize EditTexts for each width
        edtWidth1 = findViewById(R.id.edtWidth1);
        edtWidth2 = findViewById(R.id.edtWidth2);
        edtWidth3 = findViewById(R.id.edtWidth3);
        edtWidth4 = findViewById(R.id.edtWidth4);
        edtWidth5 = findViewById(R.id.edtWidth5);
        edtWidth6 = findViewById(R.id.edtWidth6);
        edtWidth7 = findViewById(R.id.edtWidth7);

        Button enterButton = findViewById(R.id.enter);
        linearContainer = findViewById(R.id.linearContainer);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read the input values
                String input1 = edtWidth1.getText().toString();
                String input2 = edtWidth2.getText().toString();
                String input3 = edtWidth3.getText().toString();
                String input4 = edtWidth4.getText().toString();
                String input5 = edtWidth5.getText().toString();
                String input6 = edtWidth6.getText().toString();
                String input7 = edtWidth7.getText().toString();

                // Convert dp to px
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                float density = displayMetrics.density;

                // Convert inputs to integers and find the sum
                int[] values = new int[7];
                int sum = 0;
                if (!input1.isEmpty()) {
                    values[0] = Integer.parseInt(input1);
                    sum += values[0];
                }
                if (!input2.isEmpty()) {
                    values[1] = Integer.parseInt(input2);
                    sum += values[1];
                }
                if (!input3.isEmpty()) {
                    values[2] = Integer.parseInt(input3);
                    sum += values[2];
                }
                if (!input4.isEmpty()) {
                    values[3] = Integer.parseInt(input4);
                    sum += values[3];
                }
                if (!input5.isEmpty()) {
                    values[4] = Integer.parseInt(input5);
                    sum += values[4];
                }
                if (!input6.isEmpty()) {
                    values[5] = Integer.parseInt(input5);
                    sum += values[5];
                }
                if (!input7.isEmpty()) {
                    values[6] = Integer.parseInt(input5);
                    sum += values[6];
                }

                // Get the screen width (in pixels)
                int screenWidth = displayMetrics.widthPixels;

                // If the sum is zero (avoid division by zero)
                if (sum == 0) {
                    return;
                }

                // Calculate the width for each LinearLayout based on the percentage
                for (int i = 0; i < 7; i++) {
                    if (values[i] != 0) {
                        // Calculate the percentage for each value
                        float percentage = ((float) values[i] / sum) * 100;

                        // Calculate the width based on the screen width and the percentage
                        float scaledWidth = (percentage / 100) * screenWidth;
                        int widthInPx = (int) scaledWidth;

                        // Set the width of each child LinearLayout
                        setChildWidth(i, widthInPx);
                    }
                }
            }
        });
    }


    // Method to set the width of each child LinearLayout
    private void setChildWidth(int index, int widthInPx) {
        // Dynamically find the LinearLayout by ID
        LinearLayout child = null;
        switch (index) {
            case 0:
                child = findViewById(R.id.linearLayout1);
                break;
            case 1:
                child = findViewById(R.id.linearLayout2);
                break;
            case 2:
                child = findViewById(R.id.linearLayout3);
                break;
            case 3:
                child = findViewById(R.id.linearLayout4);
                break;
            case 4:
                child = findViewById(R.id.linearLayout5);
                break;
            case 5:
                child = findViewById(R.id.linearLayout6);
                break;
            case 6:
                child = findViewById(R.id.linearLayout7);
                break;
        }

        if (child != null) {
            // Set the width of the identified LinearLayout
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
            params.width = widthInPx;
            child.setLayoutParams(params);
        }
    }

}