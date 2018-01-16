package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;

/**
 * Created by matteoma on 1/16/2018.
 */

public class MarkerPlacer {

    public static void fillInTheMapWithMarkes(GoogleMap map, ArrayList<User> friendList){
        for (User u: friendList) {
            double lat = u.getLastState().getLatitude();
            double lon = u.getLastState().getLongitude();
            LatLng latLng = new LatLng(lat,lon);
            String userName = u.getUserName();
            String status = u.getLastState().getStato();
            map.addMarker(new MarkerOptions().position(latLng).title(userName).snippet(status));

        }
    }
}
