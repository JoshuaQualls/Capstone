package com.example.tedge.barbuds;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.havrylyuk.alphabetrecyclerview.BaseAlphabeticalAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CurrentMemberAdapter extends BaseAlphabeticalAdapter<Friend> {
    SharedPreferences myPrefs;
    Context context;

    public CurrentMemberAdapter(Context context) {
        super(context);
        this.context = context;
    }

    public class GroupItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView add;
        public GroupItemViewHolder(View itemView) {
            super(itemView);
            Log.e("ItemViewHolder", "Create View Holder");
            this.textView = itemView.findViewById(R.id.group_friend_name);
            this.add = itemView.findViewById(R.id.group_add_friend);
        }
    }
    public static class GroupHeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        public GroupHeaderViewHolder(View headerView) {
            super(headerView);
            this.title = headerView.findViewById(R.id.header_text);
        }
    }


    @Override
    public void initHeadersLetters() {
        SortedSet<Character> characters = new TreeSet<>();
        for (Friend f : entityList) {
            characters.add(f.getName().charAt(0));
        }
        Log.e("Headers",characters.toString());
        setHeadersLetters(characters);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_add_friend, parent, false);
        return new GroupItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final GroupItemViewHolder viewHolder= (GroupItemViewHolder) holder;
        String name = entityList.get(position).getName();
        viewHolder.textView.setText(name);
        ((GroupItemViewHolder) holder).add.setOnClickListener(v -> {
            Log.e("Name", name);
            Log.e("ID", entityList.get(position).getID());
            myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            String groupID = myPrefs.getString("groupID", "0");
            addFriend(entityList.get(position).getID(), groupID );
            entityList.remove(viewHolder.getAdapterPosition());
            notifyDataSetChanged();

            });



    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_header, parent, false);
        return new GroupHeaderViewHolder(itemView);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        GroupHeaderViewHolder headerViewHolder = (GroupHeaderViewHolder) viewHolder;
        String itemTitle = String.valueOf(entityList.get(position).getName().charAt(0));
        headerViewHolder.title.setText(itemTitle);

    }
    @Override
    public void sortList() {
        Collections.sort(entityList);
        Log.e("sortedList", entityList.toString());
    }

    @Override
    public long getHeaderId(int position) {

        return entityList.get(position).getName().charAt(0);
    }


    @Override
    public long getItemId(int position) {

        return position;
    }

    private void addFriend(String friendID, String groupID){
        myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userID = myPrefs.getString("userID", "0");

        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("groupID", groupID)
                .add("friendID", friendID)
                .add("userID", userID)
                .build();
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/add_friend_group.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseJ= response.body().string();
                    Log.e("addFriendResponse", responseJ);


                    JSONArray responseArray = new JSONArray(responseJ);
                    JSONObject friend = responseArray.getJSONObject(0);
                    String firebaseID = friend.getString("firebaseID");
                    String fname = friend.getString("fname");
                    String lname = friend.getString("lname");
                    String fullName = fname + " " + lname;

                    sendRequest(makeNotificationJSON(firebaseID, fullName));
                } catch (JSONException e) {
                    e.printStackTrace();
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
        info.put("title", "Group Request");   // Notification title
        info.put("body", name + " " + "wants you to join their group"); // Notification body
        info.put("sound", "mySound"); // Notification sound
        json.put("notification", info);
        json.put("to", firebaseID);
        return json;
    }

}
