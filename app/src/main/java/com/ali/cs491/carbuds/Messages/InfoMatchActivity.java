package com.ali.cs491.carbuds.Messages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.ali.cs491.carbuds.FundamentalActivities.Main2Activity;
import com.ali.cs491.carbuds.FundamentalActivities.RegisterActivity;
import com.ali.cs491.carbuds.Source.Connection;
import com.ali.cs491.carbuds.Source.MapDialogFragment;
import com.ali.cs491.carbuds.R;
import com.ali.cs491.carbuds.Source.User;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoMatchActivity extends FragmentActivity {

    private GoogleMap mMap;
    private String music_preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_match);


        Intent intent = getIntent();
        int id = intent.getIntExtra("user_id", -1);
        int match_id = intent.getIntExtra("match_id", -1);
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");
        String exchange = intent.getStringExtra("exchange");
        String queue = intent.getStringExtra("queue");
        String intersectionPolyline = intent.getStringExtra("intersectionPolyline");
        String tripStartTime = intent.getStringExtra("tripStartTime");
        String startPoint = intent.getStringExtra("startPoint");
        String endPoint = intent.getStringExtra("endPoint");
        boolean isDriver = intent.getBooleanExtra("isDriver", false);

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("token", User.token);
            jsonObject.put("user_id", id);
            jsonObject.put("user_type", isDriver);
        } catch(JSONException e){
            e.printStackTrace();
        }

        TextView preferencesView = (TextView) findViewById(R.id.preferencesView);
        AndroidNetworking.post(Connection.IP + Connection.GET_MUSIC_PREFERENCES )
                .addJSONObjectBody(jsonObject) // posting any type of file
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String str) {
                        if(str.equals("false\n")){
                            return;
                        }
                        try {
                            JSONObject response = new JSONObject(str);
                            music_preferences = response.getString("music_preference");
                            music_preferences = music_preferences.substring(1, music_preferences.length()-1);
                            preferencesView.setText(music_preferences);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println("Error on fetching profile prefrences");
                    }
                });

        TextView nameView = (TextView) findViewById(R.id.name);
        nameView.setText(name + " " + surname);

        TextView tripView = (TextView) findViewById(R.id.tripTime);
        tripView.setText("Trip Start Time: " + tripStartTime);

        Button cancelButton = (Button) findViewById(R.id.cancelMatch);

        CircleImageView profilePic = (CircleImageView)findViewById(R.id.profilePicture);
        Glide.with(profilePic)
                .load("http://35.205.45.78/get_user_image?user_image_id="+id)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .into(profilePic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id == -1 || match_id == -1) {
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("token", User.token);
                    jsonObject.put("user_id", id);
                    jsonObject.put("match_id", match_id);
                } catch(JSONException e){
                    e.printStackTrace();
                }

                AndroidNetworking.post(Connection.IP + Connection.REMOVE_MATCH )
                        .addJSONObjectBody(jsonObject) // posting any type of file
                        .setTag("test")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String str) {
                                if (str.equals("false\n")) {
                                    Toast.makeText(InfoMatchActivity.this, "Trip Cancel Failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(InfoMatchActivity.this, Main2Activity.class);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                System.out.println("Error on canceling trip");
                            }
                        });
            }
        });
        Button openMap = findViewById(R.id.openMapsButton);
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject1 = new JSONObject();
                try {
                    MapDialogFragment mapDialogFragment = new MapDialogFragment();

                    jsonObject.put("intersection_polyline",intersectionPolyline);
                    mapDialogFragment.setCandidateInfo(jsonObject);
                    mapDialogFragment.setCancelable(true);
                    mapDialogFragment.show(getSupportFragmentManager(), "mapsfragment");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
