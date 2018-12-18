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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.hootsuite.nachos.NachoTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DriverProfilePageActivity extends AppCompatActivity {

    private TextView passengerSeatView;
    private TextView genderPreferenceView;
    private TextView carModelView;
    private TextView carBrandView;
    private TextView licensePlateView;
    private TextView musicPreferenceView;
    private TextView userNameView;
    private TextView currentRoleView;

    private static int user_id;
    public static String token;
    private static String user_name;
    private static String user_type;

    private View formView;
    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile_page);

        passengerSeatView = findViewById(R.id.passenger_seat_view);
        genderPreferenceView = findViewById(R.id.gender_preference_view);
        carModelView = findViewById(R.id.car_model_view);
        carBrandView = findViewById(R.id.car_brand_view);
        licensePlateView = findViewById(R.id.license_plate_view);
        musicPreferenceView = findViewById(R.id.music_preference_view);
        userNameView = findViewById(R.id.name);
        currentRoleView = findViewById(R.id.current_role_view);

        readShared();
        currentRoleView.setText(user_type);



        switch (user_type) {
            case "driver": {
                GetDriverProfileTask task = new GetDriverProfileTask();
                task.execute((Void) null);

                break;
            }
            case "hitchhiker": {
                GetHitchhikerProfileTask task = new GetHitchhikerProfileTask();
                task.execute((Void) null);
                break;
            }
            default: {
                Intent intent = new Intent(DriverProfilePageActivity.this, RoleSelectionActivity.class);
                startActivity(intent);
                break;
            }
        }

        ImageButton editProfileButton = (ImageButton) findViewById(R.id.edit_profile_button);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (user_type) {
                    case "driver": {
                        Intent intent = new Intent(DriverProfilePageActivity.this, InitialDriverProfileActivity.class);
                        startActivity(intent);

                        break;
                    }
                    case "hitchhiker": {
                        Intent intent = new Intent(DriverProfilePageActivity.this, InitialHitchhikerProfileActivity.class);
                        startActivity(intent);
                        break;
                    }
                    default: {
                        Intent intent = new Intent(DriverProfilePageActivity.this, RoleSelectionActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });


    }

    public void writeShared(String profile_type) {
        SharedPreferences sharedPref = this.getSharedPreferences("SHARED", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("type", profile_type);
        editor.commit();
    }

    public void readShared() {
        SharedPreferences sharedPref = this.getSharedPreferences("SHARED", Context.MODE_PRIVATE);
        user_id = sharedPref.getInt("user_id", -1);
        token = sharedPref.getString("token", "");
        user_name = sharedPref.getString("name", "");
        user_type = sharedPref.getString("type", "");

    }

    public class GetDriverProfileTask extends AsyncTask<Void, Void, Boolean> {

        private String passengerSeat;
        private String carModel;
        private String carBrand;
        private String licensePlate;
        private String genderPreference;
        private String musicPreference;
        private String fullUsername;


        GetDriverProfileTask() {

        }

        private String setupURLConnection() {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Connection connection = new Connection();
            connection.setConnection(Connection.GET_DRIVER_PROFILE, jsonObject);
            return connection.getResponseMessage();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String msg = setupURLConnection();
            JSONObject jsonObj = null;
            Log.i("Carbuds",msg);
            try {
                jsonObj = new JSONObject(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (msg.equals("false\n")) {
                return false;
            } else {
                try {
                    assert jsonObj != null;
                    passengerSeat = jsonObj.getString("passenger_seat");
                    carBrand = jsonObj.getString("brand");
                    carModel = jsonObj.getString("model");
                    licensePlate = jsonObj.getString("license_plate");
                    genderPreference = jsonObj.getString("hitchhiker_gender_preference");
                    genderPreference = genderPreference.replace("{", "");
                    genderPreference = genderPreference.replace("}", "");

                    musicPreference = jsonObj.getString("music_preference");
                    musicPreference = musicPreference.replace("{", "");
                    musicPreference = musicPreference.replace("}", "");

                    fullUsername = jsonObj.getString("name") + " " + jsonObj.getString("lastname");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
            // return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                passengerSeatView.setText(passengerSeat);
                carBrandView.setText(carBrand);
                carModelView.setText(carModel);
                licensePlateView.setText(licensePlate);
                genderPreferenceView.setText(genderPreference);
                musicPreferenceView.setText(musicPreference);
                userNameView.setText(fullUsername);

            }
        }

    }

    public class GetHitchhikerProfileTask extends AsyncTask<Void, Void, Boolean> {


        private String genderPreference;
        private String musicPreference;
        private String fullUsername;

        GetHitchhikerProfileTask() {

        }

        private String setupURLConnection() {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Connection connection = new Connection();
            connection.setConnection(Connection.GET_HITCHHIKER_PROFILE, jsonObject);
            return connection.getResponseMessage();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String msg = setupURLConnection();
            JSONObject jsonObj = null;
            Log.i("Carbuds",msg);
            try {
                jsonObj = new JSONObject(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (msg.equals("false\n")) {
                return false;
            } else {
                try {
                    assert jsonObj != null;
                    genderPreference = jsonObj.getString("driver_gender_preference");
                    genderPreference = genderPreference.replace("{", "");
                    genderPreference = genderPreference.replace("}", "");

                    musicPreference = jsonObj.getString("music_preference");
                    musicPreference = musicPreference.replace("{", "");
                    musicPreference = musicPreference.replace("}", "");

                    fullUsername = jsonObj.getString("name") + " " + jsonObj.getString("lastname");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
            // return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                genderPreferenceView.setText(genderPreference);
                musicPreferenceView.setText(musicPreference);
                userNameView.setText(fullUsername);
            }
        }

        @Override
        protected void onCancelled() {
        }
    }
}
