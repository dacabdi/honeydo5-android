package com.honeydo5.honeydo.app;

import java.util.Date;
import java.util.ArrayList;

public class Task {

    private String header, body;
    private boolean priority;
    private ArrayList<Tag> tags;
    private Date due, reminder;

    public Task(String body, String header, boolean priority, ArrayList<Tag> tags, Date due, Date reminder) {
        this.body = body;
        this.header = header;
        this.priority = priority;
        this.tags = tags;
        this.due = due;
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

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }

    public Date getReminder() {
        return reminder;
    }

    public void setReminder(Date reminder) {
        this.reminder = reminder;
    }
}
