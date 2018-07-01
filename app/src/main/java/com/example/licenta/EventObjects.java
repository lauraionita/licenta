package com.example.licenta;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventObjects {

    private String date;
    private String startMeeting;
    private String endMeeting;

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartMeeting() {
        return startMeeting;
    }

    public void setStartMeeting(String startMeeting) {
        this.startMeeting = startMeeting;
    }

    public String getEndMeeting() {
        return endMeeting;
    }

    public void setEndMeeting(String endMeeting) {
        this.endMeeting = endMeeting;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
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

    public String getDetails(){
        return this.date+"_"+this.startMeeting+"_"+this.endMeeting;
    }
}