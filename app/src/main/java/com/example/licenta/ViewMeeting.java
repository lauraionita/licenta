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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewMeeting extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ViewMeetingAdapter adapter;
    private List<Meeting> meetingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meeting);
        recyclerView = findViewById(R.id.recyclerViewMeeting);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        meetingList = new ArrayList<>();
        adapter = new ViewMeetingAdapter(this, meetingList);
        recyclerView.setAdapter(adapter);

        String domain = getDomainCompany();
        domain = domain.substring(0,1).toUpperCase() + domain.substring(1).toLowerCase();

        Query query = FirebaseDatabase.getInstance().getReference("RoomsMeeting"+domain)
                .orderByChild("idUser")
                .equalTo(getIdUser());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                meetingList.clear();
                if (dataSnapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Meeting meeting = snapshot.getValue(Meeting.class);
                        String id = snapshot.getKey();
                        setIdMeeting(i, id);
                        meetingList.add(meeting);
                        i = i+1;
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(ViewMeeting.this, "No room availables", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ViewMeeting.this, ProfileUser.class));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

   private String getIdUser ()
   {
       SharedPreferences prefs = getSharedPreferences("MainActivity", MODE_PRIVATE);
       String idUser =prefs.getString("idUser", "No name defined");
       return idUser;
   }

    private String getDomainCompany ()
    {
        SharedPreferences prefs = getSharedPreferences("MainActivity", MODE_PRIVATE);
        String domain = prefs.getString("domain", "No name defined");
        return domain;
    }

    private void setIdMeeting (int unique, String idMeeting){
        String idUnique = Integer.toString(unique);
        SharedPreferences.Editor editor = getSharedPreferences("ViewMeeting", MODE_PRIVATE).edit();
        editor.putString(idUnique, idMeeting);
        editor.apply();
    }
}



