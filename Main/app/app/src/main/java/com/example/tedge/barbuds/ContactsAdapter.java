package com.example.tedge.barbuds;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<Friend> contactList;
    private List<Friend> contactListFiltered;
    private ContactsAdapterListener listener;
    private SharedPreferences myPrefs;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, id;
        public ImageView add;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.contact_list_new_friend_name);
            add = view.findViewById(R.id.contact_list_add_friend);
           // id = view.findViewById(R.id.phone);
           // thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(view1 -> {
                // send selected contact in callback
                listener.onContactSelected(contactListFiltered.get(getAdapterPosition()));
            });
        }
    }
    public ContactsAdapter(Context context, List<Friend> contactList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_new_friend, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Friend friend = contactListFiltered.get(position);
        holder.name.setText(friend.getName());
        holder.add.setOnClickListener(v -> {
            Log.e("Contact Added", friend.getName());
            myPrefs =  PreferenceManager.getDefaultSharedPreferences(context);
            String userID = myPrefs.getString("userID", "0");
            sendFriendRequest(userID, friend.getID());
            contactListFiltered.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<Friend> filteredList = new ArrayList<>();
                    for (Friend row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getID().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<Friend>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }
    private void sendFriendRequest(String userID, String friendID){

        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .add("friendID", friendID)
                .build();
        Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/send_friend_request.php")
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
    public interface ContactsAdapterListener {
        void onContactSelected(Friend friend);
    }


}
