package com.ali.cs491.carbuds.Profile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ali.cs491.carbuds.Source.Connection;
import com.ali.cs491.carbuds.FundamentalActivities.Main2Activity;
import com.ali.cs491.carbuds.R;
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
import com.hootsuite.nachos.NachoTextView;

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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InitialHitchhikerProfileActivity extends AppCompatActivity {

    private InitialHitchhikerProfileTask mAuthTask;

    private Spinner genderPreferenceSpinner;
    private NachoTextView musicPreferenceSpinner;
    private TextView userNameView;

    private View formView;
    private View progressView;

    private static int user_id;
    private static String token;
    private static String user_name;
    private Bitmap pp;
    private ArrayList<Image> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_hitchhiker_profile);

        genderPreferenceSpinner = findViewById(R.id.gender_preference_spinner);
        String[] gender_pref_array = new String[] {"Female", "Male", "Both"};
        genderPreferenceSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, gender_pref_array));

        formView = findViewById(R.id.formView);
        progressView = findViewById(R.id.initial_driver_progress);

        musicPreferenceSpinner = findViewById(R.id.music_preference_spinner);
        String[] musicGenres = new String[] {"Pop", "Rock", "Rap", "Electro"};
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
        CircleImageView profilePic = (CircleImageView)findViewById(R.id.initial_hitchhiker_pp);
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

            }        bitmap =Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2, false);

        } catch (Exception e){
            String str = e.getMessage();
        }
        if(bitmap == null){
            Toast.makeText(this, "Image Selection Failed, Size of image too big", Toast.LENGTH_SHORT).show();
            return;
        }
        int scale = bitmap.getWidth() > 1500 ? 2:1;
        bitmap =Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/scale, bitmap.getHeight()/scale, false);



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
                            CircleImageView profilePic = (CircleImageView)findViewById(R.id.initial_hitchhiker_pp);
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

        String genderPreference = genderPreferenceSpinner.getSelectedItem().toString();
        List<String> musicPreference = musicPreferenceSpinner.getChipValues();
        String musicPreferenceString = musicPreference.toString();
        musicPreferenceString = musicPreferenceString.replace("[", "{");
        musicPreferenceString = musicPreferenceString.replace("]", "}");

        boolean cancel = false;

        if (mAuthTask != null) {
            return;
        }
        View focusView = null;

        if (genderPreference.isEmpty() || musicPreferenceString.isEmpty()){
            focusView = genderPreferenceSpinner;
            cancel = true;
        }
        if(cancel){
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new InitialHitchhikerProfileTask();
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


    public class InitialHitchhikerProfileTask extends AsyncTask<Void, Void, Boolean> {

        private String genderPreference;
        private String musicPreferenceString;

        InitialHitchhikerProfileTask() {
            genderPreference = genderPreferenceSpinner.getSelectedItem().toString();
            List<String> musicPreference = musicPreferenceSpinner.getChipValues();
            musicPreferenceString = musicPreference.toString();
            musicPreferenceString = musicPreferenceString.replace("[", "{");
            musicPreferenceString = musicPreferenceString.replace("]", "}");

        }
        private String setupURLConnection(){

            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("gender_preference", genderPreference);
                jsonObject.put("music_preference", musicPreferenceString);
                jsonObject.put("user_id", user_id);
                jsonObject.put("token", token);
            } catch(JSONException e){
                e.printStackTrace();
            }
            Connection connection= new Connection();
            connection.setConnection(Connection.INITIAL_HITCHIKER_PROFILE_SETUP, jsonObject);
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
                writeShared("hitchhiker");
                Intent intent = new Intent(InitialHitchhikerProfileActivity.this,Main2Activity.class);
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
