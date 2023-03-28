package com.example.attendanceandengagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class StudentActivity extends AppCompatActivity {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    final Fragment Timetable = new Timetable();
    final Fragment Study = new Study();
    final Fragment Deadlines = new Deadlines();
    final Fragment Resources = new Resources();
    final Fragment Dashboard = new Dashboard();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationBar);
        MaterialToolbar topBar = findViewById(R.id.topAppBar);


        Bundle bundle = getIntent().getExtras();
        Study.setArguments(bundle);
        Deadlines.setArguments(bundle);

        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.timetable:
                                topBar.setTitle("Timetable");
                                topBar.setNavigationIcon(R.drawable.round_calendar_today_24);
                                fragmentManager.beginTransaction().replace(R.id.fragmentView, Timetable).commit();
                                break;
                            case R.id.study:
                                topBar.setTitle("Study");
                                topBar.setNavigationIcon(R.drawable.round_school_24);
                                fragmentManager.beginTransaction().replace(R.id.fragmentView, Study).commit();
                                break;
                            case R.id.deadlines:
                                topBar.setTitle("Deadlines");
                                topBar.setNavigationIcon(R.drawable.round_warning_24);
                                fragmentManager.beginTransaction().replace(R.id.fragmentView, Deadlines).commit();
                                break;
                            case R.id.resources:
                                topBar.setTitle("Resources");
                                topBar.setNavigationIcon(R.drawable.round_library_books_24);
                                fragmentManager.beginTransaction().replace(R.id.fragmentView, Resources).commit();
                                break;
                            case R.id.dashboard:
                                topBar.setTitle("Dashboard");
                                topBar.setNavigationIcon(R.drawable.round_space_dashboard_24);
                                fragmentManager.beginTransaction().replace(R.id.fragmentView, Dashboard).commit();
                                break;
                        }

                        return true;
                    }
                });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.timetable);
    }


}