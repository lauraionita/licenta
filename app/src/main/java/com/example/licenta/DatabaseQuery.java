package com.example.licenta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.licenta.Database.COL_1;
import static com.example.licenta.Database.COL_2;
import static com.example.licenta.Database.COL_3;
import static com.example.licenta.Database.TABLE_NAME;

public class DatabaseQuery extends DatabaseObject{
    private static final String TAG = Database.class.getSimpleName();
    public DatabaseQuery(Context context) {
        super(context);
    }
    public List<EventObjects> getAllFutureEvents(){
        Date dateToday = new Date();
        List<EventObjects> events = new ArrayList<>();
        //String query = "select * from reminder";
        String query = "select * from reminder";

        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("int"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date"));
                //convert start date to date object
                Date reminderDate = convertStringToDate(startDate);
//                if(reminderDate.after(dateToday) || reminderDate.equals(dateToday)){
                events.add(new EventObjects(id, message, reminderDate));
                //}
            }while (cursor.moveToNext());
        }
        //cursor.close();
        //this.closeDbConnection();
        return events;

    }

    public boolean insertData(String start_date,String message,int i) {
        SQLiteDatabase db = this.getDbConnection();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,start_date);
        contentValues.put(COL_2,message);
        contentValues.put(COL_3, i);
        long result = db.insert(TABLE_NAME, null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
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
}