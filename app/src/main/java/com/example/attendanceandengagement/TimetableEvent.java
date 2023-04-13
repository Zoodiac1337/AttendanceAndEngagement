package com.example.attendanceandengagement;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TimetableEvent extends AppCompatActivity {

    private TextView eventTitle;
    private TextView eventTime;
    private TextView eventLocation;
    private TextView eventDescription;
    private Spinner spinnerAttendance;
    private Spinner spinnerEngagement;
    private Bundle bundle;
    private Button submitButton;
    private String previousEngagement = "";
    private String previousAttendance = "";
    private Long previousEngagementScore = 0l;
    private DocumentReference docRef;
    private DocumentReference docAttendanceAndEngagement;
    private ArrayAdapter<String> attendanceAdapter;
    private ArrayAdapter<String> engagementAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_event);
        bundle = getIntent().getExtras();

        docRef = db.collection("Users/" + bundle.getString("Email") + "/AttendanceAndEngagement/"+bundle.getString("Module")+"/TimetableEvents").document(bundle.getString("EventID"));
        docAttendanceAndEngagement = db.collection("Users/" + bundle.getString("Email") + "/AttendanceAndEngagement/").document(bundle.getString("Module"));

        spinnerAttendance = findViewById(R.id.spinnerAttendance);
        spinnerEngagement = findViewById(R.id.spinnerEngagement);
        eventTitle = findViewById(R.id.eventTitle);
        eventTime = findViewById(R.id.eventTime);
        eventLocation = findViewById(R.id.eventLocation);
        eventDescription = findViewById(R.id.eventDescription);
        submitButton = findViewById(R.id.submitAttendance);

        eventTitle.setText(bundle.getString("Title"));
        if (bundle.getString("Title").equals(""))
            findViewById(R.id.titleLayout).setVisibility(View.GONE);
        eventTime.setText(bundle.getString("Time"));
        if (bundle.getString("Time").equals(""))
            findViewById(R.id.timeLayout).setVisibility(View.GONE);
        eventLocation.setText(bundle.getString("Location"));
        if (bundle.getString("Location").equals(""))
            findViewById(R.id.locationLayout).setVisibility(View.GONE);
        eventDescription.setText(bundle.getString("Description"));
        if (bundle.getString("Description").equals(""))
            findViewById(R.id.descriptionLayout).setVisibility(View.GONE);

        attendanceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Present", "Absent", "Not recorded"});
        engagementAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Very Poor", "Poor", "Average", "Good", "Very Good"});

        spinnerAttendance.setAdapter(attendanceAdapter);
        spinnerEngagement.setAdapter(engagementAdapter);

        setFromDatabase();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerAttendance.getSelectedItem() != "Not recorded") {
                    if (previousEngagement == "") {
                        docAttendanceAndEngagement.update(spinnerEngagement.getSelectedItem().toString(), FieldValue.increment(1));
                        docAttendanceAndEngagement.update("Engagement", FieldValue.increment(spinnerEngagement.getSelectedItemPosition()));
                    }
                    else if (previousEngagement != spinnerEngagement.getSelectedItem()) {
                        docAttendanceAndEngagement.update(previousEngagement, FieldValue.increment(-1));
                        docAttendanceAndEngagement.update(spinnerEngagement.getSelectedItem().toString(), FieldValue.increment(1));
                        docAttendanceAndEngagement.update("Engagement", FieldValue.increment(-previousEngagementScore));
                        docAttendanceAndEngagement.update("Engagement", FieldValue.increment(spinnerEngagement.getSelectedItemPosition()));
                    }
                    if (previousAttendance == "")
                        docAttendanceAndEngagement.update(spinnerAttendance.getSelectedItem().toString(), FieldValue.increment(1));

                    else if (previousAttendance != spinnerAttendance.getSelectedItem()) {
                        if (spinnerAttendance.getSelectedItem().equals("Absent")){
                            docAttendanceAndEngagement.update(spinnerEngagement.getSelectedItem().toString(), FieldValue.increment(-1));
                            docAttendanceAndEngagement.update("Engagement", FieldValue.increment(-spinnerEngagement.getSelectedItemPosition()));
                            previousEngagement = "";
                            previousEngagementScore = 0l;
                        }
                        docAttendanceAndEngagement.update(previousAttendance, FieldValue.increment(-1));
                        docAttendanceAndEngagement.update(spinnerAttendance.getSelectedItem().toString(), FieldValue.increment(1));
                    }

                    Map<String, Object> data = new HashMap<>();
                    data.put("Attendance", spinnerAttendance.getSelectedItem());
                    previousAttendance = spinnerAttendance.getSelectedItem().toString();
                    if (spinnerAttendance.getSelectedItem() != "Absent") {
                        data.put("Engagement", spinnerEngagement.getSelectedItem());
                        previousEngagement = spinnerEngagement.getSelectedItem().toString();
                        data.put("EngagementScore", spinnerEngagement.getSelectedItemPosition());
                        previousEngagementScore = Long.valueOf(spinnerEngagement.getSelectedItemPosition());
                    }
                    docRef.set(data);

                }
            }

        });

    }

    private void setFromDatabase() {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.getString("Attendance")!=null) {
                        spinnerAttendance.setSelection(attendanceAdapter.getPosition(document.getString("Attendance")));
                        previousAttendance = document.getString("Attendance");
                    }
                    else
                        spinnerAttendance.setSelection(attendanceAdapter.getPosition("Not recorded"));
                    if (document.getLong("EngagementScore")!=null) {
                        spinnerEngagement.setSelection(Math.toIntExact(document.getLong("EngagementScore")));
                        previousEngagementScore = document.getLong("EngagementScore");
                        previousEngagement = document.getString("Engagement");
                    }
                    else
                        spinnerEngagement.setSelection(engagementAdapter.getPosition("Average"));
                }
            }
        });
    }
}
