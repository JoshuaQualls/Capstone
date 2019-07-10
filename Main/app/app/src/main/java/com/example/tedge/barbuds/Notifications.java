package com.example.tedge.barbuds;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Notifications extends AppCompatActivity {
    SharedPreferences myPrefs;

    String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);


        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ImageView navigationButton = findViewById(R.id.navigationButton);

        LinearLayout profileTab = findViewById(R.id.notifications_profile_tab);
        LinearLayout friendsTab = findViewById(R.id.notifications_friends_tab);
        LinearLayout mapTab = findViewById(R.id.notifications_map_tab);
        LinearLayout groupsTab = findViewById(R.id.notifications_group_tab);
        LinearLayout logout = findViewById(R.id.logout_view);


        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userID = myPrefs.getString("userID", "0");


        CircleImageView profilePic = findViewById(R.id.navigation_profile_pic);
        Glide.with(Notifications.this)
                .load("https://cgi.sice.indiana.edu/~team48/photos/" + userID + ".png")
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.bb_profile_picture)
                .into(profilePic);

        navigationButton.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));

        profileTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(Notifications.this, EditProfile.class);
            startActivity(intent);
            finish();
        });

        friendsTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(Notifications.this, Friends.class);
            startActivity(intent);
            finish();
        });

        mapTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(Notifications.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        groupsTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(Notifications.this, Groups.class);
            startActivity(intent);
            finish();
        });

        logout.setOnClickListener(v -> {
            myPrefs.edit().putBoolean("logged_in", false).commit();
            myPrefs.edit().putString("userID", "0").commit();
            Intent intent = new Intent(Notifications.this, LogIn.class);
            startActivity(intent);
            finish();
        });

        getPendingFriendRequest();
        getPendingGroupRequest();
        setName();

    }

    private void getPendingFriendRequest(){
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .build();
        Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/get_friend_requests.php")
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
                        String responseJ= response.body().string();
                        Log.e("addFriendResponse", responseJ);
                        final JSONArray responseArray = new JSONArray(responseJ);
                        final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        Notifications.this.runOnUiThread(() -> {
                                JSONObject request1 = null;
                                try {
                                    for(int i = 0; i<responseArray.length();i++) {
                                    request1 = responseArray.getJSONObject(i);
                                        final String friendID = request1.getString("friend_1");
                                        String fname = request1.getString("fname");
                                        String lname = request1.getString("lname");
                                        String fullName = fname + " " + lname;
                                        LinearLayout friendsNotificationGallery = findViewById(R.id.friends_notification_gallery);
                                        final View view = inflater.inflate(R.layout.notification, friendsNotificationGallery, false);

                                        TextView tv = view.findViewById(R.id.notification_name);
                                        TextView accept = view.findViewById(R.id.accept_request);
                                        TextView decline = view.findViewById(R.id.decline_request);
                                        tv.setText(fullName + " wants to be your friend." );
                                        accept.setOnClickListener(v -> {
                                            acceptFriendRequest(userID, friendID);
                                            view.setVisibility(View.GONE);
                                        });
                                        decline.setOnClickListener(v -> {
                                            declineFriendRequest(userID, friendID);
                                            view.setVisibility(View.GONE);
                                        });

                                        ImageView image = view.findViewById(R.id.notification_image);
                                        Glide.with(Notifications.this)
                                                .load("https://cgi.sice.indiana.edu/~team48/photos/" + friendID + ".png")
                                                .skipMemoryCache(true)
                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .error(R.drawable.bb_profile_picture)
                                                .into(image);

                                        if(i%2 == 1) view.setBackgroundColor(Color.argb(10, 255,255,255));

                                        friendsNotificationGallery.addView(view);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void acceptFriendRequest(String userID, String friendID){
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .add("friendID", friendID)
                .build();
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/accept_friend_request.php")
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
                    final String responseData = response.body().string();
                    Log.e("acceptFRequest", responseData);
                }
            }
        });
    }
    private void declineFriendRequest(String userID, String friendID){
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .add("friendID", friendID)
                .build();
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/decline_friend_request.php")
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
                    final String responseData = response.body().string();
                    Log.e("acceptFRequest", responseData);
                }
            }
        });
    }

    private void getPendingGroupRequest(){
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .build();
        Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/get_group_requests.php")
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
                        String responseJ= response.body().string();
                        Log.e("addGroupResponse", responseJ);
                        final JSONArray responseArray = new JSONArray(responseJ);
                        final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        final String userID = myPrefs.getString("userID", "0");
                        Notifications.this.runOnUiThread(() -> {
                            JSONObject request1;
                            try {
                                for(int i = 0; i<responseArray.length();i++) {
                                    request1 = responseArray.getJSONObject(i);
                                    final String groupID = request1.getString("groupID");
                                    String friendID = request1.getString("patronID");
                                    String fname = request1.getString("fname");
                                    String lname = request1.getString("lname");
                                    String fullName = fname + " " + lname;
                                    LinearLayout groupsNotificationGallery = findViewById(R.id.groups_notification_gallery);
                                    final View view = inflater.inflate(R.layout.notification, groupsNotificationGallery, false);

                                    TextView tv = view.findViewById(R.id.notification_name);
                                    TextView accept = view.findViewById(R.id.accept_request);
                                    TextView decline = view.findViewById(R.id.decline_request);
                                    tv.setText(fullName + " wants to group up." );
                                    accept.setOnClickListener(v -> {
                                        acceptGroupRequest(userID, groupID);
                                        view.setVisibility(View.GONE);
                                    });
                                    decline.setOnClickListener(v -> {
                                        declineGroupRequest(userID, groupID);
                                        view.setVisibility(View.GONE);
                                    });

                                    ImageView image = view.findViewById(R.id.notification_image);
                                    Glide.with(Notifications.this)
                                            .load("https://cgi.sice.indiana.edu/~team48/photos/" + friendID + ".png")
                                            .skipMemoryCache(true)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .error(R.drawable.bb_profile_picture)
                                            .into(image);

                                    if(i%2 == 1) view.setBackgroundColor(Color.argb(10, 255,255,255));
                                    groupsNotificationGallery.addView(view);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
    private void acceptGroupRequest(String userID, String groupID){
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .add("groupID", groupID)
                .build();
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/accept_group_request.php")
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
                    final String responseData = response.body().string();
                    Log.e("acceptGRequest", responseData);
                }
            }
        });
    }
    private void declineGroupRequest(String userID, String groupID){
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .add("groupID", groupID)
                .build();
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/delete_group_request.php")
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
                    final String responseData = response.body().string();
                    Log.e("DeclineGRequest", responseData);
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Notifications.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setName(){
        TextView name = findViewById(R.id.name);
        name.setText(myPrefs.getString("name", ""));
    }

}
