package com.example.tedge.barbuds;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;

import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{


    private GoogleMap mMap;
    private Location mLocation;
    private SharedPreferences myPrefs;
    private boolean mPermissionDenied = false;
    private String userID;
    private CircleImageView profilePic;
    private HashMap<Marker, String> barIDs = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Main Activity", "Created");

        setContentView(R.layout.activity_main);
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        final ImageView navigationButton = findViewById(R.id.navigationButton);

        final LinearLayout logout = findViewById(R.id.logout_view);
        final LinearLayout friendsTab = findViewById(R.id.main_friends_tab);
        final LinearLayout groupTab = findViewById(R.id.main_group_tab);
        final LinearLayout notificationsTab = findViewById(R.id.main_notifications_tab);
        final LinearLayout profileTab = findViewById(R.id.main_profile_tab);

        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userID = myPrefs.getString("userID", "0");
        Log.e("userIDMain", userID + "");

        profilePic = findViewById(R.id.navigation_profile_pic);

        Glide.with(MainActivity.this)
                .load("https://cgi.sice.indiana.edu/~team48/photos/" + userID + ".png")
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.bb_profile_picture)
                .into(profilePic);



        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("firebase Token", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    // Log and toast

                    String msg = token;

                    Log.d("firebase Token2", msg);
                    SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String uID = myPrefs.getString("userID", "0");

                    final OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .add("userID", uID)
                            .add("firebaseID", token)
                            .build();
                    Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/add_firebase.php")
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
                            }
                        }

                    });

                });
        setName();


        profileTab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditProfile.class);
            startActivity(intent);
            finish();
        });
        navigationButton.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));


        logout.setOnClickListener(view -> {
            myPrefs.edit().putBoolean("logged_in", false).commit();
            myPrefs.edit().putString("userID", "0").commit();
            Intent intent = new Intent(MainActivity.this, LogIn.class);
            startActivity(intent);
            finish();
        });

        friendsTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(MainActivity.this, Friends.class);
            startActivity(intent);
            finish();
        });

        groupTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(MainActivity.this, Groups.class);
            startActivity(intent);
            finish();

        });
        notificationsTab.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(MainActivity.this, Notifications.class);
            startActivity(intent);
            finish();
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }



    @Override
    public void onBackPressed() {

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String userID = myPrefs.getString("userID", "0");

        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        @SuppressLint("ResourceType") View myLocationButton = mapFragment.getView().findViewById(0x2);

        if (myLocationButton != null && myLocationButton.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            // location button is inside of RelativeLayout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myLocationButton.getLayoutParams();

            // Align it to - parent BOTTOM|LEFT
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);

            // Update margins, set to 10dp
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics());
            params.setMargins(margin, margin, margin, margin);

            myLocationButton.setLayoutParams(params);
        }
        enableMyLocation();


        String TAG = "Map Styling";

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        getBars();
        mMap.setOnMarkerClickListener(marker -> {
            if (!marker.getTitle().equals("your position")) {
                String barPosition = marker.getPosition().toString();
                Double myLat = mLocation.getLatitude();
                Double myLng = mLocation.getLongitude();
                LatLng myLocation = new LatLng(myLat, myLng);
                String ID = barIDs.get(marker);
                Intent intent;
                intent = new Intent(MainActivity.this, BarInfo.class);
                intent.putExtra("name", marker.getTitle());
                intent.putExtra("bar_position", barPosition);
                intent.putExtra("userID", userID);
                intent.putExtra("my_position", myLocation.toString());
                intent.putExtra("barID", ID);
                startActivity(intent);
            } else {
            }
            return false;
        });
        if(mLocation != null) {
            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f));
        }
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, 1,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            mLocation = locationManager.getLastKnownLocation((locationManager.getBestProvider(criteria, false)));
            try {
                LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.1f));
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != 1) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    public void getBars() {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .build();
        Request request = new Request.Builder().url("http://cgi.sice.indiana.edu/~team48/get_bars.php")
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
                        final JSONArray responseData = new JSONArray(response.body().string());
                        MainActivity.this.runOnUiThread(() -> {
                            try {
                                for (int i = 0; i < responseData.length(); i++) {
                                    JSONObject currBar = (JSONObject) responseData.get(i);
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    String placeName = currBar.get("bar_name").toString();
                                    float lat = Float.parseFloat((String) currBar.get("lat"));
                                    float lng = Float.parseFloat((String) currBar.get("lon"));
                                    String ID = currBar.getString("barID");

                                    LatLng latLng = new LatLng(lat, lng);
                                    markerOptions.position(latLng);
                                    markerOptions.title(placeName);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                                    Marker marker = mMap.addMarker(markerOptions);
                                    barIDs.put(marker, ID);
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



    private void setName() {

        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("userID", userID)
                .build();
        Log.e("userID", userID);
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/get_user_name.php")
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
                    try {
                        final String responseData = response.body().string();
                        Log.e("setGroupID", responseData);

                        final JSONArray responseArray = new JSONArray(responseData);
                        final JSONObject responseJ = responseArray.getJSONObject(0);
                        String fname = responseJ.getString("fname");
                        String lname = responseJ.getString("lname");
                        String fullName = fname + " " + lname;

                        MainActivity.this.runOnUiThread(() -> {
                            TextView name = findViewById(R.id.name);
                            name.setText(fullName);
                            myPrefs.edit().putString("name", fullName).commit();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }
        });
    }
}
