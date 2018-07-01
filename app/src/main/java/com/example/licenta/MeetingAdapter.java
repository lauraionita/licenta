package com.example.licenta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder> {

    private Context mCtx;
    private List<Meeting> meetingList;


    public MeetingAdapter(Context mCtx, List<Meeting> meetingtList) {
        this.mCtx = mCtx;
        this.meetingList= meetingtList;
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_meeting, parent, false);
        return new MeetingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingViewHolder holder, int position) {

        final Meeting meeting = meetingList.get(position);
        final Meeting meetingDetails = getDetails();
        meetingDetails.name = meeting.name;

        holder.textViewName.setText(meeting.name);
//        holder.textViewDate.setText("Data: " + meeting.date);
//        holder.textViewStartMeeting.setText("Start meeting: " + meeting.startMeeting);
//        holder.textViewEndMeeting.setText("End meeting: " + meeting.endMeeting);
//        holder.textViewCapacity.setText("Capacity: " + meeting.capacity);

        holder.buttonAddMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("RoomsMeeting"+getDomainCompany());
                String key = ref.push().getKey();
                ref.child(key).setValue(meetingDetails);

                Toast.makeText(mCtx, "Meeting added succesfully", Toast.LENGTH_LONG).show();
                mCtx.startActivity((new Intent(mCtx, BookMeeting.class)));


                }

            });
    }
    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    class MeetingViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewFloor, textViewEquipment;
        Button buttonAddMeeting;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.tv_name);
            textViewFloor = itemView.findViewById(R.id.tv_floor);
            textViewEquipment = itemView.findViewById(R.id.tv_equipment);

        }
    }

    private Meeting getDetails()
    {   Meeting meetingDetails = new Meeting();
        SharedPreferences prefs = mCtx.getSharedPreferences("BookMeeting", MODE_PRIVATE);
        //String restoredText = prefs.getString("text", null);
        meetingDetails.date = prefs.getString("date", "No name defined");//"No name defined" is the default value.
        meetingDetails.startMeeting = prefs.getString("hourStart", "No name defined");
        meetingDetails.endMeeting= prefs.getString("hourEnd", "No name defined");
        meetingDetails.capacity = prefs.getInt("capacity", 0); //0 is the default value.

        SharedPreferences pref = mCtx.getSharedPreferences("MainActivity", MODE_PRIVATE);
        meetingDetails.idUser =pref.getString("idUser", "No name defined");

        return meetingDetails;
    }

    private String getDomainCompany ()
    {
        SharedPreferences prefs = mCtx.getSharedPreferences("MainActivity", MODE_PRIVATE);
        String domain = prefs.getString("domain", "No name defined");
        domain = domain.substring(0,1).toUpperCase() + domain.substring(1).toLowerCase();

        return domain;
    }
}