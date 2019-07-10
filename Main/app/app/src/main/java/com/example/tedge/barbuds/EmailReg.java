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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

public class EmailReg extends AppCompatActivity {
    private ImageButton register;
    TextView check_message;
    private SharedPreferences myPrefs;
    private String instanceID;
    Bitmap bmp;
    CircleImageView ivGalImg;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_reg);
        register = findViewById(R.id.reg_create_account_button);
        TextView upload = findViewById(R.id.upload_photo);
        ivGalImg = findViewById(R.id.reg_profile_pic);

        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        upload.setOnClickListener(v -> {
            getImagePermissions();
        });

        register.setOnClickListener(view -> {

            EditText password = findViewById(R.id.register_password);
            EditText check_pass = findViewById(R.id.register_confirm_password);
            EditText nameView = findViewById(R.id.register_name);
            String pass = password.getText() + "";
            String pass_check = check_pass.getText() + "";

            if (pass.equals(pass_check)) {
                getInstanceID();
            } else {
                Animation shake = AnimationUtils.loadAnimation(EmailReg.this, R.anim.shake);
                register.startAnimation(shake);
                Snackbar snackbar = Snackbar.make(register, "PASSWORDS DONT MATCH", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    private void getInstanceID() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("firebase_fail", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    // Log and toast
                    String msg = token;
                    instanceID = msg;
                    Log.d("token", msg);

                    EditText email = findViewById(R.id.register_email);
                    EditText password = findViewById(R.id.register_password);
                    EditText phoneView = findViewById(R.id.register_phone);
                    EditText nameView = findViewById(R.id.register_name);
                    String username = email.getText() + "";
                    String pass = password.getText() + "";
                    String phone = phoneView.getText().toString();
                    List<String> nameInfo = Arrays.asList(nameView.getText().toString().split(" "));
                    String fname = nameInfo.get(0);
                    String lname = nameInfo.get(1);

                    registerUser(username, pass, phone, fname, lname);

                });
    }

    private void registerUser(String username, String pass, String phone, String fname, String lname) {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("email", username)
                .add("pass", pass)
                .add("instanceID", instanceID)
                .add("phone", phone)
                .add("fname", fname)
                .add("lname", lname)
                .build();
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/user_credential_data.php")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {


                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    final String responseData = response.body().string();


                    // Run view-related code back on the main thread
                    EmailReg.this.runOnUiThread(() -> {
                        try {
                            String responseJ = responseData;
                            if (!responseJ.equals("false")) {
                                myPrefs.edit().putBoolean("logged_in", true).apply();
                                myPrefs.edit().putString("userID", responseJ).commit();
                                userID = responseJ;
                                Log.e("registerResponse", responseJ);
                                try {
                                    send_profile(bitmapToString(bmp));
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                startActivity(new Intent(EmailReg.this, MainActivity.class));
                                finish();
                            } else {
                                Animation shake = AnimationUtils.loadAnimation(EmailReg.this, R.anim.shake);
                                register.startAnimation(shake);
                                Snackbar snackbar = Snackbar.make(register, "EMAIL ALREADY REGISTERED", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

        });
    }

    private void getImagePermissions(){
        Log.e("activityResult", "Ran");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            }, 1);

            Log.e("Permissions", "You don't have permission");
            return;
        }
        else{
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent intent)
    {

        Log.e("onActivityResult", "ran");
        if (intent != null && resultcode == RESULT_OK)
        {

            Uri selectedImage = intent.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            if(bmp != null && !bmp.isRecycled())
            {
                bmp = null;
            }

            bmp = BitmapFactory.decodeFile(filePath);
            ivGalImg.setBackgroundResource(0);
            ivGalImg.setImageBitmap(bmp);
        }
        else
        {
            Log.d("Status:", "Photopicker canceled");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent intent = EmailReg.this.getIntent();
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
