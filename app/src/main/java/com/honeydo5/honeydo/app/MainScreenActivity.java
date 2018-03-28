package com.honeydo5.honeydo.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainScreenActivity extends HoneyDoActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    Button buttonSettings, buttonRewards, buttonAddTask;
    FloatingActionButton FAButtonAddTask;
    RecyclerView listViewTasks;
    TaskAdapter adapter;

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTag("MAINSCREEN");

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
        itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(listViewTasks);

        // add divider line to recycleview
        DividerItemDecoration verticalDecor = new DividerItemDecoration(
                listViewTasks.getContext(),
                DividerItemDecoration.VERTICAL
        );
        listViewTasks.addItemDecoration(verticalDecor);

        //TODO talk to Nikos about this, seems like notification services
        //Alarm Manager
        /*Calendar calendarMili = Calendar.getInstance();
        calendarMili.add(Calendar.SECOND, 1);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent("notification");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarMili.getTimeInMillis(), broadcast);*/ //had to raise min SDK

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
        Log.d(tag, "Refreshing view on resume.");

        TaskSystem.clearEditTask();
        adapter.notifyDataSetChanged();
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
                    TaskSystem.clearAll();
                    JSONArray tasks = response.getJSONArray("tasks");
                    for (int i=0; i < tasks.length(); i++) {
                        TaskSystem.addTask(new Task(tasks.getJSONObject(i)));
                    }
                    adapter.notifyDataSetChanged();
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
                        Log.d(tag, "API /" + endpoint + " response is back.");
                        try {
                            Log.d(tag, "API /" + endpoint + " raw response : " + response.toString());

                            /*String status = response.get("status").toString();
                            switch(status)
                            {
                                case "not logged in" :

                                    AppController.getInstance().sessionExpired(MainScreenActivity.this);
                                    break;

                                //TODO: get Mitch to do this
                                //case "success" :
                                default :*/
                                    parseResponseToAdapter(response);
                                    /*break;
                            }*/
                        } catch(JSONException e) {
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
                //TODO: treat this error!
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

    void editTask(int position)
    {
        Intent intent = new Intent(this, EditTaskActivity.class);
        TaskSystem.setEditTask(TaskSystem.getTask(position));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
    }

    // when item is swiped for deletion
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof TaskAdapter.TaskViewHolder) {
            //TODO: this is where I edit or remove tasks, DAVID adapter.removeItem(viewHolder.getAdapterPosition()) removes a task
            if(direction == ItemTouchHelper.LEFT) {
                try {
                    Task task = TaskSystem.getTask(position);
                    JSONObject postMessage = new JSONObject();
                    postMessage.put("task_id", task.getId());
                    requestRemoveTask(position, postMessage);
                } catch (JSONException e) {
                    Log.e(tag, "API /delete_task error composing message: " + e.getMessage());
                    Log.getStackTraceString(e);
                }
            }
            else if(direction == ItemTouchHelper.RIGHT) {
                editTask(position);
                itemTouchHelperCallback.clearView(listViewTasks, viewHolder);
            }
        }
    }


    public void requestRemoveTask(final int position, JSONObject postMessage){
        final String endpoint = "delete_task";
        AppController.getInstance().cancelPendingRequests(tag + ":" + endpoint);

        Log.d(tag, "API /" + endpoint + " Request POST Body : " + postMessage.toString());

        // request object to be added to volley's request queue
        Log.d(tag, "API /" + endpoint + " creating request object.");
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // request method
                AppController.defaultBaseUrl + "/" + endpoint, // target url
                postMessage, // json payload
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(tag, "API /" + endpoint + " raw response : " + response.toString());
                        try {

                            /* Possible endpoint responses

                                {‘status’: ‘success’}
                                ??? // TODO talk to backend!

                             */
                            String status = response.get("status").toString();

                            switch(status)
                            {
                                case "success" :
                                    adapter.removeItem(position);
                                    break;

                                case "not logged in":
                                    AppController.getInstance().sessionExpired(MainScreenActivity.this);
                                    break;

                                case "must specify name, priority" :
                                    Log.e(tag, "API /" + endpoint + " response : invalid data.");
                                    //TODO : show on activity body
                                    break;
                            }
                        } catch(JSONException e) {
                            // TODO: show parsing error on UI
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
                // TODO: show network error on UI
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
}