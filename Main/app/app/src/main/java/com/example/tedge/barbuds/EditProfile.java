package com.example.tedge.barbuds;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProfile extends AppCompatActivity {

    private ImageView saveButton;
    String userID;
    Bitmap bmp;
    CircleImageView profilePicture, navProfilePic;
    TextView upload, nameView;
    LinearLayout groupsTab, friendTab, notificationTab, mapTab, logout;

    EditText editName, editPhone, editEmail, editPassword, editPasswordConfirm;

    SharedPreferences myPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profilePicture = findViewById(R.id.edit_profile_pic);
        navProfilePic = findViewById(R.id.navigation_profile_pic);
        upload = findViewById(R.id.upload_photo);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ImageView navigationButton = findViewById(R.id.navigationButton);
        ImageView editButton = findViewById(R.id.edit);

        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userID = myPrefs.getString("userID", "0");

        groupsTab = findViewById(R.id.edit_group_tab);
        friendTab = findViewById(R.id.edit_friends_tab);
        notificationTab = findViewById(R.id.edit_notifications_tab);
        mapTab = findViewById(R.id.edit_map_tab);
        logout = findViewById(R.id.logout_view);

        editName = findViewById(R.id.edit_name);
        editPhone = findViewById(R.id.edit_phone);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editPasswordConfirm = findViewById(R.id.edit_confirm_password);

        nameView = findViewById(R.id.name);

        saveButton = findViewById(R.id.edit_save_button);
        ConstraintLayout saveView = findViewById(R.id.edit_save);


        navigationButton.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));

        editButton.setOnClickListener(v -> {
            editName.setEnabled(true);
            editPhone.setEnabled(true);
            editEmail.setEnabled(true);
            editPassword.setEnabled(true);
            editPasswordConfirm.setEnabled(true);

            saveView.setVisibility(View.VISIBLE);

            upload.setVisibility(View.VISIBLE);
            upload.setOnClickListener(v1 -> getImagePermissions());

        });

        saveButton.setOnClickListener(v -> {
            try {
                String bitmap = bitmapToString(bmp);
                send_profile(bitmap);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            updateInfo();
        });


        mapTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(EditProfile.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        groupsTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(EditProfile.this, Groups.class);
            startActivity(intent);
            finish();
        });

        friendTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(EditProfile.this, Friends.class);
            startActivity(intent);
            finish();
        });

        notificationTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(EditProfile.this, Notifications.class);
            startActivity(intent);
            finish();
        });

        logout.setOnClickListener(v -> {
            myPrefs.edit().putBoolean("logged_in", false).commit();
            myPrefs.edit().putString("userID", "0").commit();
            Intent intent = new Intent(EditProfile.this, LogIn.class);
            startActivity(intent);
            finish();
        });


        Glide.with(EditProfile.this)
                .load("https://cgi.sice.indiana.edu/~team48/photos/" + userID + ".png")
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.bb_profile_picture)
                .into(navProfilePic);
        Glide.with(EditProfile.this)
                .load("https://cgi.sice.indiana.edu/~team48/photos/" + userID + ".png")
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.bb_profile_picture)
                .into(profilePicture);
        setInfo();


    }

    private void getImagePermissions() {
        Log.e("activityResult", "Ran");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            }, 1);

            Log.e("Permissions", "You don't have permission");
            return;
        } else {
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent intent) {

        Log.e("onActivityResult", "ran");
        if (intent != null && resultcode == RESULT_OK) {

            Uri selectedImage = intent.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            if (bmp != null && !bmp.isRecycled()) {
                bmp = null;
            }

            bmp = BitmapFactory.decodeFile(filePath);
            profilePicture.setBackgroundResource(0);
            profilePicture.setImageBitmap(bmp);
        } else {
            Log.d("Status:", "Photopicker canceled");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent intent = EditProfile.this.getIntent();
        //Regardless of whether the permission was granted we carry on.
        //If perms have been denied then the app must cater for it
        Log.e("PermissionsResult", "called");
        if (requestCode == 1) {
            Log.e("PermissionResult", "granted, execute");
            //Permission Granted
            //Do your work here
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);
//Perform operations here only which requires permission

        }
    }

    private void setInfo() {

        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .build();
        Log.e("setInfoUserID", userID);
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/get_user_info.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    try {
                        final String responseData = response.body().string();
                        Log.e("setGroupID", responseData);

                        final JSONArray responseArray = new JSONArray(responseData);
                        final JSONObject responseJ = responseArray.getJSONObject(0);
                        String fname = responseJ.getString("fname");
                        String lname = responseJ.getString("lname");
                        String fullName = fname + " " + lname;
                        String phone = responseJ.getString("phone");
                        String email = responseJ.getString("user_email");
                        String password = responseJ.getString("user_pass");

                        EditProfile.this.runOnUiThread(() -> {
                            nameView.setText(fullName);
                            editName.setText(fullName);
                            editPhone.setText(phone);
                            editEmail.setText(email);
                            editPassword.setText(password);
                            editPasswordConfirm.setText(password);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }
        });
    }

    private void updateInfo(){
        String fullName = editName.getText().toString();
        String phone = editPhone.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String passwordConfirm = editPasswordConfirm.getText().toString();
        List<String> name = Arrays.asList(fullName.split(" "));
        String fname = name.get(0);
        String lname = name.get(1);


        if(password.equals(passwordConfirm)) {

            final OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("userID", userID)
                    .add("phone", phone)
                    .add("email", email)
                    .add("password", password)
                    .add("fname", fname)
                    .add("lname", lname)
                    .build();
            Log.e("setGroupID", "UserID:" + userID);
            Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/update_user_info.php")
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    } else {
                        Log.e("updateInfo", response.body().string());
                        Intent intent = new Intent(EditProfile.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
        else{
            Snackbar snackbar = Snackbar.make(findViewById(R.id.edit_profile_main_layout), "PASSWORDS MUST MATCH", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProfile.this, MainActivity.class);
        startActivity(intent);
    }

    private String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream bacs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bacs);
        byte[] b = bacs.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private void send_profile(String bitmap){
        final OkHttpClient client = new OkHttpClient();
        Log.e("sendProfileUserID", userID);
        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .add("image", bitmap)
                .build();
        Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/uploadfile.php")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("sendProfileResponse", response.body().string());
            }
        });
    }

}
