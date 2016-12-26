package beitproject.com.restrofinder.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import beitproject.com.restrofinder.R;

/**
 * Created by User on 2/8/2016.
 */
public class PermissionActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 11;
    String allRequiredPermissions[] = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        invokePermission();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 2 * 1000);
    }

    private void invokePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> requestList = getNotGrantedPermissions();
            if (requestList.size() > 0) {
                requestPermission(requestList);
            } else {
                goToSplashScreen();
            }
        } else {
            goToSplashScreen();
        }
    }

    private ArrayList<String> getNotGrantedPermissions() {
        ArrayList<String> notGrantedPermissions = new ArrayList<>();
        checkForPermission(notGrantedPermissions, allRequiredPermissions);
        return notGrantedPermissions;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkForPermission(ArrayList<String> permissionList, String... permissions) {
        for (String permission : permissions)
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                permissionList.add(permission);
    }

    private void goToSplashScreen() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestPermission(ArrayList<String> allPermissions) {
        ActivityCompat.requestPermissions(this, allPermissions.toArray(new String[allPermissions.size()]), PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                boolean permissionDenied = false;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        permissionDenied = true;
                        break;
                    }
                }
                if (permissionDenied) {
                    invokePermission();
                } else {
                    goToSplashScreen();
                }
                break;
        }
    }
}
