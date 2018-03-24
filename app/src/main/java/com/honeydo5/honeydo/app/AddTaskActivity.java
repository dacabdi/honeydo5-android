package com.honeydo5.honeydo.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.honeydo5.honeydo.R;
import com.honeydo5.honeydo.util.InputValidation;
import com.honeydo5.honeydo.util.Task;
import com.honeydo5.honeydo.util.TaskSystem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {
    private String tag = "ADDTASK";

    Calendar date;
    //int y, m, d, hr, min;
    static final int date_dialog_id = 0;
    static final int time_dialog_id = 1;

    EditText inputName, inputDescription;
    TextView textName, textDescription;

    ImageButton buttonDate, buttonTime;
    Button buttonAdd;
    EditText inputTime, inputDate;
    Spinner spinnerTag;
    ArrayAdapter<CharSequence> adapter;

    Switch switchPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        inputName = findViewById(R.id.addTaskEditViewName);
        inputDescription = findViewById(R.id.addTaskMultiLineTaskDesc);

        textName = findViewById(R.id.addTaskTextViewNameLabel);
        textDescription = findViewById(R.id.addTaskTextViewDiscLabel);

        buttonDate = findViewById(R.id.addTaskDatePickerDate);
        buttonTime = findViewById(R.id.addTaskTimePickerTime);

        buttonAdd = findViewById(R.id.addTaskButtonAdd);


        date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY + 1));

        inputTime = findViewById(R.id.addTaskEditTextTimeText);
        inputDate = findViewById(R.id.addTaskEditTextDateText);

        inputTime.setText(android.text.format.DateFormat.format("hh:mm a", date));
        inputDate.setText(android.text.format.DateFormat.format("MM/dd/yyyy", date));

        switchPriority = (Switch) findViewById(R.id.addTaskSwitchPriority);

        showDate();
        showTime();

        // tag spinner
        spinnerTag = (Spinner) findViewById(R.id.addTaskSpinnerTags);
        adapter = ArrayAdapter.createFromResource(this, R.array.tags, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTag.setAdapter(adapter);
        spinnerTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getBaseContext(), adapterView.getItemAtPosition(i) + " selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // add button
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task t = new Task(inputDescription.getText().toString(), inputName.getText().toString(), switchPriority.isChecked(), null, date, null);
                JSONObject json = getFieldsData(t);
                if(!json.equals(null)) {
                    sendNewTaskToServer(t);
                    TaskSystem.addTask(t);

                    Log.d(tag, "Sent task to server and local task system");

                    onBackPressed();
                }
            }
        });
    }

    @Nullable
    private JSONObject getFieldsData(Task t){
        JSONObject json = new JSONObject();

        Log.d(tag, "Gathering input data.");

        try{
            // check if required fields are filled
            if(InputValidation.checkIfEmpty(inputName.getText().toString())){
                Log.d(tag, "Name field is blank");
                textName.setTextColor(getResources().getColor(R.color.textColor));
                return null;
            }
            else {
                textName.setTextColor(getResources().getColor(R.color.textColor));
            }

            if(!InputValidation.validateTime(inputTime.getText().toString())) {
                Log.d(tag, "Time field empty");
                buttonTime.setColorFilter(getResources().getColor(R.color.colorError));
                return null;
            }
            else {
                buttonTime.setColorFilter(getResources().getColor(R.color.colorIcon));
            }

            if(!InputValidation.validateDate(inputDate.getText().toString())) {
                Log.d(tag, "Date field empty");
                buttonDate.setColorFilter(getResources().getColor(R.color.colorError));
                return null;
            }
            else {
                buttonDate.setColorFilter(getResources().getColor(R.color.colorIcon));
            }

            json.put("name", t.getHeader());
            json.put("description", t.getBody());
            json.put("date", t.getDate().toString());
            json.put("priority", t.isPriority()+"");
        } catch (JSONException e){
            Log.e(tag, e.getMessage());
            Log.e(tag, Log.getStackTraceString(e));
            return null;
        }

        return json;
    }

    // Set click listeners for diaglogs
    public void showDate(){
        buttonDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(date_dialog_id);
                    }
                }
        );

        inputDate.setOnClickListener(
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
            return new DatePickerDialog(this, dplistener, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
            //Current day
        }
        if(id == 1){
            return new TimePickerDialog(this, tplistener, date.get(Calendar.HOUR_OF_DAY)+ 1, 0, false);
            //One hour from "now"
        }
        return null;
    }


    private DatePickerDialog.OnDateSetListener dplistener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            Log.d(tag, "Set date to: " + date.get(Calendar.MONTH) + "/" + date.get(Calendar.DAY_OF_MONTH) + "/" + date.get(Calendar.YEAR));
            date.set(year, monthOfYear, dayOfMonth);
            inputDate.setText(android.text.format.DateFormat.format("MM/dd/yyyy", date));
        }
    };


    public void showTime(){
        buttonTime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(time_dialog_id);
                    }
                }
        );

        inputTime.setOnClickListener(
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
            Log.d(tag, "Set time to: " + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE) + "");
            date.set(Calendar.HOUR, hour);
            date.set(Calendar.MINUTE, minute);
            inputTime.setText(android.text.format.DateFormat.format("hh:mm a", date));
        }
    };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(this, MainScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }

    public void sendNewTaskToServer(Task t) {
        HashMap<String, String> postMessage = new HashMap<>(); // assumes <String, String>

        Log.d("API-LOGIN-POST", postMessage.toString());
        JsonObjectRequest loginReq = new JsonObjectRequest(
                Request.Method.POST, // request method
                AppController.defaultBaseUrl + "/add_task", // target url
                new JSONObject(postMessage), // json object from hashmap
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API-LOGIN-RESPONSE", response.toString());
                        Log.d(tag, "Added task to server");
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
