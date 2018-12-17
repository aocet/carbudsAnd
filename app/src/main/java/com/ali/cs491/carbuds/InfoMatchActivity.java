package com.ali.cs491.carbuds;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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

import java.util.List;

public class InfoMatchActivity extends FragmentActivity {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_match);



        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");
        String exchange = intent.getStringExtra("exchange");
        String queue = intent.getStringExtra("queue");
        String intersectionPolyline = intent.getStringExtra("intersectionPolyline");
        String tripStartTime = intent.getStringExtra("tripStartTime");
        String startPoint = intent.getStringExtra("startPoint");
        String endPoint = intent.getStringExtra("endPoint");

        TextView nameView = (TextView) findViewById(R.id.name);
        nameView.setText(name + " " + surname);

        TextView tripView = (TextView) findViewById(R.id.tripTime);
        tripView.setText("Trip Start Time: " + tripStartTime);

        MapView mapView = (MapView) findViewById(R.id.map);

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
        });
    }
}
