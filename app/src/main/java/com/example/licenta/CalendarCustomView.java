package com.example.licenta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.example.licenta.MainActivity.domain;

public class CalendarCustomView extends LinearLayout{
    private static final String TAG = CalendarCustomView.class.getSimpleName();
    private ImageView previousButton, nextButton;
    private TextView currentDate;
    private GridView calendarGridView;
    private Button addEventButton;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private int month, year;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private Context context;
    private GridAdapter mAdapter;
    private EventAdapter mEventsAdapter;
    private int i;
    private List<EventObjects> mEvents = new ArrayList<EventObjects>();
    private List<EventObjects> mEventsDay = new ArrayList<EventObjects>();
    Context ctx ;
    private RecyclerView recyclerView;


    private Date convertStringToDate(String dateInString){
        DateFormat format = new SimpleDateFormat("d-MM-yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public CalendarCustomView(Context context) {
        super(context);
    }
    public CalendarCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeUILayout();
        setUpCalendarAdapter();
        setPreviousButtonClickEvent();
        setNextButtonClickEvent();
       // setGridCellClickEvents();
        Log.d(TAG, "I need to call this method");
    }
    public CalendarCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void initializeUILayout(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        previousButton = (ImageView)view.findViewById(R.id.previous_month);
        nextButton = (ImageView)view.findViewById(R.id.next_month);
        currentDate = (TextView)view.findViewById(R.id.display_current_date);
        addEventButton = (Button)view.findViewById(R.id.add_calendar_event);
        calendarGridView = (GridView)view.findViewById(R.id.calendar_grid);

        String idUser = getIdUser();
        domain = domain.substring(0, 1).toUpperCase() + domain.substring(1).toLowerCase();

    }
    private void setPreviousButtonClickEvent(){
        previousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, -1);
                setUpCalendarAdapter();
            }
        });
    }
    private void setNextButtonClickEvent(){
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, 1);
                setUpCalendarAdapter();
            }
        });
    }
//    private void setGridCellClickEvents(){
//        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                for(i = 0; i < mEvents.size(); i++) {
//                    Toast.makeText(context, "Clicked " + mEvents.get(i).getMessage(), Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }
    private void setUpCalendarAdapter(){
       final List<Date> dayValueInCells = new ArrayList<Date>();
//        mQuery = new DatabaseQuery(context);
//        final List<EventObjects> mEvents = mQuery.getAllFutureEvents();
//        mQuery.insertData("14-08-2018", "lalal", 7);
       // final List<EventObjects> mEvents = new ArrayList<>();
        Calendar mCal = (Calendar)cal.clone();

        String domain = getDomainCompany();
        domain = domain.substring(0,1).toUpperCase() + domain.substring(1).toLowerCase();
        String idUser = getIdUser();
        //requestEventsDetails(idUser);
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 1;
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
        while(dayValueInCells.size() < MAX_CALENDAR_COLUMN){
            dayValueInCells.add(mCal.getTime());
            mCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        Log.d(TAG, "Number of date " + dayValueInCells.size());
        String sDate = formatter.format(cal.getTime());
        currentDate.setText(sDate);
        mAdapter = new GridAdapter(context, dayValueInCells, cal, mEvents);
        calendarGridView.setAdapter(mAdapter);

        Query query = FirebaseDatabase.getInstance().getReference("Events" + domain).child(idUser);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEvents.clear();
                if (dataSnapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        EventObjects events = snapshot.getValue(EventObjects.class);
                        //String idUser = snapshot.getKey();
                        mEvents.add(events);
                        i = i+1;

                    }
                      mAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(context, "No events availables", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(mCtx, ProfileUser.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEventsDay = new ArrayList<>();

                Calendar dateCal = Calendar.getInstance();
                dateCal.setTime(dayValueInCells.get(position));
                int dayValue = dateCal.get(Calendar.DAY_OF_MONTH);
                int displayMonth = dateCal.get(Calendar.MONTH) + 1;
                int displayYear = dateCal.get(Calendar.YEAR);
                int currentMonth = cal.get(Calendar.MONTH) + 1;
                int currentYear = cal.get(Calendar.YEAR);
                Calendar eventCalendar = Calendar.getInstance();

                for(i = 0; i < mEvents.size(); i++) {
                    eventCalendar.setTime(mEvents.get(i).getDate());
                    if (dayValue == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH) + 1
                            && displayYear == eventCalendar.get(Calendar.YEAR)) {

                        Toast.makeText(context, "Clicked " + mEvents.get(i), Toast.LENGTH_LONG).show();
                        mEventsDay.add(mEvents.get(i));

                        SharedPreferences.Editor editor = context.getSharedPreferences("CalendarCustomView", MODE_PRIVATE).edit();
                        String idUnique = Integer.toString(i);
                        editor.putString(idUnique, mEvents.get(i).getDetails());
                        editor.apply();
                    }
                   // context.startActivity(new Intent(context, EventDetails.class));
                }
               // mEventsAdapter.notifyDataSetChanged();
            }
        });
    }
    private String getDomainCompany ()
    {
        SharedPreferences prefs = context.getSharedPreferences("MainActivity", MODE_PRIVATE);
        String domain = prefs.getString("domain", "No name defined");
        return domain;
    }

    private String getIdUser ()
    {
        SharedPreferences prefs = context.getSharedPreferences("MainActivity", MODE_PRIVATE);
        String idUser = prefs.getString("idUser", "No name defined");
        return idUser;
    }


}