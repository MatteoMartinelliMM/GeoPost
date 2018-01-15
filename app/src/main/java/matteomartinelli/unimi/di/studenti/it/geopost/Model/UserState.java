package matteomartinelli.unimi.di.studenti.it.geopost.Model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by utente2.academy on 12/20/2017.
 */

public class UserState implements Serializable{
    private double latitude;
    private double longitude;
    private String stato;

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
}
