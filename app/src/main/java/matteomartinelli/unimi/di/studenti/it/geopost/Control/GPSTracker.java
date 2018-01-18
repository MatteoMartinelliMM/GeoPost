package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;

/**
 * Created by teo on 13/01/18.
 */

public class GPSTracker implements LocationListener {

    public static final String USER_LOCATION = "UserLocation";

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    public static Location getUserLocation(User u){
        Location location =  new Location(USER_LOCATION);
        location.setLatitude(u.getCurrentLatitude());
        location.setLongitude(u.getCurrentLongitude());
        return location;
    }
}
