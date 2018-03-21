package com.honeydo5.honeydo.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.honeydo5.honeydo.R;

public class AddTask extends AppCompatActivity {

    int y, m, d, hr, min;
    static final int date_dialog_id = 0;
    static final int time_dialog_id = 1;

    Button date, time;
    EditText timeText, dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        date = (Button) findViewById(R.id.date);
        time = (Button) findViewById(R.id.time);

        timeText = (EditText) findViewById(R.id.timeText);
        dateText = (EditText) findViewById(R.id.dateText);

        showDate();
        showTime();
    }


    public void showDate(){
        date.setOnClickListener(
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
        time.setOnClickListener(
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
        Intent i = new Intent(AddTask.this, MainScreen.class);
        startActivity(i);
    }
}
