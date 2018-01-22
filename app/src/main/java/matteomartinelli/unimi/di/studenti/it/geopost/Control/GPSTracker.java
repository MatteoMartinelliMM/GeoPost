package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.PositionEvent;

/**
 * Created by teo on 13/01/18.
 */

public class GPSTracker implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private double latitude;
    private double longitude;
    private boolean isRunning = false;
    private Location mLocation;
    private GoogleApiClient googleApiClient;
    private Context context;
    private boolean googleApiClientReady = false, permissionGranted = false;
    private LocationRequest locationRequest;

    public GPSTracker(Context context, boolean permissionGranted) {
        this.context = context;
        this.permissionGranted = permissionGranted;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    public LatLng getNewLocation() {
        checkAndStartLocationUpdate();
        return getLatLng();
    }

    public void askForNewLocation() {
        checkAndStartLocationUpdate();
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public Location getLocation() {
        return mLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isReady() {
        return googleApiClientReady && permissionGranted;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mLocation == null) {
            settingLatLng(location, true);
        } else if (mLocation.getAccuracy() < mLocation.getAccuracy()) {
            settingLatLng(location, false);
        }
    }

    private void settingLatLng(Location location, boolean haveNullValue) {
        mLocation = new Location(location);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if(haveNullValue) EventBus.getDefault().post(new PositionEvent(getLatLng()));

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleApiClientReady = true;
        checkAndStartLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void checkAndStartLocationUpdate() {
        if (googleApiClientReady && permissionGranted) {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            } catch (SecurityException e) {
                // this should not happen because the exception fires when the user has not
                // granted permission to use location, but we already checked this
                e.printStackTrace();
            }
        }
    }
}




