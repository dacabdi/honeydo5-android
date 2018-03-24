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

    Calendar calendarDate;
    //int y, m, d, hr, min;
    static final int date_dialog_id = 0;
    static final int time_dialog_id = 1;

    ArrayAdapter<CharSequence> adapter;

    //fields
    EditText inputName, inputDescription, inputDate, inputTime;
    Switch switchPriority;
    Spinner inputTag;
    //labels
    TextView labelName, labelDescription, labelTag;
    ImageButton imageButtonDate, imageButtonTime;

    Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "Setting AddTaskActivity content view.");
        setContentView(R.layout.activity_add_task);

        // get components -----------------------------------------

        Log.d(tag, "Finding components and views.");
        //fields
        inputName = findViewById(R.id.addTaskEditViewName);
        inputDescription = findViewById(R.id.addTaskMultilineTextDescription);
        inputTag = findViewById(R.id.addTaskSpinnerTags);
        inputDate = findViewById(R.id.addTaskEditTextDateText);
        inputTime = findViewById(R.id.addTaskEditTextTimeText);
        //labels (used for invalid input highlighting)
        labelName = findViewById(R.id.addTaskTextViewNameLabel);
        labelDescription = findViewById(R.id.addTaskTextViewDiscLabel);
        labelTag = findViewById(R.id.addTaskTextViewTagsLabel);
        imageButtonDate = findViewById(R.id.addTaskDatePickerDate);
        imageButtonTime = findViewById(R.id.addTaskTimePickerTime);
        //buttons
        buttonAdd = findViewById(R.id.addTaskButtonAdd);


        // set components -----------------------------------------

        //get date
        calendarDate = Calendar.getInstance();
        calendarDate.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY + 1));

        //init date and time fields using calendarDate
        inputTime.setText(android.text.format.DateFormat.format("hh:mm a", calendarDate));
        inputDate.setText(android.text.format.DateFormat.format("MM/dd/yyyy", calendarDate));

        //put tags on spinner
        adapter = ArrayAdapter.createFromResource(this, R.array.tags, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputTag.setAdapter(adapter);

        // set event handlers components ---------------------------

        //date and time
        imageButtonDate.setOnClickListener(
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
        imageButtonTime.setOnClickListener(
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

        //tags spinner
        inputTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getBaseContext(), adapterView.getItemAtPosition(i) + " selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //add task button
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject json = getFieldsData();
                if(json != null) {
                    Log.d(tag, "Sending new task to server");
                    submitNewTask(json);
                }
            }
        });
    }



    @Nullable
    private JSONObject getFieldsData(Task t){
        JSONObject json = new JSONObject();

        Log.d(tag, "Gathering input data.");

        try{

            /*
                 name,        (string)
                 description, (string)
                 tag,         (string)
                 priority,    (bool string)
                 date,        (mm/dd/yyyy string)
                 time         (hh:mm am|pm string)
            */

            //validate input

            String name = inputName.getText().toString(),
                   task_tag = inputTag.getSelectedItem().toString(),
                   priority = Boolean.toString(switchPriority.isChecked()),
                   date = inputDate.getText().toString(),
                   time = inputDate.getText().toString();

            Log.d(tag, "Validating add task entries.");

            if(InputValidation.checkIfEmpty(name)){
                Log.d(tag, "Name field is invalid : " + name);
                labelName.setTextColor(getResources().getColor(R.color.textColor));
                return null;
            } else {
                labelName.setTextColor(getResources().getColor(R.color.errorColor));
            }

            if(!InputValidation.checkIfEmpty(task_tag)) {
                Log.d(tag, "Tag field is invalid : " + task_tag);
                imageButtonTime.setColorFilter(getResources().getColor(R.color.colorError));
                return null;
            } else {
                imageButtonTime.setColorFilter(getResources().getColor(R.color.colorIcon));
            }

            if(!InputValidation.checkIfEmpty(task_tag)) {
                Log.d(tag, "Tag field is invalid : " + task_tag);
                imageButtonTime.setColorFilter(getResources().getColor(R.color.colorError));
                return null;
            } else {
                imageButtonTime.setColorFilter(getResources().getColor(R.color.colorIcon));
            }

            if(!InputValidation.validateDate(date)) {
                Log.d(tag, "Date field is invalid : " + date);
                imageButtonDate.setColorFilter(getResources().getColor(R.color.colorError));
                return null;
            } else {
                imageButtonDate.setColorFilter(getResources().getColor(R.color.colorIcon));
            }

            if(!InputValidation.validateTime(time)) {
                Log.d(tag, "Time field is invalid : " + time);
                imageButtonTime.setColorFilter(getResources().getColor(R.color.colorError));
                return null;
            } else {
                imageButtonTime.setColorFilter(getResources().getColor(R.color.colorIcon));
            }

            Log.d(tag, "Conforming JSON payload.");

            // make json payload for the request
            json.put("name", inputName.getText().toString());
            json.put("description", inputDescription.getText().toString());
            json.put("tag", inputTag.getSelectedItem().toString());
            json.put("priority", Boolean.toString(switchPriority.isChecked()));
            json.put("date", inputDate.getText().toString());
            json.put("time", inputDate.getText().toString());
        } catch (JSONException e){
            Log.e(tag, e.getMessage());
            Log.e(tag, Log.getStackTraceString(e));
            return null;
        }

        return json;
    }

    // date/time dialogs
    @Override
    public Dialog onCreateDialog(int id){

        if(id == date_dialog_id){
            // set to current day
            return new DatePickerDialog(
                    this, datePickerHandler,
                    calendarDate.get(Calendar.YEAR),
                    calendarDate.get(Calendar.MONTH),
                    calendarDate.get(Calendar.DAY_OF_MONTH));

        }

        if(id == time_dialog_id){
            // set to one hour from "now"
            return new TimePickerDialog(
                    this, timePickerHandler,
                    calendarDate.get(Calendar.HOUR_OF_DAY)+ 1,
                    0,
                    false);
        }

        return null;
    }


    private DatePickerDialog.OnDateSetListener datePickerHandler = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            Log.d(tag, "Set date to: "
                    +       calendarDate.get(Calendar.MONTH)
                    + "/" + calendarDate.get(Calendar.DAY_OF_MONTH)
                    + "/" + calendarDate.get(Calendar.YEAR));
            calendarDate.set(year, monthOfYear, dayOfMonth);
            inputDate.setText(android.text.format.DateFormat.format("MM/dd/yyyy", calendarDate));
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerHandler = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            Log.d(tag, "Set time to: "
                    +       calendarDate.get(Calendar.HOUR_OF_DAY)
                    + ":" + calendarDate.get(Calendar.MINUTE));
            calendarDate.set(Calendar.HOUR, hour);
            calendarDate.set(Calendar.MINUTE, minute);
            inputTime.setText(android.text.format.DateFormat.format("hh:mm a", calendarDate));
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }

    public void submitNewTask(JSONObject postMessage) {
        final String endpoint = "add_task";
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
                            // TODO: response treatment
                            onBackPressed();
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
