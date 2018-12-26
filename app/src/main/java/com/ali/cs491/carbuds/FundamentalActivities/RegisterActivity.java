package com.ali.cs491.carbuds.FundamentalActivities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;

import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import com.ali.cs491.carbuds.R;
import com.ali.cs491.carbuds.Source.Connection;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.common.hash.HashCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class RegisterActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mRePasswordView;
    private EditText mNameView;
    private EditText mSurnameView;
    private EditText mUsernameView;
    private Spinner mGender;
    private Button mRegisterButton;
    private static final String[] genders = {"Male", "Female"};


    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mRePasswordView = (EditText) findViewById(R.id.passwordRe);
        mNameView = (EditText) findViewById(R.id.name);
        mSurnameView = (EditText) findViewById(R.id.surname);
        mUsernameView = (EditText) findViewById(R.id.username);
        mGender = (Spinner) findViewById(R.id.gender);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, genders);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mGender.setAdapter(adapter);
        mGender.setSelection(0);

        mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
    }

    public void writeShared(String token, int user_id, String name){
        SharedPreferences sharedPref = this.getSharedPreferences("SHARED",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.putInt("user_id", user_id);
        editor.putString("name", name);
        editor.apply();
    }
    private void connection(String email, String password, String name, String surname,
                            String username, String gender, String device) {
        JSONObject jsonObject = new JSONObject();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes("UTF-8"));
            byte[] digest = md.digest();
            password = HashCode.fromBytes(digest).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("name", name);
            jsonObject.put("surname", surname);
            jsonObject.put("email", email);
            jsonObject.put("gender", gender);
            jsonObject.put("device_reg_id", device);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //TODO: replace ip
        AndroidNetworking.post(Connection.IP + Connection.SIGNUP)
                .addJSONObjectBody(jsonObject) // posting any type of file
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String str) {
                        //TODO: daha farkli hata mesajlari gelecek
                        if (str.equals("true\n")) {
                            showProgress(false);
                            writeShared("",-1,"");
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            return;
                        }
                        if (str.equals("not bilkent\n")) {
                            //TODO: show error for not bilkent mail
                        }
                        showProgress(false);
                        mPasswordView.setError("Register failed, try again");
                        mPasswordView.requestFocus();
                    }

                    @Override
                    public void onError(ANError anError) {
                        showProgress(false);
                        mPasswordView.setError("Connection can't be established, please try again");
                        mPasswordView.requestFocus();
                        Log.e("Carbuds", "Signup connection screw up");
                    }
                });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mRePasswordView.setError(null);
        mNameView.setError(null);
        mSurnameView.setError(null);
        mUsernameView.setError(null);


        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String rePassword = mRePasswordView.getText().toString();
        String name = mNameView.getText().toString();
        String surname = mSurnameView.getText().toString();
        String username = mUsernameView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password, rePassword)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!TextUtils.isEmpty(rePassword) && !isPasswordMatch(password, rePassword)) {
            mRePasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mRePasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(surname)) {
            mSurnameView.setError(getString(R.string.error_field_required));
            focusView = mSurnameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            if (deviceId == null)
                deviceId = "oddDevice";
            Log.d("Custom", deviceId);
            String gender = (String) mGender.getSelectedItem();
            String device = deviceId;
            connection(email, password, name, surname, username, gender, device);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password, String confirmation) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isPasswordMatch(String password, String confirmation) {
        return password.equals(confirmation);
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

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}




