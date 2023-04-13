package com.example.attendanceandengagement;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.attendanceandengagement.ListAdapters.ListAdapterCalendars;
import com.example.attendanceandengagement.ListAdapters.ListAdapterTimetable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Timetable#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Timetable extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Cursor cur = null;
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };
    ListView listview;

    private static final String DEBUG_TAG = "MyActivity";
    public static final String[] INSTANCE_PROJECTION = new String[] {
            CalendarContract.Instances.EVENT_ID,      // 0
            CalendarContract.Instances.BEGIN,         // 1
            CalendarContract.Instances.END,           // 2
            CalendarContract.Instances.TITLE,         // 3
            CalendarContract.Events.DESCRIPTION,      // 4
            CalendarContract.Events.EVENT_LOCATION    // 5
    };
    private Button chooseCalendarsButton;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Button backToTimetable;
    private Button previousWeekButton;
    private Button nextWeekButton;
    private TextView noEventsText;
    private LocalDate month;
    private Bundle bundle;
    private View calendarsHeader;
    private View timetableHeader;
    private View timetableFooter;

    public Timetable() {
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
    public static Timetable newInstance(String param1, String param2) {
        Timetable fragment = new Timetable();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        bundle = getArguments();
        preferences = this.getActivity().getSharedPreferences(bundle.getString("email"), Context.MODE_PRIVATE);
        editor = preferences.edit();
        listview = (ListView) view.findViewById(R.id.calendarsOrTimetableList);

        if (preferences.getString("calendar", "noPreference") == "noPreference")
            switchListToCalendars();
        else switchListToTimetable(preferences.getString("calendar", "noPreference"));
        return view;
    }

    private void getAllCalendars(){
        ContentResolver cr = getContext().getContentResolver();

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        cur = cr.query(uri, EVENT_PROJECTION, null, null, null);

        String[] displayNames = new String[cur.getCount()];
        String[] accountNames = new String[cur.getCount()];
        String[] ownerNames = new String[cur.getCount()];
        Long[] calIDs = new Long[cur.getCount()];
        int i = 0;

        while (cur.moveToNext()) {
            long calID;
            String displayName;
            String accountName;
            String ownerName;

            // Get the field values
            calID = cur.getLong(0);
            accountName = cur.getString(1);
            displayName = cur.getString(2);
            ownerName = cur.getString(3);

            // Do something with the values...
            calIDs[i] = calID;
            displayNames[i] = displayName;
            accountNames[i] = accountName;
            ownerNames[i] = ownerName;
            i++;
        }

        ListAdapterCalendars lAdapter = new ListAdapterCalendars(getContext(), displayNames, accountNames, ownerNames, calIDs);
        listview.setAdapter(lAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putString("calendar", calIDs[i-1].toString());
                editor.apply();
                switchListToTimetable(calIDs[i-1].toString());
            }
        });
    }

    private void getEventsInDates(String calID, LocalDate startDate){
        // Specify the date range you want to search for recurring event instances
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth(), 0, 0);
        long startMillis = beginTime.getTimeInMillis();

        LocalDate nextMonth = startDate.plusMonths(1);
        Calendar endTime = Calendar.getInstance();
        endTime.set(nextMonth.getYear(), nextMonth.getMonthValue(), nextMonth.getDayOfMonth(), 0, 0);
        long endMillis = endTime.getTimeInMillis();
        ContentResolver cr = getContext().getContentResolver();

// Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

        String [] calendarID = new String[]{calID};

