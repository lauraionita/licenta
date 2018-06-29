package com.example.licenta;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

public class ProfileUser extends AppCompatActivity {
    private CardView userBookMeeting, userViewMeeting, userCalendar, userNotes, userSettings, userLogOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        setupUIViews();

        userBookMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileUser.this, BookMeeting.class));
            }
        });

        userViewMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileUser.this, ViewMeeting.class));
            }
        });

        userCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileUser.this, CustomCalendarActivity.class));
            }
        });


    }


    private void setupUIViews(){
        userBookMeeting = (CardView)findViewById(R.id.cvBookMeet);
        userViewMeeting = (CardView)findViewById(R.id.cvViewMeet);
        userCalendar = (CardView)findViewById(R.id.cvCalendar);
        userNotes = (CardView)findViewById(R.id.cvNotes);
        userSettings= (CardView)findViewById(R.id.cvSettings);
        userLogOut = (CardView)findViewById(R.id.cvLogOut);

    }
}
