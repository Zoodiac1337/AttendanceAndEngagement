package com.example.attendanceandengagement;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.attendanceandengagement.ListAdapters.ListAdapterDeadlines;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Deadlines#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Deadlines extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Deadlines() {
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
    public static Deadlines newInstance(String param1, String param2) {
        Deadlines fragment = new Deadlines();
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
    Bundle bundle;
    String currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListView deadlinesListView;
    ListAdapterDeadlines lAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_deadlines, container, false);
        bundle = getArguments();
        deadlinesListView = (ListView) view.findViewById(R.id.deadlinesList);
        currentUser = bundle.getString("email");
        getListItems();


        return view;
    }

    public void getListItems() {
        db.collection("Users/"+currentUser+"/Deadlines").orderBy("Date", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String[] name = new String[task.getResult().size()];
                Date[] date = new Date[task.getResult().size()];
                String[] description = new String[task.getResult().size()];
                if (task.isSuccessful()) {
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        name[i] = document.getString("Name");
                        date[i] = document.getDate("Date");
                        description[i] = document.getString("Description");
                        i++;
                    }
                    populateListWithItems(name, date, description);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
        });

    }

    public void populateListWithItems(String[] name, Date[] date, String[] description) {

        lAdapter = new ListAdapterDeadlines(getActivity(), name, date, description);
        deadlinesListView.setAdapter(lAdapter);
    }
}