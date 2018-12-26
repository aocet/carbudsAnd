package com.ali.cs491.carbuds.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ali.cs491.carbuds.Source.Connection;
import com.ali.cs491.carbuds.MainActivity;
import com.ali.cs491.carbuds.Source.MapDialogFragment;
import com.ali.cs491.carbuds.R;
import com.ali.cs491.carbuds.FundamentalActivities.RoleSelectionActivity;
import com.ali.cs491.carbuds.SetTrip.RouteManager;
import com.ali.cs491.carbuds.SetTrip.StartSelectionActivity;
import com.ali.cs491.carbuds.Source.User;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.constraint.Constraints.TAG;

public class ProfileFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public ProfileFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    private ArrayList<Image> images = new ArrayList<>();

    private TextView passengerSeatView;
    private TextView genderPreferenceView;
    private TextView carModelView;
    private TextView carBrandView;
    private TextView licensePlateView;
    private TextView musicPreferenceView;
    private TextView userNameView;
    private TextView currentRoleView;

    private TextView passengerSeatView2;
    private TextView carModelView2;
    private TextView carBrandView2;
    private TextView licensePlateView2;

    private TextView tripStartTimeView;
    private TextView tripStartTimeView2;

    private DrawerLayout mDrawerLayout;
    public static Bitmap pp;

    private View v;
    private Fragment f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        f = this;
        readShared();

        passengerSeatView = v.findViewById(R.id.passenger_seat_view);
        genderPreferenceView = v.findViewById(R.id.gender_preference_view);
        carModelView = v.findViewById(R.id.car_model_view);
        carBrandView = v.findViewById(R.id.car_brand_view);
        licensePlateView = v.findViewById(R.id.license_plate_view);
        musicPreferenceView = v.findViewById(R.id.music_preference_view);
        userNameView = v.findViewById(R.id.name);
        currentRoleView = v.findViewById(R.id.current_role_view);

        tripStartTimeView = v.findViewById(R.id.trip_start_time);
        tripStartTimeView2 = v.findViewById(R.id.trip_start_time_view);
        tripStartTimeView.setVisibility(View.GONE);
        tripStartTimeView2.setVisibility(View.GONE);

        licensePlateView2 = v.findViewById(R.id.license_plate);
        passengerSeatView2 = v.findViewById(R.id.passenger_seat);
        carModelView2 = v.findViewById(R.id.car_model);
        carBrandView2 = v.findViewById(R.id.car_brand);

        switch (User.userType) {
            case "driver": {
                passengerSeatView.setVisibility(View.VISIBLE);
                carBrandView.setVisibility(View.VISIBLE);
                carModelView.setVisibility(View.VISIBLE);
                licensePlateView.setVisibility(View.VISIBLE);
                passengerSeatView2.setVisibility(View.VISIBLE);
                carBrandView2.setVisibility(View.VISIBLE);
                carModelView2.setVisibility(View.VISIBLE);
                licensePlateView2.setVisibility(View.VISIBLE);

                break;
            }
            case "hitchhiker": {
                passengerSeatView.setVisibility(View.GONE);
                carBrandView.setVisibility(View.GONE);
                carModelView.setVisibility(View.GONE);
                licensePlateView.setVisibility(View.GONE);
                passengerSeatView2.setVisibility(View.GONE);
                carBrandView2.setVisibility(View.GONE);
                carModelView2.setVisibility(View.GONE);
                licensePlateView2.setVisibility(View.GONE);
                break;
            }
            default: {
                passengerSeatView.setVisibility(View.GONE);
                carBrandView.setVisibility(View.GONE);
                carModelView.setVisibility(View.GONE);
                licensePlateView.setVisibility(View.GONE);
                passengerSeatView2.setVisibility(View.GONE);
                carBrandView2.setVisibility(View.GONE);
                carModelView2.setVisibility(View.GONE);
                licensePlateView2.setVisibility(View.GONE);
                break;
            }
        }

        currentRoleView.setText(User.userType);

        switch (User.userType) {
            case "driver": {
                ProfileFragment.GetDriverProfileTask task = new ProfileFragment.GetDriverProfileTask();
                task.execute((Void) null);

                break;
            }
            case "hitchhiker": {
                ProfileFragment.GetHitchhikerProfileTask task = new ProfileFragment.GetHitchhikerProfileTask();
                task.execute((Void) null);
                break;
            }
            default: {
                Intent intent = new Intent(getActivity(), RoleSelectionActivity.class);
                startActivity(intent);
                break;
            }
        }
        ImageButton editProfileButton = v.findViewById(R.id.edit_profile_button);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (User.userType) {
                    case "driver": {
                        Intent intent = new Intent(getActivity(), InitialDriverProfileActivity.class);
                        startActivity(intent);

                        break;
                    }
                    case "hitchhiker": {
                        Intent intent = new Intent(getActivity(), InitialHitchhikerProfileActivity.class);
                        startActivity(intent);
                        break;
                    }
                    default: {
                        Intent intent = new Intent(getActivity(), RoleSelectionActivity.class);
                        startActivity(intent);
                        break;
                    }

                }
                refresh();
            }
        });



        mDrawerLayout = v.findViewById(R.id.profile_drawer);

        NavigationView navigationView = v.findViewById(R.id.nav_view);
        Menu navMenu = navigationView.getMenu();

        CheckCurrentTrip(navMenu);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        //menuItem.setChecked(true);
                        String menuTitle = menuItem.getTitle().toString();

                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawer(navigationView);

                        switch (menuTitle){
                            case "Change Role":{
                                Intent intent = new Intent(getActivity(), RoleSelectionActivity.class);
                                startActivity(intent);
                                break;
                            }
                            case "Logout":{
                                logout();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                break;
                            }
                            case "Set Trip":{
                                if(User.userType.equals("driver")){
                                    RouteManager.setUserType(RouteManager.DRIVER);
                                }
                                else {
                                    RouteManager.setUserType(RouteManager.HITCHHIKER);
                                }
                                Intent intent = new Intent(getActivity(), StartSelectionActivity.class);
                                startActivity(intent);
                                break;
                            }
                            case "Cancel Current Trip":{
                                CancelTrip();
                                refresh();
                                break;
                            }
                            case "Show Current Trip":{
                                JSONObject jsonObject1 = new JSONObject();
                                try {
                                    MapDialogFragment mapDialogFragment = new MapDialogFragment();

                                    jsonObject1.put("intersection_polyline",User.tripPolyline);
                                    mapDialogFragment.setCandidateInfo(jsonObject1);
                                    mapDialogFragment.setCancelable(true);
                                    mapDialogFragment.show(getFragmentManager(), "mapsfragment");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        // refresh();
                        return true;
                    }
                });

        CircleImageView profilePic = (CircleImageView)v.findViewById(R.id.profile_fragment_pic);
        String loadUrl = "http://35.205.45.78/get_user_image?user_image_id=" + User.user_id;
        if(pp == null) {
            Glide.with(profilePic)
                    .load(loadUrl)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(profilePic);
        } else {
            profilePic.setImageBitmap(pp);
        }
        ImageButton settingsButton = v.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);

            }
        });

        FloatingActionButton insertImageButton = v.findViewById(R.id.insert_image_button);
        insertImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Image ekleme buraya bağlanacak
                selectImage();

            }
        });
        return v;
    }
    private void refresh(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(f).attach(f).commit();
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
    private void compressAndRotate(File file) {
        BitmapFactory.Options options;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (OutOfMemoryError e) {
            try {
                options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                FileInputStream fileInputStream = new FileInputStream(file);
                bitmap = BitmapFactory.decodeStream(fileInputStream, null, options);
            } catch(Exception excepetion) {

            }
        } catch (Exception e){
            String str = e.getMessage();
        }
        if(bitmap == null){
            Toast.makeText(getContext(), "Image Selection Failed, Size of image too big", Toast.LENGTH_SHORT).show();
            return;
        }
        bitmap =Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2, false);


        OutputStream os = null;
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(file.getAbsolutePath());

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    pp = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    pp = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    pp = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                    pp = bitmap;
                    break;
                    default:
                        pp = bitmap;
                        break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            pp = bitmap;
        }
      /*  File uploadFile =getActivity().getCacheDir();
        try {
            uploadFile = File.createTempFile("uploadImage", ".jpeg", uploadFile);
            uploadFile.createNewFile(); // if file already exists will do nothing
            FileOutputStream oFile = new FileOutputStream(uploadFile, false);
            os = new BufferedOutputStream(oFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/  // it is for creating new
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pp.compress(Bitmap.CompressFormat.JPEG, 75, os);
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(file == null){
            return;
        }
        uploadImage(file);
    }

    private void uploadImage(File file){

        AndroidNetworking.upload(Connection.IP + Connection.UPLOAD_USER_IMAGE)
                .addMultipartFile("pic",file)
                .setTag("uploadTest")
                .addMultipartParameter("user_image_id", Integer.toString(User.user_id))
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("true\n")) {
                            CircleImageView profilePic = (CircleImageView)v.findViewById(R.id.profile_fragment_pic);
                            if(pp != null){
                                profilePic.setImageBitmap(pp);
                                refresh();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        String str = anError.getErrorBody();
                    }
                });
    }
    private void selectImage(){
        ImagePicker imagePicker= ImagePicker.create(getActivity()).language("in") // Set image picker language
                .returnMode(ReturnMode.ALL) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
                .folderMode(true) // set folder mode (false by default)
                .includeVideo(false) // include video (false by default)
                .toolbarArrowColor(Color.RED) // set toolbar arrow up color
                .toolbarFolderTitle("Folder") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarDoneButtonText("DONE"); // done button text
        imagePicker.single();
        imagePicker.limit(1) // max images can be selected (99 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera")// captured image directory name ("Camera" folder by default)
                .imageFullDirectory(Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(imagePicker.getIntent(getActivity()),111);// can be full path
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (requestCode == 111 && resultCode != ReturnMode.NONE.ordinal()) {
            images = (ArrayList<Image>) ImagePicker.getImages(data);
            File imgFile = new File(images.get(0).getPath());
            if(imgFile.exists()){
                compressAndRotate(imgFile);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void writeShared(String profile_type) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("SHARED", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("type", profile_type);

        editor.commit();
    }

    public void logout() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("SHARED", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("type", "");
        editor.putInt("user_id", -1);
        editor.putString("token", "");
        editor.putString("name", "");

        editor.commit();
    }

    public void readShared() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("SHARED", Context.MODE_PRIVATE);
        User.user_id = sharedPref.getInt("user_id", -1);
        User.token = sharedPref.getString("token", "");
        User.username = sharedPref.getString("name", "");
        User.username = sharedPref.getString("type", "");

    }

    public void CheckCurrentTrip(Menu navMenu){
        if(User.isTripSetted == false){
            tripStartTimeView.setVisibility(View.GONE);
            tripStartTimeView2.setVisibility(View.GONE);
            navMenu.findItem(R.id.cancel_trip).setVisible(false);
            navMenu.findItem(R.id.set_trip).setVisible(true);
            navMenu.findItem(R.id.show_trip).setVisible(false);
        }
        else{
            tripStartTimeView.setVisibility(View.VISIBLE);
            tripStartTimeView2.setVisibility(View.VISIBLE);
            tripStartTimeView2.setText(User.tripStartTime);
            navMenu.findItem(R.id.cancel_trip).setVisible(true);
            navMenu.findItem(R.id.set_trip).setVisible(false);
            navMenu.findItem(R.id.show_trip).setVisible(true);
        }
    }

    public void CancelTrip(){
        String URL ;

        URL = Connection.IP + Connection.CANCEL_TRIP;

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
                            Toast.makeText(getContext(), "Trip Cancel Failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        tripStartTimeView.setVisibility(View.GONE);
                        tripStartTimeView2.setVisibility(View.GONE);
                        User.isTripSetted = false;
                        refresh();
                    }
                    @Override
                    public void onError(ANError error) {
                        String str = error.getErrorBody();
                    }
                });
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
                jsonObject.put("token", User.token);
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
                passengerSeatView.setVisibility(View.VISIBLE);

                carBrandView.setText(carBrand);
                carBrandView.setVisibility(View.VISIBLE);

                carModelView.setText(carModel);
                carModelView.setVisibility(View.VISIBLE);

                licensePlateView.setText(licensePlate);
                licensePlateView.setVisibility(View.VISIBLE);

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
                jsonObject.put("token", User.token);
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
                passengerSeatView.setVisibility(View.GONE);
                carBrandView.setVisibility(View.GONE);
                carModelView.setVisibility(View.GONE);
                licensePlateView.setVisibility(View.GONE);

                genderPreferenceView.setText(genderPreference);
                musicPreferenceView.setText(musicPreference);
                userNameView.setText(fullUsername);
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "Initializing sounds...");

        v = this.getView();

        readShared();

    }
}

