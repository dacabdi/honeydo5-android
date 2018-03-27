package com.honeydo5.honeydo.app;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.honeydo5.honeydo.R;
import com.honeydo5.honeydo.util.RecyclerItemTouchHelper;
import com.honeydo5.honeydo.util.Task;
import com.honeydo5.honeydo.util.TaskAdapter;
import com.honeydo5.honeydo.util.TaskSystem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainScreenActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    String tag = "MAINSCREEN";

    Button buttonSettings, buttonRewards, buttonAddTask;
    FloatingActionButton FAButtonAddTask;
    RecyclerView listViewTasks;
    TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(tag, "Setting MainScreenActivity activity content view.");
        setContentView(R.layout.activity_main_screen);

        // grab in order top to bottom of page
        Log.d(tag, "Finding components and views.");
        buttonSettings = findViewById(R.id.MainScreenButtonSettings);
        buttonRewards = findViewById(R.id.MainScreenButtonRewards);
        FAButtonAddTask = findViewById(R.id.MainScreenButtonAddTask);
        listViewTasks = findViewById(R.id.MainScreenRecyclerTaskList);

        Log.d(tag, "Setting listViewTasks formatting and layout.");
        listViewTasks.setHasFixedSize(true);
        listViewTasks.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(this, TaskSystem.getTaskList());
        listViewTasks.setAdapter(adapter);

        // Add touch helper
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(listViewTasks);

        // add divider line to recycleview
        DividerItemDecoration verticalDecor = new DividerItemDecoration(
                listViewTasks.getContext(),
                DividerItemDecoration.VERTICAL
        );
        listViewTasks.addItemDecoration(verticalDecor);

        //Alarm Manager
        Calendar calendarMili = Calendar.getInstance();
        calendarMili.add(Calendar.SECOND, 1);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent("notification");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarMili.getTimeInMillis(), broadcast); //had to raise min SDK

        FAButtonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            createNewTask();
            }
        });
    }


    @Override
    protected void onResume(){
        super.onResume();
        Log.d(tag, "Calling /get_tasks to refresh the view.");
        getTaskList();
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(tag, "Calling /get_tasks to refresh the view.");
        getTaskList();
    }


    void parseResponseToAdapter(final JSONObject response) throws JSONException {
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                try {
                    // clear the list first
                    // adapter.clearAll();

                    JSONArray tasks = response.getJSONArray("tasks");
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject task = tasks.getJSONObject(i);
                        ArrayList<com.honeydo5.honeydo.util.Tag> tags = new ArrayList<>();

                        try{
                            JSONArray jsonTags = task.getJSONArray("tags");

                            for(int j = 0; i < jsonTags.length(); j++)
                                tags.add(new com.honeydo5.honeydo.util.Tag(jsonTags.getString(j)));
                        }catch (JSONException e){
                            // log and do a stack trace
                            Log.e(tag, "Error parsing JSON:" + e.getMessage());
                            Log.getStackTraceString(e);
                        }

                        TaskSystem.addTask(new Task(
                                task.getString("name"),                 //name
                                task.getString("description"),          //description
                                task.getBoolean("priority"),            //priority
                                tags, // TODO: can we use JSONArray?
                                AppController.getInstance().parseDateTimeString(
                                        task.getString("due_date") + task.getString("due_time")),
                                null //TODO: @Aaron, what is reminder?!
                        ));

                        adapter.notifyDataSetChanged();
                    }
                } catch(JSONException e) {
                    // log and do a stack trace
                    Log.e(tag, "Error parsing JSON:" + e.getMessage());
                    Log.getStackTraceString(e);
                }
            }
        });


    }

    void getTaskList() {
        final String endpoint = "get_tasks";
        AppController.getInstance().cancelPendingRequests(tag + ":" + endpoint);

        Log.d(tag, "API /" + endpoint + " Request POST Body : [empty]");

        // request object to be added to volley's request queue
        Log.d(tag, "API /" + endpoint + " creating request object.");
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, // request method
                AppController.defaultBaseUrl + "/" + endpoint, // target url
                null, // json object from hashmap
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(tag, "API /" + endpoint + " raw response : " + response.toString());
                        try {
                            // TODO: test local storage
                            parseResponseToAdapter(response);
                            AppController.getInstance().writeLocalFile("tasks.json", response.toString(4));
                        } catch(JSONException e) {
                            // TODO: parsing data error, try local
                            // log and do a stack trace
                            Log.e(tag, "API /" + endpoint + " error parsing response: " + e.getMessage());
                            Log.getStackTraceString(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // log the error
                AppController.getInstance().requestNetworkError(error, tag, "/" + endpoint);

                Log.d(tag, "Reading from local storage tasks.json");
                String localData = AppController.getInstance().readLocalFile("tasks.json");

                try {
                    parseResponseToAdapter(new JSONObject(localData));
                } catch(JSONException e) {
                    // log and do a stack trace
                    Log.e(tag, "Error parsing local file:" + e.getMessage());
                    Log.getStackTraceString(e);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>(); // assumes <String, String> template params
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        Log.d(tag, "API /" + endpoint + " adding request object to request queue.");
        AppController.getInstance().addToRequestQueue(request, tag + ":" + endpoint);
    }

    void createNewTask()
    {
        Intent intent = new Intent(this, AddTaskActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
    }

    // when item is swiped for deletion
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof TaskAdapter.TaskViewHolder) {
            adapter.removeItem(viewHolder.getAdapterPosition());
        }
    }
}