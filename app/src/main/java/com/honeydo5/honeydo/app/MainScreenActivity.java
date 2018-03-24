package com.honeydo5.honeydo.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.honeydo5.honeydo.R;
import com.honeydo5.honeydo.util.TaskAdapter;
import com.honeydo5.honeydo.util.TaskSystem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainScreenActivity extends AppCompatActivity {
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

        Log.d(tag, "Calling /get_tasks to refresh the view.");
        getTaskList();

        adapter = new TaskAdapter(this, TaskSystem.getTaskList());
        listViewTasks.setAdapter(adapter);

        FAButtonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                createNewTask();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }


    void parseResponseToAdapter(JSONObject response) throws JSONException {
        // iterate addTask() with JSON data
    }


    void getTaskList() {
        final String endpoint = "get_tasks";
        AppController.getInstance().cancelPendingRequests(tag + ":" + endpoint);
        JSONObject postMessage = null;

        Log.d(tag, "API /" + endpoint + " Request POST Body : [empty]");

        // request object to be added to volley's request queue
        Log.d(tag, "API /" + endpoint + " creating request object.");
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, // request method
                AppController.defaultBaseUrl + "/" + endpoint, // target url
                postMessage, // json object from hashmap
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
}