package com.example.licenta;

import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GridAdapter extends ArrayAdapter {
    private static final String TAG = GridAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private List<Date> monthlyDates;
    private Calendar currentDate;
    private List<EventObjects> allEvents;
    private int i;
    private Context ctx;

    public GridAdapter(Context context, List<Date> monthlyDates, Calendar currentDate, List<EventObjects> allEvents) {
        super(context, R.layout.recycler_grid_adapter);
        this.monthlyDates = monthlyDates;
        this.currentDate = currentDate;
        this.allEvents = allEvents;
        mInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Date mDate = monthlyDates.get(position);
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(mDate);
        int dayValue = dateCal.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCal.get(Calendar.MONTH) + 1;
        int displayYear = dateCal.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);
        View view = convertView;
        if(view == null){
            view = mInflater.inflate(R.layout.recycler_grid_adapter, parent, false);
        }
        if(displayMonth == currentMonth && displayYear == currentYear){
            view.setBackgroundColor(Color.parseColor("#FFE4C4"));
        }else{
            view.setBackgroundColor(Color.parseColor("#FF7F50"));
        }
        //Add day to calendar
        TextView cellNumber = (TextView)view.findViewById(R.id.calendar_date_id);
       // final TextView cell = (TextView)view.findViewById(R.id.calendar_date);
        cellNumber.setText(String.valueOf(dayValue));
        //Add events to the calendar
        final TextView eventIndicator = (TextView)view.findViewById(R.id.event_id);
        Calendar eventCalendar = Calendar.getInstance();

        for(i = 0; i < allEvents.size(); i++) {
            eventCalendar.setTime(allEvents.get(i).getDate());
            if (dayValue == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH) + 1
                    && displayYear == eventCalendar.get(Calendar.YEAR)) {
                eventIndicator.setBackgroundColor(Color.parseColor("#000000"));//toate evenimentele din baza de date
               // cellNumber.setText(allEvents.get(i).getMessage());

//                if (cellNumber.getText().toString().equals(String.valueOf(eventCalendar.get(Calendar.DAY_OF_MONTH)))) {
//                    cellNumber.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Toast.makeText(ctx, "Clicked " + allEvents.get(i).getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
                }
            }
       // }

        return view;
    }
    @Override
    public int getCount() {
        return monthlyDates.size();
    }
    @Nullable
    @Override
    public Object getItem(int position) {
        return monthlyDates.get(position);
    }
    @Override
    public int getPosition(Object item) {
        return allEvents.indexOf(item);
    }
}
