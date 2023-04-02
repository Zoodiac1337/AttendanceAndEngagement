package com.example.attendanceandengagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Resources#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Resources extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Resources() {
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
    public static Resources newInstance(String param1, String param2) {
        Resources fragment = new Resources();
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

    LinearLayout library;
    LinearLayout international_support;
    LinearLayout employability;
    LinearLayout disability;
    LinearLayout student_support;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resources, container, false);
        library = (LinearLayout) view.findViewById(R.id.library);
        international_support = (LinearLayout) view.findViewById(R.id.international_support);
        employability = (LinearLayout) view.findViewById(R.id.employability);
        disability = (LinearLayout) view.findViewById(R.id.disability);
        student_support = (LinearLayout) view.findViewById(R.id.student_support);

        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Library.", Toast.LENGTH_SHORT).show();
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                startActivity(new Intent(getContext(), Resources2.class).putExtra("link", "https://www.ntu.ac.uk/m/library"));
            }
        });

        international_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "international_support.", Toast.LENGTH_SHORT).show();
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                startActivity(new Intent(getContext(), Resources2.class).putExtra("link", "https://www.ntu.ac.uk/studenthub/international-student-support"));
            }
        });

        employability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "employability.", Toast.LENGTH_SHORT).show();
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                startActivity(new Intent(getContext(), Resources2.class).putExtra("link", "https://www.ntu.ac.uk/studenthub/student-help-advice-and-services/employability"));
            }
        });

        disability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "disability.", Toast.LENGTH_SHORT).show();
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                startActivity(new Intent(getContext(), Resources2.class).putExtra("link", "https://www.ntu.ac.uk/life-at-ntu/support/disability-support"));
            }
        });

        student_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "student_support.", Toast.LENGTH_SHORT).show();
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                startActivity(new Intent(getContext(), Resources2.class).putExtra("link", "https://www.ntu.ac.uk/studenthub/student-help-advice-and-services"));
            }
        });

        return view;
    }
}