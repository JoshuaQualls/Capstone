package com.example.tedge.barbuds;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.havrylyuk.alphabetrecyclerview.StickyHeadersBuilder;
import com.havrylyuk.alphabetrecyclerview.StickyHeadersItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddFriendGroup extends AppCompatActivity {
    CurrentMemberAdapter adapter;
    RecyclerView recyclerView;
    SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_group);


        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userID = myPrefs.getString("userID", "0");
        String barID = myPrefs.getString("barID", "0");
        Log.e("userID", userID);
        initRecyclerView();
        getAvailableFriends(userID, barID);
    }



    public void getAvailableFriends(String userID, String barID){
        Log.e("MakeFriendsList", "Started Request");
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("barID", barID)
                .add("userID", userID)
                .build();
        Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/get_available_friends.php")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final ArrayList<Friend> friends = new ArrayList<>();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    try {
                        final String responseData = response.body().string();
                        Log.e( "onResponseCreateGroup: ", responseData );
                        final JSONArray responseJ = new JSONArray(responseData);
                        AddFriendGroup.this.runOnUiThread(() -> {
                            for (int i = 0; i < responseJ.length(); i++) {
                                JSONObject cFriend = null;
                                try {
                                    cFriend = responseJ.getJSONObject(i);
                                    String id = cFriend.getString("friend_2");
                                    String fname = cFriend.getString("fname");
                                    String lname = cFriend.getString("lname");
                                    String fullName = fname + " " + lname;

                                    Friend friend = new Friend(fullName, id);

                                    friends.add(friend);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            adapter.setData(friends);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            });
        }

    public void sendRequest(JSONObject notification){
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        final OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, notification.toString());
        Request request = new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyAC2zMH_h39PwF0SmluJyqecq7CLtJNfrs")
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

                }
            }

        });
    }
    public JSONObject makeNotificationJSON(String firebaseID, String name) throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject info = new JSONObject();
        info.put("title", "Friend Request");   // Notification title
        info.put("body", name + " " + "wants to be friends"); // Notification body
        info.put("sound", "mySound"); // Notification sound
        json.put("notification", info);
        json.put("to",firebaseID);

        return json;
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.friends_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CurrentMemberAdapter(this);
        StickyHeadersItemDecoration topStickyHeadersItemDecoration = new StickyHeadersBuilder()
                .setAdapter(adapter)
                .setRecyclerView(recyclerView)
                .build();
        recyclerView.addItemDecoration(topStickyHeadersItemDecoration);
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(AddFriendGroup.this, Groups.class);
        startActivity(intent);
    }
}
