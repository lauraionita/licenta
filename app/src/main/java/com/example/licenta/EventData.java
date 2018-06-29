package com.example.licenta;

public class EventData {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String name;
    public int count;
    public String date;
    
    public EventData(int count) {
        this.count = count;
    }
    public EventData(){}

    
    
}
