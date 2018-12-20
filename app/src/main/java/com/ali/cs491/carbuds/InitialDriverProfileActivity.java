package com.ali.cs491.carbuds;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hootsuite.nachos.NachoTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InitialDriverProfileActivity extends AppCompatActivity {

    private InitialDriverProfileTask mAuthTask;

    private Spinner passengerSeatSpinner;
    private Spinner genderPreferenceSpinner;
    private Spinner carModelSpinner;
    private Spinner carBrandSpinner;
    private EditText licensePlateField;
    private NachoTextView musicPreferenceSpinner;
    private TextView userNameView;

    private static int user_id;
    public static String token;
    private static String user_name;

    private View formView;
    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_driver_profile);

        genderPreferenceSpinner = findViewById(R.id.gender_preference_spinner);
        String[] gender_pref_array = new String[] {"Female", "Male", "Both"};
        genderPreferenceSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, gender_pref_array));

        passengerSeatSpinner = findViewById(R.id.passenger_seat_spinner);
        String[] seats = new String[] {"1", "2", "3", "4"};
        passengerSeatSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, seats));

        carBrandSpinner = findViewById(R.id.car_brand_spinner);
        String[] car_brands = new String[] {"BMW"};
        carBrandSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, car_brands));

        carModelSpinner = findViewById(R.id.car_model_spinner);
        String[] car_models = new String[] {"320d"};
        carModelSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, car_models));

        licensePlateField = findViewById(R.id.license_plate_field);
        formView = findViewById(R.id.formView);
        progressView = findViewById(R.id.initial_driver_progress);

        musicPreferenceSpinner = findViewById(R.id.music_preference_spinner);
        String[] musicGenres = new String[] {"Pop", "Rock", "Rap"};
        musicPreferenceSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, musicGenres));

        Button mEmailSignInButton = (Button) findViewById(R.id.button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        readShared();
        userNameView = findViewById(R.id.name);
        userNameView.setText(user_name);

        CircleImageView profilePic = (CircleImageView)findViewById(R.id.profile);
        Glide.with(profilePic)
                .load("http://35.205.45.78/get_user_image?user_image_id=" + user_id)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .into(profilePic)
                .waitForLayout();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            formView.setVisibility(show ? View.GONE : View.VISIBLE);
            formView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    formView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void attemptLogin() {

        String passengerSeat = passengerSeatSpinner.getSelectedItem().toString();
        String carBrand = carBrandSpinner.getSelectedItem().toString();
        String carModel = carModelSpinner.getSelectedItem().toString();
        String licensePlate = licensePlateField.getText().toString();
        String genderPreference = genderPreferenceSpinner.getSelectedItem().toString();
        List<String> musicPreference = musicPreferenceSpinner.getChipValues();
        String musicPreferenceString = musicPreference.toString();
        musicPreferenceString = musicPreferenceString.replace("[", "{");
        musicPreferenceString = musicPreferenceString.replace("]", "}");

        licensePlateField.setError(null);
        boolean cancel = false;

        if (mAuthTask != null) {
            return;
        }
        View focusView = null;

        if (passengerSeat.isEmpty() || carBrand.isEmpty() || carModel.isEmpty() || genderPreference.isEmpty() || musicPreferenceString.isEmpty()){
            focusView = passengerSeatSpinner;
            cancel = true;
        }
        if(licensePlate.isEmpty()){
            licensePlateField.setError("License Plate Cannot Leave Blank");
            focusView = licensePlateField;
            cancel = true;

        }
        if(cancel){
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new InitialDriverProfileTask();
            mAuthTask.execute((Void) null);
        }


    }

    public void writeShared(String profile_type){
        SharedPreferences sharedPref = this.getSharedPreferences("SHARED",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("type", profile_type);
        editor.commit();
    }
    public void readShared(){
        SharedPreferences sharedPref = this.getSharedPreferences("SHARED",Context.MODE_PRIVATE);
        user_id = sharedPref.getInt("user_id", -1);
        token = sharedPref.getString("token", "");
        user_name = sharedPref.getString("name", "");
    }


    public class InitialDriverProfileTask extends AsyncTask<Void, Void, Boolean> {

        private String passengerSeat;
        private String carModel;
        private String carBrand;
        private String licensePlate;
        private String genderPreference;
        private String musicPreferenceString;


        InitialDriverProfileTask() {
            passengerSeat = passengerSeatSpinner.getSelectedItem().toString();
            carBrand = carBrandSpinner.getSelectedItem().toString();
            carModel = carModelSpinner.getSelectedItem().toString();
            licensePlate = licensePlateField.getText().toString();
            genderPreference = genderPreferenceSpinner.getSelectedItem().toString();
            List<String> musicPreference = musicPreferenceSpinner.getChipValues();
            musicPreferenceString = musicPreference.toString();
            musicPreferenceString = musicPreferenceString.replace("[", "{");
            musicPreferenceString = musicPreferenceString.replace("]", "}");

        }
        private String setupURLConnection(){

            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("passenger_seats", passengerSeat);
                jsonObject.put("car_brand", carBrand);
                jsonObject.put("car_model", carModel);
                jsonObject.put("gender_preference", genderPreference);
                jsonObject.put("license_plate", licensePlate);
                jsonObject.put("music_preference", musicPreferenceString);
                jsonObject.put("user_id", user_id);
                jsonObject.put("token", token);
            } catch(JSONException e){
                e.printStackTrace();
            }
            Connection connection= new Connection();
            connection.setConnection(Connection.INITIAL_DRIVER_PROFILE_SETUP, jsonObject);
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
            mAuthTask = null;
            showProgress(false);

            if (success) {
                writeShared("driver");
                Intent intent = new Intent(InitialDriverProfileActivity.this,Main2Activity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
