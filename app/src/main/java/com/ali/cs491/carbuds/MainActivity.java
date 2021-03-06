package com.ali.cs491.carbuds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.os.Handler;
import android.os.Looper;

import com.ali.cs491.carbuds.FundamentalActivities.LoginActivity;
import com.ali.cs491.carbuds.FundamentalActivities.Main2Activity;
import com.ali.cs491.carbuds.FundamentalActivities.RegisterActivity;
import com.ali.cs491.carbuds.FundamentalActivities.RoleSelectionActivity;
import com.ali.cs491.carbuds.Source.Connection;
import com.ali.cs491.carbuds.Source.User;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private static final String AUTH_KEY = "key=YOUR_SERVER_KEY";
    private TextView mTextView;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    public void readShared(){
        SharedPreferences sharedPref = this.getSharedPreferences("SHARED",Context.MODE_PRIVATE);
        User.user_id = sharedPref.getInt("user_id", -1);
        User.token = sharedPref.getString("token", "");
        User.userType =  sharedPref.getString("type", "");
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
                        startActivity(new Intent(MainActivity.this,Main2Activity.class));
                    }
                    @Override
                    public void onError(ANError error) {
                        User.isTripSetted = false;
                        startActivity(new Intent(MainActivity.this,Main2Activity.class));
                        String str = error.getErrorBody();
                    }
                });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readShared();
        if(User.user_id != -1){
            if(User.userType.equals(""))
                startActivity(new Intent(MainActivity.this,RoleSelectionActivity.class));
            else{
                CheckAndRetrieveCurrentTrip();
            }
        } else{
            User.isTripSetted = false;
            User.userType = "";
            User.username = "";
            User.tripPolyline = "";
            User.tripStartTime = "";
        }
        Button loginButton = findViewById(R.id.loginButton);
        Button signUpButton = findViewById(R.id.signUpButton);
        //  to do add color for buttons and background
        showToken(null);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);

            //   Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            //     startActivity(intent);
            }
        });
    }

    public void showToken(View view) {
     //   mTextView.setText(FirebaseInstanceId.getInstance().getToken());
     //   Log.i("token", FirebaseInstanceId.getInstance().getToken());
    }

    public void subscribe(View view) {
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        mTextView.setText("asd");
    }

    public void unsubscribe(View view) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
        mTextView.setText("unsasd");
    }

    public void sendToken(View view) {
        sendWithOtherThread("token");
    }

    public void sendTokens(View view) {
        sendWithOtherThread("tokens");
    }

    public void sendTopic(View view) {
        sendWithOtherThread("topic");
    }

    private void sendWithOtherThread(final String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification(type);
            }
        }).start();
    }

    private void pushNotification(String type) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "Google I/O 2016");
            jNotification.put("body", "Firebase Cloud Messaging (App)");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            jNotification.put("icon", "ic_notification");

            jData.put("picture", "http://opsbug.com/static/google-io.jpg");

            switch(type) {
                case "tokens":
                    JSONArray ja = new JSONArray();
                    ja.put("c5pBXXsuCN0:APA91bH8nLMt084KpzMrmSWRS2SnKZudyNjtFVxLRG7VFEFk_RgOm-Q5EQr_oOcLbVcCjFH6vIXIyWhST1jdhR8WMatujccY5uy1TE0hkppW_TSnSBiUsH_tRReutEgsmIMmq8fexTmL");
                    ja.put(FirebaseInstanceId.getInstance().getToken());
                    jPayload.put("registration_ids", ja);
                    break;
                case "topic":
                    jPayload.put("to", "/topics/news");
                    break;
                case "condition":
                    jPayload.put("condition", "'sport' in topics || 'news' in topics");
                    break;
                default:
                    jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
            }

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", AUTH_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(resp);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }


}
