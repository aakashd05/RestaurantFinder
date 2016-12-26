package beitproject.com.restrofinder.activities;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import beitproject.com.restrofinder.R;
import beitproject.com.restrofinder.adapters.RestaurantListAdapter;
import beitproject.com.restrofinder.models.RestaurantModel;
import beitproject.com.restrofinder.utils.GetPlaces;
import beitproject.com.restrofinder.utils.Utils;

/**
 * Created by AkaashD on 12/21/2016.
 */
public class HomeActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    TextView toggleView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    RestaurantListAdapter restaurantRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initRecyclerView();
        buildGoogleApiClient();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private void initRecyclerView() {
        RecyclerView restaurantRecyclerView = (RecyclerView) findViewById(R.id.recycler_restaurant);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantRecyclerAdapter = new RestaurantListAdapter(new ArrayList<RestaurantModel>(), this);
        restaurantRecyclerView.setAdapter(restaurantRecyclerAdapter);
        toggleView = (TextView) findViewById(R.id.toggle_view);
        toggleView.setOnClickListener(onClickListener);
    }

    public void fetchRestaurantList(final double lat, final double lang) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetPlaces(restaurantRecyclerAdapter, lat, lang).execute();
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            toggleView();
        }
    };

    public void toggleView() {
        if (restaurantRecyclerAdapter.isShowingAll())
            showTopThree();
        else
            showAll();
    }

    public void showAll() {
        restaurantRecyclerAdapter.showAll();
        updateText();
    }


    public void updateText() {
        if (restaurantRecyclerAdapter.isShowingAll())
            toggleView.setText("Show Top");
        else
            toggleView.setText("Show All");
    }

    public void showTopThree() {
        restaurantRecyclerAdapter.showTopThree();
        updateText();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Utils.isNetworkConnected(this, false))
            if (Utils.gpsEnabled(this)) {
                Utils.showLoadingPopup(this);
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                }

            }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void checkLocation() {
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        Utils.isNetworkConnected(this, false);
        if (mLastLocation == null) {
            LocationRequest request = LocationRequest.create();
            request.setNumUpdates(1);
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, request, new LocationListener() {
                        @Override
                        public void onLocationChanged(final Location location) {
                            Utils.hideLoadingPopup();
                            fetchRestaurantList(location.getLatitude(), location.getLongitude());
                            displayHeader(location.getLatitude(), location.getLongitude());
                        }
                    });
        } else {
            Utils.hideLoadingPopup();
            fetchRestaurantList(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            displayHeader(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    }

    private void displayHeader(double latitude, double longitude) {
        String cityName = "";
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            cityName = addresses.get(0).getAddressLine(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextView toolbarHeader;
        toolbarHeader = (TextView) findViewById(R.id.toolbar_text);
        toolbarHeader.setText(cityName);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}
