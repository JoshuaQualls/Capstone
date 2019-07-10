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
import android.widget.TextView;

import com.havrylyuk.alphabetrecyclerview.BaseAlphabeticalAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CurrentContactsAdapter extends BaseAlphabeticalAdapter<Friend> {
 SharedPreferences myPrefs;
 Context context;

    public CurrentContactsAdapter(Context context) {
        super(context);
        this.context = context;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textView, delete;
        public ItemViewHolder(View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.contact_list_friend_name);
            this.delete = itemView.findViewById(R.id.contact_list_delete_friend);
        }
    }
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        public HeaderViewHolder(View headerView) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_friend, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemViewHolder viewHolder= (ItemViewHolder) holder;
        viewHolder.textView.setText(entityList.get(position).getName());
        ((ItemViewHolder) holder).delete.setOnClickListener(v -> {
            Log.e("deleteFriend", "clicked");
            Snackbar mySnackbar = Snackbar.make(viewHolder.delete, "ARE YOU SURE?", Snackbar.LENGTH_LONG);
            mySnackbar.setAction("YES", v1 -> {
                myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                String userID = myPrefs.getString("userID", "0");
                Log.e("friendIDRemove",entityList.get(viewHolder.getAdapterPosition()).getID());
                removeFriend(userID ,entityList.get(viewHolder.getAdapterPosition()).getID());
                entityList.remove(viewHolder.getAdapterPosition());
                notifyItemRemoved(viewHolder.getAdapterPosition());

            });
            mySnackbar.show();
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_header, parent, false);
        return new HeaderViewHolder(itemView);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
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

    private void removeFriend(String userID, String friendID){
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .add("friendID", friendID)
                .build();
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/delete_friend.php")
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
                    Log.e("removeFriendResponse",responseData);
                }
            }
        });
    }

}
