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
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ali.cs491.carbuds.Source.Connection;
import com.ali.cs491.carbuds.R;
import com.ali.cs491.carbuds.FundamentalActivities.RoleSelectionActivity;
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
    private Bitmap pp;
    private ArrayList<Image> images = new ArrayList<>();

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
        CircleImageView profilePic = (CircleImageView)findViewById(R.id.driver_page_pp);
        if(ProfileFragment.pp==null) {
            Glide.with(profilePic)
                    .load("http://35.205.45.78/get_user_image?user_image_id=" + user_id)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(profilePic)
                    .waitForLayout();
        } else {
            profilePic.setImageBitmap(ProfileFragment.pp);
        }
        profilePic.setClickable(true);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

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
            Toast.makeText(this, "Image Selection Failed, Size of image too big", Toast.LENGTH_SHORT).show();
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
                            CircleImageView profilePic = (CircleImageView)findViewById(R.id.initial_driver_pp);
                            if(pp != null){
                                profilePic.setImageBitmap(pp);
                                ProfileFragment.pp = pp;
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
        ImagePicker imagePicker= ImagePicker.create(this).language("in") // Set image picker language
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

        startActivityForResult(imagePicker.getIntent(this),111);// can be full path
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
