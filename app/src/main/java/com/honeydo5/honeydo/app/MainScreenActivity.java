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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.honeydo5.honeydo.R;

import org.json.JSONObject;

public class MainScreenActivity extends AppCompatActivity {

    RecyclerView taskListView;
    TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Button settings = (Button) findViewById(R.id.MainScreenButtonSettings);
        Button rewards = (Button) findViewById(R.id.MainScreenButtonRewards);
        Button add = (Button) findViewById(R.id.add);

        taskListView = findViewById(R.id.MainScreenRecyclerTaskList);
        taskListView.setHasFixedSize(true);
        taskListView.setLayoutManager(new LinearLayoutManager(this));

        getTaskList();

        adapter = new TaskAdapter(this, TaskSystem.getTaskList());
        taskListView.setAdapter(adapter);
        //taskListView.scrollTo(0,2);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.MainScreenButtonAddTask);
        fab.setOnClickListener(new View.OnClickListener() {
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

    void parseResponseToAdapter(JSONObject response) {
        // iterate addTask() with JSON data

    }

    void getTaskList() {
        JsonObjectRequest taskRequest = new JsonObjectRequest (
                Request.Method.GET, AppController.defaultBaseUrl + "/get_tasks", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseResponseToAdapter(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API-LOGIN-ERROR", "Something happened in the way of heaven : " + error.getMessage());
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(taskRequest);
    }

    void createNewTask()
    {
        Intent intent = new Intent(this, AddTaskActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
    }

}