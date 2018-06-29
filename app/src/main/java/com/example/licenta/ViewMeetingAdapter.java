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
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ViewMeetingAdapter extends RecyclerView.Adapter<ViewMeetingAdapter.ViewMeetingViewHolder> {

    private Context mCtx;
    private List<Meeting> meetingList;
    private int position;;


    public ViewMeetingAdapter(Context mCtx, List<Meeting> meetingtList) {
        this.mCtx = mCtx;
        this.meetingList= meetingtList;
    }

    @NonNull
    @Override
    public ViewMeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_view_meeting, parent, false);
        final  ViewMeetingViewHolder holder = new ViewMeetingViewHolder(view);
        return new ViewMeetingViewHolder(view);
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewMeetingViewHolder holder, final int position) {

        final Meeting meeting = meetingList.get(position);

        holder.textViewDayMeeting.setText("Ziua: "+ getDayfromDate(meeting.date));
        holder.textViewMonthMeeting.setText("Luna: " + getMonthfromDate(meeting.date));
        holder.textViewStartMeeting.setText("Start meeting: " + meeting.startMeeting);
        holder.textViewEndMeeting.setText("End meeting: " + meeting.endMeeting);
        holder.textViewLocation.setText("Meeting Room: " + meeting.name);
        holder.textViewIdMeet.setText("Id meeting: " + getIdMeeting(position));


        holder.textViewAddPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mCtx.getSharedPreferences("ViewMeetingAdapter", MODE_PRIVATE).edit();
                editor.putString("adapterPosition", Integer.toString(position));
                editor.apply();
                mCtx.startActivity((new Intent(mCtx, AddPeople.class)));
            }

        });

        holder.textViewAtendees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mCtx.getSharedPreferences("ViewMeetingAdapter", MODE_PRIVATE).edit();
                editor.putString("adapterPosition", Integer.toString(position));
                editor.apply();
                mCtx.startActivity((new Intent(mCtx, Atendees.class)));
            }

        });
    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    class ViewMeetingViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDayMeeting, textViewMonthMeeting, textViewStartMeeting, textViewEndMeeting, textViewLocation, textViewAddPeople, textViewAtendees, textViewIdMeet;
        ImageButton buttonCancel;



        private AlertDialog AskOption()
        {
            AlertDialog myQuittingDialogBox =new AlertDialog.Builder(mCtx)
                    //set message, title, and icon
                    .setTitle("Cancel MEETING")
                    .setMessage("Are you sure you want to cancel this meeting?")
                    .setPositiveButton("Cancel MEETING", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            String domain = getDomainCompany();
                            domain = domain.substring(0,1).toUpperCase() + domain.substring(1).toLowerCase();
                            Toast.makeText(mCtx, "Item Removed!", Toast.LENGTH_SHORT).show();
                            deleteLocation(domain);
                            dialog.dismiss();
                            meetingList.remove( position );
                            notifyItemRemoved( position );
                        }
                    })

                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            return myQuittingDialogBox;
        }

        private void deleteLocation(String domainCompany){
            String idMeeting = getIdMeeting(position);
            //String idUser = getId
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Map <String,Object> updates= new HashMap<String,Object>();
            //updates.put("/RoomsMeeting"+domainCompany+"/"+idMeeting+"/",null);
            updates.put("/Atendees"+domainCompany+"/"+idMeeting+"/", null);

            ref.updateChildren(updates, new DatabaseReference.CompletionListener(){
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        System.out.println("Error updating data: " + databaseError.getMessage());
                        Toast.makeText(mCtx, "Item Removed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
           // FirebaseDatabase.getInstance().getReference(dataBase).child(getIdMeeting(getAdapterPosition()).toString()).removeValue();
        }

        public ViewMeetingViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewDayMeeting = itemView.findViewById(R.id.tvDayMeeting);
            textViewMonthMeeting = itemView.findViewById(R.id.tvMonthMeeting);
            textViewStartMeeting = itemView.findViewById(R.id.tvStartMeeting);
            textViewEndMeeting = itemView.findViewById(R.id.tvEndMeeting);
            textViewLocation = itemView.findViewById(R.id.tvLocation);
            textViewAddPeople = itemView.findViewById(R.id.tvAddPeople);
            textViewAtendees= itemView.findViewById(R.id.tvAtendees);
            textViewIdMeet = itemView.findViewById(R.id.tvIdMeet);
            buttonCancel = itemView.findViewById(R.id.img_cancel);
            buttonCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    AlertDialog diaBox = AskOption();
                    diaBox.show();
                }
            });

        }
    }

    private String getDomainCompany ()
    {
        SharedPreferences prefs = mCtx.getSharedPreferences("MainActivity", MODE_PRIVATE);
        String domain = prefs.getString("domain", "No name defined");

        return domain;
    }

    private String getIdMeeting (int i)
    {
        String idUnique = Integer.toString(i);
        SharedPreferences prefs = mCtx.getSharedPreferences("ViewMeeting", MODE_PRIVATE);
        String idMeeting= prefs.getString(idUnique, "No name defined");

        return idMeeting;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getDayfromDate(String dateMeeting)
    {   DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "M/d/yyyy" );
        LocalDate localDate = LocalDate.parse( dateMeeting , formatter );
        int dayOfMonth = localDate.getDayOfMonth();
        return Integer.toString(dayOfMonth);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getMonthfromDate(String dateMeeting)
    {   DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "M/d/yyyy" );
        LocalDate localDate = LocalDate.parse( dateMeeting , formatter );
        String monthName = localDate.getMonth().getDisplayName( TextStyle.FULL , Locale.US );
        return monthName;
    }
}