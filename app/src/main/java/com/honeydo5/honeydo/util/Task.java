package com.honeydo5.honeydo.util;

import com.honeydo5.honeydo.util.Tag;

import java.util.ArrayList;
import java.util.Calendar;

public class Task {

    private String header, body;
    private boolean priority;
    private ArrayList<Tag> tags;
    private Calendar date, reminder;

    public Task(String body, String header, boolean priority, ArrayList<Tag> tags, Calendar date, Calendar reminder) {
        this.body = body;
        this.header = header;
        this.priority = priority;
        this.tags = tags;
        this.date = date;
        this.reminder = reminder;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Calendar getReminder() {
        return reminder;
    }

    public void setReminder(Calendar reminder) {
        this.reminder = reminder;
    }
}
