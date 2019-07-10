package com.example.tedge.barbuds;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmailLog extends AppCompatActivity {
    //discuss security

    private ImageButton log_in;

    private TextView log_status;

    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_log);

        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        log_in = findViewById(R.id.email_log_login_button);

        log_status = findViewById(R.id.log_status);

        //OnClick listener for log in with email button
        log_in.setOnClickListener(view -> {
            EditText email = findViewById(R.id.email_log_email_field);
            EditText password = findViewById(R.id.email_log_password_field);
            final OkHttpClient client = new OkHttpClient();

            //connects to php to verify hashed passwords match and also email
            String username = email.getText() + "";
            String pass = password.getText() + "";
            //will need to change urlBuilder for megans php
            RequestBody formBody = new FormBody.Builder()
                    .add("email", username)
                    .add("pass", pass).build();

            Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/user_credential_verify.php")
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
                        EmailLog.this.runOnUiThread(() -> {

                            try {
                                String responseJ = responseData;
                                JSONArray response1 = new JSONArray(responseJ);
                                JSONObject user = response1.getJSONObject(0);
                                Log.e("userInfo", user.toString());
                                String type = "";
                                int uID = 0;
                                if (responseJ != "") {

                                    uID = Integer.parseInt(user.getString("uID"));
                                    type = user.getString("type");
                                }
                                if (uID != 0) {
                                    myPrefs.edit().putBoolean("logged_in", true).commit();
                                    if (type.equals("p")) {
                                        Intent intent = new Intent(EmailLog.this, MainActivity.class);
                                        myPrefs.edit().putString("userID", Integer.toString(uID)).commit();
                                        myPrefs.edit().putString("type", "p").commit();
                                        intent.putExtra("userID", uID);
                                        Log.d("loggedinEmail", myPrefs.getBoolean("logged_in", false) + "");
                                        startActivity(intent);
                                        EmailLog.this.finish();
                                    } else {
                                        myPrefs.edit().putString("type", "b").commit();
                                        Intent intent = new Intent(EmailLog.this, qrScanner.class);
                                        startActivity(intent);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Animation shake = AnimationUtils.loadAnimation(EmailLog.this, R.anim.shake);
                                log_in.startAnimation(shake);
                                Snackbar myBar = Snackbar.make(log_status, "INCORRECT EMAIL OR PASSWORD", Snackbar.LENGTH_SHORT);
                                myBar.show();
                            }
                        });
                    }
                }

            });
        });
    }


}
