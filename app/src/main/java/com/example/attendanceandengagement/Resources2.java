package com.example.attendanceandengagement;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class Resources2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_2);
        Bundle bundle = getIntent().getExtras();

        WebView website = (WebView) findViewById(R.id.websiteView);
        website.loadUrl(bundle.getString("link"));
    }
}