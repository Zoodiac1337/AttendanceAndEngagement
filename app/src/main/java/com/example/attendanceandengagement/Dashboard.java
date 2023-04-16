package com.example.attendanceandengagement;

import static android.content.ContentValues.TAG;

import static java.lang.Math.round;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dashboard extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Bundle bundle;
    private TextView attendanceText;
    private TextView engagementText;

    private BarChart attendanceChart;
    private BarChart engagementChart;

    public Dashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Timetable.
     */
    // TODO: Rename and change types and number of parameters
    public static Dashboard newInstance(String param1, String param2) {
        Dashboard fragment = new Dashboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_dashboard, container, false);
        bundle = getArguments();

        attendanceText = (TextView) view.findViewById(R.id.attendanceScore);
        engagementText = (TextView) view.findViewById(R.id.engagementScore);
        attendanceChart = (BarChart) view.findViewById(R.id.attendanceChart);
        engagementChart = (BarChart) view.findViewById(R.id.engagementChart);
        Map<String, Float> barChartEntries = new TreeMap<String, Float>();
        barChartEntries.put("AAD", 90f);
        barChartEntries.put("MAs", 60f);

        getAttendance();
        return view;
    }


    private void getAttendance(){
        db.collection("Users/" + bundle.getString("email") + "/AttendanceAndEngagement/").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Map<String, Long> attendanceEntries = new TreeMap<String, Long>();
                Map<String, float[]> engagementEntries = new TreeMap<String, float[]>();
                Float overallAttendance = 0f;
                Float overallEngagement = 0f;
                String[] engagementScores = new String[]{"Very Poor", "Poor", "Average", "Good", "Very Good"};

                if (task.isSuccessful()) {
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        attendanceEntries.put(document.getString("Title"), ((document.getLong("Present")*100l)/(document.getLong("Absent")+document.getLong("Present"))));
                        overallAttendance = overallAttendance + ((document.getLong("Present")*100l)/(document.getLong("Absent")+document.getLong("Present")));
                        engagementEntries.put(document.getString("Title"), new float[] {document.getLong("Very Good"), document.getLong("Good"), document.getLong("Average"), document.getLong("Poor"), document.getLong("Very Poor")});
                        overallEngagement = overallEngagement + ((float)document.getLong("Engagement")/(float)document.getLong("Present"));
                        i++;
                    }

                    attendanceText.setText("Combined attendance: "+overallAttendance/i+"%");

                    engagementText.setText("Combined engagement: "+engagementScores[(int) round(overallEngagement/i)-1]);

                    setBarData(attendanceEntries);
                    setBarData2(engagementEntries);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
        });
    }

    private void setBarData(Map<String, Long> entries) {
        ArrayList barEntriesArrayList = new ArrayList<>();
        ArrayList Labels = new ArrayList();

        int number = 0;
        for (String i : entries.keySet()) {
            Labels.add(i);
            barEntriesArrayList.add(new BarEntry(entries.get(i), number));
            number++;
        }
        BarDataSet barDataSet = new BarDataSet(barEntriesArrayList, "Attendance percentage per module");

        BarData barData = new BarData(Labels, barDataSet);


        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);
        barDataSet.setBarSpacePercent(50f);
        XAxis xAxis = attendanceChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        YAxis leftAxis = attendanceChart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setEnabled(false);
        YAxis rightAxis = attendanceChart.getAxisRight();
        rightAxis.setValueFormatter(new PercentFormatter());
        rightAxis.setDrawAxisLine(false);
        attendanceChart.setData(barData);
        attendanceChart.setDescription("");
        attendanceChart.animateY(750);
        attendanceChart.setDrawBarShadow(false);
        attendanceChart.setDrawGridBackground(false);
        attendanceChart.notifyDataSetChanged();

        attendanceChart.invalidate();
    }

    private void setBarData2(Map<String, float[]> entries) {
        ArrayList barEntriesArrayList = new ArrayList<>();
        ArrayList Labels = new ArrayList();

        int number = 0;
        for (String i : entries.keySet()) {
            Labels.add(i);
            barEntriesArrayList.add(new BarEntry(entries.get(i), number));
            number++;
        }
        BarDataSet barDataSet = new BarDataSet(barEntriesArrayList, "");

        BarData barData = new BarData(Labels, barDataSet);


        barDataSet.setColors(new int[] { R.color.green, R.color.light_green, R.color.yellow, R.color.orange, R.color.red }, getContext());
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(0);
        barDataSet.setBarSpacePercent(50f);
        String[] labels = {"Very Good", "Good", "Average", "Poor", "Very Poor"};
        barDataSet.setStackLabels(labels);

        XAxis xAxis = engagementChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        YAxis leftAxis = engagementChart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setEnabled(false);
        YAxis rightAxis = engagementChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        engagementChart.setData(barData);
        engagementChart.setDescription("");
        engagementChart.animateY(750);
        engagementChart.setDrawBarShadow(false);
        engagementChart.setDrawGridBackground(false);
        engagementChart.notifyDataSetChanged();
        engagementChart.invalidate();
    }

}