package com.honeydo5.honeydo.util;

import com.honeydo5.honeydo.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;


public class Task  {
    private String name, description;
    private boolean priority;
    private ArrayList<Tag> tags;
    private Calendar dateAndTime;
    private int id;

    private Task(int id, String name, String description, boolean priority) {
        setId(id);
        setName(name);
        setDescription(description);
        setPriority(priority);
    }

    public Task(int id, String name, String description, boolean priority, ArrayList<Tag> tags, Calendar dateAndTime) throws JSONException {
        this(id, name, description, priority);
        setTags(tags);
        setDateAndTime(dateAndTime);
    }

    public Task(int id, String name, String description, boolean priority, JSONArray tags, String dateAndTime) throws JSONException {
        this(id, name, description, priority);
        setTags(tags);
        setDateAndTime(dateAndTime);
    }

    public Task(JSONObject json) throws JSONException {
        this(   json.getInt("task_id"),
                json.getString("name"),
                json.getString("description"),
                json.getBoolean("priority"),
                json.getJSONArray("tags"),
                json.getString("due_date") + " " + json.getString("due_time")  );
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

    public JSONArray getTagsJSON(){
        return new JSONArray(this.tags);
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public void setTags(JSONArray tags) throws JSONException {
        this.tags = extractTags(tags);
    }

    public Calendar getDateAndTime() {
        return this.dateAndTime;
    }

    public void setDateAndTime(Calendar date) {
        this.dateAndTime = date;
    }

    public void setDateAndTime(String date) {
        this.dateAndTime = AppController.getInstance().parseDateTimeString(date);
    }

    public int getId() {
        return id;
    }

    public void setId(int ID) {
        this.id = ID;
    }

    private ArrayList<Tag> extractTags(JSONArray jsonTags) throws JSONException
    {
        ArrayList<Tag> tags = new ArrayList<>();
        for(int i = 0; i < jsonTags.length(); i++)
            tags.add(new Tag(jsonTags.getString(i)));

        return tags;
    }
}
