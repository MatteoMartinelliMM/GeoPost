package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;
import matteomartinelli.unimi.di.studenti.it.geopost.View.OverviewActivity;

/**
 * Created by teo on 13/01/18.
 */

public class GPSTracker implements  LocationListener{
    private double latitude;
    private double longitude;
    private boolean isRunning = false;
    private Location mLocation;

    public GPSTracker() {


    }

     public boolean isRunning() {
        return this.isRunning;
    }

    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }

    public Location getLocation(){
        return mLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mLocation == null){
            settingLocationAndDoubleLatLng(location);
            isRunning = true;
        } else if(location.getAccuracy() > mLocation.getAccuracy()){
            settingLocationAndDoubleLatLng(location);
        }
    }

    private void settingLocationAndDoubleLatLng(Location location) {
        mLocation = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
}




