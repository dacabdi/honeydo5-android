package com.honeydo5.honeydo.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Task implements Serializable {

    private String name, description;
    private boolean priority;
    private ArrayList<Tag> tags;
    private Calendar date, reminder;

    private int ID;

    public Task(String name, String description, boolean priority, ArrayList<Tag> tags, Calendar date, Calendar reminder) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.tags = tags;
        this.date = date;
        this.reminder = reminder;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return this.date;
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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
