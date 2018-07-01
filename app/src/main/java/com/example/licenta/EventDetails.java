package com.example.licenta;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventDetails extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<EventObjects> meetingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meeting);
        recyclerView = findViewById(R.id.recyclerEvents);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        meetingList = new ArrayList<>();
        adapter = new EventAdapter(this, meetingList);
        recyclerView.setAdapter(adapter);
        getDetails();



    }


    public void getDetails() {
        SharedPreferences prefA = getSharedPreferences("CustomCalendarView", MODE_PRIVATE);

        Map<String, ?> allEntries = prefA.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }
    }

}