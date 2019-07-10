package com.example.tedge.barbuds;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
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

import static android.view.View.GONE;

public class Groups extends AppCompatActivity {


    String userID, barID;
    String groupID = "";
    Bitmap bitmap;
    ImageView createGroupButton, disbandGroupButton, barHeader;
    SharedPreferences myPrefs;
    TextView buttonText, barName, barCover;
    Boolean creator;
    int groupSize;
    LinearLayout groupOverlay, friendSection, dealSection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userID = myPrefs.getString("userID", "0");
        barID = myPrefs.getString("barID", "0");


        //Log.e("groupID", groupID);
        Log.e("userID", userID);
        Log.e("barID", barID);

        creator = false;

        LinearLayout profileTab = findViewById(R.id.groups_profile_tab);
        LinearLayout friendsTab = findViewById(R.id.groups_friends_tab);
        LinearLayout mapTab = findViewById(R.id.groups_map_tab);
        LinearLayout notificationsTab = findViewById(R.id.groups_notifications_tab);
        LinearLayout logout = findViewById(R.id.logout_view);
        createGroupButton = findViewById(R.id.create_group_button);
        buttonText = findViewById(R.id.disband_group_text);
        disbandGroupButton = findViewById(R.id.disband_group_button);
        barName = findViewById(R.id.barName);
        barCover = findViewById(R.id.bar_cover);
        barHeader = findViewById(R.id.bar_header);
        groupOverlay = findViewById(R.id.group_overlay);
        friendSection = findViewById(R.id.group_friends_section);
        dealSection = findViewById(R.id.group_deals_section);

