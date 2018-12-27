package com.ali.cs491.carbuds.SetTrip;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ali.cs491.carbuds.Source.Connection;
import com.ali.cs491.carbuds.FundamentalActivities.Main2Activity;
import com.ali.cs491.carbuds.R;
import com.ali.cs491.carbuds.Source.User;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private void CheckAndRetrieveCurrentTrip(){
        String URL ;

        URL = Connection.IP + Connection.CHECK_ACTIVE_TRIP;

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("token", User.token);
            jsonObject.put("type", User.userType);
        } catch(JSONException e){
            e.printStackTrace();
        }
        AndroidNetworking.post(URL)
                .addJSONObjectBody(jsonObject) // posting any type of file
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("false\n")){
                            User.isTripSetted = false;
                        }
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            User.tripPolyline = jsonObj.getString("destination_polyline");
                            User.tripStartTime = jsonObj.getString("trip_start_time");
                            User.isTripSetted = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(DatePickerActivity.this, Main2Activity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onError(ANError error) {
                        User.isTripSetted = false;
                        Intent intent = new Intent(DatePickerActivity.this, Main2Activity.class);
                        startActivity(intent);
                        String str = error.getErrorBody();
                    }
                });
    }
    public void sendRoute(){
        Trip trip = RouteManager.getTrip();
        JSONObject jsonObj = new JSONObject();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(formatter.format(calendar.getTime()));
        Toast.makeText(DatePickerActivity.this, "Trip creating, please wait", Toast.LENGTH_SHORT).show();
        try {
            jsonObj.put("token", User.token );
            jsonObj.put("user_id", User.user_id); // get id
            jsonObj.put("trip_start_point", RouteManager.getPointString(trip.getStartPoint()));
            jsonObj.put("trip_end_point", RouteManager.getPointString(trip.getEndPoint()));
            jsonObj.put("trip_start_time", formatter.format(calendar.getTime()));

            String URL;
            if(User.userType.equals("driver")) {
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
                            CheckAndRetrieveCurrentTrip();
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(DatePickerActivity.this, "Trip Creation Failed", Toast.LENGTH_SHORT).show();
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
