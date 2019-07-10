package com.example.tedge.barbuds;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class qrScanner extends AppCompatActivity {

    SurfaceView surfaceView;
    CameraSource cameraSource;
    ImageView logout;
    BarcodeDetector barcodeDetector;
    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        surfaceView = findViewById(R.id.camerapreview);
        logout = findViewById(R.id.scanner_logout);

        logout.setOnClickListener(v -> {
            myPrefs.edit().putBoolean("logged_in", false).commit();
            myPrefs.edit().putString("type", "x").commit();
            Intent intent = new Intent(qrScanner.this, EmailLog.class);
            startActivity(intent);
            finish();
        });

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(qrScanner.this,
                            new String[]{Manifest.permission.CAMERA},
                            100);
                    try {
                        cameraSource.start(holder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        cameraSource.start(holder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if (qrCodes.size() != 0) {
                    String s = qrCodes.valueAt(0).displayValue;
                    List<String> info = Arrays.asList(s.split(","));
                    String groupID = info.get(0);
                    String dealID = info.get(1);
                    redeemDeal(groupID, dealID);
                }
                release();
            }
        });
    }

    private void redeemDeal(String groupID, String dealID) {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("groupID", groupID)
                .add("dealID", dealID)
                .build();

        Log.e("leaveGroup", "Group ID:" + groupID);
        Log.e("leaveGroup", "User ID:" + dealID);
        Request request = new Request.Builder().url("http://cgi.soic.indiana.edu/~team48/redeem_deal.php")
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
                    if (responseData.equals("True")) {
                        Snackbar.make(surfaceView, "DEAL REDEEMED", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(surfaceView, "PROBLEM REDEEMING DEAL", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.d("permission", "Granted");

            } else {

                Log.d("permission", "Denied");

            }

        }
    }

    @Override
    public void onBackPressed(){
    }

}