        CircleImageView profilePic = findViewById(R.id.navigation_profile_pic);
        Glide.with(Groups.this)
                .load("https://cgi.sice.indiana.edu/~team48/photos/" + userID + ".png")
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.bb_profile_picture)
                .into(profilePic);


        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        final ImageView navigationButton = findViewById(R.id.navigationButton);

        navigationButton.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));

        profileTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(Groups.this, EditProfile.class);
            startActivity(intent);
            finish();
        });

        friendsTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(Groups.this, Friends.class);
            startActivity(intent);
            finish();
        });

        mapTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(Groups.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        notificationsTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(Groups.this, Notifications.class);
            startActivity(intent);
            finish();
        });

        logout.setOnClickListener(v -> {
            myPrefs.edit().putBoolean("logged_in", false).commit();
            myPrefs.edit().putString("userID", "0").commit();
            Intent intent = new Intent(Groups.this, LogIn.class);
            startActivity(intent);
            finish();
        });
        setGroupID();
        setName();
    }

    public void getBarInfo(String barID) {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("barID", barID)
                .build();
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/get_bar_info.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseJ = response.body().string();
                try {
                    JSONArray responseJSON = new JSONArray(responseJ);
                    JSONObject barInfo = (JSONObject) responseJSON.get(0);
                    String bar_name = barInfo.getString("bar_name");
                    String bar_cover = barInfo.getString("cover");
                    Groups.this.runOnUiThread(() -> {
                        barName.setText(bar_name);
                        barCover.setText("Cover: $" + bar_cover);
                        barName.setVisibility(View.VISIBLE);
                        barCover.setVisibility(View.VISIBLE);
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void createGroup(String userID) {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .build();
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/create_group.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                groupID = response.body().string();
                Log.e("createGroup", groupID);
                myPrefs.edit().putString("groupID", groupID).commit();
                Intent intent = new Intent(Groups.this, AddFriendGroup.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void makeFriendsList(String userID, String groupID) {
        Log.e("MakeFriendsList", "Started Request");
        Log.e("MakeFriendsList", groupID);
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("groupID", groupID)
                .add("userID", userID)
                .build();
        Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/get_group_members.php")
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
                        Log.e("makeFriendList", string);
                        final JSONArray responseData = new JSONArray(string);
                        Groups.this.runOnUiThread(() -> {
                            LinearLayout friendGallery = findViewById(R.id.group_friends_gallery);
                            try {
                                groupSize = responseData.length() + 1;
                                Log.e("groupSize", groupSize + "");
                                for (int i = 0; i < responseData.length(); i++) {
                                    JSONObject currFriend = (JSONObject) responseData.getJSONObject(i);
                                    String friendID = currFriend.get("patronID").toString();
                                    String fname = currFriend.get("fname").toString();
                                    String lname = currFriend.get("lname").toString();
                                    String fullName = fname + " " + lname;
                                    Log.e("fullName", fullName);
                                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                                    View view = inflater.inflate(R.layout.friend, friendGallery, false);

                                    TextView tv = view.findViewById(R.id.friend_name);
                                    tv.setText(fullName);

                                    ImageView image = view.findViewById(R.id.imageView);
                                    Log.e("friendURL", "https://cgi.sice.indiana.edu/~team48/photos/" + friendID + ".png");
                                    Glide.with(Groups.this)
                                            .load("https://cgi.sice.indiana.edu/~team48/photos/" + friendID + ".png")
                                            .error(R.drawable.bb_profile_picture)
                                            .skipMemoryCache(true)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .into(image);

                                    if (creator) {
                                        Log.e("creator", "You are the creator");
                                        view.setOnClickListener(v -> {
                                            Snackbar mySnackbar = Snackbar.make(view, "REMOVE FROM GROUP?", Snackbar.LENGTH_LONG);
                                            mySnackbar.setAction("REMOVE", v1 -> {
                                                removeFriend(friendID, groupID);
                                                view.setVisibility(GONE);
                                            });
                                            mySnackbar.show();

                                        });
                                    }
                                    friendGallery.addView(view);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    makeDealList(groupID);
                }
            }
        });
    }

    private void removeFriend(String friendID, String groupID) {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("groupID", groupID)
                .add("friendID", friendID)
                .build();

        Log.e("removeFriend", "Group ID:" + groupID);
        Log.e("removeFriend", "friendID:" + friendID);
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/delete_friend_group.php")
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
                    Log.e("removeFriend", responseData);
                }
            }
        });
    }

    private void makeDealList(String groupID) {
        Log.e("makeDealList", "Started Request");
        Log.e("makeDealList", groupID);
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("groupID", groupID)
                .build();
        Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/get_group_deals.php")
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
                        Log.e("makeDealList", string);
                        final JSONArray responseData = new JSONArray(string);
                        Groups.this.runOnUiThread(() -> {
                            LinearLayout dealGallery = findViewById(R.id.group_deals_gallery);
                            try {
                                for (int i = 0; i < responseData.length(); i++) {

                                    JSONObject currDeal = responseData.getJSONObject(i);

                                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                                    View view = inflater.inflate(R.layout.deal, dealGallery, false);

                                    String deal = currDeal.getString("deal_description");
                                    String dealGroupSize = currDeal.getString("group_size");
                                    String dealID = currDeal.getString("dealID");

                                    TextView tv = view.findViewById(R.id.deal_name);
                                    tv.setText(deal);

                                    ImageView image = view.findViewById(R.id.group_icon);
                                    image.setImageResource(R.drawable.bb_group_24px);


                                    TextView tv2 = view.findViewById(R.id.deal_size);
                                    tv2.setText(dealGroupSize);

                                    if (i % 2 == 1)
                                        view.setBackgroundColor(Color.argb(10, 255, 255, 255));
                                    Log.e("makeDealsGroupSize", groupSize + "");
                                    Log.e("dealGroupSize", dealGroupSize);
                                    if (groupSize < Integer.parseInt(dealGroupSize)) {
                                        tv.setAlpha(.5f);
                                        tv2.setAlpha(.5f);
                                        image.setAlpha(.5f);
                                    } else {
                                        view.setOnClickListener(v -> {
                                            Intent intent = new Intent(Groups.this, qrViewer.class);
                                            intent.putExtra("groupID", groupID);
                                            intent.putExtra("dealID", dealID);
                                            intent.putExtra("dealDescription", deal);
                                            startActivity(intent);
                                            finish();
                                        });
                                    }

                                    dealGallery.addView(view);
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

    private void setButtons(String groupID) {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("groupID", groupID)
                .build();
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/get_creator_id.php")
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
                    Groups.this.runOnUiThread(() -> {
                        if (responseData.equals(userID)) {
                            Log.e("creator", "you're the creator");
                            creator = true;
                            createGroupButton.setOnClickListener(v -> {
                                Intent intent = new Intent(Groups.this, AddFriendGroup.class);
                                startActivity(intent);
                                finish();
                            });
                            disbandGroupButton.setOnClickListener(v -> {
                                disbandGroup();
                                Intent intent = new Intent(Groups.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            createGroupButton.setVisibility(GONE);
                            buttonText.setText("LEAVE GROUP");
                            disbandGroupButton.setOnClickListener(v -> {
                                leaveGroup();
                                Intent intent = new Intent(Groups.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        }

                    });
                }
            }
        });
    }

    private void disbandGroup() {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("groupID", groupID)
                .build();

        Log.e("disbandGroup", "Group ID:" + groupID);
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/disband_group.php")
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
                    Log.e("disbandGroup", responseData);
                }
            }
        });
    }

    private void leaveGroup() {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("groupID", groupID)
                .add("userID", userID)
                .build();

        Log.e("leaveGroup", "Group ID:" + groupID);
        Log.e("leaveGroup", "User ID:" + userID);
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
                    Log.e("leaveGroup", responseData);
                }
            }
        });
    }

    private void setGroupID() {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .build();
        Log.e("setGroupID", "UserID:" + userID);
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/group_from_user.php")
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
                    Log.e("setGroupID", responseData);
                    groupID = responseData;

                    Groups.this.runOnUiThread(() -> {

                        if (groupID.equals("")) {
                            memberHideUI();

                        } else {
                            showUI();
                            setButtons(groupID);
                            getBarInfo(barID);
                            makeFriendsList(userID, groupID);
                        }

                    });
                }
            }
        });
    }

    private void memberHideUI() {
        createGroupButton.setOnClickListener(v -> createGroup(userID));
        disbandGroupButton.setVisibility(GONE);
        buttonText.setVisibility(GONE);
        barHeader.setVisibility(GONE);
        groupOverlay.setVisibility(GONE);
        friendSection.setVisibility(GONE);
        dealSection.setVisibility(GONE);
        TextView createGroupText = findViewById(R.id.create_group_text);
        createGroupText.setVisibility(View.VISIBLE);
        View v = findViewById(R.id.divider);
        v.setVisibility(GONE);
    }

    private void showUI(){
        disbandGroupButton.setVisibility(View.VISIBLE);
        buttonText.setVisibility(View.VISIBLE);
        barHeader.setVisibility(View.VISIBLE);
        groupOverlay.setVisibility(View.VISIBLE);
        friendSection.setVisibility(View.VISIBLE);
        dealSection.setVisibility(View.VISIBLE);
        TextView createGroupText = findViewById(R.id.create_group_text);
        createGroupText.setVisibility(View.GONE);
        View v = findViewById(R.id.divider);
        v.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Groups.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setName(){
        TextView nameView = findViewById(R.id.name);
        String name = myPrefs.getString("name", "");
        Log.e("name", name);
        nameView.setText(name);
    }
}
