package com.honeydo5.honeydo.app;

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
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.honeydo5.honeydo.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainScreen extends AppCompatActivity {

    RecyclerView taskListView;
    TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Button settings = (Button) findViewById(R.id.settings);
        Button rewards = (Button) findViewById(R.id.rewards);
        Button add = (Button) findViewById(R.id.add);

        taskListView = findViewById(R.id.task_list);
        taskListView.setHasFixedSize(true);
        taskListView.setLayoutManager(new LinearLayoutManager(this));

        // dummy tasks
        TaskSystem.addTask(new Task("Test body", "Test 1", true, null, null, null));
        TaskSystem.addTask(new Task("Test body", "Test 2", true, null, null, null));
        TaskSystem.addTask(new Task("Test body", "Test 3", true, null, null, null));
        TaskSystem.addTask(new Task("Test body", "Test 4", true, null, null, null));
        TaskSystem.addTask(new Task("Test body", "Test 5", true, null, null, null));

        adapter = new TaskAdapter(this, TaskSystem.getTaskList());
        taskListView.setAdapter(adapter);

        getTaskList();

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addTaskBtn);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                createNewTask();
            }
        });
    }

    void parseResponseToAdapter(JSONObject response) {
        // iterate addTask() with JSON data

    }

    void getTaskList() {
        JsonObjectRequest taskRequest = new JsonObjectRequest (
                Request.Method.GET, AppController.defaultBaseUrl + "/tasks", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseResponseToAdapter(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
                );

        AppController.getInstance().addToRequestQueue(taskRequest);
    }

    void createNewTask()
    {
        Intent intent = new Intent(this, AddTask.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }

}
