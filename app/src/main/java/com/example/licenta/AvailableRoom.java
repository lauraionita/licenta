package com.example.licenta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class AvailableRoom extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MeetingAdapter adapter;
    private List<Meeting> meetingList, meetingList1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_room);

        recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //GridLayoutManager mGrid = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        //recyclerView.setHasFixedSize(false);
        //recyclerView.setNestedScrollingEnabled(false);

        meetingList = new ArrayList<>();
        //meetingList1 = new ArrayList<>();
        adapter = new MeetingAdapter(this, meetingList);
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("BookMeeting", MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);

        int capacity = prefs.getInt("capacity", 0); //0 is the default value.
        String domain = getDomainCompany();
        domain = domain.substring(0,1).toUpperCase() + domain.substring(1).toLowerCase();

        Query query = FirebaseDatabase.getInstance().getReference("RoomsMeetingCentric"+domain)
                .orderByChild("capacity")
                .equalTo(capacity);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                meetingList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Meeting meeting = snapshot.getValue(Meeting.class);
                        //meeting.id = snapshot.getKey();

                        if(validateMeeting(meeting)) {
                            meetingList.add(meeting);
                        }
                    }
//                    Set<Meeting> setOfMeetings = new LinkedHashSet<>(meetingList);
//
//                    meetingList.clear();
//                    meetingList.addAll(setOfMeetings);
                    removeDuplicates(meetingList);
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(AvailableRoom.this, "No room availables", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AvailableRoom.this, BookMeeting.class));
                }
            }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
        });
    }

    private Boolean validateMeeting(Meeting meeting) {
        Boolean result = true;
//        SharedPreferences prefs = getSharedPreferences("BookMeeting", MODE_PRIVATE);
//        //String restoredText = prefs.getString("text", null);
//            final String date = prefs.getString("date", "No name defined");//"No name defined" is the default value.
//            final String hourStart = prefs.getString("hourStart", "No name defined");
//            final String hourEnd = prefs.getString("hourEnd", "No name defined");
//            final int capacity = prefs.getInt("capacity", 0); //0 is the default value.
        Meeting meetingDetails = getDetails();

            if (meeting.date.equals(meetingDetails.date)){
                if (meeting.startMeeting.equals(meetingDetails.startMeeting)){
                        return false;
                    }
            }

            return true;
    }

    public Meeting getDetails()
    {    Meeting meeting = new Meeting();
         SharedPreferences prefs = getSharedPreferences("BookMeeting", MODE_PRIVATE);
        //String restoredText = prefs.getString("text", null);
         meeting.date = prefs.getString("date", "No name defined");//"No name defined" is the default value.
         meeting.startMeeting = prefs.getString("hourStart", "No name defined");
         meeting.endMeeting= prefs.getString("hourEnd", "No name defined");
         meeting.capacity = prefs.getInt("capacity", 0); //0 is the default value.

    return meeting;
    }

    private List<Meeting> removeDuplicates(List<Meeting> listWithDuplicates) {
        /* Set of all attributes seen so far */
        Set<String> attributes = new HashSet<String>();
        /* All confirmed duplicates go in here */
        List duplicates = new ArrayList<Meeting>();

        for(Meeting m : listWithDuplicates) {
            if(attributes.contains(m.name)) {
                duplicates.add(m);
            }
            attributes.add(m.name);
        }
        /* Clean list without any dups */
        listWithDuplicates.removeAll(duplicates);

        return listWithDuplicates;
    }
    private String getDomainCompany ()
    {
        SharedPreferences prefs = getSharedPreferences("MainActivity", MODE_PRIVATE);
        String domain =prefs.getString("domain", "No name defined");
        return domain;
    }
}

