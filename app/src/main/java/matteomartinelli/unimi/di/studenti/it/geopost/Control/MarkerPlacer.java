package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import matteomartinelli.unimi.di.studenti.it.geopost.Model.User;

/**
 * Created by matteoma on 1/16/2018.
 */

public class MarkerPlacer {
    private static LatLng latLng;
    private static double latitude;
    private static double longitude;
    private static String userName;
    private static String status;

    public static void fillInTheMapWithFriendsMarkers(GoogleMap map, ArrayList<User> friendList) {
        for (User u : friendList) {
            settingTheMarkerData(u, map, false);

        }

    }


    public static void addNewStatusMarker(GoogleMap map, User personalProfile) {
        settingTheMarkerData(personalProfile, map, true);


    }

    private static void settingTheMarkerData(User u, GoogleMap map, boolean isTheLoggedUser) {
        MarkerOptions newMarkerOption;
        latitude = u.getLastState().getLatitude();
        longitude = u.getLastState().getLongitude();
        latLng = new LatLng(latitude, longitude);
        userName = u.getUserName();
        status = u.getLastState().getStato();
        newMarkerOption = new MarkerOptions().position(latLng).title(userName).snippet(status);
        if (isTheLoggedUser)
            newMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        else
            newMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        map.addMarker(newMarkerOption);
    }
}
