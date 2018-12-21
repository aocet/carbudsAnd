package com.ali.cs491.carbuds;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerActivity extends AppCompatActivity {
    private Button datePicker;
    private Button timePicker;
    private Button setTrip;
    public static Calendar calendar;

    public void setDate(){
        calendar = Calendar.getInstance();
    }

    public void setNewDate(int year, int month, int day) {
        this.calendar.set(Calendar.YEAR, year);
        this.calendar.set(Calendar.MONTH, month);
        this.calendar.set(Calendar.DAY_OF_MONTH, day);
    }

    public void setNewTime(int hour, int minute) {
        this.calendar.set(Calendar.HOUR_OF_DAY, hour);
        this.calendar.set(Calendar.MINUTE, minute);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        datePicker = findViewById(R.id.datePickerButton);
        timePicker = findViewById(R.id.timePickerButton);
        setTrip    = findViewById(R.id.pickFinishButton);
        datePicker.setText(dateFormat.format(calendar.getTime()));
        timePicker.setText(timeFormat.format(calendar.getTime()));
    }

    public void changeDateButton() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        datePicker.setText(dateFormat.format(calendar.getTime()));
    }

    public void ChangeTimeButton() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timePicker.setText(timeFormat.format(calendar.getTime()));
    }
    public void sendRoute(){
        Trip trip = RouteManager.getTrip();
        JSONObject jsonObj = new JSONObject();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(formatter.format(calendar.getTime()));
        try {
            jsonObj.put("token", LoginActivity.token );
            jsonObj.put("user_id", LoginActivity.user_id); // get id
            jsonObj.put("trip_start_point", RouteManager.getPointString(trip.getStartPoint()));
            jsonObj.put("trip_end_point", RouteManager.getPointString(trip.getEndPoint()));
            jsonObj.put("trip_start_time", formatter.format(calendar.getTime()));

            String URL;
            if(trip.getUserType() == RouteManager.DRIVER) {
                jsonObj.put("available_seat", "2");

                URL = Connection.IP + Connection.SET_TRIP_DRIVER;
            } else {
                URL = Connection.IP + Connection.SET_TRIP_HITCHHIKER;
            }
            AndroidNetworking.post(URL)
                    .addJSONObjectBody(jsonObj) // posting any type of file
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String str) {
                            Intent intent = new Intent(DatePickerActivity.this, Main2Activity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        RouteManager.setDate(calendar);
        sendRoute();
    }
}
