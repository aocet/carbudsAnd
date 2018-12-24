package com.ali.cs491.carbuds.Source;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ali.cs491.carbuds.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MapDialogFragment extends DialogFragment
        implements OnMapReadyCallback{

    GoogleMap mMap;
    public static String TAG;
    private JSONObject jsonObject;
    public MapDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapsFragment);
        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // safety check
        if (getDialog() == null)
            return;

        int dialogWidth = 1050; // specify a value here
        int dialogHeight = 1500; // specify a value here

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        String intersectionPolyline = null;
        try {
            intersectionPolyline = jsonObject.getString("intersection_polyline");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<LatLng> polylines = PolyUtil.decode(intersectionPolyline);
        PolylineOptions polyOptions = new PolylineOptions();

        for (int i = 0; i < polylines.size(); i++) {
            polyOptions.add(polylines.get(i));
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polylines.get(0), 15));
        polyOptions.color(Color.BLUE);
        polyOptions.width(15);
        Polyline line = googleMap.addPolyline(polyOptions);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(polylines.get(0));
        markerOptions.title("Start Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap.addMarker(markerOptions);

        markerOptions = new MarkerOptions();
        markerOptions.position(polylines.get(polylines.size()-1));
        markerOptions.title("End Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(markerOptions);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapsFragment);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(mapFragment);
        ft.attach(mapFragment);
        ft.commit();
        dialog.cancel();
    }
    public void setCandidateInfo(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }
}
