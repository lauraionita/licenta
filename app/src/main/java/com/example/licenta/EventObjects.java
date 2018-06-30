package com.example.licenta;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventObjects {
   // private int id;
   // private String message;
    private String date;
    private String startMeeting, endMeeting, location;
    public EventObjects(){}


    public String getMessage() {
        return date + " "+ startMeeting;
    }

    public Date getDate() {
        return convertStringToDate(date);
    }

    private Date convertStringToDate(String dateInString){
        DateFormat format = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("d-MM-yyyy");
        Date newDate = null;
        Date date=null;
        try {
            date = format.parse(dateInString);
            String formattedDate = targetFormat.format(date);
            newDate = targetFormat.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }
}