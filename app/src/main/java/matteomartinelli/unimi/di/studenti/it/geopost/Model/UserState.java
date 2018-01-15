package matteomartinelli.unimi.di.studenti.it.geopost.Model;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by utente2.academy on 12/20/2017.
 */

public class UserState implements Serializable, Comparable<UserState>{
    private double latitude;
    private double longitude;
    private String stato;
    private float distanceToLoggedUser;

    public UserState(double latitude , double longitude, String stato) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.stato = stato;
    }

    public UserState() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public float getDistanceToLoggedUser() {
        return distanceToLoggedUser;
    }

    public void setDistanceToLoggedUser(float distanceToLoggedUser) {
        this.distanceToLoggedUser = distanceToLoggedUser;
    }


    @Override
    public int compareTo(@NonNull UserState o) {
        return Float.compare(this.distanceToLoggedUser,o.distanceToLoggedUser);
    }
}