// Submit the query
        cur =  cr.query(builder.build(),
                INSTANCE_PROJECTION,
                CalendarContract.Instances.CALENDAR_ID + " = ?",
                calendarID,
                CalendarContract.Events.DTSTART + " ASC");

        String[] titles = new String[cur.getCount()];
        Long[] eventIDs = new Long[cur.getCount()];
        Long[] beginVals = new Long[cur.getCount()];
        Long[] endVals = new Long[cur.getCount()];

        String[] locations = new String [cur.getCount()];
        String[] descriptions = new String [cur.getCount()];
        int i = 0;

        while (cur.moveToNext()) {
            // Get the field values
            eventIDs[i] = cur.getLong(0);
            beginVals[i] = cur.getLong(1);
            endVals[i] = cur.getLong(2);
            titles[i] = cur.getString(3);

            descriptions [i] = cur.getString(4);
            locations [i] = cur.getString(5);
            i++;
        }
        if (cur.getCount() == 0) {
            noEventsText.setText(startDate + "   until   " + nextMonth+"\n\nYou have no events this week!\n");
        }
        else {
            noEventsText.setText(startDate + "   until   " + nextMonth);
        }
        ListAdapterTimetable lAdapter = new ListAdapterTimetable(getContext(), titles, beginVals, endVals, eventIDs);
        listview.setAdapter(lAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Calendar begin = Calendar.getInstance();
                begin.setTimeInMillis(beginVals[i-1]);
                Calendar end = Calendar.getInstance();
                end.setTimeInMillis(endVals[i-1]);

                String module ="";
                if (titles[i-1].contains("COMP30231"))
                    module = "COMP30231";
                else if (titles[i-1].contains("ISYS30221"))
                    module = "ISYS30221";
                else if (titles[i-1].contains("ITEC31041"))
                    module = "ITEC31041";
                else if (titles[i-1].contains("COMP30151"))
                    module = "COMP30151";
                else if (titles[i-1].contains("SOFT30121"))
                    module = "SOFT30121";

                DateFormat time = new SimpleDateFormat("HH:mm");

                Intent intent = new Intent(getActivity(), TimetableEvent.class);
                intent.putExtra("Title", titles[i-1]);
                intent.putExtra("Module", module);
                intent.putExtra("Time", time.format(begin.getTime())+"-"+time.format(end.getTime()));
                intent.putExtra("Location", locations[i-1]);
                intent.putExtra("Description", descriptions[i-1]);
                intent.putExtra("EventID", calID+"_"+eventIDs[i-1]);
                intent.putExtra("Email", bundle.getString("email"));

                startActivity(intent);
            }
        });
    }

    private void switchListToTimetable(String calendarID){
        timetableHeader = getLayoutInflater().inflate(R.layout.timetable_header, null);
        timetableFooter = getLayoutInflater().inflate(R.layout.timetable_footer, null);

        chooseCalendarsButton = (Button) timetableHeader.findViewById(R.id.chooseCalendarsButton);
        previousWeekButton = (Button) timetableHeader.findViewById(R.id.previousWeekButton);
        noEventsText = (TextView) timetableHeader.findViewById(R.id.noEventsText);
        nextWeekButton = (Button) timetableFooter.findViewById(R.id.nextWeekButton);

        listview.removeHeaderView(calendarsHeader);
        listview.addHeaderView(timetableHeader);
        listview.addFooterView(timetableFooter);

        getEventsInDates(calendarID, LocalDate.now());

        month = LocalDate.now();

        nextWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                month = month.plusMonths(1);
                getEventsInDates(calendarID, month);
            }
        });

        previousWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                month = month.minusMonths(1);
                getEventsInDates(calendarID, month);
            }
        });

        chooseCalendarsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchListToCalendars();
            }
        });
    }

    private void switchListToCalendars(){
        calendarsHeader = getLayoutInflater().inflate(R.layout.calendar_header, null);
        backToTimetable = (Button) calendarsHeader.findViewById(R.id.backToCalendar);

        listview.removeFooterView(timetableFooter);
        listview.removeHeaderView(timetableHeader);
        listview.addHeaderView(calendarsHeader);

        if (preferences.getString("calendar", "noPreference") == "noPreference")
            backToTimetable.setVisibility(View.GONE);
        else {
            backToTimetable.setVisibility(View.VISIBLE);
            backToTimetable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchListToTimetable(preferences.getString("calendar", "noPreference"));
                }
            });
        }
        getAllCalendars();
    }
}