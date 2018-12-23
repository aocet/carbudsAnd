package com.ali.cs491.carbuds;

import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

class User {
    public static String token;
    public static int user_id;
    public static String userType;
    public static String username;
    public static boolean isTripSetted;
    public static String tripStartTime;
    public static String tripPolyline;
    String nickname;
    String profileUrl;
    int userId;
    public static void CheckAndRetrieveCurrentTrip(){
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
                            isTripSetted = false;
                        }
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            tripPolyline = jsonObj.getString("destination_polyline");
                            tripStartTime = jsonObj.getString("trip_start_time");
                            isTripSetted = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        String str = error.getErrorBody();
                    }
                });
    }
    public User(String nickname, int userId) {
        this.nickname = nickname;
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}