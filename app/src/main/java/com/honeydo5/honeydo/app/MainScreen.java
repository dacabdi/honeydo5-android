package com.honeydo5.honeydo.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.honeydo5.honeydo.R;

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

        TaskSystem.addTask(new Task("Test body", "Test 1", true, null, null, null));
        TaskSystem.addTask(new Task("Test body", "Test 2", true, null, null, null));
        TaskSystem.addTask(new Task("Test body", "Test 3", true, null, null, null));
        TaskSystem.addTask(new Task("Test body", "Test 4", true, null, null, null));
        TaskSystem.addTask(new Task("Test body", "Test 5", true, null, null, null));

        adapter = new TaskAdapter(this, TaskSystem.getTaskList());
        taskListView.setAdapter(adapter);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addTaskBtn);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                addTask();
            }
        });
    }

    void addTask()
    {
        Intent intent = new Intent(this, AddTask.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }

}
