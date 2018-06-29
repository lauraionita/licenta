package com.example.licenta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseObject {
    private static Database dbHelper;
    private SQLiteDatabase db;

    public DatabaseObject(Context context) {
        this.dbHelper = new Database(context);
        //dbHelper.openDataBase();
        //this.dbHelper.getWritableDatabase();
        this.db = dbHelper.getWritableDatabase();
    }
    public SQLiteDatabase getDbConnection(){
        return this.db;
    }
    public void closeDbConnection(){
        if(this.db != null){
            this.db.close();
        }
    }
}