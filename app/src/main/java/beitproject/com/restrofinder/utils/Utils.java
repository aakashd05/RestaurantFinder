package beitproject.com.restrofinder.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import beitproject.com.restrofinder.activities.HomeActivity;

/**
 * Created by AkaashD on 12/23/2016.
 */
public class Utils {
    static ProgressDialog progressDialog;

    public static boolean isNetworkConnected(Activity activity, boolean checkInBackground) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        else if (!checkInBackground)
            showSettingsPopup(activity);
        return false;
    }

    public static void showSettingsPopup(final Activity activity) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("No Internet connection.");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                activity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity");
                activity.startActivity(intent);}
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    public static void showLoadingPopup(Activity activity) {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static AlertDialog alert = null;

    public static boolean gpsEnabled(HomeActivity activity) {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(activity);
            return false;
        }
        return true;
    }

    private static void buildAlertMessageNoGps(final HomeActivity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        alert.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public static void hideLoadingPopup() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }
}
