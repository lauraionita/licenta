package com.example.licenta;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookMeeting extends AppCompatActivity {
    private TextView mDisplayDate, mDisplayTimeStart, mDisplayTimeEnd, textViewName, textViewDate, textViewStartMeeting, textViewEndMeeting, textViewCapacity;;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeStartSetListener, mTimeEndSetListener;
    private CardView mcvMeeting;
    private EditText metCapacity;
    private Context mContext = BookMeeting.this;



    DatabaseReference dbMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_meeting);


        mDisplayDate = (TextView) findViewById(R.id.tvDate);
        mDisplayTimeStart = (TextView) findViewById(R.id.tvHourStart);
        mDisplayTimeEnd = (TextView) findViewById(R.id.tvHourEnd);
        mcvMeeting = (CardView) findViewById(R.id.cvMeeting);
        metCapacity = (EditText) findViewById(R.id.etCapacity);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        BookMeeting.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });


        mDisplayTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(
                        BookMeeting.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeStartSetListener,
                        hour, minute, false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();


            }
        });

        mDisplayTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(
                        BookMeeting.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeEndSetListener,
                        hour, minute, false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();


            }
        });

        mTimeStartSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String time = hour + ":" + minute;
                mDisplayTimeStart.setText(time);
                mDisplayTimeStart.setTextColor(Color.parseColor("#fffafa"));
            }
        };

        mTimeEndSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String time = hour + ":" + minute;
                mDisplayTimeEnd.setText(time);
                mDisplayTimeEnd.setTextColor(Color.parseColor("#fffafa"));
            }
        };

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
                mDisplayDate.setTextColor(Color.parseColor("#fffafa"));
            }
        };




        mcvMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int capacity = Integer.parseInt(metCapacity.getText().toString());
                String date = mDisplayDate.getText().toString().trim();
                String hourStart = mDisplayTimeStart.getText().toString().trim();
                String hourEnd = mDisplayTimeEnd.getText().toString().trim();
                setLocalData(capacity, date, hourStart, hourEnd);
                startActivity(new Intent(BookMeeting.this, AvailableRoom.class));


        };
    });
}
    public void setLocalData (int capacity, String date, String hourStart, String hourEnd){
        SharedPreferences.Editor editor = getSharedPreferences("BookMeeting", MODE_PRIVATE).edit();
        editor.putString("date", date);
        editor.putString("hourStart", hourStart);
        editor.putString("hourEnd", hourEnd);
        editor.putInt("capacity", capacity);
        editor.apply();

    }
}