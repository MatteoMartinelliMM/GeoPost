package matteomartinelli.unimi.di.studenti.it.geopost.Model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Teo on 22/01/2018.
 */

public class PositionEvent {
    public final LatLng latLng;

    public PositionEvent(LatLng latLng) {
        this.latLng = latLng;
    }
}
