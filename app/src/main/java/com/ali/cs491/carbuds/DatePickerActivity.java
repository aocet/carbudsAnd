package com.ali.cs491.carbuds;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerActivity extends AppCompatActivity {
    private Button datePicker;
    private Button timePicker;
    public static Date date;
    public static void setDate(){
        Date dt = new Date();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        datePicker = findViewById(R.id.datePickerButton);
        timePicker = findViewById(R.id.timePickerButton);
        datePicker.setText(dateFormat.format(date));
        timePicker.setText(timeFormat.format(date));
    }
    public void showDatePicker(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        ((DatePickerFragment) newFragment).setType(DatePickerFragment.DATE);
        newFragment.show(getSupportFragmentManager(), "date picker");
    }
    public void showTimePicker(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        ((DatePickerFragment) newFragment).setType(DatePickerFragment.TIME);
        newFragment.show(getSupportFragmentManager(), "time picker");
    }
    public void finish(View v){
        RouteManager.setDate(date);
        Intent intent = new Intent(DatePickerActivity.this, MatchmakingActivity.class);
        startActivity(intent);
    }
}
