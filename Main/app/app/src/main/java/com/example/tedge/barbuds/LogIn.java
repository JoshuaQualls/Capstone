package com.example.tedge.barbuds;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import android.widget.VideoView;


public class LogIn extends AppCompatActivity  {

    private ImageButton fb_log_button;
    private CallbackManager fb_callback;
    private static final String EMAIL = "email";
    private String email; //Need fb permissions
    private String id;
    private String name;
    private String picture;
    private ImageButton log_in_button;
    private ImageButton register_button;

    private SharedPreferences myPrefs;




    //Need to get fb permissions for more data.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);



        //Keep User logged in
        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean bool = myPrefs.getBoolean("logged_in", false);
        String type = myPrefs.getString("type", "x");
        Log.d("loggedin", bool + "");
        if(bool && type.equals("p") ){
            Intent intent = new Intent(LogIn.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(bool && type.equals("b")){
            Intent intent = new Intent(LogIn.this, qrScanner.class);
            startActivity(intent);
            finish();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = "fcm_default_channel";
            String channelName = "Friends";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        //Instantiating fb button and callback manager objects for user verification
        fb_callback = CallbackManager.Factory.create();
        fb_log_button = findViewById(R.id.fb_login_button);


        //Pulling buttons for controller use
        log_in_button = findViewById(R.id.log_in_button);
        register_button = findViewById(R.id.register_button);

        //Sets the permissions requested by the app for the facebook user
        //Public Profile permission do not require approval from facebook

        // If you are using in a fragment, call loginButton.setFragment(this);

        //Tagging the callback manager(handles response) to facebook log in request button
        LoginManager.getInstance().registerCallback(fb_callback,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //The users specific access token used to query their information and log in status.
                        //will be used to check if user is logged in (partially)
                        String accesstoken = loginResult.getAccessToken().getToken();
                        myPrefs.edit().putBoolean("logged_in", true).apply();
                        //Test to see that access token is reached
                        //System.out.println(accesstoken);

                        //The request for user profile information
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //Prints response object to the Logcat
                                Log.d("response", response.toString());

                                //Pulls and parses data of the returned JSONObject
                                getData(object);

                                //opens profile edit activity
                                openProfEdit();

                            }
                        });
                        //Puts query parameters into bundle
                        //Adds parameters to query request
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        //OnClick listener for log in with email button
        log_in_button.setOnClickListener(view -> openEmailLog());

        //OnClick listener for register with email button
        register_button.setOnClickListener(view -> openEmailReg());

        //Listener for log in with facebook
        fb_log_button.setOnClickListener(view -> LoginManager.getInstance().logInWithReadPermissions(LogIn.this, Arrays.asList("public_profile", "email")));
    }


    //Pulls and parses fb query data
    private void getData(JSONObject object) {
        try {
            //Should be link to profile pic (can use PICASSO API to store display pic in app memory?)
            picture = "https://graph.facebook.com/" +object.getString("id") + "/picture?width=250&height=250";

            //sets JSON data fields to local memory
            if (object.has("email")) {
                email = object.getString("email");
            }
            name = object.getString("name");
            id = object.getString("id");

            Log.e("facebookName", name);
            Log.e("facebookEmail", email);
        }  catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fb_callback.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //opens edit profile activity
    private void openProfEdit(){
        //Packages facebook user data into intent to pass data between activities
        //Edit profile activity should save data to database
        Intent intent = new Intent(this, EditProfile.class);
        intent.putExtra("name", name);
        intent.putExtra("id", id);
        intent.putExtra("picture", picture);
        startActivity(intent);
        this.finish();
    }

    //opens email log in activity
    private void openEmailLog(){
        Intent intent = new Intent(this, EmailLog.class);
        startActivity(intent);
        this.finish();
    }
    //opens email register activity
    private void openEmailReg(){
        Intent intent = new Intent(this, EmailReg.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){

    }
}
