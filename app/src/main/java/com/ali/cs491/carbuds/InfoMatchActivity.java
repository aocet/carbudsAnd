package com.ali.cs491.carbuds;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.maps.SupportMapFragment;
import android.support.v4.app.DialogFragment;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

        CircleImageView profilePic = (CircleImageView)findViewById(R.id.profilePicture);
        Glide.with(profilePic)
                .load("http://35.205.45.78/get_user_image?user_image_id="+id)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .into(profilePic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*ImagePopup imagePopup = new ImagePopup(InfoMatchActivity.this);
                imagePopup.setBackgroundColor(Color.BLACK);
                imagePopup.setImageOnClickClose(true);
                ImageView imageView = new ImageView(InfoMatchActivity.this);
                imagePopup.initiatePopup(imageView.getDrawable());
                imagePopup.initiatePopupWithGlide("http://35.205.45.78/get_user_image?user_image_id="+id);
                imagePopup.viewPopup();*/
            }
        });
        /*MapView mapView = (MapView) findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {

                String startLocation = startPoint.substring(1, startPoint.length()-1);
                String endLocation = endPoint.substring(1, endPoint.length()-1);
                startLocation = startLocation.replace(" ", "");
                endLocation = endLocation.replace(" ", "");

                String[] startLocations = startLocation.split(",");
                String[] endLocations = endLocation.split(",");

                Double[] startPoints = {Double.valueOf(startLocations[0]), Double.valueOf(startLocations[1])};
                Double[] endPoints = {Double.valueOf(endLocations[0]), Double.valueOf(endLocations[1])};
                LatLng coordinates = new LatLng(startPoints[0], startPoints[1]);

                List<LatLng> polylines = PolyUtil.decode(intersectionPolyline);
                PolylineOptions polyOptions = new PolylineOptions();

                for (int i = 0; i < polylines.size(); i++) {
                    polyOptions.add(polylines.get(i));
                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
                Polyline line = googleMap.addPolyline(polyOptions.geodesic(true));
                mapView.onResume();
            }
        });*/
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
