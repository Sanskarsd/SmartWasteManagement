package com.example.smartwastemanagement;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Signup_Map_Activity extends AppCompatActivity {

    WebView webView;
    EditText location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_map);

        location = findViewById(R.id.coordinates);

        webView = findViewById(R.id.webviewSignIn);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        String url = "https://www.google.com/maps"; // Simple Google Map
        webView.loadUrl(url);
    }



    public void gotoRegister3(View view)
    {
        String locationText = location.getText().toString().trim();
        Intent intent = new Intent();
        intent.putExtra("location", locationText);
        setResult(RESULT_OK, intent);
        finish();
    }
}