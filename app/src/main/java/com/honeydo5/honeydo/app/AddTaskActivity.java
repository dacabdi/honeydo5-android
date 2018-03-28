package com.honeydo5.honeydo.app;

import android.app.DatePickerDialog;
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
import com.honeydo5.honeydo.util.Tag;
import com.honeydo5.honeydo.util.Task;
import com.honeydo5.honeydo.util.TaskSystem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends HoneyDoActivity {

    private Calendar calendarDate;

    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;

    private TextView textMessage;

    private ArrayAdapter<CharSequence> adapter;

    //fields
    private EditText inputName, inputDescription, inputDate, inputTime;
    private Switch inputPriority;
    private Spinner inputTag;

    //labels
    private TextView labelName, labelDescription, labelTag;
    private ImageButton imageButtonDate, imageButtonTime;

    Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTag("ADDTASK");

        Log.d(tag, "Setting AddTaskActivity content view.");
        setContentView(R.layout.activity_add_task);
      
        // get components -----------------------------------------

        Log.d(tag, "Finding components and views.");
        //fields
        inputName = findViewById(R.id.addTaskEditViewName);
        inputDescription = findViewById(R.id.addTaskMultiLineTaskDesc);
        inputPriority = findViewById(R.id.addTaskSwitchPriority);
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
        // error messaging
        textMessage = findViewById(R.id.addTaskTextViewMessage);


        // set components -----------------------------------------

        //get date
        calendarDate = Calendar.getInstance();
        calendarDate.set(Calendar.HOUR_OF_DAY, calendarDate.get(Calendar.HOUR_OF_DAY + 1));

        //init date and time fields using calendarDate
        inputTime.setText(android.text.format.DateFormat.format("hh:mm a", calendarDate));
        inputDate.setText(android.text.format.DateFormat.format("MM/dd/yyyy", calendarDate));

        //put tags on spinner
        adapter = ArrayAdapter.createFromResource(this, R.array.tags, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputTag.setAdapter(adapter);


        // create dialogs (date|time pickers) ----------------------

        datePicker = new DatePickerDialog(
                this,
                datePickerHandler,
                calendarDate.get(Calendar.YEAR),
                calendarDate.get(Calendar.MONTH),
                calendarDate.get(Calendar.DAY_OF_MONTH));

        timePicker = new TimePickerDialog(
                this,
                timePickerHandler,
                calendarDate.get(Calendar.HOUR_OF_DAY)+ 1,
                0, false);


        // set event handlers components ---------------------------

        //date and time
        imageButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                datePicker.show();
            }
        });
        inputDate.setShowSoftInputOnFocus(false);
        inputDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    datePicker.show();
                } else {
                    datePicker.hide();
                }
            }
        });
        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.show();
            }
        });
        imageButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.show();
            }
        });

        inputTime.setShowSoftInputOnFocus(false);
        inputTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    timePicker.show();
                } else {
                    timePicker.hide();
                }
            }
        });
        inputTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.show();
            }
        });

        //tags spinner
        inputTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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
    private JSONObject getFieldsData(){
        JSONObject json = new JSONObject();

        Log.d(tag, "Gathering input data.");

        try{

            /*
                 name,        (string)
                 description, (string)
                 tag,         (array of strings)
                 priority,    (bool string)
                 date,        (mm/dd/yyyy string)
                 time         (hh:mm am|pm string)
            */

            //validate input

            boolean valid = true;
            String errorMessage = "";

            String name = inputName.getText().toString(),
                   description = inputDescription.getText().toString(),
                   task_tag = inputTag.getSelectedItem().toString(),
                   priority = Boolean.toString(inputPriority.isChecked()),
                   date = inputDate.getText().toString(),
                   time = inputTime.getText().toString();

            Log.d(tag, "Validating add task entries.");

            if(InputValidation.checkIfEmpty(name)){
                Log.d(tag, "Name field is invalid : " + name);
                errorMessage += getString(R.string.message_invalid_name) + " ";
                labelName.setTextColor(getResources().getColor(R.color.colorError));
                valid = false;
            } else {
                labelName.setTextColor(getResources().getColor(R.color.colorText));
            }

            if(InputValidation.checkIfEmpty(task_tag)) {
                Log.d(tag, "Tag field is invalid : " + task_tag);
                imageButtonTime.setColorFilter(getResources().getColor(R.color.colorError));
                valid = false;
            } else {
                imageButtonTime.setColorFilter(getResources().getColor(R.color.colorText));
            }

            if(!InputValidation.validateDate(date)) {
                Log.d(tag, "Date field is invalid : " + date);
                errorMessage += getString(R.string.message_invalid_date) + " ";
                imageButtonDate.setColorFilter(getResources().getColor(R.color.colorError));
                valid = false;
            } else {
                imageButtonDate.setColorFilter(getResources().getColor(R.color.colorIcon));
            }

            if(!InputValidation.validateTime(time)) {
                Log.d(tag, "Time field is invalid : " + time);
                errorMessage += getString(R.string.message_invalid_time) + " ";
                imageButtonTime.setColorFilter(getResources().getColor(R.color.colorError));
                valid = false;
            } else {
                imageButtonTime.setColorFilter(getResources().getColor(R.color.colorIcon));
            }

            if(!valid) {
                textMessage.setText(errorMessage);
                textMessage.setVisibility(View.VISIBLE);
                return null;
            }else{
                textMessage.setText("");
                textMessage.setVisibility(View.INVISIBLE);
                Log.d(tag, "All input fields are valid.");
            }

            Log.d(tag, "Conforming JSON payload.");

            // put tags on an array (backend expects)
            ArrayList<String> tags = new ArrayList<>();
            tags.add(task_tag); JSONArray tagsJSON = new JSONArray(tags);

            // make json payload for the request
            json.put("name", name);
            json.put("description", description);
            json.put("tags", tagsJSON);
            json.put("priority", priority);
            json.put("due_date", date);
            json.put("due_time", time);
            // NOTE: using 'due_date' and 'due_time' for the task request
            //       because backend uses 'date' and 'time' for creation timestamp

        } catch (JSONException e){
            Log.e(tag, e.getMessage());
            Log.e(tag, Log.getStackTraceString(e));
            return null;
        }

        return json;
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

    public void submitNewTask(final JSONObject postMessage) {
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

                            /* Possible endpoint responses

                                {‘status’: ‘success’}
                                {‘status’: ‘not logged in’},
                                {‘status’: ‘must specify name, priority’},
                                {‘status’: ‘invalid request’}

                             */

                            String status = response.get("status").toString();
                            String errorMessage = null;

                            switch(status)
                            {
                                case "success" :
                                    JSONObject taskJSON = new JSONObject(postMessage.toString());
                                    taskJSON.put("task_id", response.getInt("task_id"));

                                    Task new_task = new Task(taskJSON);
                                    TaskSystem.addTask(new_task);
                                    onBackPressed();

                                    break;

                                case "not logged in":
                                    AppController.getInstance().sessionExpired(AddTaskActivity.this);
                                    break;

                                case "must specify name, priority" :
                                    Log.e(tag, "API /" + endpoint + " response : invalid data.");
                                    //TODO : show on activity body
                                    break;
                            }
                            if(errorMessage != null) {
                                textMessage.setText(status);
                                textMessage.setVisibility(View.VISIBLE);
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
