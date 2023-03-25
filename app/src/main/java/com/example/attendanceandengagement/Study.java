package com.example.attendanceandengagement;

import static java.lang.Long.parseLong;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.Period;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Study#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Study extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Study() {
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
    public static Study newInstance(String param1, String param2) {
        Study fragment = new Study();
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

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CountDownTimer[] timer = {null};
    Button startButton;
    Button stopButton;
    TextView timerText;
    TableRow timerRow1;
    TableRow timerRow2;
    TextView timerSeconds;
    TextView timerMinutes;
    EditText pomodoroWork;
    EditText pomodoroBreak;
    MediaPlayer notification;
    TextView day0;
    TextView day1;
    TextView day2;
    TextView day3;
    TextView day4;
    TextView day5;
    TextView day6;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_study, container, false);
        updatePastDays();
        // Inflate the layout for this fragment
        notification = MediaPlayer.create(getContext(), R.raw.notification);
        pomodoroBreak = (EditText) view.findViewById(R.id.pomodoroBreak);
        timerSeconds = (TextView) view.findViewById(R.id.timerSeconds);
        timerMinutes = (TextView) view.findViewById(R.id.timerMinutes);
        pomodoroWork = (EditText) view.findViewById(R.id.pomodoroWork);
        startButton = (Button) view.findViewById(R.id.timerStartButton);
        stopButton = (Button) view.findViewById(R.id.timerStopButton);
        timerText = (TextView) view.findViewById(R.id.timerText);
        timerRow1 = (TableRow) view.findViewById(R.id.timerRow1);
        timerRow2 = (TableRow) view.findViewById(R.id.timerRow2);
        day0 = (TextView) view.findViewById(R.id.day0);
        day1 = (TextView) view.findViewById(R.id.day1);
        day2 = (TextView) view.findViewById(R.id.day2);
        day3 = (TextView) view.findViewById(R.id.day3);
        day4 = (TextView) view.findViewById(R.id.day4);
        day5 = (TextView) view.findViewById(R.id.day5);
        day6 = (TextView) view.findViewById(R.id.day6);

        timerRow1.setVisibility(View.GONE);
        timerRow2.setVisibility(View.VISIBLE);

        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    studyTimer();
                }
                catch (NumberFormatException e)
                {
                    Toast.makeText(getContext(), "Missing fields.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        stopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stopTimer();
            }
        });
        return view;
    }

    public void studyTimer()
    {
        if (timer[0] != null)
            timer[0].cancel();
        int workMinutes = Integer.parseInt(pomodoroWork.getText().toString());
        int workSeconds = workMinutes * 60;

        timerText.setText("Study time: ");
        timerRow1.setVisibility(View.VISIBLE);
        timerRow2.setVisibility(View.GONE);

        timer[0] = new CountDownTimer(workSeconds * 1000, 1000)
        {
            int seconds = 0;
            int minutes = workMinutes;

            @Override
            public void onTick(long millisUntilFinished)
            {
                timerSeconds.setText(String.valueOf(seconds));
                timerMinutes.setText(String.valueOf(minutes));
                if (seconds == 0) {
                    seconds = 60;
                    timerMinutes.setText(String.valueOf(minutes));
                    minutes--;
                }
                seconds--;
            }

            @Override
            public void onFinish()
            {
                breakTimer();
                updateMinutes(workMinutes);
                updatePastDays();
                notification.start();
            }
        };
        timer[0].start();
    }

    public void breakTimer()
    {
        final MediaPlayer notification = MediaPlayer.create(getContext(), R.raw.notification);

        if (timer[0] != null)
            timer[0].cancel();

        int breakMinutes = Integer.parseInt(pomodoroBreak.getText().toString());
        int breakSeconds = breakMinutes * 60;

        timerText.setText("Break time: ");

        timer[0] = new CountDownTimer(breakSeconds * 1000, 1000)
        {
            int seconds = 0;
            int minutes = breakMinutes;

            @Override
            public void onTick(long millisUntilFinished)
            {
                timerSeconds.setText( String.valueOf(seconds));
                timerMinutes.setText(String.valueOf(minutes));
                if (seconds == 0) {
                    seconds = 60;
                    timerMinutes.setText(String.valueOf(minutes));
                    minutes--;
                }
                seconds--;
            }

            @Override
            public void onFinish()
            {
                notification.start();
                studyTimer();
            }
        };
        timer[0].start();
    }

    public void stopTimer()
    {


        if (timer[0]!=null) {
            timerRow1.setVisibility(View.GONE);
            timerRow2.setVisibility(View.VISIBLE);
            if (timerText.getText().toString().equals("Study time: ")) {
                if (Integer.parseInt(timerSeconds.getText().toString()) < 10)
                    updateMinutes(Integer.parseInt(pomodoroWork.getText().toString()) - (Integer.parseInt(timerMinutes.getText().toString())));
                else
                    updateMinutes(Integer.parseInt(pomodoroWork.getText().toString()) - (Integer.parseInt(timerMinutes.getText().toString()))-1);
                updatePastDays();
            }
                timer[0].cancel();
        }
    }
    public void updateMinutes(int minutesStudied)
    {
        String todayString = LocalDate.now().toString();
        Bundle bundle = getArguments();
        DocumentReference docRef = db.collection("Users").document(bundle.getString("email"));
        docRef.update("minutesStudied."+todayString, FieldValue.increment(minutesStudied));
    }

    public void updatePastDays()
    {
        Bundle bundle = getArguments();
        DocumentReference docRef = db.collection("Users").document(bundle.getString("email"));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Long[] studiedTime = new Long[7];
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    for (int i = 0; i <= 6; i++) {
                        LocalDate date = LocalDate.now().minus(Period.ofDays(i));
                        studiedTime[i] = document.getLong("minutesStudied."+date);
                        if (studiedTime[i]==null)
                            studiedTime[i] = 0L;
                    }
                    setPastDays(studiedTime);
                }
            }

        });

    }

    public void setPastDays(Long[] studiedTime)
    {
        day0.setText(studiedTime[0].toString());
        day1.setText(studiedTime[1].toString());
        day2.setText(studiedTime[2].toString());
        day3.setText(studiedTime[3].toString());
        day4.setText(studiedTime[4].toString());
        day5.setText(studiedTime[5].toString());
        day6.setText(studiedTime[6].toString());
    }
}