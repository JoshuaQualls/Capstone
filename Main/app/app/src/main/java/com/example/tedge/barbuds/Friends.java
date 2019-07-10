package com.example.tedge.barbuds;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.havrylyuk.alphabetrecyclerview.BaseAlphabeticalAdapter;
import com.havrylyuk.alphabetrecyclerview.StickyHeadersBuilder;
import com.havrylyuk.alphabetrecyclerview.StickyHeadersItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Friends extends AppCompatActivity {
    ImageView add_new_friend;
    ArrayList<Friend> friends = new ArrayList<>();
    RecyclerView recyclerView;
    CurrentContactsAdapter adapter;
    SharedPreferences myPrefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        final ImageView navigationButton = findViewById(R.id.navigationButton);
        final LinearLayout groupTab = findViewById(R.id.friends_group_tab);
        final LinearLayout notificationsTab = findViewById(R.id.friends_notifications_tab);
        final LinearLayout profileTab = findViewById(R.id.friends_profile_tab);
        final LinearLayout mapTab = findViewById(R.id.friends_map_tab);
        final LinearLayout logout = findViewById(R.id.logout_view);

        add_new_friend = findViewById(R.id.add_new_friend);

        myPrefs =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String userID = myPrefs.getString("userID", "0");

        CircleImageView profilePic = findViewById(R.id.navigation_profile_pic);
        Glide.with(Friends.this)
                .load("https://cgi.sice.indiana.edu/~team48/photos/" + userID + ".png")
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.bb_profile_picture)
                .into(profilePic);

        navigationButton.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));

        profileTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(Friends.this, EditProfile.class);
            startActivity(intent);
            finish();
        });

        mapTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(Friends.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        groupTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(Friends.this, Groups.class);
            startActivity(intent);
            finish();

        });
        notificationsTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(Friends.this, Notifications.class);
            startActivity(intent);
            finish();
        });

        logout.setOnClickListener(v -> {
            myPrefs.edit().putBoolean("logged_in", false).commit();
            myPrefs.edit().putString("userID", "0").commit();
            Intent intent = new Intent(Friends.this, LogIn.class);
            startActivity(intent);
            finish();
        });


        initRecyclerView();
        fetchFriends();


        add_new_friend.setOnClickListener(v -> {
            Log.e("Clicked", "Click!");
            Intent intent = new Intent(Friends.this, AddFriend.class);
            startActivity(intent);
        });

        setName();

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Friends.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void fetchFriends(){
        try{
            final OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("userID", myPrefs.getString("userID", "0"))
                    .build();
            Request request = new Request.Builder().url("https://cgi.soic.indiana.edu/~team48/get_friends.php")
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final ArrayList<Friend> friends = new ArrayList<>();
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    } else {
                        final String responseData = response.body().string();

                        try {
                            final JSONArray responseJ = new JSONArray(responseData);
                            Friends.this.runOnUiThread(() -> {
                                for(int i = 0; i < responseJ.length(); i++){
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
        catch (Exception e){

        }

    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.friends_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CurrentContactsAdapter(this);
        adapter.setOnItemClickListener((BaseAlphabeticalAdapter.OnItemClickListener<Friend>) (position, entity) -> {
            //do work
        });
        StickyHeadersItemDecoration topStickyHeadersItemDecoration = new StickyHeadersBuilder()
                .setAdapter(adapter)
                .setRecyclerView(recyclerView)
                .build();
        recyclerView.addItemDecoration(topStickyHeadersItemDecoration);
    }
    private void setName(){
        TextView nameView = findViewById(R.id.name);
        String name = myPrefs.getString("name", "");
        Log.e("name", name);
        nameView.setText(name);
    }
}
