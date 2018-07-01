package com.example.licenta;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterHolder> {

    private Context mCtx;
    private List<EventObjects> meetingList;
    private int position;;


    public EventAdapter(Context mCtx, List<EventObjects> meetingtList) {
        this.mCtx = mCtx;
        this.meetingList= meetingtList;
    }

    @NonNull
    @Override
    public EventAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recycler_events, parent, false);
        final  EventAdapterHolder holder = new EventAdapterHolder(view);
        return new EventAdapterHolder(view);
    }

    //    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull EventAdapterHolder holder, final int position) {

        final EventObjects events = meetingList.get(position);

        holder.textViewDayMeeting.setText("Ziua: "+ events.getDate());
        holder.textViewMonthMeeting.setText("Luna: " + events.getMessage());




    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    class EventAdapterHolder extends RecyclerView.ViewHolder {

        TextView textViewDayMeeting, textViewMonthMeeting, textViewStartMeeting, textViewEndMeeting, textViewLocation, textViewAddPeople, textViewAtendees, textViewIdMeet;
        ImageButton buttonCancel;




        public EventAdapterHolder(@NonNull View itemView) {
            super(itemView);

            textViewDayMeeting = itemView.findViewById(R.id.tv_event_date);
            textViewMonthMeeting = itemView.findViewById(R.id.tv_event_location);


        }
    }


}