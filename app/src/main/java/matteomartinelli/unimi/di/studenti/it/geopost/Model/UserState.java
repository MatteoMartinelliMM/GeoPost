package matteomartinelli.unimi.di.studenti.it.geopost.Model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by utente2.academy on 12/20/2017.
 */

public class UserState implements Serializable{
    private LatLng latLng;
    private String stato;

    public UserState(LatLng latLng, String stato) {
        this.latLng = latLng;
        this.stato = stato;
    }

    public UserState() {
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }
}
