package com.honeydo5.honeydo.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.honeydo5.honeydo.R;
import com.honeydo5.honeydo.util.DateHelper;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddTask extends AppCompatActivity {

    int y, m, d, hr, min;
    static final int date_dialog_id = 0;
    static final int time_dialog_id = 1;

    EditText nameText, descriptionText;

    ImageButton dateBtn, timeBtn;
    Button addBtn;
    EditText timeText, dateText;

    Switch prioritySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        nameText = (EditText) findViewById(R.id.nameText);
        descriptionText = (EditText) findViewById(R.id.descriptionText);

        dateBtn = (ImageButton) findViewById(R.id.date);
        timeBtn = (ImageButton) findViewById(R.id.time);

        addBtn = (Button) findViewById(R.id.add_task_btn);

        timeText = (EditText) findViewById(R.id.timeText);
        dateText = (EditText) findViewById(R.id.dateText);

        prioritySwitch = (Switch) findViewById(R.id.prioritySwitch);

        showDate();
        showTime();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date due = DateHelper.getDate(y, m, d, hr, min);
                Task t = new Task(nameText.getText().toString(), descriptionText.getText().toString(), prioritySwitch.isChecked(), null, due, null);
                sendNewTaskToServer(t);
                TaskSystem.addTask(t);

                onBackPressed();
            }
        });
    }

    // Set click listeners for diaglogs
    public void showDate(){
        dateBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(date_dialog_id);
                    }
                }
        );

        dateText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(date_dialog_id);
                    }
                }
        );
    }

    // Creates dialogs
    @Override
    public Dialog onCreateDialog(int id){
        if(id == 0){
            return new DatePickerDialog(this, dplistener, y, m, d);
        }
        if(id == 1){
            return new TimePickerDialog(this, tplistener, hr, min, false);
        }
        return null;
    }


    private DatePickerDialog.OnDateSetListener dplistener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            dateText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year );
            y = year;
            m = monthOfYear;
            d = dayOfMonth;
        }
    };


    public void showTime(){
        timeBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(time_dialog_id);
                    }
                }
        );

        timeText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(time_dialog_id);
                    }
                }
        );
    }

    private TimePickerDialog.OnTimeSetListener tplistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            timeText.setText(hour + ":" + minute);
            hr = hour;
            min = minute;
        }
    };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(this, MainScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }

    public void sendNewTaskToServer(Task t) {
        HashMap<String, String> postMessage = new HashMap<>(); // assumes <String, String>
        postMessage.put("name", t.getHeader());
        //postMessage.put("description", t.getBody());
        //postMessage.put("date", t.getDue().toString());
        postMessage.put("priority", t.isPriority()+"");

        Log.d("API-LOGIN-POST", postMessage.toString());
        JsonObjectRequest loginReq = new JsonObjectRequest(
                Request.Method.POST, // request method
                AppController.defaultBaseUrl + "/add_task", // target url
                new JSONObject(postMessage), // json object from hashmap
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API-LOGIN-RESPONSE", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API-LOGIN-ERROR", "Something happened in the way of heaven : " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>(); // assumes <String, String> template params
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        try {
            Log.d("API-LOGIN-BODY", new String(loginReq.getBody(), "UTF-8"));
            Log.d("API-LOGIN-BODYCTYPE", loginReq.getBodyContentType());
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

            AppController.getInstance().addToRequestQueue(loginReq, "add_task");
    }
}
