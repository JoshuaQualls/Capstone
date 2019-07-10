package com.example.tedge.barbuds;

import android.content.Intent;

import android.graphics.Color;

import android.content.SharedPreferences;

import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.location.Location;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class BarInfo extends AppCompatActivity {
    private float BAR_PROXIMITY = 35f;
    private float distance;
    TextView title, button_text;
    ImageView check_in_button;

    SharedPreferences myPrefs;
    Boolean checkedIn;
    private String barID, barName, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_info);
        check_in_button = findViewById(R.id.check_in_button);
        button_text = findViewById(R.id.check_in_text);
        Intent intent = getIntent();
        String barPosition = intent.getStringExtra("bar_position").replace("lat/lng: (", "").replace(")", "");
        String myPosition = intent.getStringExtra("my_position").replace("lat/lng: (", "").replace(")", "");

        barID = intent.getStringExtra("barID");
        barName = intent.getStringExtra("name");
        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userID = myPrefs.getString("userID", "0");



        //Set Bar Name
        TextView bn = findViewById(R.id.barName);
        bn.setText(barName);
        getCover();
        makeFriendsList();
        makeDealsList();

        String[] bar_location = barPosition.split(",");
        String[] my_location = myPosition.split(",");

        LatLng barLocation = new LatLng(Float.parseFloat(bar_location[0]), Float.parseFloat(bar_location[1]));

        LatLng myLocation = new LatLng(Float.parseFloat(my_location[0]), Float.parseFloat(my_location[1]));

        distance = distanceBetween(barLocation, myLocation);

        //Initialize onClick listeners for check_in_button
        intializeCheckinButton();

    }

    private void intializeCheckinButton(){
        checkedIn = myPrefs.getBoolean("checkedIn", false);
        Log.e("checkinButton", "Called");
        Log.e("checkinButton","Proximity: " + Boolean.toString(distance<=BAR_PROXIMITY ));
        Log.e("checkinButton", "Checked In: " + Boolean.toString(checkedIn));
        if(distance <= BAR_PROXIMITY && !checkedIn){
            Log.e("checkinButton", "Check me in!");
            check_in_button.setColorFilter(Color.argb(150, 0, 64, 128));
            button_text.setTextColor(Color.argb(150, 0, 0, 0));
            check_in_button.setOnClickListener(v -> {
                checkIn();
                check_in_button.setColorFilter(Color.argb(150, 0, 64, 128));
                button_text.setTextColor(Color.argb(150, 0, 0, 0));


            });
        }
        else if(distance <= BAR_PROXIMITY && checkedIn){
            Log.e("checkinButton","Already Checked In");
            button_text.setTextColor(Color.argb(150, 100, 100, 100));
            check_in_button.setOnClickListener(v -> {
                Animation shake = AnimationUtils.loadAnimation(BarInfo.this, R.anim.shake);
                check_in_button.startAnimation(shake);
                Snackbar myBar = Snackbar.make(button_text, "ALREADY CHECKED IN", Snackbar.LENGTH_SHORT );
                myBar.setAction("CHECK OUT?", v1 -> {
                    checkOut();
                    check_in_button.setColorFilter(Color.argb(250, 0, 64, 128));
                    button_text.setTextColor(getResources().getColor(R.color.black));
                });
                myBar.show();
            });
        }
        else{
            Log.e("checkinButton","Get closer!");
            button_text.setTextColor(Color.argb(150, 100, 100, 100));
            check_in_button.setOnClickListener(v -> {
                Animation shake = AnimationUtils.loadAnimation(BarInfo.this, R.anim.shake);
                check_in_button.startAnimation(shake);
                Snackbar myBar = Snackbar.make(button_text, "TOO FAR", Snackbar.LENGTH_SHORT );
                myBar.show();
            });
        }
    }

    private void checkIn() {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("barID", barID)
                .add("userID", userID)
                .build();
        Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/check_in.php")
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

                        String string = response.body().string();
                        Log.e("checkInResponse", string);

                        BarInfo.this.runOnUiThread(() -> {
                            myPrefs.edit().putString("barID", barID).commit();
                        });
                    }
                }
        });
        myPrefs.edit().putBoolean("checkedIn", true).commit();
        intializeCheckinButton();
    }

    private void checkOut(){
        final OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .build();
        Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/check_out.php")
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

                    String string = response.body().string();
                    BarInfo.this.runOnUiThread(() -> {
                        myPrefs.edit().putString("barID", "0").commit();
                    });
                }
            }
        });
        myPrefs.edit().putBoolean("checkedIn", false).commit();
        intializeCheckinButton();
    }

    private float distanceBetween(LatLng latLng1, LatLng latLng2) {

        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
        Location loc2 = new Location(LocationManager.GPS_PROVIDER);

        loc1.setLatitude(latLng1.latitude);
        loc1.setLongitude(latLng1.longitude);

        loc2.setLatitude(latLng2.latitude);
        loc2.setLongitude(latLng2.longitude);


        return loc1.distanceTo(loc2);
    }

    public void makeFriendsList(){
        Log.e("MakeFriendsList", "Started Request");
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("barID", barID)
                .add("userID", userID)
                .build();
        Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/get_friends_bar.php")
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
                    try {
                        String string = response.body().string();
                        final JSONArray responseData = new JSONArray(string);
                        BarInfo.this.runOnUiThread(() -> {
                            LinearLayout friendGallery = findViewById(R.id.bar_info_friends_gallery);
                            try {
                                for (int i = 0; i < responseData.length(); i++) {
                                    JSONObject currFriend =  (JSONObject) responseData.getJSONObject(i);
                                    int friendID = Integer.parseInt(currFriend.get("friend_2").toString());
                                    String fname = currFriend.get("fname").toString();
                                    String lname = currFriend.get("lname").toString();
                                    String fullName = fname + " " + lname;
                                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                                    View view = inflater.inflate(R.layout.friend, friendGallery, false);

                                    TextView tv = view.findViewById(R.id.friend_name);
                                    tv.setText(fullName);

                                    ImageView image = view.findViewById(R.id.imageView);
                                    Glide.with(BarInfo.this)
                                            .load("https://cgi.sice.indiana.edu/~team48/photos/" + friendID + ".png")
                                            .skipMemoryCache(true)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .error(R.drawable.bb_profile_picture)
                                            .into(image);
                                    friendGallery.addView(view);
                                    }
                                    }
                             catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (JSONException e) {
                        BarInfo.this.runOnUiThread(() -> {
                            LinearLayout no_friends = findViewById(R.id.bar_info_no_friends);
                            HorizontalScrollView friends_sv = findViewById(R.id.bar_info_friends_list);

                            friends_sv.setVisibility(View.GONE);
                            no_friends.setVisibility(View.VISIBLE);
                        });
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void makeDealsList(){
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("barID", barID)
                .build();
        Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/get_deals.php")
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
                    try {
                        String string = response.body().string();
                        final JSONArray responseData = new JSONArray(string);

                        BarInfo.this.runOnUiThread(() -> {
                            LinearLayout dealGallery = findViewById(R.id.bar_info_deals_gallery);
                            try {
                                for (int i = 0; i < responseData.length(); i++) {
                                    JSONObject currDeal = responseData.getJSONObject(i);

                                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                                    View view = inflater.inflate(R.layout.deal, dealGallery, false);

                                    String deal = currDeal.getString("deal_description");
                                    String groupSize = currDeal.getString("group_size");

                                    TextView tv = view.findViewById(R.id.deal_name);
                                    tv.setText( deal );

                                    ImageView image = view.findViewById(R.id.group_icon);
                                    image.setImageResource(R.drawable.bb_group_24px);


                                    TextView tv2 = view.findViewById(R.id.deal_size);
                                    tv2.setText(groupSize);

                                    if(i%2 == 1) view.setBackgroundColor(Color.argb(10, 255,255,255));

                                    dealGallery.addView(view);
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (JSONException e) {
                        BarInfo.this.runOnUiThread(() -> {
                            ScrollView deals_sv = findViewById(R.id.deals_scroll_view);
                            TextView no_deals = findViewById(R.id.bar_info_no_deals);

                            deals_sv.setVisibility(View.GONE);
                            no_deals.setVisibility(View.VISIBLE);
                        });

                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getCover(){
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("barID", barID)
                .build();
        Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/get_bar_cover.php")
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

                        String string = response.body().string();
                        BarInfo.this.runOnUiThread(() -> {
                            TextView cover = findViewById(R.id.bar_info_cover);
                            cover.setText("Cover: $" + string);
                        });


                    }
                }
        });

    }
}
