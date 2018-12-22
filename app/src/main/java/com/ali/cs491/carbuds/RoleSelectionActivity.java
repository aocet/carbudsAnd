package com.ali.cs491.carbuds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RoleSelectionActivity extends AppCompatActivity {

    private Button hitchhiker_button;
    private Button driver_button;

    private FetchDriverProfileTask mDriverTask;
    private FetchHitchhikerProfileTask mHitchhikerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        hitchhiker_button = findViewById(R.id.hitchhiker_button);
        driver_button = findViewById(R.id.driver_button);
        readShared();

        hitchhiker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.userType = "hitchhiker";
                mHitchhikerTask = new FetchHitchhikerProfileTask();
                mHitchhikerTask.execute((Void) null);
            }
        });
        driver_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.userType = "driver";
                mDriverTask = new FetchDriverProfileTask();
                mDriverTask.execute((Void) null);
            }
        });
    }
    public void writeShared(String profile_type){
        SharedPreferences sharedPref = this.getSharedPreferences("SHARED",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("type", profile_type);
        editor.commit();
    }
    public void readShared(){
        SharedPreferences sharedPref = this.getSharedPreferences("SHARED",Context.MODE_PRIVATE);
        User.token = sharedPref.getString("token", "");
        User.userType = sharedPref.getString("type", "");
        User.user_id = sharedPref.getInt("user_id", -1);
    }

    public class FetchHitchhikerProfileTask extends AsyncTask<Void, Void, Boolean> {

        FetchHitchhikerProfileTask() {


        }
        private String setupURLConnection(){

            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("token", User.token);
            } catch(JSONException e){
                e.printStackTrace();
            }
            Connection connection= new Connection();
            connection.setConnection(Connection.CHECK_HITCHHIKER_PROFILE, jsonObject);
            return connection.getResponseMessage();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String msg = setupURLConnection();
            Log.i("Carbuds",msg);
            if(msg.equals("false\n")){
                return false;
            } else {
                return true;
            }
            // return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Intent intent;
            if(!success){
                intent = new Intent(RoleSelectionActivity.this, InitialHitchhikerProfileActivity.class);
            }
            else {
                writeShared("hitchhiker");
                RouteManager.setUserType(RouteManager.HITCHHIKER);
                intent = new Intent(RoleSelectionActivity.this, Main2Activity.class);
            }
            startActivity(intent);
            finish();
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class FetchDriverProfileTask extends AsyncTask<Void, Void, Boolean> {

        FetchDriverProfileTask() {


        }
        private String setupURLConnection(){

            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("token", User.token);
            } catch(JSONException e){
                e.printStackTrace();
            }
            Connection connection= new Connection();
            connection.setConnection(Connection.CHECK_DRIVER_PROFILE, jsonObject);
            return connection.getResponseMessage();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String msg = setupURLConnection();
            Log.i("Carbuds",msg);
            if(msg.equals("false\n")){
                return false;
            } else {
                return true;
            }
            // return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Intent intent;
            if(!success){
                intent = new Intent(RoleSelectionActivity.this, InitialDriverProfileActivity.class);
            }
            else {
                writeShared("driver");
                RouteManager.setUserType(RouteManager.DRIVER);
                intent = new Intent(RoleSelectionActivity.this, Main2Activity.class);
            }
            startActivity(intent);
        }

        @Override
        protected void onCancelled() {

        }
    }
}
