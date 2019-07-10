package com.example.tedge.barbuds;

import android.Manifest;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;


import com.android.volley.toolbox.StringRequest;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddFriend extends AppCompatActivity /*implements ContactsAdapter.ContactsAdapterListener */ {
    private static final String TAG = AddFriend.class.getSimpleName();
    private RecyclerView recyclerView;
    private TextView tv;
    private List<Friend> contactList;
    private ContactsAdapter mAdapter;
    private SearchView searchView;
    private ImageView addContacts;
    private SharedPreferences myPrefs;
    private static final int PERMISSION_REQUEST_CODE = 7001;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);



        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        pb = findViewById(R.id.addContactsProgressBar);

        addContacts = findViewById(R.id.add_from_contacts);
        addContacts.setOnClickListener(v -> {
            searchView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);
            addContacts.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }

                    runOnUiThread(() -> getContactList());
                }
            };
            thread.start();


        });

        recyclerView = findViewById(R.id.new_friend_RV);
        tv = findViewById(R.id.add_friend_text);
        contactList = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fetchContacts();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = findViewById(R.id.add_friend_search_view);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tv.setVisibility(View.GONE);
                addContacts.setVisibility(View.GONE);
                if (!newText.equals("")) {
                    recyclerView.setVisibility(View.VISIBLE);
                    mAdapter.getFilter().filter(newText);
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
                return false;
            }
        });
        searchView.setOnCloseListener(() -> {
            recyclerView.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            addContacts.setVisibility(View.VISIBLE);
            return false;
        });
    }

    private void fetchContacts() {

        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, "http://cgi.sice.indiana.edu/~team48/get_people.php",
                response -> {
                    try {
                        JSONArray people = new JSONArray(response);

                        for (int i = 0; i < people.length(); i++) {
                            JSONObject person = people.getJSONObject(i);
                            int id = person.getInt("patronID");
                            String fname = person.getString("fname");
                            String lname = person.getString("lname");
                            String fullName = fname + " " + lname;
                            Friend friend = new Friend(fullName, Integer.toString(id));
                            contactList.add(friend);
                        }
                        mAdapter = new ContactsAdapter(AddFriend.this, contactList,
                                friend -> Log.e("Person_name", friend.getName()));
                        recyclerView.setAdapter(mAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

                }) {
            @Override
            public Map<String, String> getParams() {
                String id = myPrefs.getString("userID", "0");
                Map<String, String> params = new HashMap<>();
                params.put("uID", id);
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(request);

    }

    private void getContactList() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS,
            }, PERMISSION_REQUEST_CODE);
            return;
        } else {
            ContentResolver cr = this.getContentResolver(); //Activity/Application android.content.Context
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            ArrayList<String> alContacts = new ArrayList<String>();
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            alContacts.add(contactNumber);
                            break;
                        }
                        pCur.close();


                    }
                } while (cursor.moveToNext());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Stream ac = alContacts.stream().map(this::normalizePhoneNumber);
                alContacts = (ArrayList<String>)ac
                        .collect(Collectors
                                .toCollection(ArrayList::new));
                alContacts.removeIf(a->a.length()<7);
            }
            Log.e("alContacts", alContacts.size() + "");
            JSONObject JSONcontacts = new JSONObject();

            for (int i = 0; i < alContacts.size(); i++) {
                try {
                    JSONcontacts.put("Count" + Integer.toString(i + 1),  alContacts.get(i));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONObject result = new JSONObject();
            try {
                result.put("userID",  myPrefs.getString("userID", "0"));
                result.put("contact", JSONcontacts);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            Log.e("getContactList: ", result.toString());
            sendContactRequests(result);

        }
    }

    private void sendContactRequests(JSONObject json){
        final OkHttpClient client = new OkHttpClient();
        Log.e(TAG, "Called" );
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // put your json here
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url("http://cgi.sice.indiana.edu/~team48/request_contacts.php")
                .post(body)
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
                }
                else {
                Log.e("contactsResponse", response.body().string());

                AddFriend.this.runOnUiThread(() -> {
                    pb.setVisibility(View.GONE);
                    searchView.setVisibility(View.VISIBLE);
                    addContacts.setVisibility(View.VISIBLE);
                    tv.setVisibility(View.VISIBLE);
                    Snackbar snackbar = Snackbar.make(recyclerView, "CONTACTS ADDED", Snackbar.LENGTH_LONG);
                    snackbar.show();
                });
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Granted
                //Do your work here
                ContentResolver cr = this.getContentResolver(); //Activity/Application android.content.Context
                Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                if (cursor.moveToFirst()) {
                    ArrayList<String> alContacts = new ArrayList<String>();
                    do {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            while (pCur.moveToNext()) {
                                String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                alContacts.add(contactNumber);
                                break;
                            }
                            pCur.close();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Stream ac = alContacts.stream().map(this::normalizePhoneNumber);
                                alContacts = (ArrayList<String>)ac
                                        .collect(Collectors
                                                .toCollection(ArrayList::new));
                                alContacts.removeIf(a->a.length()<7);
                            }
                            Log.e("Phone Numbers", alContacts.toString());
                        }

                    } while (cursor.moveToNext());
                }
//Perform operations here only which requires permission
            }
        }
    }
    public String normalizePhoneNumber(String number) {

        number = number.replaceAll("[^+0-9]", ""); // All weird characters such as /, -, ...
        number = number.replaceAll("\\s+",""); //All spaces
        number = number.replaceAll("\\+1",""); //All +1
        number = number.replaceAll("^[0]{1,4}", ""); // e.g. 004912345678 -> +4912345678
        if(number.length()>=7) {
            number = number.substring(number.length() - 7);
        }

        return number;
    }
}
